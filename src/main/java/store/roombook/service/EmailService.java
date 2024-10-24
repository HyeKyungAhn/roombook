package store.roombook.service;

import store.roombook.domain.ServiceResult;

public interface EmailService {
    ServiceResult sendSignupAuthMail(String email, String name, String authCode);
}
