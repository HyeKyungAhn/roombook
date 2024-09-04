package site.roombook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.dao.BlngDeptDao;
import site.roombook.dao.DeptDao;
import site.roombook.dao.EmplDao;
import site.roombook.domain.BlngDeptDto;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;
import site.roombook.domain.ServiceResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class DeptServiceImplIntegrationTest {

    @Autowired
    DeptService deptService;

    @Autowired
    BlngDeptDao blngDeptDao;

    @Autowired
    DeptDao deptDao;

    @Autowired
    EmplDao emplDao;

    @Nested
    class DeptInsertionTest {
        private List<EmplDto> sixEmpls;
        private DeptDto existingDept;
        private EmplDto emplAdmin;

        @BeforeEach
        void setup(){
            emplDao.deleteAll();
            blngDeptDao.deleteAllBlngDept();
            deptDao.deleteAll();

            //관리자 한 명 추가
            emplAdmin = getEmplAdmin();
            assertEquals(1, emplDao.insertEmpl(emplAdmin));

            //사원 6명 추가
            sixEmpls = createSixEmplList();

            for (EmplDto sixEmpl : sixEmpls) {
                assertEquals(1, emplDao.insertEmpl(sixEmpl));
            }

            //부서 1개 추가

            existingDept = DeptDto.DeptDtoBuilder()
                    .deptCd("123456")
                    .uppDeptCd("#")
                    .emplId(emplAdmin.getEmplId())
                    .deptNm("test부서")
                    .engDeptNm("testDept")
                    .deptSortOdr(10)
                    .registerId(emplAdmin.getEmplId()).build();

            assertEquals(1, deptDao.insertDept(existingDept));
        }

        @Test
        @DisplayName("부서 저장 성공")
        void saveOneDeptSuccess() {
            DeptDto newDept = DeptDto.DeptDtoBuilder()
                    .deptCd("0")
                    .uppDeptCd("#")
                    .emplId(sixEmpls.get(0).getEmplId())
                    .deptNm("새부서")
                    .engDeptNm("newDept")
                    .deptSortOdr(10)
                    .registerId(emplAdmin.getEmplId()).build();

            List<DeptDto> deptList = List.of(newDept, existingDept);
            assertTrue(deptService.saveOneDept(deptList, emplAdmin.getEmplId()));
        }

        @Test
        @DisplayName("중복된 이름을 가진 부서 저장")
        void saveDeptWithDuplicatedName() {
            String emplAdminId = emplAdmin.getEmplId();

            DeptDto newDept = DeptDto.DeptDtoBuilder()
                    .deptCd("0")
                    .uppDeptCd("#")
                    .emplId(sixEmpls.get(0).getEmplId())
                    .deptNm(existingDept.getDeptNm())
                    .engDeptNm(existingDept.getEngDeptNm())
                    .deptSortOdr(10)
                    .registerId(emplAdmin.getEmplId()).build();

            List<DeptDto> deptList = List.of(newDept, existingDept);
            assertThrows(DataIntegrityViolationException.class, () -> deptService.saveOneDept(deptList, emplAdminId));
        }
    }

    @Nested
    @Transactional
    @DisplayName("부서 구성원 수정 테스트")
    @WithMockUser(username = "testEmplAdminId", roles = "EMPL_ADMIN")
    class DeptMemberModificationTest {
        private List<EmplDto> sixEmpls;
        private DeptDto deptDto;

        @BeforeEach
        void setup(){
            emplDao.deleteAll();
            blngDeptDao.deleteAllBlngDept();
            deptDao.deleteAll();

            sixEmpls = createSixEmplList();
            EmplDto emplAdmin = sixEmpls.get(0);

            for (EmplDto sixEmpl : sixEmpls) {
                assertEquals(1, emplDao.insertEmpl(sixEmpl));
            }

            deptDto = DeptDto.DeptDtoBuilder()
                    .deptCd("123456")
                    .uppDeptCd("#")
                    .emplId(emplAdmin.getEmplId())
                    .deptNm("test부서")
                    .engDeptNm("testDept")
                    .deptSortOdr(10)
                    .registerId(emplAdmin.getEmplId()).build();

            assertEquals(1, deptDao.insertDept(deptDto));

            for (int i = 0; i < 3; i++) {

                BlngDeptDto blngDeptDto = BlngDeptDto.BlngDeptDtoBuilder()
                        .blngDeptCd(deptDto.getDeptCd())
                        .blngEmplId(sixEmpls.get(i).getEmplId())
                        .repDeptYn('N')
                        .registerId(emplAdmin.getEmplId()).build();

                assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto));
            }
        }

        @Test
        @DisplayName("부서 구성원 2명 추가, 1명 삭제")
        void success() {
            String deptCd = deptDto.getDeptCd();
            String existingEmplAdminId = sixEmpls.get(0).getEmplId();
            String existingEmpl = sixEmpls.get(1).getEmplId();
            String newEmpl1 = sixEmpls.get(3).getEmplId();
            String newEmpl2 = sixEmpls.get(4).getEmplId();
            List<String> newMemIds = List.of(existingEmplAdminId, existingEmpl, newEmpl1, newEmpl2);

            ServiceResult result = deptService.modifyDeptMem(deptCd, newMemIds, existingEmplAdminId);

            assertTrue(result.isSuccessful());
        }

        @Test
        @DisplayName("추가 및 삭제할 사원이 없을 때")
        void noMemberToDelete() {
            String deptCd = deptDto.getDeptCd();
            String existingEmplAdminId = sixEmpls.get(0).getEmplId();
            String existingEmplId1 = sixEmpls.get(1).getEmplId();
            String existingEmplId2 = sixEmpls.get(2).getEmplId();
            List<String> newMemIds = List.of(existingEmplAdminId, existingEmplId1, existingEmplId2);

            ServiceResult result = deptService.modifyDeptMem(deptCd, newMemIds, existingEmplAdminId);

            assertTrue(result.isSuccessful());
        }

        @Test
        @DisplayName("없는 사원 추가 실패")
        void invalidMemberToAdd() {
            String deptCd = deptDto.getDeptCd();
            String existingEmplAdminId = sixEmpls.get(0).getEmplId();
            String existingEmplId1 = sixEmpls.get(1).getEmplId();
            String existingEmplId2 = sixEmpls.get(2).getEmplId();
            String invalidId = "invalidTestId";
            List<String> newMemIds = List.of(existingEmplAdminId, existingEmplId1, existingEmplId2, invalidId);

            assertThrows(DataIntegrityViolationException.class, () ->
                deptService.modifyDeptMem(deptCd, newMemIds, existingEmplAdminId)
            );
        }
    }

    List<EmplDto> createSixEmplList(){
        return List.of(
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("science").pwd("science").email("science@asdf.com")
                        .pwdErrTms(0).rnm("science").engNm("science").entDt("2024-01-01").emplAuthNm("ROLE_EMPL_ADMIN").brdt("2000-01-01")
                        .wncomTelno("6543321").empno(584393).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("mathematical").pwd("mathematical").email("mathematical@asdf.com")
                        .pwdErrTms(0).rnm("mathematical").engNm("mathematical").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(385737).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("geometry").pwd("geometry").email("geometry@asdf.com")
                        .pwdErrTms(0).rnm("geometry").engNm("geometry").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(102947).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("architecture").pwd("architecture").email("architecture@asdf.com")
                        .pwdErrTms(0).rnm("architecture").engNm("architecture").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(382947).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("musical").pwd("musical").email("musical@asdf.com")
                        .pwdErrTms(0).rnm("musical").engNm("musical").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(8228384).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("electronic").pwd("electronic").email("electronic@asdf.com")
                        .pwdErrTms(0).rnm("electronic").engNm("electronic").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(747586).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build()
        );
    }

    private EmplDto getEmplAdmin() {
        return EmplDto.EmplDtoBuilder().emplNo("44444444").emplId("adminId").pwd("1234").email("testid1@gmail.com")
                .pwdErrTms(0).rnm("감자영").engNm("gamja").entDt("2024-01-01").emplAuthNm("ROLE_EMPL_ADMIN").brdt("2000-01-01")
                .wncomTelno("111111").empno(1111).msgrId(null).prfPhotoPath(null)
                .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
    }
}