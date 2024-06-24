package site.roombook.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.DeptDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class DeptServiceImplTest {

    @Autowired
    DeptService deptService;

    @Test
    @Transactional
    @DisplayName("부서 이름 중복 확인 성공 테스트")
    void haveIdenticalDeptNmTest(){
        //GIVEN
        deptService.deleteAll();

        String emplNo = "asdfd";
        DeptDto newDeptDto = new DeptDto(DeptService.NO_DEPT_CD, "#", "kim", "a", "eng", 0,"asdf","asdf");

        List<DeptDto> list = new ArrayList<>();
        list.add(newDeptDto);


        //WHEN
        boolean insertResult = deptService.saveOneDept(list, emplNo);
        boolean selectResult = deptService.haveIdenticalDeptNm(newDeptDto.getDEPT_NM());

        //THEN
        assertTrue(insertResult);
        assertTrue(selectResult);
    }

    @Test
    @Transactional
    @DisplayName("최초 부서 한 개 저장 성공 테스트")
    void firstSaveOneDeptTest(){
        //GIVEN
        deptService.deleteAll();

        String emplNo = "asdfd";
        DeptDto newDeptDto = new DeptDto(DeptService.NO_DEPT_CD, "#", "kim", "a", "eng", 0,"asdf","asdf");

        List<DeptDto> list = new ArrayList<>();
        list.add(newDeptDto);

        //WHEN
        boolean result = deptService.saveOneDept(list, emplNo);

        //THEN
        assertTrue(result);
    }

    @Test
    @Transactional
    @DisplayName("기존 부서가 있을 때 순서변경하지 않고 부서 한 개 저장 성공 테스트")
    void insertOneDeptTestWithDeptsNoOdrChanged(){
        //GIVEN
        deptService.deleteAll();

        String emplNo = "asdfd";
        DeptDto fstDept = new DeptDto(DeptService.NO_DEPT_CD, "#", "kim", "a","eng", 0,"asdf","asdf");
        List<DeptDto> list = new ArrayList<>();
        list.add(fstDept);

        deptService.saveOneDept(list, emplNo);

        List<DeptDto> allDept = deptService.getAllDeptTreeData();
        String fstDeptCd = allDept.get(0).getDEPT_CD();


        DeptDto scdDept = new DeptDto(DeptService.NO_DEPT_CD, "#", "jung", "b","eng", 1,"asdf","asdf");
        allDept.add(scdDept);
        deptService.saveOneDept(allDept, emplNo);


        List<DeptDto> allDept2 = deptService.getAllDeptTreeData();
        DeptDto newDeptDto = new DeptDto(DeptService.NO_DEPT_CD, fstDeptCd, "park", "c","eng", 0,"asdf","asdf");
        allDept2.add(newDeptDto);

        //WHEN
        boolean result = deptService.saveOneDept(allDept2, emplNo);

        //THEN
        assertTrue(result);
    }

    @Test
    @Transactional
    @DisplayName("기존 부서가 있을 때 순서 변경하고 부서 한 개 저장 성공 테스트")
    void insertOneDeptTestWithDeptsChangedOdr(){
        //GIVEN
        deptService.deleteAll();

        String emplNo = "asdfd";
        DeptDto fstDept = new DeptDto(DeptService.NO_DEPT_CD, "#", "kim", "a","eng", 0,"asdf","asdf");
        List<DeptDto> list = new ArrayList<>();
        list.add(fstDept);

        deptService.saveOneDept(list, emplNo);

        List<DeptDto> allDept = deptService.getAllDeptTreeData();
        String fstDeptCd = allDept.get(0).getDEPT_CD();


        DeptDto scdDept = new DeptDto(DeptService.NO_DEPT_CD, "#", "jung", "b","eng", 1,"asdf","asdf");
        allDept.add(scdDept);
        deptService.saveOneDept(allDept, emplNo);

        List<DeptDto> allDept2 = deptService.getAllDeptTreeData();
        String scdDeptCd = "";
        for (DeptDto dept : allDept2) {
            if (dept.getDEPT_NM().equals(scdDept.getDEPT_NM())) {
                scdDeptCd = dept.getDEPT_CD();
                dept.setDEPT_SORT_ODR(scdDept.getDEPT_SORT_ODR()+1);
            }
        }

        DeptDto newDept = new DeptDto(DeptService.NO_DEPT_CD, "#", "park", "c","eng", scdDept.getDEPT_SORT_ODR(),"asdf","asdf");
        allDept2.add(newDept);

        //WHEN
        boolean result = deptService.saveOneDept(allDept2, emplNo);
        List<DeptDto> allDept3 = deptService.getAllDeptTreeData();
        String trdDeptCd = "";

        //THEN
        assertTrue(result);
        assertEquals(3, allDept3.size());

        for (DeptDto dept : allDept3) {
            if (dept.getDEPT_NM().equals(newDept.getDEPT_NM())) {
                trdDeptCd = dept.getDEPT_CD();
            }
        }

        for (DeptDto dept : allDept3) {
            String deptCd = dept.getDEPT_CD();
            if (deptCd.equals(fstDeptCd)) {
                assertEquals(0, dept.getDEPT_SORT_ODR());
            } else if (deptCd.equals(scdDeptCd)) {
                assertEquals(2, dept.getDEPT_SORT_ODR());
            } else if (deptCd.equals(trdDeptCd)) {
                assertEquals(1, dept.getDEPT_SORT_ODR());
            } else {
                assert false : "부서코드가 일치하지 않음";
            }
        }



    }
//
//    @Test
//    @Transactional
//    @DisplayName("부서 중복 저장 실패 테스트")
//    void saveDuplicatedDeptTest(){
//        //GIVEN
//        deptService.deleteAll();
//
//        DeptDto deptDto = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");
//        DeptDto deptDto2 = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");
//
//        //WHEN
//        boolean successResult = deptService.saveOneDept(deptDto);
//        boolean failedResult = false;
//        try {
//            failedResult = deptService.saveOneDept(deptDto);
//        } catch (DuplicateKeyException e) {
//            e.printStackTrace();
//        }
//        //THEN
//        assertTrue(successResult);
//        assertFalse(failedResult);
//    }
//
//    @Test
//    @Transactional
//    @DisplayName("부서 한 개 저장 시 속성값 미 입력으로 실패 테스트")
//    void saveOneDeptFailTest(){
//        //GIVEN
//        deptService.deleteAll();
//
//        DeptDto deptDto = new DeptDto();
//
//        deptDto.setDEPT_CD("1111111");
//        deptDto.setUPP_DEPT_CD("#");
//
//        //WHEN
//        boolean result = deptService.saveOneDept(deptDto);
//
//        //THEN
//        assertFalse(result);
//    }
//
//    @Test
//    @Transactional
//    void getAllDeptTest(){
//        //GIVEN
//        deptService.deleteAll();
//
//        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");
//        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", 1,"asdf","asdf");
//        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", 0,"asdf","asdf");
//        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", 1,"asdf","asdf");
//        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", 0,"asdf","asdf");
//
//        deptService.saveOneDept(deptDto1);
//        deptService.saveOneDept(deptDto2);
//        deptService.saveOneDept(deptDto3);
//        deptService.saveOneDept(deptDto4);
//        deptService.saveOneDept(deptDto5);
//
//        List<DeptDto> list = deptService.getAllDeptTreeData();
//        assertEquals(5, list.size());
//    }
//
//    @Test
//    @Transactional
//    void modifyDeptOdrTest(){
//        //GIVEN
//        deptService.deleteAll();
//
//        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");
//        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", 1,"asdf","asdf");
//        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", 0,"asdf","asdf");
//        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", 1,"asdf","asdf");
//        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", 0,"asdf","asdf");
//
//        assertTrue(deptService.saveOneDept(deptDto1));
//        assertTrue(deptService.saveOneDept(deptDto2));
//        assertTrue(deptService.saveOneDept(deptDto3));
//        assertTrue(deptService.saveOneDept(deptDto4));
//        assertTrue(deptService.saveOneDept(deptDto5));
//
//        List<DeptDto> list = new ArrayList<>();
//
//        deptDto3.setUPP_DEPT_CD("11112222");
//        deptDto3.setDEPT_SORT_ODR(0);
//        deptDto3.setLAST_UPDR_IDNF_NO("aaaa");
//        deptDto4.setUPP_DEPT_CD("11112222");
//        deptDto4.setDEPT_SORT_ODR(0);
//        deptDto4.setLAST_UPDR_IDNF_NO("aaaa");
//
//        list.add(deptDto1);
//        list.add(deptDto2);
//        list.add(deptDto3);
//        list.add(deptDto4);
//        list.add(deptDto5);
//
//        //WHEN
//        int result = deptService.modifyDeptOdr(list);
//
//        //THEN
//        assertEquals(2, result);
//    }
//
//    @Test
//    @Transactional
//    @DisplayName("모든 부서 코드 및 이름 데이터가 없을 때 조회 테스트")
//    void getDeptCdAndNmTestWhenNoData(){
//        deptService.deleteAll();
//        List<DeptDto> resultList = deptService.getDeptCdAndNm();
//        assertEquals(0, resultList.size()); //null이 아님
//    }
//
//    @Test
//    @Transactional
//    @DisplayName("모든 부서 코드 및 이름 조회 성공 테스트")
//    void getDeptCdAndNmTest(){
//        //GIVEN
//        deptService.deleteAll();
//
//        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");
//        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", 1,"asdf","asdf");
//        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", 0,"asdf","asdf");
//        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", 1,"asdf","asdf");
//        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", 0,"asdf","asdf");
//
//        assertTrue(deptService.saveOneDept(deptDto1));
//        assertTrue(deptService.saveOneDept(deptDto2));
//        assertTrue(deptService.saveOneDept(deptDto3));
//        assertTrue(deptService.saveOneDept(deptDto4));
//        assertTrue(deptService.saveOneDept(deptDto5));
//
//        List<DeptDto> insertList = new ArrayList<>();
//        insertList.add(deptDto1);
//        insertList.add(deptDto2);
//        insertList.add(deptDto3);
//        insertList.add(deptDto4);
//        insertList.add(deptDto5);
//
//        List<DeptDto> selectList = deptService.getDeptCdAndNm();
//
//        for (DeptDto selectDeptDto : selectList) {
//            for (DeptDto insertDeptDto : insertList) {
//                if (selectDeptDto.getDEPT_CD().equals(insertDeptDto.getDEPT_CD())) {
//                    assertEquals(selectDeptDto.getDEPT_NM(), insertDeptDto.getDEPT_NM());
//                }
//            }
//        }
//    }
//
}