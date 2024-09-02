package site.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import site.roombook.domain.EmailVerfDto;

import java.util.concurrent.TimeUnit;

@Service
public class AuthRequestRecordServiceImpl implements AuthRequestRecordService{

    @Autowired
    private RedisTemplate<String, EmailVerfDto> redisTemplate;

    @Override
    public void storeTempValue(String key, EmailVerfDto emailVerfDto) throws RedisConnectionFailureException {
        redisTemplate.opsForValue().set(key, emailVerfDto, 10L, TimeUnit.MINUTES);
    }

    @Override
    public EmailVerfDto getTempValue(String key) throws RedisConnectionFailureException {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean deleteKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
