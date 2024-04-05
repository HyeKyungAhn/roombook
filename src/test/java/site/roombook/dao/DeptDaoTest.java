package site.roombook.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.DeptDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class DeptDaoTest {
    @Autowired
    DeptDao deptDao;

    @Test
    @Transactional
    void insertDeptTest() throws Exception{
        DeptDto deptDto = new DeptDto("11111112", "", "asdf", "HR", 0,"asdf","asdf");
        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);
    }

    @Test
    @Transactional
    void insertDeptFailTest() throws Exception{
        deptDao.deleteAll();

        DeptDto deptDto = new DeptDto();
        deptDto.setDEPT_CD("11111112");

        int rowCnt = 0;
        try{
            rowCnt = deptDao.insertDept(deptDto);
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
        }

        assertEquals(0, rowCnt);
    }

    @Test
    @Transactional
    void selectDeptTest() throws Exception{
        DeptDto deptDto = deptDao.selectDept("11111111");
        assertEquals("HR", deptDto.getDEPT_NM());
    }

    @Test
    @Transactional
    void updateManagerTest() throws Exception {
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111112", "", "asdf", "HR", 0,"asdf","asdf");
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
        DeptDto deptDto = new DeptDto("11111111", "", "asdf", "HR", 0,"asdf","asdf");
        int rowCnt = deptDao.insertDept(deptDto);
        assertEquals(1, rowCnt);

        DeptDto deptDto2 = new DeptDto("11111112", "", "asdf", "HR", 0,"asdf","asdf");
        rowCnt = deptDao.insertDept(deptDto2);
        assertEquals(1, rowCnt);

        List<DeptDto> list = deptDao.selectAllDept();
        assertEquals(2, list.size());
    }

    @Test
    @Transactional
    void selectAllDeptCntTest() throws Exception {
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111111", "", "asdf", "HR", 0,"asdf","asdf");
        DeptDto deptDto2 = new DeptDto("11111112", "", "asdf", "HR", 0,"asdf","asdf");
        deptDao.insertDept(deptDto);
        deptDao.insertDept(deptDto2);

        int rowCnt = deptDao.selectAllDeptCnt();
        assertEquals(2, rowCnt);
    }

    @Test
    @Transactional
    void deleteAllTest() throws Exception {
        deptDao.deleteAll();
        DeptDto deptDto = new DeptDto("11111111", "", "asdf", "HR", 0,"asdf","asdf");
        DeptDto deptDto2 = new DeptDto("11111112", "", "asdf", "HR", 0,"asdf","asdf");
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
        DeptDto deptDto1 = new DeptDto("11111111", "#", "kim", "a", 0,"asdf","asdf");
        DeptDto deptDto2 = new DeptDto("11112222", "#", "lee", "b", 1,"asdf","asdf");
        DeptDto deptDto3 = new DeptDto("22222222", "11111111", "park", "c", 0,"asdf","asdf");
        DeptDto deptDto4 = new DeptDto("22222233", "11111111", "yang", "d", 1,"asdf","asdf");
        DeptDto deptDto5 = new DeptDto("33333333", "22222222", "kwang", "e", 0,"asdf","asdf");
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

        int rowCnt = deptDao.updateAllDeptTreeData(modifiedList);
        assertEquals(3, rowCnt);
    }
}