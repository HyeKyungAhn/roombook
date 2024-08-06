package site.roombook.service;

import site.roombook.domain.ServiceResult;
import site.roombook.domain.SignupInput;

public interface EmplService {
    boolean hasEmpl(String emplId);

    ServiceResult sendVerificationCode(String ipAddress, String email);

    ServiceResult processSignup(SignupInput inputs, String ip);
}
