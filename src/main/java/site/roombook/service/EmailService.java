package site.roombook.service;

import site.roombook.domain.ServiceResult;

public interface EmailService {
    ServiceResult sendSignupAuthMail(String email, String name, String authCode);
}
