package store.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import store.roombook.domain.EmplDto;
import store.roombook.domain.JwtDto;
import store.roombook.service.JwtService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:web/WEB-INF/spring/**/testContext.xml")
class JwtDaoImplTest {

    @Autowired
    private JwtDao jwtDao;

    @Autowired
    private EmplDao emplDao;

    @Autowired
    private JwtService jwtService;

    private EmplDto dummyEmpl;
    private String refreshToken;

    @BeforeEach
    void setup(){
        emplDao.deleteAll();
        jwtDao.deleteAll();

        dummyEmpl = EmplDto.EmplDtoBuilder().emplNo("0000001").emplId("aaaa").pwd("aaaa").email("aaaa@asdf.com")
                .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                .wncomTelno("01123123").empno(1111).msgrId(null).prfPhotoPath(null)
                .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();

        refreshToken = jwtService.createRefreshToken();

        JwtDto jwtDto = JwtDto.JwtDtoBuilder()
                .tknId(UUID.randomUUID().toString())
                .tkn(refreshToken)
                .creDtm(LocalDateTime.now())
                .expiDtm(LocalDateTime.now().plusMinutes(1L))
                .creEmplId(dummyEmpl.getEmplId())
                .build();

        assertEquals(1, emplDao.insertEmpl(dummyEmpl));
        assertEquals(1, jwtDao.insertToken(jwtDto));
    }


    @Nested
    @DisplayName("토큰 조회 테스트")
    class SelectTokenTest {
        @Test
        @DisplayName("토큰으로 조회 성공")
        void selectByToken() {
            assertNotNull(jwtDao.selectByToken(refreshToken));
        }

        @Test
        @DisplayName("토큰으로 만료되지 않은 토큰 조회")
        void selectUnexpiredTokenByToken() {
            assertNotNull(jwtDao.selectUnexpiredTokenByToken(refreshToken));
        }

        @Test
        @DisplayName("토큰으로 아이디와 만료되지 않은 토큰 조회")
        void selectIdAndUnexpiredTokenByToken() {
            JwtDto jwtDto = jwtDao.selectUnexpiredTokenByToken(refreshToken);
            assertEquals(dummyEmpl.getEmplId(), jwtDto.getCreEmplId());
        }

        @Test
        @DisplayName("토큰으로 만료되지 않은 토큰과 권한 조회")
        void selectUnexpiredTokenAndAuthority() {
            JwtDto jwtDto = jwtDao.selectUnexpiredTokenAndAuthority(refreshToken);
            assertEquals(dummyEmpl.getEmplAuthNm(), jwtDto.getEmplAuthNm());
        }
    }

    @Nested
    @DisplayName("토큰 만료 테스트")
    class ExpireTokenTest {
        @Test
        @DisplayName("토큰으로 토큰 만료하기")
        void expireByToken() {
            assertEquals(1, jwtDao.expireTokenByToken(refreshToken));
            assertNull(jwtDao.selectUnexpiredTokenByToken(refreshToken));
        }

        @Test
        @DisplayName("아이디로 토큰 만료하기")
        void expireByEmplId() {
            assertEquals(1, jwtDao.expireTokenByEmplId(dummyEmpl.getEmplId()));
            assertNull(jwtDao.selectUnexpiredTokenByToken(refreshToken));
        }

        @Test
        @DisplayName("존재하지 않는 아이디로 만료하기")
        void expireByNonexistentEmplId() {
            assertEquals(0,  jwtDao.expireTokenByEmplId("nonExistentId"));
        }

        @Test
        @DisplayName("토큰 중 만료 기간이 가장 최신인 토큰을 아이디로 만료하기")
        void expireLatestTokenById() throws InterruptedException {
            Thread.sleep(1000);
            String refreshToken1 = jwtService.createRefreshToken();
            Thread.sleep(1000);
            String refreshToken2 = jwtService.createRefreshToken();


            JwtDto jwtDto1 = JwtDto.JwtDtoBuilder()
                    .tknId(UUID.randomUUID().toString())
                    .tkn(refreshToken1)
                    .creDtm(LocalDateTime.now())
                    .expiDtm(LocalDateTime.now().plusMinutes(1L))
                    .creEmplId(dummyEmpl.getEmplId())
                    .build();

            Thread.sleep(1000);
            JwtDto jwtDto2 = JwtDto.JwtDtoBuilder()
                    .tknId(UUID.randomUUID().toString())
                    .tkn(refreshToken2)
                    .creDtm(LocalDateTime.now())
                    .expiDtm(LocalDateTime.now().plusMinutes(1L))
                    .creEmplId(dummyEmpl.getEmplId())
                    .build();

            assertEquals(1, jwtDao.insertToken(jwtDto1));
            assertEquals(1, jwtDao.insertToken(jwtDto2));

            assertEquals(1, jwtDao.expireTokenByEmplId(dummyEmpl.getEmplId()));

            assertNotNull(jwtDao.selectUnexpiredTokenByToken(refreshToken));
            assertNotNull(jwtDao.selectUnexpiredTokenByToken(refreshToken1));
            assertNull(jwtDao.selectUnexpiredTokenByToken(refreshToken2));
        }

