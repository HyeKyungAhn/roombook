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
import site.roombook.domain.DeptDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class DeptDaoTest {
    @Autowired
    DeptDao deptDao;

    @Autowired
    BlngDeptDao blngDeptDao;

    @Test
    @Transactional
    void insertDeptTest(){
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111111", "#", "asdf", "인사부2", "HR", 0, "asdf", "asdf");
        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);
    }

    @Test
    @Transactional
    @DisplayName("중복된 부서 코드 insert 테스트")
    void insertDuplicatedDeptTest(){
        DeptDto deptDto = new DeptDto("11111112", "#", "asdf", "인사팀", "HR", 0, "asdf", "asdf");
        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);
        DeptDto duplicatedDeptDto = new DeptDto("11111112", "#", "asdf", "AA", "AA", 0, "asdf", "asdf");
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

        DeptDto deptDto = new DeptDto("11111111", "#", "asdf", "HR", "eng", 0, "asdf", "asdf");
        deptDao.insertDept(deptDto);

        DeptDto duplicatedDeptDto = new DeptDto("22222222", "#", "asdf", "HR", "eng", 0, "asdf", "asdf");
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

        DeptDto deptDto = new DeptDto();
        deptDto.setDEPT_CD("11111112");
        deptDto.setUPP_DEPT_CD("#");
        deptDto.setDEPT_NM("a");
        deptDto.setENG_DEPT_NM("a");
        deptDto.setDEPT_SORT_ODR(0);
        deptDto.setFST_REGR_IDNF_NO("asdf");
        deptDto.setLAST_UPDR_IDNF_NO("asdf");

        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);

        DeptDto deptdto2 = deptDao.selectDept(deptDto.getDEPT_CD());
        System.out.println("deptdto2 = " + deptdto2);
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
        DeptDto deptDto = new DeptDto("11111112", "#", "asdf", "인사부", "HR", 0, "asdf", "asdf");
        int rowCnt = deptDao.insertDept(deptDto);

        assertEquals(1, rowCnt);
        assertEquals(deptDto.getENG_DEPT_NM(), deptDao.selectDept(deptDto.getDEPT_CD()).getENG_DEPT_NM());
    }

    @Test
    @Transactional
    @DisplayName("부서명으로 부서 수 조회 테스트")
    void selectDeptNmTest(){
        DeptDto deptDto = new DeptDto("11111112", "#", "asdf", "인사부", "HR", 0, "asdf", "asdf");
        int rowCnt = deptDao.insertDept(deptDto);

        assertEquals(1, rowCnt);
        assertEquals(1, deptDao.selectDeptCntWithNm(deptDto.getDEPT_NM()));
    }

    @Test
    @Transactional
    void updateManagerTest() {
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111112", "", "asdf", "HR", "eng", 0, "asdf", "asdf");
        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);

        DeptDto deptDto1 = deptDao.selectDept(deptDto.getDEPT_CD());
        assertNotNull(deptDto1);

        Map<String, String> map = new HashMap<>();
        map.put("manager", "1111");
        map.put("empId", "bbbb");
        map.put("deptCd", deptDto.getDEPT_CD());
        rowCnt = deptDao.updateManager(map);
        assertEquals(1, rowCnt);

        DeptDto deptDto2 = deptDao.selectDept(deptDto.getDEPT_CD());
        assertNotNull(deptDto2);
    }

    @Test
    @Transactional
    void selectAllDept() throws Exception {
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111111", "", "asdf", "HR", "eng", 0, "asdf", "asdf");
        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);

        DeptDto deptDto2 = new DeptDto("11111112", "", "asdf", "AA", "eng", 0, "asdf", "asdf");
        rowCnt = deptDao.insertDept(deptDto2);
        assertEquals(1, rowCnt);

        List<DeptDto> list = deptDao.selectAllDept();
        assertEquals(2, list.size());
    }

    @Test
    @Transactional
    void selectAllDeptCntTest(){
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111111", "", "asdf", "HR", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11111112", "", "asdf", "AA", "eng", 0, "asdf", "asdf");
        deptDao.insertDept(deptDto);
        deptDao.insertDept(deptDto2);

        int rowCnt = deptDao.selectAllDeptCnt();
        assertEquals(2, rowCnt);
    }

    @Test
    @Transactional
    void deleteAllTest() {
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111111", "", "asdf", "HR", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11111112", "", "asdf", "AA", "eng", 0, "asdf", "asdf");
        deptDao.insertDept(deptDto);
        deptDao.insertDept(deptDto2);

        int rowCnt = deptDao.selectAllDeptCnt();
        assertEquals(2, rowCnt);

        int deletedRowCnt = deptDao.deleteAll();
        assertEquals(2, deletedRowCnt);

        int finalSelectedRowCnt = deptDao.selectAllDeptCnt();
        assertEquals(0, finalSelectedRowCnt);
    }

    @Test
    @Transactional
    void updateAllDeptTreeDataTest() throws Exception {
        deptDao.deleteAll();
        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", "eng", 0, "asdf", "asdf");
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
    void selectDeptCdAndNm() {
        //GIVEN
        deptDao.deleteAll();

        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", "eng", 0, "asdf", "asdf");

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

        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", "eng", 0, "asdf", "asdf");

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

        DeptDto deptDto1 = new DeptDto(deptCd1, "#", "kim", "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto(deptCd2, "#", "lee", "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto(deptCd3, "11111111", "park", "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto(deptCd4, "11111111", "yang", "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto(deptCd5, "22222222", "kwang", "e", "eng", 0, "asdf", "asdf");

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

        DeptDto deptDto1 = new DeptDto(deptCd1, "#", "kim", "a", "eng", 0, "asdf", "asdf");
        DeptDto deptDto2 = new DeptDto(deptCd2, "#", "lee", "b", "eng", 1, "asdf", "asdf");
        DeptDto deptDto3 = new DeptDto(deptCd3, "11111111", "park", "c", "eng", 0, "asdf", "asdf");
        DeptDto deptDto4 = new DeptDto(deptCd4, "11111111", "yang", "d", "eng", 1, "asdf", "asdf");
        DeptDto deptDto5 = new DeptDto(deptCd5, "22222222", "kwang", "e", "eng", 0, "asdf", "asdf");

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
}