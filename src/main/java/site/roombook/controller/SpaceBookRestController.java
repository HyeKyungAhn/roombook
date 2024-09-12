package site.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import site.roombook.domain.ServerState;
import site.roombook.domain.ServiceResult;
import site.roombook.domain.SpaceBookDto;
import site.roombook.service.SpaceBookService;

import java.time.format.DateTimeParseException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class SpaceBookRestController {

    @Autowired
    private SpaceBookService spaceBookService;
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
    public ResponseEntity<List<SpaceBookDto>> getTimeslotOfTheDay(@PathVariable("spaceNo") Integer spaceNo, @RequestParam(required = false) String date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<SpaceBookDto> list = spaceBookService.getBookedTimeslotsOfTheDay(spaceNo, date, userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/book/timeslots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerState> bookTimeslot(@RequestBody SpaceBookDto body){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (body.getSpaceBookSpaceNo() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약정보가 잘못되었습니다.\n새로고침 후 다시 예약해주세요.").build());
        } else if (body.getSpaceBookBgnTm() == null || body.getSpaceBookEndTm() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약 시간을 입력해주세요.").build());
        } else if (body.getSpaceBookCn() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약 내용을 입력해주세요.").build());
        } else if (body.getSpaceBookDate() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약 날짜를 선택해주세요.").build());
        }

        String emplRole = userDetails.getAuthorities().toArray()[0].toString();
        ServiceResult result = spaceBookService.bookTimeslot(body, userDetails.getUsername(), emplRole);

        if(!result.isSuccessful()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ServerState.Builder().result("FAIL").errorMessage("예약에 실패했습니다. 예약정보를 다시 확인해주세요.").build());
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(linkTo(methodOn(SpaceBookController.class).getSpaceBookingPage(body.getSpaceBookSpaceNo(), null, null, null)).toUri()).build(); //TODO: 내 예약 페이지 구현 후 그 링크로 변경
    }

    @PatchMapping(value = "/book/timeslots/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerState> modifyTimeslot(@PathVariable("bookId") String bookId, @RequestBody SpaceBookDto inputs) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emplRole = userDetails.getAuthorities().toArray()[0].toString();

        ServiceResult result = spaceBookService.modifyBooking(inputs, bookId, userDetails.getUsername(), emplRole);

        if(result.isSuccessful()){
            return ResponseEntity.status(HttpStatus.OK).build(); //TODO 내 예약 목록 기능 구현 후 그 페이지 URL로 변경
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(ServerState.Builder().result("FAIL").errorMessage("예악 정보를 확인 후 다시 입력해주세요.").build());
        }
    }

    @PatchMapping(value = "/book/timeslots/{bookId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerState> cancelBooking(@PathVariable("bookId") String bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emplRole = userDetails.getAuthorities().toArray()[0].toString();

        ServiceResult result = spaceBookService.cancelBooking(userDetails.getUsername(), emplRole, bookId);

        if (result.isSuccessful()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(ServerState.Builder().result("FAIL").errorMessage("예약 취소에 실패하였습니다. 다시 시도해주세요.").build());
        }
    }

}