        @Test
        @DisplayName("토큰 중 만료 기간이 가장 최신인 토큰을 refresh 토큰으로 만료하기")
        void expireLatestTokenByToken() throws InterruptedException {
            Thread.sleep(1000);
            String refreshToken1 = jwtService.createRefreshToken();
            Thread.sleep(1000);
            String refreshToken2 = jwtService.createRefreshToken();


            JwtDto jwtDto1 = JwtDto.JwtDtoBuilder()
                    .tknId(UUID.randomUUID().toString())
                    .tkn(refreshToken1)
                    .creDtm(LocalDateTime.now())
                    .expiDtm(LocalDateTime.now().plusMinutes(1L))
                    .creEmplId(dummyEmpl.getEmplId())
                    .build();


            JwtDto jwtDto2 = JwtDto.JwtDtoBuilder()
                    .tknId(UUID.randomUUID().toString())
                    .tkn(refreshToken2)
                    .creDtm(LocalDateTime.now())
                    .expiDtm(LocalDateTime.now().plusMinutes(1L))
                    .creEmplId(dummyEmpl.getEmplId())
                    .build();

            assertEquals(1, jwtDao.insertToken(jwtDto1));
            assertEquals(1, jwtDao.insertToken(jwtDto2));

            assertEquals(1, jwtDao.expireTokenByToken(refreshToken2));

            assertNotNull(jwtDao.selectUnexpiredTokenByToken(refreshToken));
            assertNotNull(jwtDao.selectUnexpiredTokenByToken(refreshToken1));
            assertNull(jwtDao.selectUnexpiredTokenByToken(refreshToken2));
        }
    }

    @Nested
    @DisplayName("토큰 추가 테스트")
    class InsertTest {

        private EmplDto dummyEmpl;

        @BeforeEach
        void setup() {
            emplDao.deleteAll();
            jwtDao.deleteAll();

            dummyEmpl = EmplDto.EmplDtoBuilder().emplNo("0000001").emplId("aaaa").pwd("aaaa").email("aaaa@asdf.com")
                    .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                    .wncomTelno("01123123").empno(1111).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
            assertEquals(1, emplDao.insertEmpl(dummyEmpl));
        }

        @Test
        @DisplayName("성공")
        void insertTokenSuccess() {
            String refreshToken = jwtService.createRefreshToken();

            JwtDto jwtDto = JwtDto.JwtDtoBuilder()
                    .tknId(UUID.randomUUID().toString())
                    .tkn(refreshToken)
                    .creDtm(LocalDateTime.now())
                    .expiDtm(LocalDateTime.now().plusMinutes(1L))
                    .creEmplId(dummyEmpl.getEmplId())
                    .build();

            int affectedRowCnt = jwtDao.insertToken(jwtDto);
            JwtDto selectedJwtDto= jwtDao.selectByToken(refreshToken);

            assertEquals(1, affectedRowCnt);
            assertEquals(dummyEmpl.getEmplNo(), selectedJwtDto.getCreEmplNo());
        }

        @Test
        @DisplayName("존재하지 않는 사용자의 토큰 추가 시 실패")
        void insertTokenOfNonExistentEmpl() {
            String refreshToken = jwtService.createRefreshToken();

            JwtDto jwtDto = JwtDto.JwtDtoBuilder()
                    .tknId(UUID.randomUUID().toString())
                    .tkn(refreshToken)
                    .creDtm(LocalDateTime.now())
                    .expiDtm(LocalDateTime.now().plusMinutes(1L))
                    .creEmplId("nonExistentEmplId")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> jwtDao.insertToken(jwtDto));
        }
    }

}