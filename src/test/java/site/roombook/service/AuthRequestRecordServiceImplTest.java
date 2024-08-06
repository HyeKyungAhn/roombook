package site.roombook.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class AuthRequestRecordServiceImplTest {

    @Autowired
    private RedisTemplate<String, String> testRedisTemplate;

    @Test
    void addTest(){
        String key = "jamambo";
        String value = "elephant";

        testRedisTemplate.opsForValue().set(key, value);

        assertEquals(value, testRedisTemplate.opsForValue().get(key));
    }

    @Test
    void timestampAddTest(){
        String timestampString = String.valueOf(Instant.now().getEpochSecond());

        HashOperations<String, String, String> operations = testRedisTemplate.opsForHash();
        operations.put("myhash", "event_time", timestampString);

        String storedTimestamp = operations.get("myhash", "event_time");

        assertEquals(timestampString, storedTimestamp);
    }

    @Nested
    class TxTest{
        private final String key = "newData";
        private final String value = "helloworld";

        @Test
        void insertDataInTx(){
            testRedisTemplate.opsForValue().set(key, value);

            String val = testRedisTemplate.opsForValue().get(key);
            assertEquals(value, val);
            List<Object> txResults = testRedisTemplate.execute(new SessionCallback<>() {
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi(); // redis transaction 시작
                    testRedisTemplate.opsForValue().set(key, value);
                    testRedisTemplate.opsForValue().get(key);

                    return testRedisTemplate.exec(); // redis transaction 종료
                }
            });


            assertNotNull(txResults.get(1));
            assertEquals(val, txResults.get(1));
        }

        @Test
        void getDataAfterTx(){
            assertNull(testRedisTemplate.opsForValue().get(key));
        }

    }
}