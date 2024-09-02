package site.roombook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.dao.AuthChgHistDao;
import site.roombook.dao.EmplDao;
import site.roombook.domain.EmplDto;
import site.roombook.domain.ServiceResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class EmplServiceImplIntegrationTest {
    @Autowired
    private EmplDao emplDao;

    @Autowired
    @InjectMocks
    private EmplService emplService;

    @Autowired
    private AuthChgHistDao authChgHistDao;

    private static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    private static final String ROLE_EMPL_ADMIN = "ROLE_EMPL_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    @Nested
    @DisplayName("사원 권한 업데이트 테스트")
    class UpdatingEmplAuthTest {
        private EmplDto dummyEmpl;
        private EmplDto dummySuperAdmin;
        private EmplDto dummyEmplAdmin;

        @BeforeEach
        void setup() {
            dummyEmpl = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("testId1").pwd("aaaa").email("aaaa@asdf.com")
                    .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm(ROLE_USER).brdt("2000-01-01")
                    .wncomTelno("01123123").empno(111111).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
            dummySuperAdmin = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("testId2").pwd("bbbb").email("bbbb@asdf.com")
                    .pwdErrTms(0).rnm("bbb").engNm("bbb").entDt("2024-01-01").emplAuthNm(ROLE_SUPER_ADMIN).brdt("2000-01-01")
                    .wncomTelno("01123123").empno(222222).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
            dummyEmplAdmin = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("testId3").pwd("cccc").email("ccc@asdf.com")
                    .pwdErrTms(0).rnm("ccc").engNm("ccc").entDt("2024-01-01").emplAuthNm(ROLE_EMPL_ADMIN).brdt("2000-01-01")
                    .wncomTelno("3333333").empno(333333).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();

            assertEquals(1, emplDao.insertEmpl(dummyEmpl));
            assertEquals(1, emplDao.insertEmpl(dummySuperAdmin));
            assertEquals(1, emplDao.insertEmpl(dummyEmplAdmin));
        }

        @Test
        @DisplayName("성공")
        void success() {
            ServiceResult result = emplService.updateEmplAuth(dummyEmpl.getEmplId(), ROLE_EMPL_ADMIN, dummySuperAdmin.getEmplId());

            assertTrue(result.isSuccessful());

            assertEquals(ROLE_EMPL_ADMIN, emplDao.selectEmplById(dummyEmpl.getEmplId()).orElseThrow().getEmplAuthNm());
            assertNotNull(authChgHistDao.selectLatestOne(dummyEmpl.getEmplNo()));
        }
    }
}