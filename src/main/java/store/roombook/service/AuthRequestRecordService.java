package store.roombook.service;

import store.roombook.domain.EmailVerfDto;

public interface AuthRequestRecordService {
    void storeTempValue(String key, EmailVerfDto emailVerfDto);

    EmailVerfDto getTempValue(String key);

    boolean deleteKey(String key);
}
