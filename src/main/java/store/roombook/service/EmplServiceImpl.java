package store.roombook.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import store.roombook.ExceptionMsg;
import store.roombook.dao.AuthChgHistDao;
import store.roombook.dao.EmplDao;
import store.roombook.domain.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmplServiceImpl implements EmplService {

    @Autowired
    private EmplDao emplDao;

    @Autowired
    private AuthChgHistDao authChgHistDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthRequestRecordService recordService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(EmplServiceImpl.class);

    private static final int MAX_AUTH_CODE_REQUEST_COUNT = 3;
    private static final int MAX_ATTEMPTS_COUNT = 3;
    private static final long AUTH_TIMEOUT = 3L;

    private final Random random = new Random();

    @Override
    public boolean hasEmpl(String emplId) {
        return emplDao.selectEmplById(emplId).isPresent();
    }

    @Override
    public ServiceResult sendVerificationCode(String email, String ip) {
        ServiceResult result = new ServiceResult();
        String key = generateKey(email);

        if (isEmailExisting(email)) {
            return new ServiceResult(false, ExceptionMsg.SIGNUP_EMAIL_ALREADY_EXIST);
        }

        try {
            EmailVerfDto authRequestRecord = recordService.getTempValue(key);

            if (authRequestRecord != null) {

                Boolean isBlocked = authRequestRecord.getIsBlocked();
                Integer authRequestCnt = authRequestRecord.getAuthRequestCnt();
                Integer maxAuthRequestCnt = authRequestRecord.getMaxAuthRequestCnt();

                if (isBlocked == null || authRequestCnt == null || maxAuthRequestCnt == null) {
                    blockSignupTemporarily(key, ip, authRequestRecord);
                    return new ServiceResult(false, ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE);
                } else if (isBlocked.equals(true)) {
                    return new ServiceResult(false, ExceptionMsg.SIGNUP_BLOCK);
                } else if(authRequestCnt >= maxAuthRequestCnt) { //null 일 때, 3번 일 때 꼭 테스트해보기
                    blockSignupTemporarily(key, ip, authRequestRecord);
                    return new ServiceResult(false, ExceptionMsg.SIGNUP_EXCEED_MAX_AUTH_REQUEST_COUNT);
                }
            }

            String verificationCode = generateVerificationCode();

            ServiceResult emailServiceResult = emailService.sendSignupAuthMail(email, "새 가입자", verificationCode);

            if (!emailServiceResult.isSuccessful()) {
                return emailServiceResult;
            }

            if (authRequestRecord != null) {
                updateAuthCodeRequestCnt(authRequestRecord, ip);
            } else {
                saveAuthCodeRequest(email, ip, verificationCode);
            }
        } catch (RedisConnectionFailureException e) {
            return new ServiceResult(false, ExceptionMsg.SIGNUP_REDIS_CONNECTION_FAIL);
        }

        result.setSuccessful(true);

        return result;
    }

    @Override
    public ServiceResult processSignup(SignupInput inputs, String ip) {

        if (isEmailExisting(inputs.getEmail())) {
            return new ServiceResult(false, ExceptionMsg.SIGNUP_EMAIL_ALREADY_EXIST);
        }

        String key = generateKey(inputs.getEmail());

        try {
            EmailVerfDto authRequestRecord = recordService.getTempValue(key);

            if (authRequestRecord == null) {
                blockSignupTemporarily(key, ip, inputs);
                return new ServiceResult(false, ExceptionMsg.SIGNUP_BLOCK_INVALID_VALUE);
            }

            if(authRequestRecord.getIsBlocked().equals(true)){
                return new ServiceResult(false, ExceptionMsg.SIGNUP_BLOCK);
            }

            if(LocalDateTime.now().isAfter(authRequestRecord.getExpiDtm())){
                return new ServiceResult(false, ExceptionMsg.SIGNUP_AUTH_CODE_EXPIRE);
            }

            if(!inputs.getVerificationCode().equals(authRequestRecord.getVerificationCode())){
                if (authRequestRecord.getAuthAttemptsCnt() >= authRequestRecord.getMaxAuthAttemptsCnt()) {
                    blockSignupTemporarily(key, ip, authRequestRecord);
                    return new ServiceResult(false, ExceptionMsg.SIGNUP_EXCEED_MAX_AUTH_ATTEMPTS_COUNT);
                }

                updateAuthAttemptsCnt(authRequestRecord, ip);

                return new ServiceResult(false, ExceptionMsg.SIGNUP_WRONG_AUTH_CODE);
            }

        } catch (RedisConnectionFailureException e) {
            return new ServiceResult(false, ExceptionMsg.SIGNUP_REDIS_CONNECTION_FAIL);
        }

        boolean doSaveSuccess = saveEmpl(inputs);

        if (doSaveSuccess) {
            recordService.deleteKey(key);
        } else {
            logger.warn("회원 가입 정보 DB 저장 실패");
            return new ServiceResult(false, ExceptionMsg.SIGNUP_DATA_SAVE_FAIL);
        }
        return new ServiceResult(true);
    }

    @Override
    public List<EmplDto> getEmplListForEmplAuthChange(String option, String optionValue, int limit, int offset) {

        Map<String, Object> map = new HashMap<>();
        map.put("option", option);
        map.put("optionValue", optionValue);
        map.put("limit", limit);
        map.put("offset", offset);

        return emplDao.selectLimitedEmplList(map);
    }

    @Override
    public int getSearchedEmplsCount(String option, String optionValue) {
        Map<String, String> searchOptionMap = new HashMap<>();
        searchOptionMap.put("option", option);
        searchOptionMap.put("optionValue", optionValue);

        return emplDao.selectSearchedEmplsCnt(searchOptionMap);
    }

    @Override
    public ServiceResult updateEmplAuth(String emplId, String emplAuthNm, String authAprvEmplId) {
        ServiceResult result = new ServiceResult();

        EmplDto emplDto = EmplDto.EmplDtoBuilder()
                .emplId(emplId)
                .emplAuthNm(emplAuthNm)
                .lastUpdDtm(LocalDateTime.now())
                .lastUpdrIdnfNo(authAprvEmplId)
                .build();

        boolean isUpdateSuccesful = emplDao.updateAuthName(emplDto)==1;
        result.setSuccessful(isUpdateSuccesful);

        if (isUpdateSuccesful) {
            AuthChgHistDto authChgHistDto = AuthChgHistDto.authChgHistDto()
                    .authChgHistId(UUID.randomUUID().toString())
                    .emplId(emplId)
                    .authNm(emplAuthNm)
                    .authYn('Y')
                    .regrIdnfId(authAprvEmplId)
                    .regDtm(LocalDateTime.now())
                    .build();

            authChgHistDao.insert(authChgHistDto);
        } else {
            result.setMsg(ExceptionMsg.EMPL_AUTH_UPDATE_FAIL);
        }

        return result;
    }

    private boolean isEmailExisting(String email) {
        return emplDao.selectEmplByEmail(email) != 0;
    }

    private boolean saveEmpl(SignupInput inputs) {
        String encodedPwd = passwordEncoder.encode(inputs.getPwd());

        EmplDto empl = EmplDto.EmplDtoBuilder()
                .emplNo(String.valueOf(UUID.randomUUID()))
                .emplId(inputs.getId())
                .pwd(encodedPwd)
                .email(inputs.getEmail())
                .pwdErrTms(0)
                .rnm(inputs.getName())
                .engNm(null)
                .entDt(null)
                .emplAuthNm("ROLE_USER")
                .brdt(null)
                .wncomTelno(null)
                .empno(Integer.parseInt(inputs.getEmplno()))
                .msgrId(null)
                .prfPhotoPath(null)
                .subsCertiYn('Y')
                .termsAgreYn('Y')
                .subsAprvYn('Y')
                .secsnYn('N')
                .build();

        return emplDao.insertEmpl(empl) == 1;
    }

    private void saveAuthCodeRequest(String email, String ip, String verificationCode) {
        EmailVerfDto emailVerfDto = EmailVerfDto.EmailVerfDtoBuilder().
                emailVerfId(UUID.randomUUID().toString()).
                email(email).
                ip(ip).
                verificationCode(verificationCode).
                creDtm(LocalDateTime.now()).
                expiDtm(LocalDateTime.now().plusMinutes(EmplServiceImpl.AUTH_TIMEOUT)).
                authRequestCnt(1).
                maxAuthRequestCnt(EmplServiceImpl.MAX_AUTH_CODE_REQUEST_COUNT).
                authAttemptsCnt(0).
                maxAuthAttemptsCnt(EmplServiceImpl.MAX_ATTEMPTS_COUNT).
                isVerfScss(false).
                isBlocked(false).build();

        recordService.storeTempValue(generateKey(email), emailVerfDto);
    }

    private void updateAuthCodeRequestCnt(EmailVerfDto authRequestRecord, String ip){
        Integer authRequestCnt = authRequestRecord.getAuthRequestCnt();

        EmailVerfDto emailVerfDto = copyEmailVerfDtoBuilder(authRequestRecord)
                .authRequestCnt(++authRequestCnt)
                .ip(ip)
                .build();

        recordService.storeTempValue(generateKey(emailVerfDto.getEmail()), emailVerfDto);
    }

    private void updateAuthAttemptsCnt(EmailVerfDto authRequestRecord, String ip) {
        Integer authAttemptsCount = authRequestRecord.getAuthAttemptsCnt();

        EmailVerfDto emailVerfDto = copyEmailVerfDtoBuilder(authRequestRecord)
                .authRequestCnt(++authAttemptsCount)
                .creDtm(LocalDateTime.now())
                .expiDtm(LocalDateTime.now().plusMinutes(EmplServiceImpl.AUTH_TIMEOUT))
                .ip(ip)
                .build();

        recordService.storeTempValue(generateKey(emailVerfDto.getEmail()), emailVerfDto);
    }

    private void blockSignupTemporarily(String key, String ip, EmailVerfDto authRequestRecord) {
        EmailVerfDto blockedSignupData = copyEmailVerfDtoBuilder(authRequestRecord).ip(ip).isBlocked(true).build();
        recordService.storeTempValue(key, blockedSignupData);
        logger.warn("signup blocked ip : {} , email: {}", ip, authRequestRecord.getEmail());
    }

    private void blockSignupTemporarily(String key, String ipAddress, SignupInput inputs) {
        EmailVerfDto blockedSignupData = EmailVerfDto.EmailVerfDtoBuilder().
                emailVerfId(String.valueOf(UUID.randomUUID())).
                email(inputs.getEmail()).
                ip(ipAddress).
                creDtm(LocalDateTime.now()).
                expiDtm(LocalDateTime.now().plusMinutes(EmplServiceImpl.AUTH_TIMEOUT)).
                authRequestCnt(0).
                maxAuthRequestCnt(MAX_AUTH_CODE_REQUEST_COUNT).
                authAttemptsCnt(1).
                maxAuthAttemptsCnt(MAX_ATTEMPTS_COUNT).
                isVerfScss(false).
                isBlocked(true).build();

        recordService.storeTempValue(key, blockedSignupData);
        logger.warn("signup blocked ip : {} , email: {}", ipAddress, inputs.getEmail());
    }

    private EmailVerfDto.EmailVerfDtoBuilder copyEmailVerfDtoBuilder(EmailVerfDto authRequestRecord) {
        return EmailVerfDto.EmailVerfDtoBuilder().
                emailVerfId(authRequestRecord.getEmailVerfId()).
                email(authRequestRecord.getEmail()).
                ip(authRequestRecord.getIp()).
                verificationCode(authRequestRecord.getVerificationCode()).
                creDtm(authRequestRecord.getCreDtm()).
                expiDtm(authRequestRecord.getExpiDtm()).
                authRequestCnt(authRequestRecord.getAuthRequestCnt()).
                maxAuthRequestCnt(authRequestRecord.getMaxAuthRequestCnt()).
                authAttemptsCnt(authRequestRecord.getAuthAttemptsCnt()).
                maxAuthAttemptsCnt(authRequestRecord.getMaxAuthAttemptsCnt()).
                isVerfScss(authRequestRecord.getIsVerfScss()).
                isBlocked(authRequestRecord.getIsBlocked());
    }

    private String generateKey(String email) {
        return "signup_auth_email:" + email;
    }

    private String generateVerificationCode() {
        return String.valueOf(random.nextInt(900000) + 100000);
    }
}
