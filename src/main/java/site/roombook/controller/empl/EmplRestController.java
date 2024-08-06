package site.roombook.controller.empl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import site.roombook.ExceptionMsg;
import site.roombook.domain.ServerState;
import site.roombook.domain.ServiceResult;
import site.roombook.domain.SignupInput;
import site.roombook.domain.SignupState;
import site.roombook.service.EmailService;
import site.roombook.service.EmplService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmplRestController {
    @Autowired
    EmailService emailService;

    @Autowired
    EmplService emplService;

    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";
    private static final String SIGNUP_UNABLE = "SIGNUP_UNABLE";

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<SignupState> handler(HttpMessageNotReadableException e){
        ServerState serverState = ServerState.Builder().
                result("INVALID_INPUTS").build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(SignupState.Builder().serverState(serverState).build());
    }

    @GetMapping(value = "/empl/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<Map<String,Boolean>> checkIdDuplication(@PathVariable("id") String emplId){
        Map<String, Boolean> map = new HashMap<>();
        map.put("hasId", emplService.hasEmpl(emplId));

        return EntityModel.of(map);
    }

    @GetMapping(value = "/signup/appstate", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<SignupState> getSignupAppState(){

        SignupInput signupInput = SignupInput.Builder().name("").id("").pwd("").email("").verificationCode("").emplno("").build();

        ServerState serverState = ServerState.Builder().result(null).errorMessage("").build();

        Map<String, String> validationMsg = new HashMap<>();

        validationMsg.put("nameEmpty", "이름을 입력해주세요");
        validationMsg.put("nameInvalid", "잘못된 이름입니다. 이름을 다시 입력해주세요.");
        validationMsg.put("idEmpty", "아이디를 입력해주세요");
        validationMsg.put("idInvalid", "아이디는 5-20글자의 영어,숫자,_,$로만 작성할 수 있습니다.");
        validationMsg.put("duplicatedId", "중복된 아이디입니다. 다른 아이디를 입력해주세요.");
        validationMsg.put("usableId", "사용할 수 있는 아이디입니다.");
        validationMsg.put("pwdInsafe", "다른 비밀번호를 입력해주세요. 안전도가 보통 이상이어야 합니다.");
        validationMsg.put("pwdEmpty", "비밀번호를 입력해주세요.");
        validationMsg.put("emailEmpty", "이메일을 입력해주세요.");
        validationMsg.put("emailInvalid", "올바른 형식의 이메일이 아닙니다. 다시 입력해주세요.");
        validationMsg.put("verificationCodeEmpty", "이메일 인증번호를 입력해주세요.");
        validationMsg.put("emplnoEmpty", "사원번호를 입력해주세요.");
        validationMsg.put("emplnoInvalid", "올바르지 않은 사원번호입니다. 6-12자리의 번호만 입력가능합니다");

        SignupState signupState = SignupState.Builder().
                signupInput(signupInput)
                .serverState(serverState)
                .isNameValid(false)
                .isIdValid(false)
                .isIdUnique(false)
                .isPwdSafe(false)
                .isVerificationCodeSent(false)
                .isVerificationCodeEmpty(true)
                .isEmplnoValid(false)
                .validationMessages(validationMsg)
                .isSignupBtnDisabled(true)
                .isIdDubCheckBtnDisabled(true)
                .isVerificationBtnDisabled(true)
                .focusedField("")
                .termsAccepted(false)
                .build();

        return EntityModel.of(signupState,
                linkTo(methodOn(EmplRestController.class).getSignupAppState()).withSelfRel(),
                linkTo(methodOn(EmplRestController.class).verifyEmplWithEmail(null, null)).withRel("emailVerification"),
                linkTo(methodOn(EmplRestController.class).checkIdDuplication(null)).withRel("idDuplicationCheck"),
                linkTo(methodOn(EmplRestController.class).signup(null, null)).withRel("signup"),
                linkTo(methodOn(EmplController.class).getSignupSuccessPage()).withRel("signupSuccess"),
                linkTo(methodOn(EmplController.class).getServerErrorPage()).withRel("error"));
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignupState> signup(@RequestBody SignupInput inputs, HttpServletRequest req){
        SignupState invalidInputState = validateSignupInputs(inputs);

        if(invalidInputState != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalidInputState);
        }

        ServiceResult result = emplService.processSignup(inputs, req.getRemoteAddr());

        ServerState serverState = ServerState.Builder()
                .result(getResultForServerState(result))
                .errorMessage(result.getMsg() == null ? null : result.getMsg().getContent())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(SignupState.Builder().serverState(serverState).build());
    }

    @PostMapping(value = "/signup/email-verf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerState> verifyEmplWithEmail(@RequestBody String body, HttpServletRequest req){
        JsonNode jsonNode;

        try {
            jsonNode = new ObjectMapper().readTree(body);
        } catch (JsonProcessingException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ServerState.Builder()
                            .result(FAIL)
                            .errorMessage(ExceptionMsg.EMAIL_INVALID_EMAIL.getContent())
                            .build());
        }

        String email = jsonNode.get("email").asText();

        if (email.equals("") || !Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ServerState.Builder()
                            .result(FAIL)
                            .errorMessage(ExceptionMsg.EMAIL_INVALID_EMAIL.getContent())
                            .build());
        }

        String ip = req.getRemoteAddr();
        ServiceResult result = emplService.sendVerificationCode(email, ip);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerState.Builder()
                        .result(getResultForServerState(result))
                        .errorMessage(result.getMsg() != null ? result.getMsg().getContent() : "")
                        .build());
    }

    private SignupState validateSignupInputs(SignupInput inputs){
        SignupState.SignupStateBuilder builder = SignupState.Builder();
        boolean hasInvalidInput = false;

        if(!isNameValid(inputs.getName())){
            builder.isNameValid(false);
            hasInvalidInput = true;
        } else {
            builder.isNameValid(true);
        }

        if(!isIdValid(inputs.getId())){
            builder.isIdValid(false).isIdUnique(false);
            hasInvalidInput = true;
        } else {
            builder.isIdValid(true).isIdUnique(true);
        }

        if (!isPasswordSafe(inputs.getPwd())) {
            builder.isPwdSafe(false);
            hasInvalidInput = true;
        } else {
            builder.isPwdSafe(true);
        }

        if (!isEmailValid(inputs.getEmail())) {
            builder.isEmailValid(false);
            hasInvalidInput = true;
        } else {
            builder.isEmailValid(true);
        }

        if (isVerificationCodeEmpty(inputs.getVerificationCode())) {
            builder.isVerificationCodeEmpty(true);
            hasInvalidInput = true;
        } else {
            builder.isVerificationCodeEmpty(false);
        }

        if (!isEmplnoValid(inputs.getEmplno())) {
            builder.isEmplnoValid(false);
            hasInvalidInput = true;
        } else {
            builder.isEmplnoValid(true);
        }

        if (hasInvalidInput) {
            ServerState serverState = ServerState.Builder()
                    .result("INVALID_INPUTS")
                    .errorMessage("유효하지 않은 입력 값입니다.")
                    .build();
            builder.serverState(serverState);
            return builder.build();
        } else {
            return null;
        }
    }

    private boolean isNameValid(String name) {
        return name != null && !name.isEmpty() && Pattern.matches("^(?:[가-힣]{2,18}|[a-zA-Z]{2,18})$", name);
    }

    private boolean isIdValid(String id) {
        return id != null && !id.isEmpty() && Pattern.matches("^[a-zA-Z0-9_$]{5,20}$", id);
    }

    private boolean isPasswordSafe(String password) {
        if (password == null || password.isEmpty()) return false;

        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        return strength.getScore() >= 2;
    }

    private boolean isEmailValid(String email) {
        return email != null && !email.isEmpty() && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email);
    }

    private boolean isVerificationCodeEmpty(String code) {
        return code == null || code.isEmpty();
    }

    private boolean isEmplnoValid(String emplno) {
        return emplno != null && !emplno.isEmpty() && Pattern.matches("^[\\d]{6,12}$", emplno);
    }

    private String getResultForServerState(ServiceResult serviceResult) {
        String resultValue ;

        if (!serviceResult.isSuccessful() && serviceResult.getMsg().equals(ExceptionMsg.SIGNUP_REDIS_CONNECTION_FAIL)) {
            resultValue = SIGNUP_UNABLE;
        } else if (!serviceResult.isSuccessful()) {
            resultValue = FAIL;
        } else {
            resultValue = SUCCESS;
        }

        return resultValue;
    }
}
