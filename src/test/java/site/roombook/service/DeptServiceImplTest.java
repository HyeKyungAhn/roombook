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
    @DisplayName("부서 한 개 저장 성공 테스트")
    void saveOneDeptTest(){
        //GIVEN
        deptService.deleteAll();

        DeptDto deptDto = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");

        //WHEN
        boolean result = deptService.saveOneDept(deptDto);

        //THEN
        assertTrue(result);
    }

    @Test
    @Transactional
    @DisplayName("부서 한 개 저장 시 속성값 미 입력으로 실패 테스트")
    void saveOneDeptFailTest(){
        //GIVEN
        deptService.deleteAll();

        DeptDto deptDto = new DeptDto();

        deptDto.setDEPT_CD("1111111");
        deptDto.setUPP_DEPT_CD("#");

        //WHEN
        boolean result = deptService.saveOneDept(deptDto);

        //THEN
        assertFalse(result);
    }

    @Test
    @Transactional
    void getAllDeptTest(){
        //GIVEN
        deptService.deleteAll();

        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", 1,"asdf","asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", 0,"asdf","asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", 1,"asdf","asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", 0,"asdf","asdf");

        deptService.saveOneDept(deptDto1);
        deptService.saveOneDept(deptDto2);
        deptService.saveOneDept(deptDto3);
        deptService.saveOneDept(deptDto4);
        deptService.saveOneDept(deptDto5);

        List<DeptDto> list = deptService.getAllDept();
        assertEquals(5, list.size());
    }

    @Test
    @Transactional
    void modifyDeptOdrTest(){
        //GIVEN
        deptService.deleteAll();

        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", 1,"asdf","asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", 0,"asdf","asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", 1,"asdf","asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", 0,"asdf","asdf");

        assertTrue(deptService.saveOneDept(deptDto1));
        assertTrue(deptService.saveOneDept(deptDto2));
        assertTrue(deptService.saveOneDept(deptDto3));
        assertTrue(deptService.saveOneDept(deptDto4));
        assertTrue(deptService.saveOneDept(deptDto5));

        List<DeptDto> list = new ArrayList<>();

        deptDto3.setUPP_DEPT_CD("11112222");
        deptDto3.setDEPT_SORT_ODR(0);
        deptDto3.setLAST_UPDR_IDNF_NO("aaaa");
        deptDto4.setUPP_DEPT_CD("11112222");
        deptDto4.setDEPT_SORT_ODR(0);
        deptDto4.setLAST_UPDR_IDNF_NO("aaaa");

        list.add(deptDto1);
        list.add(deptDto2);
        list.add(deptDto3);
        list.add(deptDto4);
        list.add(deptDto5);

        //WHEN
        int result = deptService.modifyDeptOdr(list);

        //THEN
        assertEquals(2, result);
    }

}