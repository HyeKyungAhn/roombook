package site.roombook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.ExceptionMsg;
import site.roombook.dao.EmplDao;
import site.roombook.domain.EmailVerfDto;
import site.roombook.domain.EmplDto;
import site.roombook.domain.ServiceResult;
import site.roombook.domain.SignupInput;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class EmplServiceImplTest {

    @InjectMocks
    @Autowired
    private EmplService mockedEmplService;

    @Mock
    private AuthRequestRecordService mockedRecordService;

    @Mock
    private EmailService mockedEmailService;

    @Mock
    private EmplDao mockedEmplDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerfDto emailVerfDto;

    private static final Logger logger = LoggerFactory.getLogger(EmplServiceImpl.class);

    @Nested
    class SendSignupAuthCodeTest {
        private final String email = "fear.wise.01@gmail.com";
        private final String ip = "0:0:0:0:0:0:0:1";

        @BeforeEach
        void setup(){
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @Transactional
        void testEmailAlreadyExist(){
            when(mockedEmplDao.selectEmplByEmail(anyString())).thenReturn(1);

            ServiceResult result = mockedEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_EMAIL_ALREADY_EXIST, result.getMsg());
        }

        @Test
        void testBlockedRequestFail(){
            when(mockedEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(true);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(1);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);

            ServiceResult result = mockedEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK, result.getMsg());
        }

        @Test
        void testRequestWithMaxAttemptsCnt(){
            when(mockedEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(3);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);


            ServiceResult result = mockedEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_EXCEED_MAX_AUTH_REQUEST_COUNT, result.getMsg());

        }

        @Test
        void testRequestWithIsBlockedNull(){
            when(mockedEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(null);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(1);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);

            ServiceResult result = mockedEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE, result.getMsg());
        }

        @Test
        void testRequestWIthAuthRequestCntNull(){
            when(mockedEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(null);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);

            ServiceResult result = mockedEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE, result.getMsg());
        }

        @Test
        void testRequestWithMaxAuthRequestCntNull(){
            when(mockedEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(1);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(null);

            ServiceResult result = mockedEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE, result.getMsg());
        }

        @Test
        void testSendVerificationCodeSuccess(){
            when(mockedEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(1);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);
            when(mockedEmailService.sendSignupAuthMail(anyString(), anyString(), anyString())).thenReturn(new ServiceResult(true));
            doNothing().when(mockedRecordService).storeTempValue(anyString(), any(EmailVerfDto.class));

            ServiceResult result = mockedEmplService.sendVerificationCode(email, ip);

            assertTrue(result.isSuccessful());
        }

        @Test
        void multipleReturnValueTest(){
            when(emailVerfDto.getIsBlocked()).thenReturn(false).thenReturn(true);

            assertFalse(emailVerfDto.getIsBlocked());
            assertTrue(emailVerfDto.getIsBlocked());
            assertTrue(emailVerfDto.getIsBlocked());
        }

        @Test
        void blockEmailExcecedMaxRequestCnt(){
            when(mockedEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockedRecordService.getTempValue(anyString())).thenReturn(null).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false); //?
            when(emailVerfDto.getAuthRequestCnt()).thenAnswer(new Answer<Integer>() {
                int count = 0;
                @Override
                public Integer answer(InvocationOnMock invocationOnMock) {
                    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                    for (StackTraceElement element : stackTraceElements) {
                        if (element.getMethodName().equals("sendVerificationCode")) {
                            logger.info("main에서 호출됨");
                            return ++count;
                        } else if (element.getMethodName().equals("updateAuthCodeRequestCnt")
                                || element.getMethodName().equals("copyEmailVerfDtoBuilder")) {
                            logger.info("곁다리에서 호출됨");
                            break;
                        }
                    }

                    return count;
                }
            });
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);
            when(mockedEmailService.sendSignupAuthMail(anyString(), anyString(), anyString())).thenReturn(new ServiceResult(true));
            doNothing().when(mockedRecordService).storeTempValue(anyString(), any(EmailVerfDto.class));

            ServiceResult result1 = mockedEmplService.sendVerificationCode(email, ip);
            ServiceResult result2 = mockedEmplService.sendVerificationCode(email, ip);
            ServiceResult result3 = mockedEmplService.sendVerificationCode(email, ip);
            ServiceResult result4 = mockedEmplService.sendVerificationCode(email, ip);

            assertTrue(result1.isSuccessful());
            assertTrue(result2.isSuccessful());
            assertTrue(result3.isSuccessful());
            assertFalse(result4.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_EXCEED_MAX_AUTH_REQUEST_COUNT, result4.getMsg());
        }

        @Test
        void encryptPasswordTest(){
            String rawPassword = "zxcvvb";

            assertNotNull(passwordEncoder.encode(rawPassword));
        }

        @Test
        void encyrptedPasswordEqualTest(){
            String rawPassword = "zxcvvb";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        }

        @Test
        void encyrptedPasswordNotEqualTest(){
            String rawPassword = "zxcvvb";
            String encodedPassword = passwordEncoder.encode("불일치비번");

            assertFalse(passwordEncoder.matches(rawPassword, encodedPassword));
        }
    }

    @Nested
    class ProcessSignupTest {

        private SignupInput input;

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
            input = SignupInput.Builder().name("아무개").id("asdf1234").pwd("tldnjsgkstnqkrwntm0").email("fear.wise.01@gmail.com").verificationCode("111111").emplno("123123").build();
        }

        @Test
        @DisplayName("인증 요청 이력 없는 회원가입 실패 테스트")
        void testSignupWithNoAuthRequestRecord() {
            when(mockedRecordService.getTempValue(anyString())).thenReturn(null);

            ServiceResult result = mockedEmplService.processSignup(input, "123.123.123");

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE, result.getMsg());
        }

        @Test
        @DisplayName("블락된 사용자 회원가입 실패 테스트")
        void testBlockedAuthRequest() {
            when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(true);

            ServiceResult result = mockedEmplService.processSignup(input, "123.123.123");

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK, result.getMsg());
        }

        @Test
        @DisplayName("인증번호 만료로 회원가입 실패")
        void testExpiredverificationCode(){
            when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getExpiDtm()).thenReturn(LocalDateTime.now().minusMinutes(1L));

            ServiceResult result = mockedEmplService.processSignup(input, "123.123.123");

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_AUTH_CODE_EXPIRE, result.getMsg());
        }

        @Nested
        @DisplayName("인증번호 불일치 횟수 테스트")
        class WrongAuthCodeTest {

            @BeforeEach
            void setup(){
                MockitoAnnotations.openMocks(this);
                input = SignupInput.Builder().name("아무개").id("asdf1234").pwd("tldnjsgkstnqkrwntm0").email("fear.wise.01@gmail.com").verificationCode("111111").emplno("123123").build();

                when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
                when(emailVerfDto.getIsBlocked()).thenReturn(false);
                when(emailVerfDto.getMaxAuthAttemptsCnt()).thenReturn(3);
                when(emailVerfDto.getVerificationCode()).thenReturn("wrongno");
                when(emailVerfDto.getExpiDtm()).thenReturn(LocalDateTime.now().plusMinutes(1L));
            }

            @ParameterizedTest
            @ValueSource(ints = {0,1,2})
            void inputWrongCode(int attemptsCnt){
                when(emailVerfDto.getAuthAttemptsCnt()).thenReturn(attemptsCnt);

                ServiceResult result = mockedEmplService.processSignup(input, "123.123.123");

                assertFalse(result.isSuccessful());
                assertEquals(ExceptionMsg.SIGNUP_WRONG_AUTH_CODE, result.getMsg());
            }

            @Test
            @DisplayName("인증번호 불일치 4회 시도")
            void typeWrongCodeFourTimes(){
                when(emailVerfDto.getAuthAttemptsCnt()).thenReturn(3);

                ServiceResult result = mockedEmplService.processSignup(input, "123.123.123");

                assertFalse(result.isSuccessful());
                assertEquals(ExceptionMsg.SIGNUP_EXCEED_MAX_AUTH_ATTEMPTS_COUNT, result.getMsg());
            }
        }

        @Nested
        class SaveSignupDataTest {

            @BeforeEach
            void setup(){
                MockitoAnnotations.openMocks(this);

                String verificationCode = "111111";

                input = SignupInput.Builder().name("아무개").id("asdf1234").pwd("tldnjsgkstnqkrwntm0").email("fear.wise.01@gmail.com").verificationCode(verificationCode).emplno("123123").build();

                when(mockedRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
                when(emailVerfDto.getIsBlocked()).thenReturn(false);
                when(emailVerfDto.getVerificationCode()).thenReturn(verificationCode);
                when(emailVerfDto.getExpiDtm()).thenReturn(LocalDateTime.now().plusMinutes(1L));
            }

            @Test
            void saveSignupDataFailTest() {
                when(mockedEmplDao.insertEmpl(any(EmplDto.class))).thenReturn(0);

                ServiceResult result = mockedEmplService.processSignup(input, "123.123.123");

                assertFalse(result.isSuccessful());
                assertEquals(ExceptionMsg.SIGNUP_DATA_SAVE_FAIL, result.getMsg());
            }

            @Test
            void saveSignupDataSuccessTest() {
                when(mockedEmplDao.insertEmpl(any(EmplDto.class))).thenReturn(1);

                ServiceResult result = mockedEmplService.processSignup(input, "123.123.123");

                assertTrue(result.isSuccessful());
            }
        }
    }

    private String generateAuthKey(String email) {
        return "signup_auth_email:" + email;
    }
}