package site.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.BlngDeptDto;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class BlngDeptImplTest {

    @Autowired
    BlngDeptDao blngDeptDao;

    @Autowired
    EmplDao emplDao;

    @Autowired
    DeptDao deptDao;

    private List<BlngDeptDto> threeBlngDepts;

    @BeforeEach
    void setUp(){
        blngDeptDao.deleteAllBlngDept();
        deptDao.deleteAll();
        emplDao.deleteAll();

        List<EmplDto> sixEmpls = createSixEmplList();
        threeBlngDepts = new ArrayList<>();
        List<DeptDto> fiveDeptList = getFiveDeptList();

        EmplDto emplAdmin = getEmplAdmin();
        emplDao.insertEmpl(emplAdmin);

        //사원 6명 추가
        for (EmplDto emplDto : sixEmpls) {
            emplDao.insertEmpl(emplDto);
        }

        //5개 부서 추가
        for (DeptDto deptDto : fiveDeptList) {
            deptDao.insertDept(deptDto);
        }

        //3개 소속부서 추가
        for (int i = 0; i < 3; i++) {
            BlngDeptDto blngDeptDto = BlngDeptDto.BlngDeptDtoBuilder()
                    .blngDeptCd(fiveDeptList.get(i).getDeptCd())
                    .blngEmplId(sixEmpls.get(i).getEmplId())
                    .repDeptYn('N')
                    .registerId(emplAdmin.getEmplId()).build();

            threeBlngDepts.add(blngDeptDto);
        }
    }

    @Test
    void insertDeptMember() {
        assertEquals(1, blngDeptDao.insertBlngDept(threeBlngDepts.get(0)));
        assertEquals(1, blngDeptDao.insertBlngDept(threeBlngDepts.get(1)));
        assertEquals(1, blngDeptDao.insertBlngDept(threeBlngDepts.get(2)));
    }

    @Test
    void selectAllBlngDept(){
        assertEquals(0, blngDeptDao.selectAllBlngDept().size());

        assertEquals(1, blngDeptDao.insertBlngDept(threeBlngDepts.get(0)));
        assertEquals(1, blngDeptDao.insertBlngDept(threeBlngDepts.get(1)));
        assertEquals(1, blngDeptDao.insertBlngDept(threeBlngDepts.get(2)));

        assertEquals(3, blngDeptDao.selectAllBlngDept().size());
    }

    @Test
    void deleteAllBlngDeptTest(){

        assertEquals(0, blngDeptDao.selectAllBlngDept().size());
    }

    @Test
    @DisplayName("같은 소속 부서 삭제 테스트")
    void deleteBlngDeptsInSameDeptTest(){
        threeBlngDepts.set(0, modifyDeptCode(threeBlngDepts.get(0), threeBlngDepts.get(1).getBlngDeptCd())); //0,1,2 사원 중 2번 사원만 다른 부서 소속

        for (BlngDeptDto threeBlngDept : threeBlngDepts) {
            blngDeptDao.insertBlngDept(threeBlngDept);
        }

        Map<String, Object> deleteDeptData = new HashMap<>();
        String[] emplIDs = {threeBlngDepts.get(0).getBlngEmplId(), threeBlngDepts.get(1).getBlngEmplId()}; //같은 부서에 속한 부서 구성원 아이디 2개
        deleteDeptData.put("blngDeptCd", threeBlngDepts.get(0).getBlngDeptCd());
        deleteDeptData.put("emplIDs", emplIDs);

        int rowCnt = blngDeptDao.deleteBlngDepts(deleteDeptData);

        assertEquals(2, rowCnt);
    }

    @Test
    @DisplayName("부서코드가 다른 소속 부서 삭제 테스트")
    void deleteBlngDeptsInDifferentDeptTest(){
        for (BlngDeptDto threeBlngDept : threeBlngDepts) {
            blngDeptDao.insertBlngDept(threeBlngDept);
        }

        Map<String, Object> deleteDeptData = new HashMap<>();
        String[] emplIDs = {threeBlngDepts.get(0).getBlngEmplId(), threeBlngDepts.get(1).getBlngEmplId()}; //각각 다른 부서에 속한 부서 구성원 아이디 2개

        deleteDeptData.put("blngDeptCd", threeBlngDepts.get(0).getBlngDeptCd());
        deleteDeptData.put("emplIDs", emplIDs);

        int rowCnt = blngDeptDao.deleteBlngDepts(deleteDeptData);

        assertEquals(1, rowCnt); //부서코드가 일치하는 사원만 삭제됨
    }

    @Test
    @DisplayName("존재하지 않는 emplId로 부서 구성원 추가 실패 테스트")
    void insertOneBlndDeptWithNonExistingEmplIdTest(){
        BlngDeptDto existingBlngDept = threeBlngDepts.get(0);

        BlngDeptDto blngDeptWithInvalidEmplId = BlngDeptDto.BlngDeptDtoBuilder()
                .blngDeptCd(existingBlngDept.getBlngDeptCd())
                .blngEmplId("INVALIDID")
                .repDeptYn(existingBlngDept.getRepDeptYn())
                .registerId(existingBlngDept.getBlngEmplId()).build();


        assertThrows(DataIntegrityViolationException.class, () -> blngDeptDao.insertOneBlngDept(blngDeptWithInvalidEmplId));
    }

    private EmplDto getEmplAdmin() {
        return EmplDto.EmplDtoBuilder().emplNo("44444444").emplId("adminId").pwd("1234").email("testid1@gmail.com")
                .pwdErrTms(0).rnm("감자영").engNm("gamja").entDt("2024-01-01").emplAuthNm("ROLE_EMPL_ADMIN").brdt("2000-01-01")
                .wncomTelno("111111").empno(1111).msgrId(null).prfPhotoPath(null)
                .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
    }
    List<EmplDto> createSixEmplList(){
        return List.of(
                EmplDto.EmplDtoBuilder().emplNo("0000001").emplId("aaaa").pwd("aaaa").email("aaaa@asdf.com")
                        .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm("ROLE_EMPL_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(1111).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000002").emplId("bbbb").pwd("bbbb").email("bbbb@asdf.com")
                        .pwdErrTms(0).rnm("bbb").engNm("bbb").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(2222).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000003").emplId("cccc").pwd("cccc").email("cccc@asdf.com")
                        .pwdErrTms(0).rnm("ccc").engNm("ccc").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(3333).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000004").emplId("dddd").pwd("dddd").email("dddd@asdf.com")
                        .pwdErrTms(0).rnm("ddd").engNm("ddd").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(4444).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000005").emplId("eeee").pwd("eeee").email("eeee@asdf.com")
                        .pwdErrTms(0).rnm("eee").engNm("eee").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(5555).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000006").emplId("ffff").pwd("ffff").email("ffff@asdf.com")
                        .pwdErrTms(0).rnm("fff").engNm("fff").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(6666).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build()
        );
    }

    /*
     *   #
     *   L 영업부(index: 0)
     *       L 영업기획팀(index: 2)
     *           L 고객만족팀(index: 4)
     *       L 영업지원팀(index: 3)
     *   L 기획부(index: 1)
     * */
    private List<DeptDto> getFiveDeptList() {
        return List.of(
                DeptDto.DeptDtoBuilder().deptCd("11111111").uppDeptCd("#").deptMngrEmplNo(null).deptNm("영업부").engDeptNm("Sales Dept").deptSortOdr(0).registerId(getEmplAdmin().getEmplId()).build(),
                DeptDto.DeptDtoBuilder().deptCd("11112222").uppDeptCd("#").deptMngrEmplNo(null).deptNm("기획부").engDeptNm("Planning Dept").deptSortOdr(1).registerId(getEmplAdmin().getEmplId()).build(),
                DeptDto.DeptDtoBuilder().deptCd("22222222").uppDeptCd("11111111").deptMngrEmplNo(null).deptNm("영업기획팀").engDeptNm("Sales Planning Team").deptSortOdr(0).registerId(getEmplAdmin().getEmplId()).build(),
                DeptDto.DeptDtoBuilder().deptCd("22222233").uppDeptCd("11111111").deptMngrEmplNo(null).deptNm("영업지원팀").engDeptNm("Sales Support Team").deptSortOdr(1).registerId(getEmplAdmin().getEmplId()).build(),
                DeptDto.DeptDtoBuilder().deptCd("33333333").uppDeptCd("22222222").deptMngrEmplNo(null).deptNm("고객만족팀").engDeptNm("Customer Satisfaction Team").deptSortOdr(0).registerId(getEmplAdmin().getEmplId()).build());
    }

    private BlngDeptDto modifyDeptCode(BlngDeptDto blngDeptDto, String blngDeptCd) {
        return BlngDeptDto.BlngDeptDtoBuilder()
                .blngDeptCd(blngDeptCd)
                .blngEmplId(blngDeptDto.getBlngEmplId())
                .repDeptYn(blngDeptDto.getRepDeptYn())
                .registerId(blngDeptDto.getRegisterId()).build();
    }
}