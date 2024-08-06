package site.roombook.service;

import site.roombook.domain.EmailVerfDto;

public interface AuthRequestRecordService {
    void storeTempValue(String key, EmailVerfDto emailVerfDto);
    EmailVerfDto getTempValue(String key);
}
