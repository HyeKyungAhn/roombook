package site.roombook.domain;

import lombok.*;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "Builder")
public class SignupState {

    private SignupInput signupInput;
    private ServerState serverState;

    //Validation
    private boolean isNameValid;
    private boolean isIdValid;
    private boolean isIdUnique;
    private boolean isPwdSafe;
    private boolean isEmailValid;
    private boolean isVerificationCodeSent;
    private boolean isVerificationCodeEmpty;
    private boolean isEmplnoValid;
    private Map<String, String> validationMessages;

    //UiState
    private boolean isSignupBtnDisabled;
    private boolean isIdDubCheckBtnDisabled;
    private boolean isVerificationBtnDisabled;
    private String focusedField;

    //Other
    private boolean termsAccepted;
}
