package site.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.AuthChgHistDto;
import site.roombook.domain.EmplDto;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
@DisplayName("권한 변경 이력 DAO 테스트")
class AuthChgHistDaoImplTest {

    @Autowired
    AuthChgHistDao authChgHistDao;

    @Autowired
    EmplDao empldao;

    private static final String USER_AUTH = "ROLE_USER";
    private static final String SUPER_ADMIN_AUTH = "ROLE_SUPER_ADMIN";
    private static final String EMPL_ADMIN_AUTH = "ROLE_EMPL_ADMIN";

    @Nested
    @DisplayName("권한 변경 이력 추가 테스트")
    class InsertTest {
        private AuthChgHistDto dummyAuthChgHist;
        private EmplDto dummyEmpl;
        private EmplDto dummySuperAdmin;
        private EmplDto dummyEmplAdmin;

        @BeforeEach
        void setup() {
            dummyEmpl = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("testId1").pwd("aaaa").email("aaaa@asdf.com")
                    .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm(USER_AUTH).brdt("2000-01-01")
                    .wncomTelno("01123123").empno(111111).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
            dummySuperAdmin = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("testId2").pwd("bbbb").email("bbbb@asdf.com")
                    .pwdErrTms(0).rnm("bbb").engNm("bbb").entDt("2024-01-01").emplAuthNm(SUPER_ADMIN_AUTH).brdt("2000-01-01")
                    .wncomTelno("01123123").empno(222222).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
            dummyEmplAdmin = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("testId3").pwd("cccc").email("ccc@asdf.com")
                    .pwdErrTms(0).rnm("ccc").engNm("ccc").entDt("2024-01-01").emplAuthNm(EMPL_ADMIN_AUTH).brdt("2000-01-01")
                    .wncomTelno("3333333").empno(333333).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();

            assertEquals(1, empldao.insertEmpl(dummyEmpl));
            assertEquals(1, empldao.insertEmpl(dummySuperAdmin));
            assertEquals(1, empldao.insertEmpl(dummyEmplAdmin));
        }

        @Test
        @DisplayName("성공")
        void success() {
            dummyAuthChgHist = AuthChgHistDto.authChgHistDto()
                    .authChgHistId(UUID.randomUUID().toString())
                    .emplId(dummyEmpl.getEmplId())
                    .authNm(EMPL_ADMIN_AUTH)
                    .authYn('Y')
                    .regDtm(LocalDateTime.now())
                    .regrIdnfId(dummySuperAdmin.getEmplId())
                    .build();

            assertEquals(1, authChgHistDao.insert(dummyAuthChgHist));
        }

        @Test
        @DisplayName("권한 없는 사용자가 권한 업데이트시 실패")
        void unauthorizedUpdateFail() {
            dummyAuthChgHist = AuthChgHistDto.authChgHistDto()
                    .authChgHistId(UUID.randomUUID().toString())
                    .emplId(dummyEmpl.getEmplId())
                    .authNm(EMPL_ADMIN_AUTH)
                    .authYn('Y')
                    .regDtm(LocalDateTime.now())
                    .regrIdnfId(dummyEmplAdmin.getEmplId())
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> authChgHistDao.insert(dummyAuthChgHist));
        }

        @Test
        @DisplayName("존재하지 않는 사용자가 권한 업데이트시 실패")
        void UpdateByNonExistingAdminFail() {
            dummyAuthChgHist = AuthChgHistDto.authChgHistDto()
                    .authChgHistId(UUID.randomUUID().toString())
                    .emplId(dummyEmpl.getEmplId())
                    .authNm(EMPL_ADMIN_AUTH)
                    .authYn('Y')
                    .regDtm(LocalDateTime.now())
                    .regrIdnfId("nonExistingId")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> authChgHistDao.insert(dummyAuthChgHist));
        }

        @Test
        @DisplayName("존재하지 않는 사용자의 권한 업데이트시 실패")
        void nonExistingEmplUpdateFail() {
            dummyAuthChgHist = AuthChgHistDto.authChgHistDto()
                    .authChgHistId(UUID.randomUUID().toString())
                    .emplId("nonExistingId")
                    .authNm(EMPL_ADMIN_AUTH)
                    .authYn('Y')
                    .regDtm(LocalDateTime.now())
                    .regrIdnfId(dummySuperAdmin.getEmplId())
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> authChgHistDao.insert(dummyAuthChgHist));
        }
    }



    @Nested
    @DisplayName("궈한 변경 이력 조회 테스트")
    class SelectTest {
        private AuthChgHistDto dummyAuthChgHist;
        private EmplDto dummyEmpl;

        @BeforeEach
        void setup() {
            dummyEmpl = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("testId1").pwd("aaaa").email("aaaa@asdf.com")
                    .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm(USER_AUTH).brdt("2000-01-01")
                    .wncomTelno("01123123").empno(1111).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
            EmplDto dummySuperAdmin = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("testId2").pwd("bbbb").email("bbbb@asdf.com")
                    .pwdErrTms(0).rnm("bbb").engNm("bbb").entDt("2024-01-01").emplAuthNm(SUPER_ADMIN_AUTH).brdt("2000-01-01")
                    .wncomTelno("01123123").empno(2222).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();

            dummyAuthChgHist = AuthChgHistDto.authChgHistDto()
                    .authChgHistId(UUID.randomUUID().toString())
                    .emplId(dummyEmpl.getEmplId())
                    .authNm(EMPL_ADMIN_AUTH)
                    .authYn('Y')
                    .regDtm(LocalDateTime.now())
                    .regrIdnfId(dummySuperAdmin.getEmplId())
                    .build();

            assertEquals(1, empldao.insertEmpl(dummyEmpl));
            assertEquals(1, empldao.insertEmpl(dummySuperAdmin));
            assertEquals(1, authChgHistDao.insert(dummyAuthChgHist));
        }

        @Test
        @DisplayName("한 건 조회")
        void selectOneTest() {
            AuthChgHistDto selectedAuthChgHistDto = authChgHistDao.selectLatestOne(dummyEmpl.getEmplNo());

            assertNotNull(selectedAuthChgHistDto);
            assertEquals(dummyAuthChgHist.getAuthChgHistId(), selectedAuthChgHistDto.getAuthChgHistId());
        }
    }
}