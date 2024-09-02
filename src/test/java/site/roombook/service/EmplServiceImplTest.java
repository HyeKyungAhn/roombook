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
import site.roombook.dao.AuthChgHistDao;
import site.roombook.dao.EmplDao;
import site.roombook.domain.*;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class EmplServiceImplTest {

    @Autowired
    @InjectMocks
    private EmplService mockEmplService;

    @Mock
    private AuthRequestRecordService mockRecordService;

    @Mock
    private EmailService mockEmailService;

    @Mock
    private EmplDao mockEmplDao;

    @Mock
    private AuthChgHistDao mockAuthChgHistDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerfDto emailVerfDto;

    private static final Logger logger = LoggerFactory.getLogger(EmplServiceImpl.class);

    @Nested
    @DisplayName("회원가입 인증코드 전송 테스트")
    class SendSignupAuthCodeTest {
        private final String email = "fear.wise.01@gmail.com";
        private final String ip = "0:0:0:0:0:0:0:1";

        @BeforeEach
        void setup(){
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @Transactional
        @DisplayName("이미 가입된 이메일 사용 시 실패")
        void testEmailAlreadyExist(){
            when(mockEmplDao.selectEmplByEmail(anyString())).thenReturn(1);

            ServiceResult result = mockEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_EMAIL_ALREADY_EXIST, result.getMsg());
        }

        @Test
        @DisplayName("차단된 사용자의 인증 요청 시 실패")
        void testBlockedRequestFail(){
            when(mockEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(true);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(1);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);

            ServiceResult result = mockEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK, result.getMsg());
        }

        @Test
        @DisplayName("인증 코드 최대 발급 횟수 초과시 실패")
        void testRequestWithMaxAttemptsCnt(){
            when(mockEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(3);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);


            ServiceResult result = mockEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_EXCEED_MAX_AUTH_REQUEST_COUNT, result.getMsg());

        }

        @Test
        @DisplayName("차단 여부 데이터가 없을 때 실패")
        void testRequestWithIsBlockedNull(){
            when(mockEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(null);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(1);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);

            ServiceResult result = mockEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE, result.getMsg());
        }

        @Test
        @DisplayName("인증 요청 수 데이터가 없을 때 실패")
        void testRequestWIthAuthRequestCntNull(){
            when(mockEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(null);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);

            ServiceResult result = mockEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE, result.getMsg());
        }

        @Test
        @DisplayName("최대 인증 요청 수 초과 시 실패")
        void testRequestWithMaxAuthRequestCntNull(){
            when(mockEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(1);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(null);

            ServiceResult result = mockEmplService.sendVerificationCode(email, ip);

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE, result.getMsg());
        }

        @Test
        @DisplayName("인증 코드 전송 성공")
        void testSendVerificationCodeSuccess(){
            when(mockEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getAuthRequestCnt()).thenReturn(1);
            when(emailVerfDto.getMaxAuthRequestCnt()).thenReturn(3);
            when(mockEmailService.sendSignupAuthMail(anyString(), anyString(), anyString())).thenReturn(new ServiceResult(true));
            doNothing().when(mockRecordService).storeTempValue(anyString(), any(EmailVerfDto.class));

            ServiceResult result = mockEmplService.sendVerificationCode(email, ip);

            assertTrue(result.isSuccessful());
        }

        @Test
        @DisplayName("인증 코드 전송 최대 요청 수 초과 시 요청자 차단")
        void blockEmailExcecedMaxRequestCnt(){
            when(mockEmplDao.selectEmplByEmail(anyString())).thenReturn(0);
            when(mockRecordService.getTempValue(anyString())).thenReturn(null).thenReturn(emailVerfDto);
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
            when(mockEmailService.sendSignupAuthMail(anyString(), anyString(), anyString())).thenReturn(new ServiceResult(true));
            doNothing().when(mockRecordService).storeTempValue(anyString(), any(EmailVerfDto.class));

            ServiceResult result1 = mockEmplService.sendVerificationCode(email, ip);
            ServiceResult result2 = mockEmplService.sendVerificationCode(email, ip);
            ServiceResult result3 = mockEmplService.sendVerificationCode(email, ip);
            ServiceResult result4 = mockEmplService.sendVerificationCode(email, ip);

            assertTrue(result1.isSuccessful());
            assertTrue(result2.isSuccessful());
            assertTrue(result3.isSuccessful());
            assertFalse(result4.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_EXCEED_MAX_AUTH_REQUEST_COUNT, result4.getMsg());
        }
    }

    @Nested
    @DisplayName("비밀번호 암호화")
    class PasswordEncryptTest {
        @Test
        @DisplayName("비밀번호 암호화 테스트")
        void encryptPasswordTest(){
            String rawPassword = "zxcvvb";

            assertNotNull(passwordEncoder.encode(rawPassword));
        }

        @Test
        @DisplayName("암호화된 비밀번호 일치 테스트")
        void encryptedPasswordEqualTest(){
            String rawPassword = "zxcvvb";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        }

        @Test
        @DisplayName("암호화된 비밀번호 불일치 테스트")
        void encryptedPasswordNotEqualTest(){
            String rawPassword = "zxcvvb";
            String encodedPassword = passwordEncoder.encode("불일치비번");

            assertFalse(passwordEncoder.matches(rawPassword, encodedPassword));
        }
    }

    @Nested
    @DisplayName("회원가입 요청 시")
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
            when(mockRecordService.getTempValue(anyString())).thenReturn(null);

            ServiceResult result = mockEmplService.processSignup(input, "123.123.123");

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE, result.getMsg());
        }

        @Test
        @DisplayName("블락된 사용자 회원가입 실패 테스트")
        void testBlockedAuthRequest() {
            when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(true);

            ServiceResult result = mockEmplService.processSignup(input, "123.123.123");

            assertFalse(result.isSuccessful());
            assertEquals(ExceptionMsg.SIGNUP_BLOCK, result.getMsg());
        }

        @Test
        @DisplayName("인증번호 만료로 회원가입 실패")
        void testExpiredverificationCode(){
            when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
            when(emailVerfDto.getIsBlocked()).thenReturn(false);
            when(emailVerfDto.getExpiDtm()).thenReturn(LocalDateTime.now().minusMinutes(1L));

            ServiceResult result = mockEmplService.processSignup(input, "123.123.123");

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

                when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
                when(emailVerfDto.getIsBlocked()).thenReturn(false);
                when(emailVerfDto.getMaxAuthAttemptsCnt()).thenReturn(3);
                when(emailVerfDto.getVerificationCode()).thenReturn("wrongno");
                when(emailVerfDto.getExpiDtm()).thenReturn(LocalDateTime.now().plusMinutes(1L));
            }

            @ParameterizedTest
            @ValueSource(ints = {0,1,2})
            @DisplayName("잘못된 인증 번호 사용시 실패")
            void inputWrongCode(int attemptsCnt){
                when(emailVerfDto.getAuthAttemptsCnt()).thenReturn(attemptsCnt);

                ServiceResult result = mockEmplService.processSignup(input, "123.123.123");

                assertFalse(result.isSuccessful());
                assertEquals(ExceptionMsg.SIGNUP_WRONG_AUTH_CODE, result.getMsg());
            }

            @Test
            @DisplayName("인증번호 불일치 4회 시도 시 실패")
            void typeWrongCodeFourTimes(){
                when(emailVerfDto.getAuthAttemptsCnt()).thenReturn(3);

                ServiceResult result = mockEmplService.processSignup(input, "123.123.123");

                assertFalse(result.isSuccessful());
                assertEquals(ExceptionMsg.SIGNUP_EXCEED_MAX_AUTH_ATTEMPTS_COUNT, result.getMsg());
            }
        }

        @Nested
        @DisplayName("회원가입 데이터 저장 테스트")
        class SaveSignupDataTest {

            @BeforeEach
            void setup(){
                MockitoAnnotations.openMocks(this);

                String verificationCode = "111111";

                input = SignupInput.Builder().name("아무개").id("asdf1234").pwd("tldnjsgkstnqkrwntm0").email("fear.wise.01@gmail.com").verificationCode(verificationCode).emplno("123123").build();

                when(mockRecordService.getTempValue(anyString())).thenReturn(emailVerfDto);
                when(emailVerfDto.getIsBlocked()).thenReturn(false);
                when(emailVerfDto.getVerificationCode()).thenReturn(verificationCode);
                when(emailVerfDto.getExpiDtm()).thenReturn(LocalDateTime.now().plusMinutes(1L));
            }

            @Test
            @DisplayName("실패")
            void saveSignupDataFailTest() {
                when(mockEmplDao.insertEmpl(any(EmplDto.class))).thenReturn(0);

                ServiceResult result = mockEmplService.processSignup(input, "123.123.123");

                assertFalse(result.isSuccessful());
                assertEquals(ExceptionMsg.SIGNUP_DATA_SAVE_FAIL, result.getMsg());
            }

            @Test
            @DisplayName("성공")
            void saveSignupDataSuccessTest() {
                when(mockEmplDao.insertEmpl(any(EmplDto.class))).thenReturn(1);

                ServiceResult result = mockEmplService.processSignup(input, "123.123.123");

                assertTrue(result.isSuccessful());
            }
        }
    }
    
    @Nested
    @DisplayName("권한 변경 테스트")
    class EmplAuthTest {
        private final String emplId = "testId";
        private final String emplAuthNm = "ROLE_USER";
        private final String authAprvEmplId = "testAuthAprvEmplId";

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @DisplayName("실패")
        void updateFail(){
            ServiceResult result = mockEmplService.updateEmplAuth(emplId, emplAuthNm, authAprvEmplId);

            assertFalse(result.isSuccessful());
        }

        @Test
        @DisplayName("성공")
        void success(){
            when(mockEmplDao.updateAuthName(any(EmplDto.class))).thenReturn(1);
            when(mockAuthChgHistDao.insert(any())).thenReturn(1);

            ServiceResult result = mockEmplService.updateEmplAuth(emplId, emplAuthNm, authAprvEmplId);

            assertTrue(result.isSuccessful());
        }
    }
}