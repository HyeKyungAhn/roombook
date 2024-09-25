package site.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import site.roombook.CmnCode;
import site.roombook.FileStorageProperties;
import site.roombook.annotation.StringDate;
import site.roombook.domain.*;
import site.roombook.service.FileService;
import site.roombook.service.RescService;
import site.roombook.service.SpaceService;
import site.roombook.service.SpaceTransactionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class SpaceRestController {
    @Autowired
    FileService fileService;

    @Autowired
    SpaceService spaceService;

    @Autowired
    RescService rescService;

    @Autowired
    SpaceTransactionService spaceTransactionService;

    @Autowired
    FileStorageProperties properties;

    private static final int SPACE_CNT_OF_LIST = 5;
    private static final int RESC_CNT_OF_LIST = 3;

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        String uri = request.getRequestURI();
        response.setHeader("location", uri);

        String jsonServerState;
        ServerState serverState = ServerState.Builder().result("FAIL").errorMessage("잘못된 요청입니다. 다시 시도해주세요.").build();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonServerState = objectMapper.writeValueAsString(serverState);
        } catch (JsonProcessingException exception) {
            jsonServerState = "";
        }
        return ResponseEntity
                .badRequest()
                .header("Content-Type", "application/json; charset=utf-8")
                .location(URI.create(request.getRequestURI()))
                .body(jsonServerState);
    }

    @PostMapping("/admin/spaces")
    public ResponseEntity<String> saveNewSpace(@RequestPart(required = false, value = "files") MultipartFile[] newFiles,
                                               @RequestParam(required = false, value = "spaceFacility") String resc,
                                               @RequestParam(required = false, value = "space") String spaceData) {
        if (spaceData == null || spaceData.equals("")) {
            return createErrorResponse("공간 정보를 입력해주세요.");
        }

        SpaceTransactionServiceResult saveResult;

        try {
            SpaceDto space = parseJson(spaceData, SpaceDto.class);
            List<RescDto> rescs = parseJson(resc, new TypeReference<>(){});

            saveResult = spaceTransactionService.saveSpace(space, newFiles, "admin", rescs); //TODO: admin 바꾸기
        } catch (DuplicateKeyException e){
            return createErrorResponse("같은 이름의 공간이 있습니다.\n공간명을 변경해주세요.");
        } catch (JsonProcessingException e){
            return createErrorResponse("유효하지 않은 값입니다. 다시 입력해주세요.");
        } catch (DateTimeParseException e){
            return createErrorResponse("날짜를 확인한 후 다시 입력해주세요.");
        } catch (IllegalArgumentException e){
            return createErrorResponse("파일은 5개까지 저장할 수 있습니다.");
        } catch (MultipartException e){
            return createErrorResponse("jpg/jpeg, png 파일만 업로드할 수 있습니다.");
        } catch (IOException e){
            return createErrorResponse("업로드할 수 없는 파일입니다. 확인 후 재업로드해주세요.");
        }

        if(!saveResult.isSpaceSaved()){
            return createErrorResponse("공간 등록이 실패했습니다.\n공간 정보 확인 후 다시 입력해주세요.");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(linkTo(methodOn(SpaceController.class).getAdminSpaceList(null, null)).toUri())
                .build();
    }

    @PostMapping("/admin/spaces/{spaceNo:[0-9]{1,9}}")
    public ResponseEntity<String> modifySpace(@PathVariable("spaceNo") Integer spaceNo
            , @RequestPart(required = false, value = "newFiles") MultipartFile[] newFiles
            , @RequestParam(required = false, value = "deletedFileNames") String deletedFileNames
            , @RequestParam(required = false, value = "spaceFacility") String resc
            , @RequestParam(required = false, value = "space") String spaceData) {

        SpaceDto space;
        List<RescDto> rescs;
        ArrayList<String> fileNameArray;
        SpaceTransactionServiceResult modifyResult;

        try {
            space = parseJson(spaceData, SpaceDto.class);
            rescs = parseJson(resc, new TypeReference<>() {
            });
            fileNameArray = parseJson(deletedFileNames, new TypeReference<>() {
            });

            modifyResult = spaceTransactionService.modifySpace(spaceNo, "admin", space, newFiles, fileNameArray, rescs);
        } catch (JsonProcessingException e) {
            return createErrorResponse("유효하지 않은 값입니다. 다시 입력하세요.");
        } catch (DateTimeParseException e) {
            return createErrorResponse("날짜를 확인한 후 다시 입력해주세요.");
        } catch (IllegalArgumentException e) {
            return createErrorResponse("파일은 5개까지 저장할 수 있습니다.");
        } catch (FileNotFoundException e) {
            return createErrorResponse("파일 삭제에 실패했습니다.");
        } catch (MultipartException | IOException e) {
            return createErrorResponse("유효하지 않은 파일입니다.\njpg/jpeg, png 파일만 업로드할 수 있습니다.");
        }

        if (!modifyResult.isSpaceSaved()) {
            return createErrorResponse("공간 등록이 실패했습니다.\n공간 정보 확인 후 다시 입력해주세요.");
        }

        String msg = "";

        if (!modifyResult.getFileSaveResult().isSaved() || !modifyResult.getFileDeleteResult().isDeleted()) {
            msg = String.format("정상처리되지 않은 파일이 있습니다.%n(저장되지 않은 파일:%s개, 삭제되지 않은 파일 %s개)"
                    , modifyResult.getFileSaveResult().getUnsavedFileCnt()
                    , modifyResult.getFileDeleteResult().getUndeletedFileCnt());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                .location(linkTo(methodOn(SpaceController.class).getAdminSpaceList(null, null)).toUri())
                .body(msg);
    }

    @GetMapping(value = "/admin/spaces", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<SpaceListDto> getSpaceListForAdmin(@RequestParam(required = false, name = "page", defaultValue = "1") String stringPage
            , @StringDate LocalDate date
            , HttpServletResponse response) {
        int page = isNumeric(stringPage) ? Integer.parseInt(stringPage) : 1;
        if (page <= 0) page = 1;

        PageHandler ph = new PageHandler(spaceService.getSpaceAllCnt(), page);

        if (ph.getTotalCnt() == 0) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return EntityModel.of(SpaceListDto.SpaceListDto().build());
        } else if (page > ph.getTotalPage()) {
            response.setStatus(HttpStatus.NOT_MODIFIED.value());
            response.setHeader("location", linkTo(methodOn(HomeController.class).getNotFoundPage()).toUri().toString());
            return EntityModel.of(SpaceListDto.SpaceListDto().build());
        }

        SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto = SpaceInfoAndTimeslotDto.SpaceRescFileDtoBuilder()
                .spaceCnt(SPACE_CNT_OF_LIST)
                .offset(ph.getOffset())
                .rescCnt(RESC_CNT_OF_LIST)
                .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .isHiddenSpaceInvisible(false)
                .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                .spaceBookDate(date)
                .build();

        SpaceListDto spaceList = spaceService.getSpaceList(ph, spaceInfoAndTimeslotDto);

        return EntityModel.of(spaceList
                , linkTo(methodOn(SpaceBookController.class).getSpaceBookingPage(null, null, null, null)).withRel("booking")
                , linkTo(methodOn(SpaceController.class).getAdminSpaceDetailPage(null)).withRel("spaceDetail")
                , linkTo(methodOn(SpaceRestController.class).getSpaceListForAdmin(null, null, null)).withSelfRel());
    }

    @GetMapping(value = "/spaces", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<SpaceListDto> getSpaceList(@RequestParam(required = false, name = "page", defaultValue = "1") String stringPage
            , @StringDate LocalDate date
            , HttpServletResponse response) {
        int page = isNumeric(stringPage) ? Integer.parseInt(stringPage) : 1;

        if (page <= 0) page = 1;

        PageHandler ph = new PageHandler(spaceService.getNotHiddenSpaceCnt(), page);

        if (ph.getTotalCnt() == 0) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return EntityModel.of(SpaceListDto.SpaceListDto().build());
        } else if (page > ph.getTotalPage()) {
            response.setStatus(HttpStatus.NOT_MODIFIED.value());
            response.setHeader("location", linkTo(methodOn(HomeController.class).getNotFoundPage()).toUri().toString());
            return EntityModel.of(SpaceListDto.SpaceListDto().build());
        }

        SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto = SpaceInfoAndTimeslotDto.SpaceRescFileDtoBuilder()
                .spaceCnt(SPACE_CNT_OF_LIST)
                .offset(ph.getOffset())
                .rescCnt(RESC_CNT_OF_LIST)
                .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .isHiddenSpaceInvisible(true)
                .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                .spaceBookDate(date)
                .build();

        SpaceListDto spaceList = spaceService.getSpaceList(ph, spaceInfoAndTimeslotDto);

        return EntityModel.of(spaceList
                , linkTo(methodOn(SpaceBookController.class).getSpaceBookingPage(null, null, null, null)).withRel("booking")
                , linkTo(methodOn(SpaceController.class).getSpaceDetailPage(null)).withRel("spaceDetail")
                , linkTo(methodOn(SpaceRestController.class).getSpaceList(null, null, null)).withSelfRel());
    }

    private boolean isNumeric(String str) {
        return str.matches("^\\d+$");
    }

    private ResponseEntity<String> createErrorResponse(String message) {
        return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                .body(message);
    }

    private <T> T parseJson(String json, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(json, clazz);
    }

    private <T> T parseJson(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(json, typeReference);
    }
}
