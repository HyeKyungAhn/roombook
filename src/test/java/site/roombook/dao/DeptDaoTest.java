package site.roombook.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.BlngDeptDto;
import site.roombook.domain.DeptAndEmplDto;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class DeptDaoTest {
    @Autowired
    DeptDao deptDao;

    @Autowired
    BlngDeptDao blngDeptDao;

    @Autowired
    EmplDao emplDao;

    @Test
    @Transactional
    void insertDeptTest(){
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        assertEquals(1, emplDao.insertEmpl(emplDto));

        DeptDto deptDto = new DeptDto("1234", "#", null, "인사부2", "HR", 0, "asdf", "asdf", emplDto.getEMPL_ID());
        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);

        DeptDto selectedDept = deptDao.selectDept(deptDto.getDEPT_CD());
        assertEquals(emplDto.getEMPL_NO(), selectedDept.getDEPT_MNGR_EMPL_NO());
    }

    @Test
    @Transactional
    @DisplayName("중복된 부서 코드 insert 테스트")
    void insertDuplicatedDeptTest(){
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        assertEquals(1, emplDao.insertEmpl(emplDto));

        DeptDto deptDto = new DeptDto("1234", "#", null, "인사팀", "HR", 0, "asdf", "asdf", emplDto.getEMPL_ID());
        assertEquals(1, deptDao.insertDept(deptDto));

        DeptDto duplicatedDeptDto = new DeptDto("1234", "#", null, "AA", "AA", 0, "asdf", "asdf", emplDto.getEMPL_ID());
        int result = 0;
        try {
            result = deptDao.insertDept(duplicatedDeptDto);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
        }
        assertEquals(0, result);
    }

    @Test
    @Transactional
    @DisplayName("중복된 부서 이름 insert 테스트")
    void insertDuplicatedDeptNmTest(){
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        assertEquals(1, emplDao.insertEmpl(emplDto));

        DeptDto deptDto = new DeptDto("1234", "#", null, "HR", "eng", 0, "admin", "admin", emplDto.getEMPL_ID());
        deptDao.insertDept(deptDto);

        DeptDto duplicatedDeptDto = new DeptDto("5678", "#", "asdf", "HR", "eng", 0, "admin", "admin", emplDto.getEMPL_ID());
        int result = 0;

        try {
            result = deptDao.insertDept(duplicatedDeptDto);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
        }

        assertEquals(0, result);
    }

    @Test
    @Transactional
    @DisplayName("매니저 이름 없는 부서 insert 테스트")
    void insertDeptTestWithoutMngr(){
        deptDao.deleteAll();

        DeptDto deptDto = new DeptDto("1234", "#", null, "HR", "eng", 0, "admin", "admin", null);

        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);
    }

    @Test
    @Transactional
    void insertDeptFailTest(){
        deptDao.deleteAll();

        DeptDto deptDto = new DeptDto();
        deptDto.setDEPT_CD("11111112");

        int rowCnt = 0;
        try {
            rowCnt = deptDao.insertDept(deptDto);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
        }

        assertEquals(0, rowCnt);
    }

    @Test
    @Transactional
    void selectDeptTest(){
        DeptDto deptDto = new DeptDto("1111", "#", null, "인사부", "HR", 0, "asdf", "asdf");
        int rowCnt = deptDao.insertDept(deptDto);

        assertEquals(1, rowCnt);
        assertEquals(deptDto.getENG_DEPT_NM(), deptDao.selectDept(deptDto.getDEPT_CD()).getENG_DEPT_NM());
    }

    @Test
    @Transactional
    @DisplayName("부서명으로 부서 수 조회 테스트")
    void selectDeptNmTest(){
        DeptDto deptDto = new DeptDto("1111", "#", null, "인사부", "HR", 0, "admin", "admin");
        assertEquals(1, deptDao.insertDept(deptDto));

        assertEquals(1, deptDao.selectDeptCntWithNm(deptDto.getDEPT_NM()));
    }

    @Test
    @Transactional
    @DisplayName("부서 관리자 수정 테스트")
    void updateManagerTest() {
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("1111", "", null, "HR", "eng", 0, "asdf", "asdf");
        assertEquals(1, deptDao.insertDept(deptDto));

        DeptDto deptDto1 = deptDao.selectDept(deptDto.getDEPT_CD());
        assertNotNull(deptDto1);

        Map<String, String> map = new HashMap<>();
        map.put("manager", "1111");
        map.put("empId", "bbbb");
        map.put("deptCd", deptDto.getDEPT_CD());
        assertEquals(1, deptDao.updateManager(map));

        DeptDto deptDto2 = deptDao.selectDept(deptDto.getDEPT_CD());
        assertNotNull(deptDto2);
    }

    @Test
    @Transactional
    @DisplayName("모든 부서 조회 테스트")
    void selectAllDept() {
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111111", "", null, "HR", "eng", 0, "asdf", "asdf");
        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);

        DeptDto deptDto2 = new DeptDto("11111112", "", null, "AA", "eng", 0, "asdf", "asdf");
        rowCnt = deptDao.insertDept(deptDto2);
        assertEquals(1, rowCnt);

        List<DeptDto> list = deptDao.selectAllDept();
        assertEquals(2, list.size());
    }

    @Test
    @Transactional
    @DisplayName("모든 부서 수 조회 테스트")
    void selectAllDeptCntTest(){
        deptDao.deleteAll();

        DeptDto deptDto = new DeptDto("1111", "", null, "HR", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("2222", "", null, "AA", "eng", 0, "asdf", "asdf");
        deptDao.insertDept(deptDto);
        deptDao.insertDept(deptDto2);

        assertEquals(2, deptDao.selectAllDeptCnt());
    }

    @Test
    @Transactional
    @DisplayName("모든 부서 삭제 테스트")
    void deleteAllTest() {
        deptDao.deleteAll();

        DeptDto deptDto = new DeptDto("11111111", "", null, "HR", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11111112", "", null, "AA", "eng", 0, "asdf", "asdf");
        deptDao.insertDept(deptDto);
        deptDao.insertDept(deptDto2);

        assertEquals(2, deptDao.selectAllDeptCnt());

        int deletedRowCnt = deptDao.deleteAll();
        assertEquals(2, deletedRowCnt);

        int finalSelectedRowCnt = deptDao.selectAllDeptCnt();
        assertEquals(0, finalSelectedRowCnt);
    }

    @Test
    @Transactional
    @DisplayName("복수 개 부서 순서 수정 테스트")
    void updateAllDeptTreeDataTest() {
        deptDao.deleteAll();
        DeptDto deptDto1 = new DeptDto("11111111", "#", null, "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", null, "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", null, "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", null, "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", null, "e", "eng", 0, "asdf", "asdf");
        deptDao.insertDept(deptDto1);
        deptDao.insertDept(deptDto2);
        deptDao.insertDept(deptDto3);
        deptDao.insertDept(deptDto4);
        deptDao.insertDept(deptDto5);

        List<DeptDto> list = deptDao.selectAllDept();
        assertEquals(5, list.size());

        deptDto3.setUPP_DEPT_CD("11112222");
        deptDto3.setDEPT_SORT_ODR(0);
        deptDto3.setLAST_UPDR_IDNF_NO("aaaa");
        deptDto4.setUPP_DEPT_CD("11112222");
        deptDto4.setDEPT_SORT_ODR(0);
        deptDto4.setLAST_UPDR_IDNF_NO("aaaa");
        deptDto5.setDEPT_SORT_ODR(1);

        List<DeptDto> modifiedList = new ArrayList<>();
        modifiedList.add(deptDto3);
        modifiedList.add(deptDto4);
        modifiedList.add(deptDto1);
        modifiedList.add(deptDto2);
        modifiedList.add(deptDto5);

        int rowCnt = deptDao.updateAllDeptTreeOdrData(modifiedList);
        assertEquals(3, rowCnt);
    }

    @Test
    @Transactional
    @DisplayName("부서 정보 및 부서 이름 조회 테스트")
    void selectDeptCdAndNm() {
        deptDao.deleteAll();

        DeptDto deptDto1 = new DeptDto("11111111", "#", null, "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", null, "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", null, "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", null, "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", null, "e", "eng", 0, "asdf", "asdf");

        deptDao.insertDept(deptDto1);
        deptDao.insertDept(deptDto2);
        deptDao.insertDept(deptDto3);
        deptDao.insertDept(deptDto4);
        deptDao.insertDept(deptDto5);

        List<DeptDto> list = new ArrayList<>();
        list.add(deptDto1);
        list.add(deptDto2);
        list.add(deptDto3);
        list.add(deptDto4);
        list.add(deptDto5);

        int allDeptCnt = deptDao.selectAllDeptCnt();
        assertEquals(5, allDeptCnt);

        List<DeptDto> selectedList = deptDao.selectDeptCdAndNm();

        for (DeptDto deptDto : selectedList) {
            for (DeptDto oldDeptDto : list) {
                if (deptDto.getDEPT_CD().equals(oldDeptDto.getDEPT_CD())) {
                    assertEquals(deptDto.getDEPT_NM(), oldDeptDto.getDEPT_NM());
                }
            }
        }
    }

    @Test
    @Transactional
    @DisplayName("부서 계층 구조 표시를 위한 부서 데이터 조회 테스트")
    void selectAllDeptForTreeTest() {
        deptDao.deleteAll();

        DeptDto deptDto1 = new DeptDto("11111111", "#", null, "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", null, "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", null, "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", null, "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", null, "e", "eng", 0, "asdf", "asdf");

        deptDao.insertDept(deptDto1);
        deptDao.insertDept(deptDto2);
        deptDao.insertDept(deptDto3);
        deptDao.insertDept(deptDto4);
        deptDao.insertDept(deptDto5);

        List<DeptDto> oldList = new ArrayList<>();
        oldList.add(deptDto1);
        oldList.add(deptDto2);
        oldList.add(deptDto3);
        oldList.add(deptDto4);
        oldList.add(deptDto5);

        List<DeptDto> selectedList = deptDao.selectAllDeptForTree();

        assertEquals(oldList.size(), selectedList.size());

        for (DeptDto inputDept : oldList) {
            for(DeptDto selectedDept : selectedList){
                if(selectedDept.getDEPT_CD().equals(inputDept.getDEPT_CD())){
                    assertNull(selectedDept.getDEPT_MNGR_EMPL_NO());
                    assertNull(selectedDept.getENG_DEPT_NM());
                }
            }
        }
    }

    @Test
    @Transactional
    @DisplayName("구성원이 있는 부서 삭제 실패 테스트")
    void deleteDeptWithEmpl(){
        deptDao.deleteAll();

        String deptCd1 = "11111111";
        String deptCd2 = "22222222";
        String deptCd3 = "33333333";
        String deptCd4 = "44444444";
        String deptCd5 = "55555555";

        DeptDto deptDto1 = new DeptDto(deptCd1, "#", null, "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto(deptCd2, "#", null, "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto(deptCd3, "11111111", null, "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto(deptCd4, "11111111", null, "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto(deptCd5, "22222222", null, "e", "eng", 0, "asdf", "asdf");

        assertEquals(1, deptDao.insertDept(deptDto1));
        assertEquals(1, deptDao.insertDept(deptDto2));
        assertEquals(1, deptDao.insertDept(deptDto3));
        assertEquals(1, deptDao.insertDept(deptDto4));
        assertEquals(1, deptDao.insertDept(deptDto5));

        BlngDeptDto blngDeptDto1 = new BlngDeptDto(deptCd1, "00000001", 'N', "00000002", "00000002");
        BlngDeptDto blngDeptDto2 = new BlngDeptDto(deptCd1, "00000002", 'N', "00000002", "00000002");
        BlngDeptDto blngDeptDto3 = new BlngDeptDto(deptCd1, "00000003", 'N', "00000002", "00000002");

        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto1));
        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto2));
        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto3));

        assertEquals(0, deptDao.deleteDeptWithNoEmpl(deptCd1));
    }

    @Test
    @Transactional
    @DisplayName("구성원이 없는 부서 삭제 성공 테스트")
    void deleteDeptWithNoEmpl(){
        deptDao.deleteAll();
        blngDeptDao.deleteAllBlngDept();

        String deptCd1 = "11111111";
        String deptCd2 = "22222222";
        String deptCd3 = "33333333";
        String deptCd4 = "44444444";
        String deptCd5 = "55555555";

        DeptDto deptDto1 = new DeptDto(deptCd1, "#", null, "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto(deptCd2, "#", null, "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto(deptCd3, "11111111", null, "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto(deptCd4, "11111111", null, "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto(deptCd5, "22222222", null, "e", "eng", 0, "asdf", "asdf");

        assertEquals(1, deptDao.insertDept(deptDto1));
        assertEquals(1, deptDao.insertDept(deptDto2));
        assertEquals(1, deptDao.insertDept(deptDto3));
        assertEquals(1, deptDao.insertDept(deptDto4));
        assertEquals(1, deptDao.insertDept(deptDto5));

        BlngDeptDto blngDeptDto1 = new BlngDeptDto(deptCd1, "00000001", 'N', "00000002", "00000002");
        BlngDeptDto blngDeptDto2 = new BlngDeptDto(deptCd1, "00000002", 'N', "00000002", "00000002");
        BlngDeptDto blngDeptDto3 = new BlngDeptDto(deptCd1, "00000003", 'N', "00000002", "00000002");

        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto1));
        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto2));
        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto3));

        assertEquals(1, deptDao.deleteDeptWithNoEmpl(deptCd2));
    }

    @Test
    @Transactional
    @DisplayName("부서 수정 테스트 성공")
    void updateDeptTest() {
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("22222222", "Mogu", "1234", "mogu@gmail.com", 0, "최모구", "mogu", "2020-01-01", "1980-10-10", "222222", 22222, null, null, 'Y', 'Y', 'Y', 'N');

        assertEquals(1, emplDao.insertEmpl(emplDto1));
        assertEquals(1, emplDao.insertEmpl(emplDto2));

        DeptDto deptDto1 = new DeptDto("1234", "#", null, "a", "engA", 0, "asdf", "asdf"); //수정할 dept
        DeptDto deptDto2 = new DeptDto("5678", "#", null, "b", "engB", 1, "asdf", "asdf");

        assertEquals(1, deptDao.insertDept(deptDto1));
        assertEquals(1, deptDao.insertDept(deptDto2));

        Map<String, String> map = new HashMap<>();
        map.put("EMPL_ID", emplDto1.getEMPL_ID());
        map.put("DEPT_NM", "바뀐이름");
        map.put("ENG_DEPT_NM", "changedName");
        map.put("LAST_UPDR_IDNF_NO", "modifierID");
        map.put("DEPT_CD", deptDto1.getDEPT_CD());

        assertEquals(1, deptDao.updateDept(map));

        DeptDto selectedDeptDto = deptDao.selectDept(map.get("DEPT_CD"));
        assertEquals(map.get("DEPT_NM"), selectedDeptDto.getDEPT_NM());
        assertEquals(emplDto1.getEMPL_NO(), selectedDeptDto.getDEPT_MNGR_EMPL_NO());
    }

    @Test
    @Transactional
    @DisplayName("부서 수정 시 잘못된 관리자 아이디 사용 테스트")
    void updateDeptFailTest() {
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("22222222", "Mogu", "1234", "mogu@gmail.com", 0, "최모구", "mogu", "2020-01-01", "1980-10-10", "222222", 22222, null, null, 'Y', 'Y', 'Y', 'N');

        assertEquals(1, emplDao.insertEmpl(emplDto1));
        assertEquals(1, emplDao.insertEmpl(emplDto2));

        DeptDto deptDto1 = new DeptDto("1234", "#", null, "a", "engA", 0, "asdf", "asdf"); //수정할 dept

        assertEquals(1, deptDao.insertDept(deptDto1));

        Map<String, String> map = new HashMap<>();
        map.put("EMPL_ID", "33333333");
        map.put("DEPT_NM", "바뀐이름");
        map.put("ENG_DEPT_NM", "changedName");
        map.put("LAST_UPDR_IDNF_NO", "modifierID");
        map.put("DEPT_CD", deptDto1.getDEPT_CD());

        assertEquals(1, deptDao.updateDept(map));

        DeptDto selectedDeptDto = deptDao.selectDept(map.get("DEPT_CD"));
        assertEquals(map.get("DEPT_NM"), selectedDeptDto.getDEPT_NM());
        assertEquals(deptDto1.getDEPT_MNGR_EMPL_NO(), selectedDeptDto.getDEPT_MNGR_EMPL_NO()); //관리자 번호는 업데이트 되지 않음
    }

    @Test
    @Transactional
    @DisplayName("기존과 동일한 데이터 입력 시 부서 수정 테스트") //동일한 데이터로 업데이트 해도 반영됨
    void updateDeptWithSameValueTest() {
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        assertEquals(1, emplDao.insertEmpl(emplDto1));

        DeptDto deptDto1 = new DeptDto("1234", "#", null, "a", "engA", 0, "asdf", "asdf", emplDto1.getEMPL_ID()); //수정할 dept

        assertEquals(1, deptDao.insertDept(deptDto1));

        Map<String, String> map = new HashMap<>();
        map.put("EMPL_ID", emplDto1.getEMPL_ID());
        map.put("DEPT_NM", deptDto1.getDEPT_NM());
        map.put("ENG_DEPT_NM", deptDto1.getENG_DEPT_NM());
        map.put("LAST_UPDR_IDNF_NO", deptDto1.getLAST_UPDR_IDNF_NO());
        map.put("DEPT_CD", deptDto1.getDEPT_CD());

        assertEquals(1, deptDao.updateDept(map));

        DeptDto selectedDeptDto = deptDao.selectDept(map.get("DEPT_CD"));
        assertEquals(map.get("DEPT_NM"), selectedDeptDto.getDEPT_NM());
    }

    @Test
    @Transactional
    @DisplayName("부서 구성원 및 부서명 조회 테스트")
    void selectDeptAndEmplDataTest(){
        deptDao.deleteAll();
        blngDeptDao.deleteAllBlngDept();
        emplDao.deleteAll();

        String deptCd1 = "1111";
        String deptCd2 = "2222";

        DeptDto deptDto1 = new DeptDto(deptCd1, "#", null, "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto(deptCd2, "#", null, "b", "eng", 1, "asdf", "asdf");

        assertEquals(1, deptDao.insertDept(deptDto1));
        assertEquals(1, deptDao.insertDept(deptDto2));

        EmplDto emplDto1 = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("22222222", "Mogu", "1234", "mogu@gmail.com", 0, "최모구", "mogu", "2020-01-01", "1980-10-10", "222222", 22222, null, null, 'Y', 'Y', 'Y', 'N');
        EmplDto emplDto3 = new EmplDto("33333333", "dongju", "1234", "dj@gmail.com", 0, "박동주", "djPumpthisparty", "2020-01-01", "1980-10-10", "333333", 33333, null, null, 'Y', 'Y', 'Y', 'N');

        assertEquals(1, emplDao.insertEmpl(emplDto1));
        assertEquals(1, emplDao.insertEmpl(emplDto2));
        assertEquals(1, emplDao.insertEmpl(emplDto3));

        BlngDeptDto blngDeptDto1 = new BlngDeptDto(deptCd1, emplDto1.getEMPL_NO(), 'N', emplDto1.getEMPL_NO(), emplDto1.getEMPL_NO());
        BlngDeptDto blngDeptDto2 = new BlngDeptDto(deptCd1, emplDto2.getEMPL_NO(), 'N', "admin1", "admin1");
        BlngDeptDto blngDeptDto3 = new BlngDeptDto(deptCd2, emplDto3.getEMPL_NO(), 'N', "admin1", "admin1");

        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto1));
        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto2));
        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto3));

        List<DeptAndEmplDto> memberofDeptCd1 = deptDao.selectMemberProfilesAndDeptName(deptCd1);
        assertEquals(2, memberofDeptCd1.size());

        List<DeptAndEmplDto> memberofDeptCd2 = deptDao.selectMemberProfilesAndDeptName(deptCd2);
        assertEquals(1, memberofDeptCd2.size());
    }

    @Test
    @Transactional
    @DisplayName("하위 부서가 있는 부서 정보 조회 테스트")
    void selectOneDeptAndMngrAndCdrDeptCntWithChildrenTest(){
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("22222222", "Mogu", "1234", "mogu@gmail.com", 0, "최모구", "mogu", "2020-01-01", "1980-10-10", "222222", 22222, null, null, 'Y', 'Y', 'Y', 'N');

        assertEquals(1, emplDao.insertEmpl(emplDto1));
        assertEquals(1, emplDao.insertEmpl(emplDto2));

        String deptCd1 = "1111";
        String deptCd2 = "2222";

        DeptDto parentDept = new DeptDto(deptCd1, "#", null, "a", "eng", 0, "asdf", "asdf", emplDto1.getEMPL_ID());
        DeptDto childDept = new DeptDto(deptCd2, parentDept.getDEPT_CD(), null, "b", "eng", 1, "asdf", "asdf", emplDto2.getEMPL_ID());

        assertEquals(1, deptDao.insertDept(parentDept));
        assertEquals(1, deptDao.insertDept(childDept));

        DeptAndEmplDto deptWithChildren = deptDao.selectOneDeptAndMngrAndCdrDeptCnt(parentDept.getDEPT_CD());
        assertEquals(1, deptWithChildren.getCDR_DEPT_CNT());

        DeptAndEmplDto deptWithNoChildren = deptDao.selectOneDeptAndMngrAndCdrDeptCnt(childDept.getDEPT_CD());
        assertEquals(0, deptWithNoChildren.getCDR_DEPT_CNT());
    }

    @Test
    @Transactional
    @DisplayName("하위 부서가 없는 부서 정보 조회 테스트")
    void selectOneDeptAndMngrAndCdrDeptCntWithNoChildrenTest(){
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        assertEquals(1, emplDao.insertEmpl(emplDto1));

        String deptCd1 = "1111";

        DeptDto dept = new DeptDto(deptCd1, "#", null, "a", "eng", 0, "asdf", "asdf", emplDto1.getEMPL_ID());
        assertEquals(1, deptDao.insertDept(dept));

        DeptAndEmplDto deptWithNoChildren = deptDao.selectOneDeptAndMngrAndCdrDeptCnt(dept.getDEPT_CD());
        assertEquals(0, deptWithNoChildren.getCDR_DEPT_CNT());
    }

    @Test
    @Transactional
    @DisplayName("하위 부서가 없고 매니저 정보 없는 부서 정보 조회 테스트")
    void selectOneDeptTest(){
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        assertEquals(1, emplDao.insertEmpl(emplDto1));

        String deptCd1 = "1111";

        DeptDto dept = new DeptDto(deptCd1, "#", null, "a", "eng", 0, "asdf", "asdf");
        assertEquals(1, deptDao.insertDept(dept));

        DeptAndEmplDto deptWithNoChildren = deptDao.selectOneDeptAndMngrAndCdrDeptCnt(dept.getDEPT_CD());
        assertEquals(0, deptWithNoChildren.getCDR_DEPT_CNT());
        assertNull(deptWithNoChildren.getEMPL_ID());
    }

    @Test
    @Transactional
    @DisplayName("하위 부서 개수 조회 테스트")
    void selectCdrDeptCntTest(){
        deptDao.deleteAll();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("11111111", "jy123", "1234", "jy123@gmail.com", 0, "김지영", "darwin", "2024-01-01", "2002-01-01", "123123", 11111, null, null, 'Y', 'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("22222222", "Mogu", "1234", "mogu@gmail.com", 0, "최모구", "mogu", "2020-01-01", "1980-10-10", "222222", 22222, null, null, 'Y', 'Y', 'Y', 'N');

        assertEquals(1, emplDao.insertEmpl(emplDto1));
        assertEquals(1, emplDao.insertEmpl(emplDto2));

        String deptCd1 = "1111";
        String deptCd2 = "2222";

        DeptDto parentDept = new DeptDto(deptCd1, "#", null, "a", "eng", 0, "asdf", "asdf");
        DeptDto childDept = new DeptDto(deptCd2, parentDept.getDEPT_CD(), null, "b", "eng", 1, "asdf", "asdf");

        assertEquals(1, deptDao.insertDept(parentDept));
        assertEquals(1, deptDao.insertDept(childDept));

        assertEquals(1, deptDao.selectCdrDeptCnt(parentDept.getDEPT_CD()));
    }
}