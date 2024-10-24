package store.roombook.service;

import store.roombook.domain.EmplDto;
import store.roombook.domain.ServiceResult;
import store.roombook.domain.SignupInput;

import java.util.List;

public interface EmplService {
    boolean hasEmpl(String emplId);

    ServiceResult sendVerificationCode(String ipAddress, String email);

    ServiceResult processSignup(SignupInput inputs, String ip);

    List<EmplDto> getEmplListForEmplAuthChange(String option, String optionValue, int limit, int offset);

    int getSearchedEmplsCount(String option, String optionValue);

    ServiceResult updateEmplAuth(String emplId, String emplAuthNm, String authAprvEmplId);
}
