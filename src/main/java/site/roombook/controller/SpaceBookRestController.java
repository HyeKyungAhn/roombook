package site.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.roombook.annotation.StringDate;
import site.roombook.annotation.User;
import site.roombook.domain.*;
import site.roombook.service.SpaceBookService;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class SpaceBookRestController {
    @Autowired
    private SpaceBookService spaceBookService;

    private static final int PERSONAL_TIMESLOTS_LIST_LIMIT = 10;

    @ExceptionHandler(JsonProcessingException.class)
    private ResponseEntity<String> errorHandler(JsonProcessingException e) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonServerState = "";

        if(((JsonMappingException) e).getPath().get(0).getFrom() instanceof SpaceBookDto){
            if(e.getCause() instanceof DateTimeParseException) {
                ServerState serverState = ServerState.Builder().result("FAIL").errorMessage("날짜 및 시간을 형식에 맞게 입력해주세요.").build();
                jsonServerState = objectMapper.writeValueAsString(serverState);
            } else if (((InvalidFormatException) e).getPath().get(0).getFieldName().equals("spaceNo")) {
                ServerState serverState = ServerState.Builder().result("FAIL").errorMessage("잘못된 예약정보가 입력되었습니다.\n새로고침 후 다시 예약해주세요.").build();
                jsonServerState = objectMapper.writeValueAsString(serverState);
            }
        }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Content-Type", "application/json; charset=utf-8").body(jsonServerState);
    }


    @GetMapping(value = "/spaces/{spaceNo}/timeslots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SpaceBookDto>> getTimeslotOfTheDay(@PathVariable("spaceNo") Integer spaceNo, @StringDate LocalDate date, @User EmplDto empl) {
        List<SpaceBookDto> list = spaceBookService.getBookedTimeslotsOfTheDay(spaceNo, date, empl.getEmplId());
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/book/timeslots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerState> bookTimeslot(@RequestBody SpaceBookDto body, @User EmplDto empl){
        if (body.getSpaceBookSpaceNo() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약정보가 잘못되었습니다.\n새로고침 후 다시 예약해주세요.").build());
        } else if (body.getSpaceBookBgnTm() == null || body.getSpaceBookEndTm() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약 시간을 입력해주세요.").build());
        } else if (body.getSpaceBookCn() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약 내용을 입력해주세요.").build());
        } else if (body.getSpaceBookDate() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약 날짜를 선택해주세요.").build());
        }

        ServiceResult result = spaceBookService.bookTimeslot(body, empl);

        if(!result.isSuccessful()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약에 실패했습니다. 예약정보를 다시 확인해주세요.").build());
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(linkTo(methodOn(SpaceBookController.class).getMyBookPage()).toUri())
                .build();
    }

    @PatchMapping(value = "/book/timeslots/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerState> modifyTimeslot(@PathVariable("bookId") String bookId, @RequestBody SpaceBookDto inputs, @User EmplDto empl) {

        ServiceResult result = spaceBookService.modifyBooking(inputs, bookId, empl);

        if(result.isSuccessful()){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .location(linkTo(methodOn(SpaceBookController.class).getMyBookPage()).toUri())
                    .body(ServerState.Builder().result("SUCCESS").errorMessage("예약이 수정되었습니다.").build());
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ServerState.Builder().result("FAIL").errorMessage("예악 정보를 확인 후 다시 입력해주세요.").build());
        }
    }

    @PatchMapping(value = "/book/timeslots/{bookId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerState> cancelBooking(@PathVariable("bookId") String bookId, @User EmplDto empl) {
        ServiceResult result = spaceBookService.cancelBooking(empl, bookId);

        if (result.isSuccessful()) {
            return ResponseEntity.status(HttpStatus.OK).body(ServerState.Builder().result("SUCCESS").errorMessage("예약이 취소되었습니다.").build());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(ServerState.Builder().result("FAIL").errorMessage("예약 취소에 실패하였습니다. 다시 시도해주세요.").build());
        }
    }

    @GetMapping(value = "/mybook/timeslots", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<MyBookDto> getMyTimeslots(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page, @User EmplDto empl, HttpServletResponse response) {
        PageHandler ph = new PageHandler(spaceBookService.getPersonalTimeslotsCount(empl.getEmplId()), page, PERSONAL_TIMESLOTS_LIST_LIMIT);

        if (ph.getTotalCnt() == 0) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            String spaceListUri = linkTo(methodOn(SpaceController.class).getSpaceList("1", null)).toUri().toString();
            response.setHeader(HttpHeaders.LOCATION, spaceListUri);
            return EntityModel.of(MyBookDto.Builder().build());
        }

        List<SpaceBookDto> timeslots = spaceBookService.getPersonalTimeslots(empl.getEmplId(), ph.getOffset(), PERSONAL_TIMESLOTS_LIST_LIMIT);

        MyBookDto.MyBookDtoBuilder myBookDto = MyBookDto.Builder();

        myBookDto.bookList(timeslots);

        myBookDto.hasNext(!page.equals(ph.getTotalPage()));

        return EntityModel.of(myBookDto.build()
        , linkTo(methodOn(SpaceBookController.class).getChangingBookingPage(null)).withRel("modification")
        , linkTo(methodOn(SpaceBookRestController.class).cancelBooking(null, null)).withRel("cancel"));
    }
}
