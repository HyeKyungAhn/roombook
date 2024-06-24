package site.roombook.controller.space;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import site.roombook.CmnCode;
import site.roombook.FileStorageProperties;
import site.roombook.domain.*;
import site.roombook.service.FileService;
import site.roombook.service.RescService;
import site.roombook.service.SpaceService;
import site.roombook.service.SpaceTransactionService;
import site.roombook.validator.ImgValidator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class SpaceController {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png");

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

    static final int SPACE_CNT_OF_LIST = 5;
    static final int RESC_CNT_OF_LIST = 3;

    @InitBinder("multipartFile")
    public void contentTypeChecker(WebDataBinder binder) {
        binder.setValidator(new ImgValidator());
    }

    @GetMapping("/admin-spaces/new")
    public String getPageForSpaceDataInput(){
        return "space/adminSpaceInsert";
    }

    @PostMapping("/admin-spaces")
    @ResponseBody
    public ResponseEntity<String> saveNewSpace(@RequestPart(required = false, value = "files") MultipartFile[] newFiles,
                               @RequestParam(required = false, value = "spaceFacility") String resc,
                               @RequestParam(value = "space") String spaceData) {

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

        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin-spaces/{spaceNo:[0-9]{1,9}}/edit")
    public String getModifySpacePage(@PathVariable("spaceNo") int spaceNo, Model m){
        try{
            getDetailPage(spaceNo, m, false);
        } catch (IllegalArgumentException e){
            return "space/notfound";
        }
        return "space/adminSpaceMod";
    }

    @PostMapping("/admin-spaces/{spaceNo:[0-9]{1,9}}")
    @ResponseBody
    public ResponseEntity<String> modifySpace(@RequestPart(required = false, value = "newFiles") MultipartFile[] newFiles,
                                      @PathVariable("spaceNo") int spaceNo,
                                      @RequestParam(required = false, value = "deletedFileNames") String deletedFileNames,
                                      @RequestParam(required = false, value = "spaceFacility") String resc,
                                      @RequestParam(value = "space") String spaceData) {

        SpaceDto space;
        List<RescDto> rescs;
        ArrayList<String> fileNameArray;
        SpaceTransactionServiceResult modifyResult;

        try{
            space = parseJson(spaceData, SpaceDto.class);
            rescs = parseJson(resc, new TypeReference<>(){});
            fileNameArray = parseJson(deletedFileNames, new TypeReference<>(){});

            modifyResult = spaceTransactionService.modifySpace(spaceNo, "admin", space, newFiles, fileNameArray, rescs);
        } catch (JsonProcessingException e){
            return createErrorResponse("유효하지 않은 값입니다. 다시 입력하세요.");
        } catch (DateTimeParseException e){
            return createErrorResponse("날짜를 확인한 후 다시 입력해주세요.");
        }catch (IllegalArgumentException e){
            return createErrorResponse("파일은 5개까지 저장할 수 있습니다.");
        } catch (FileNotFoundException e){
            return createErrorResponse("파일 삭제에 실패했습니다.");
        } catch (MultipartException | IOException e){
            return createErrorResponse("유효하지 않은 파일입니다.\njpg/jpeg, png 파일만 업로드할 수 있습니다.");
        }

        if(!modifyResult.isSpaceSaved()){
            return createErrorResponse("공간 등록이 실패했습니다.\n공간 정보 확인 후 다시 입력해주세요.");
        }

        String msg = "";

        if(!modifyResult.getFileSaveResult().isSaved() || !modifyResult.getFileDeleteResult().isDeleted()){
            msg = String.format("정상처리되지 않은 파일이 있습니다.%n(저장되지 않은 파일:%s개, 삭제되지 않은 파일 %s개)"
                    , modifyResult.getFileSaveResult().getUnsavedFileCnt()
                    , modifyResult.getFileDeleteResult().getUndeletedFileCnt());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                .body(msg);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException e){
        return "space/notfound";
    }

    @GetMapping("/admin-spaces")
    public String getAdminSpaceList(@RequestParam(name = "page", defaultValue = "1") int page, Model m){
        PageHandler ph = new PageHandler(spaceService.getSpaceAllCnt(), page);

        if(page<0 || page>ph.getTotalPage()){
            return "space/notfound";
        }

        List<SpaceRescFileDto> list = spaceService.getSpaceList(SPACE_CNT_OF_LIST, ph.getOffset(), RESC_CNT_OF_LIST, CmnCode.ATCH_LOC_CD_SPACE, false);
        m.addAttribute("list", list);
        m.addAttribute("thumbnailPath", properties.getThumbnailUploadPath());
        m.addAttribute("ph", ph);

        return "space/adminSpaceList";
    }

    @GetMapping("/admin-spaces/{spaceNo:[0-9]{1,9}}")
    public String getAdminSpaceDetailPage(@PathVariable("spaceNo") int spaceNo, Model m){
        //TODO : 권한 조회
        try{
            getDetailPage(spaceNo, m, false);
            m.addAttribute("imgPath", properties.getOriginalUploadPath());
        } catch (IllegalArgumentException e){
            return "space/notfound";
        }

        return "space/adminSpaceDetail";
    }

    @GetMapping("/spaces")
    public String getSpaceList(@RequestParam(name = "page", defaultValue = "1") int page, Model m){
        PageHandler ph = new PageHandler(spaceService.getNotHiddenSpaceCnt(), page);

        if(page<0 || page>ph.getTotalPage()){
            return "space/notfound";
        }

        List<SpaceRescFileDto> list = spaceService.getSpaceList(SPACE_CNT_OF_LIST, ph.getOffset(), RESC_CNT_OF_LIST, CmnCode.ATCH_LOC_CD_SPACE, true);
        m.addAttribute("list", list);
        m.addAttribute("thumbnailPath", properties.getThumbnailUploadPath());
        m.addAttribute("ph", ph);

        return "space/spaceList";
    }

    @GetMapping("/spaces/{spaceNo:[0-9]{1,9}}")
    public String getSpaceDetailPage(@PathVariable("spaceNo") int spaceNo, Model m){
        //TODO : 권한 조회
        try{
            getDetailPage(spaceNo, m, true);
            m.addAttribute("imgPath", properties.getOriginalUploadPath());
        } catch (IllegalArgumentException e){
            return "space/notfound";
        }
        return "space/spaceDetail";
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

    private ResponseEntity<String> createErrorResponse(String message) {
        return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                .body(message);
    }

    private void getDetailPage(int spaceNo, Model m, boolean isHiddenSpaceInvisible) throws IllegalArgumentException{
        Map<String, Object> spaceDetails = spaceService.getOneSpaceAndDetails(spaceNo, CmnCode.ATCH_LOC_CD_SPACE, isHiddenSpaceInvisible);

        if(spaceDetails.isEmpty()){
            throw new IllegalArgumentException("Non-existing or hidden space");
        }

        String jsonRescs = null;
        String jsonFiles = null;

        try {
            jsonRescs = convertToJson(spaceDetails, "rescs");
            jsonFiles = convertToJson(spaceDetails, "files");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        m.addAttribute("space", spaceDetails.get("space"));
        m.addAttribute("files", jsonFiles);
        m.addAttribute("resources", jsonRescs);
    }

    private String convertToJson(Map<String, Object> spaceDetail, String targetDataName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return objectMapper.writeValueAsString(spaceDetail.get(targetDataName));
    }
}
