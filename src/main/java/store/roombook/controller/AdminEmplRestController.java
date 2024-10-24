package store.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import store.roombook.ExceptionMsg;
import store.roombook.domain.EmplDto;
import store.roombook.domain.PageHandler;
import store.roombook.domain.ServerState;
import store.roombook.domain.ServiceResult;
import store.roombook.service.EmplService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/admin")
public class AdminEmplRestController {

    @Autowired
    EmplService emplService;

    private static final int EMPL_LIST_LIMIT = 10;
    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";
    private static final String INVALID = "INVALID";

    @ExceptionHandler(NumberFormatException.class)
    private ResponseEntity<String> numberFormatExceptionHandler() {
        Map<String, String> map = new HashMap<>();
        map.put("msg", "INVALID_REQUEST");
        map.put("redirectUrl", "/not-found");

        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }


    @GetMapping(value = "/empls", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<Map<String,Object>> getAdminEmplListAppState(@RequestParam(name = "page", defaultValue = "1") int page
            , String option, String optionValue){

        if ("authName".equals(option)) {
            optionValue = convertToOriginalAuthName(optionValue);
            if(optionValue.equals(INVALID)) return EntityModel.of(Map.of("msg", "검색어를 다시 입력해주세요."));
        }

        PageHandler ph = new PageHandler(emplService.getSearchedEmplsCount(option, optionValue), page, EMPL_LIST_LIMIT);

        List<EmplDto> emplList = emplService.getEmplListForEmplAuthChange(option, optionValue, EMPL_LIST_LIMIT, ph.getOffset());
        List<EmplDto> convertedEmplList = convertAuthNameInEmplList(emplList);

        Map<String, Object> emplListAndAppState = new HashMap<>();
        emplListAndAppState.put("emplList", convertedEmplList);
        emplListAndAppState.put("ph", ph);
        emplListAndAppState.put("msg", SUCCESS);

        return EntityModel.of(emplListAndAppState
                , linkTo(methodOn(AdminEmplRestController.class).updateEmpl(null, null)).withRel("updateAuth"));
    }

    @PatchMapping("/empls/{id}")
    public ResponseEntity<ServerState> updateEmpl(@PathVariable("id") String emplId, @RequestBody String body){
        JsonNode jsonNode;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        try {
            jsonNode = new ObjectMapper().readTree(body);
        } catch (JsonProcessingException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ServerState.Builder()
                            .result(FAIL)
                            .errorMessage(ExceptionMsg.EMPL_AUTH_UNEXPECTED_PROBLEM.getContent())
                            .build());
        }

        String authNm = convertToOriginalAuthName(jsonNode.get("authNm").asText());

        if (authNm.equals(INVALID)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ServerState.Builder()
                            .result(FAIL)
                            .errorMessage(ExceptionMsg.EMPL_AUTH_INVALID_AUTH_NAME.getContent())
                            .build());
        }

        ServiceResult result = emplService.updateEmplAuth(emplId, authNm, userDetails.getUsername());
        if (result.isSuccessful()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ServerState.Builder()
                            .result(SUCCESS)
                            .build());
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ServerState.Builder()
                            .result(FAIL)
                            .errorMessage(ExceptionMsg.EMPL_AUTH_UPDATE_FAIL.getContent())
                            .build());
        }
    }

    private List<EmplDto> convertAuthNameInEmplList(List<EmplDto> list) {
        List<EmplDto> newList = new ArrayList<>();

        for (EmplDto selectedEmpl : list) {
            EmplDto newEmpl = EmplDto.EmplDtoBuilder()
                    .rnm(selectedEmpl.getRnm())
                    .email(selectedEmpl.getEmail())
                    .emplId(selectedEmpl.getEmplId())
                    .empno(selectedEmpl.getEmpno())
                    .emplAuthNm(convertToReadableAuthName(selectedEmpl.getEmplAuthNm())).build();
            newList.add(newEmpl);
        }

        return newList;
    }

    private String convertToReadableAuthName(String authName) {
        return switch (authName) {
            case "ROLE_SUPER_ADMIN" -> "슈퍼관리자";
            case "ROLE_EMPL_ADMIN" -> "사원관리자";
            case "ROLE_RSC_ADMIN" -> "공간예약관리자";
            default -> "사원";
        };
    }

    private String convertToOriginalAuthName(String optionValue) {
        return switch (optionValue) {
            case "슈퍼관리자" -> "ROLE_SUPER_ADMIN";
            case "사원관리자" -> "ROLE_EMPL_ADMIN";
            case "공간예약관리자" -> "ROLE_RSC_ADMIN";
            case "관리자" -> "ADMIN";
            case "사원" -> "ROLE_USER";
            default -> INVALID;
        };
    }
}
