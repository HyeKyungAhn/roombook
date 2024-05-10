package site.roombook.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.BlngDeptAndEmplIdDto;
import site.roombook.domain.BlngDeptDto;
import site.roombook.domain.EmplDto;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class BlngDeptImplTest {

    @Autowired
    BlngDeptDao blngDeptDao;

    @Autowired
    EmplDao emplDao;

    @Test
    @Transactional
    void insertDeptMember() {
        blngDeptDao.deleteAllBlngDept();

        BlngDeptDto blngDeptDto1 = new BlngDeptDto("1234", "00000001", 'N', "00000002", "00000002");
        BlngDeptDto blngDeptDto2 = new BlngDeptDto("1234", "00000002", 'N', "00000002", "00000002");
        BlngDeptDto blngDeptDto3 = new BlngDeptDto("1234", "00000003", 'N', "00000002", "00000002");

        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto1));
        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto2));
        assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto3));
    }

    @Test
    @Transactional
    void selectAllBlngDept(){
        blngDeptDao.deleteAllBlngDept();

        assertEquals(0, blngDeptDao.selectAllBlngDept().size());


        BlngDeptDto blngDeptDto1 = new BlngDeptDto("1234", "00000001", 'N', "00000002", "00000002");
        BlngDeptDto blngDeptDto2 = new BlngDeptDto("1234", "00000002", 'N', "00000002", "00000002");
        BlngDeptDto blngDeptDto3 = new BlngDeptDto("1234", "00000003", 'N', "00000002", "00000002");

        blngDeptDao.insertBlngDept(blngDeptDto1);
        blngDeptDao.insertBlngDept(blngDeptDto2);
        blngDeptDao.insertBlngDept(blngDeptDto3);

        assertEquals(3, blngDeptDao.selectAllBlngDept().size());
    }

    @Test
    @Transactional
    void deleteAllBlngDeptTest(){
        blngDeptDao.deleteAllBlngDept();

        assertEquals(0, blngDeptDao.selectAllBlngDept().size());
    }

    @Test
    @Transactional
    @DisplayName("같은 소속 부서 삭제 테스트")
    void deleteBlngDeptsInSameDeptTest(){
        blngDeptDao.deleteAllBlngDept();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("0000001", "aaaa", "aaaa", "aaaa@asdf.com",
                0, "aaa", "aaa", "2024-01-01", "2000-01-01",
                "01123123", 1111, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("0000002", "bbbb", "bbbb", "bbbb@asdf.com",
                0, "bbb", "bbb", "2024-01-01", "2000-01-01",
                "01123123", 2222, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto3 = new EmplDto("0000003", "cccc", "cccc", "cccc@asdf.com",
                0, "ccc", "ccc", "2024-01-01", "2000-01-01",
                "01123123", 3333, null, null, 'Y',
                'Y', 'Y', 'N');

        assertEquals(1, emplDao.insertEmpl(emplDto1));
        assertEquals(1, emplDao.insertEmpl(emplDto2));
        assertEquals(1, emplDao.insertEmpl(emplDto3));

        BlngDeptDto blngDeptDto1 = new BlngDeptDto("1234", emplDto1.getEMPL_NO(), 'N', "admin", "admin");
        BlngDeptDto blngDeptDto2 = new BlngDeptDto("1234", emplDto2.getEMPL_NO(), 'N', "admin", "admin");
        BlngDeptDto blngDeptDto3 = new BlngDeptDto("1234", emplDto3.getEMPL_NO(), 'N', "admin", "admin");

        blngDeptDao.insertBlngDept(blngDeptDto1);
        blngDeptDao.insertBlngDept(blngDeptDto2);
        blngDeptDao.insertBlngDept(blngDeptDto3);

        assertEquals(3, blngDeptDao.selectAllBlngDept().size());

        Map<String, Object> deleteDeptData = new HashMap<>();
        String[] emplIDs = {emplDto1.getEMPL_ID(), emplDto2.getEMPL_ID()};
        deleteDeptData.put("BLNG_DEPT_CD", blngDeptDto1.getBLNG_DEPT_CD());
        deleteDeptData.put("emplIDs", emplIDs);

        int rowCnt;
        try {
            rowCnt = blngDeptDao.deleteBlngDepts(deleteDeptData);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return;
        }
        assertEquals(2, rowCnt);
    }

    @Test
    @Transactional
    @DisplayName("부서코드가 다른 소속 부서 삭제 테스트")
    void deleteBlngDeptsInDifferentDeptTest(){
        blngDeptDao.deleteAllBlngDept();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("0000001", "aaaa", "aaaa", "aaaa@asdf.com",
                0, "aaa", "aaa", "2024-01-01", "2000-01-01",
                "01123123", 1111, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("0000002", "bbbb", "bbbb", "bbbb@asdf.com",
                0, "bbb", "bbb", "2024-01-01", "2000-01-01",
                "01123123", 2222, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto3 = new EmplDto("0000003", "cccc", "cccc", "cccc@asdf.com",
                0, "ccc", "ccc", "2024-01-01", "2000-01-01",
                "01123123", 3333, null, null, 'Y',
                'Y', 'Y', 'N');

        assertEquals(1, emplDao.insertEmpl(emplDto1));
        assertEquals(1, emplDao.insertEmpl(emplDto2));
        assertEquals(1, emplDao.insertEmpl(emplDto3));

        BlngDeptDto blngDeptDto1 = new BlngDeptDto("1234", emplDto1.getEMPL_NO(), 'N', "admin", "admin");
        BlngDeptDto blngDeptDto2 = new BlngDeptDto("1234", emplDto2.getEMPL_NO(), 'N', "admin", "admin");
        BlngDeptDto blngDeptDto3 = new BlngDeptDto("1111", emplDto3.getEMPL_NO(), 'N', "admin", "admin");

        blngDeptDao.insertBlngDept(blngDeptDto1);
        blngDeptDao.insertBlngDept(blngDeptDto2);
        blngDeptDao.insertBlngDept(blngDeptDto3);

        assertEquals(3, blngDeptDao.selectAllBlngDept().size());

        Map<String, Object> deleteDeptData = new HashMap<>();
        String[] emplIDs = {emplDto1.getEMPL_ID(), emplDto3.getEMPL_ID()}; //부서코드가 다른 소속 부서 삭제
        deleteDeptData.put("BLNG_DEPT_CD", blngDeptDto1.getBLNG_DEPT_CD());
        deleteDeptData.put("emplIDs", emplIDs);

        int rowCnt;
        try {
            rowCnt = blngDeptDao.deleteBlngDepts(deleteDeptData);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        assertEquals(1, rowCnt); //부서코드가 일치하는 사원만 삭제됨
    }

    @Test
    @Transactional
    @DisplayName("여러 소속부서 업데이트 테스트")
    void insertBlngDeptsTest(){
        blngDeptDao.deleteAllBlngDept();
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("0000001", "aaaa", "aaaa", "aaaa@asdf.com",
                0, "aaa", "aaa", "2024-01-01", "2000-01-01",
                "01123123", 1111, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("0000002", "bbbb", "bbbb", "bbbb@asdf.com",
                0, "bbb", "bbb", "2024-01-01", "2000-01-01",
                "01123123", 2222, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto3 = new EmplDto("0000003", "cccc", "cccc", "cccc@asdf.com",
                0, "ccc", "ccc", "2024-01-01", "2000-01-01",
                "01123123", 3333, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto4 = new EmplDto("0000004", "dddd", "dddd", "dddd@asdf.com",
                0, "ddd", "ddd", "2024-01-01", "2000-01-01",
                "01123123", 4444, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto5 = new EmplDto("0000005", "eeee", "eeee", "eeee@asdf.com",
                0, "eee", "eee", "2024-01-01", "2000-01-01",
                "01123123", 5555, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto6 = new EmplDto("0000006", "ffff", "ffff", "ffff@asdf.com",
                0, "fff", "fff", "2024-01-01", "2000-01-01",
                "01123123", 6666, null, null, 'Y',
                'Y', 'Y', 'N');

        assertEquals(1, emplDao.insertEmpl(emplDto1));
        assertEquals(1, emplDao.insertEmpl(emplDto2));
        assertEquals(1, emplDao.insertEmpl(emplDto3));
        assertEquals(1, emplDao.insertEmpl(emplDto4));
        assertEquals(1, emplDao.insertEmpl(emplDto5));
        assertEquals(1, emplDao.insertEmpl(emplDto6));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        BlngDeptAndEmplIdDto blngDeptDto1 = new BlngDeptAndEmplIdDto("1234", emplDto1.getEMPL_ID(), "admin","admin");
        BlngDeptAndEmplIdDto blngDeptDto2 = new BlngDeptAndEmplIdDto("1234", emplDto2.getEMPL_ID(),  "admin","admin");

        blngDeptDao.insertOneBlngDept(blngDeptDto1);
        blngDeptDao.insertOneBlngDept(blngDeptDto2);

        assertEquals(2, blngDeptDao.selectAllBlngDept().size());

        List<BlngDeptAndEmplIdDto> list = new ArrayList<>();

        BlngDeptAndEmplIdDto blngDeptDto3 = new BlngDeptAndEmplIdDto("1234", emplDto3.getEMPL_ID(), "admin", "admin");
        BlngDeptAndEmplIdDto blngDeptDto4 = new BlngDeptAndEmplIdDto("1111", emplDto4.getEMPL_ID(), "admin", "admin");
        BlngDeptAndEmplIdDto blngDeptDto5 = new BlngDeptAndEmplIdDto("1111", emplDto5.getEMPL_ID(), "admin", "admin");
        BlngDeptAndEmplIdDto blngDeptDto6 = new BlngDeptAndEmplIdDto("2222", emplDto6.getEMPL_ID(), "admin", "admin");

        list.add(blngDeptDto3);
        list.add(blngDeptDto4);
        list.add(blngDeptDto5);
        list.add(blngDeptDto6);

        int rowCnt = 0;
        try{
            rowCnt = blngDeptDao.insertBlngDepts(list);
        } catch (InvocationTargetException e){
            e.printStackTrace();

        }
        List<BlngDeptDto> allBlngDept = blngDeptDao.selectAllBlngDept();

        assertEquals(4, rowCnt);
        assertEquals(6, allBlngDept.size());
    }

    @Test
    @Transactional
    @DisplayName("부서 구성원 한 명 추가 테스트")
    void insertOneBlndDeptTest(){
        blngDeptDao.deleteAllBlngDept();
        emplDao.deleteAll();

        EmplDto emplDto = new EmplDto("0000001", "aaaa", "aaaa", "aaaa@asdf.com",
                0, "aaa", "aaa", "2024-01-01", "2000-01-01",
                "01123123", 1111, null, null, 'Y',
                'Y', 'Y', 'N');

        emplDao.insertEmpl(emplDto);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        BlngDeptAndEmplIdDto bded = new BlngDeptAndEmplIdDto("1234", emplDto.getEMPL_ID(), 'N', date, "admin", date, "admin");
        int rowCnt = 0;
        try{
             rowCnt = blngDeptDao.insertOneBlngDept(bded);
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
        }
        assertEquals(1, rowCnt);
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 emplId로 부서 구성원 추가 실패 테스트")
    void insertOneBlndDeptWithNonExistingEmplIdTest(){
        blngDeptDao.deleteAllBlngDept();
        emplDao.deleteAll();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        BlngDeptAndEmplIdDto bded = new BlngDeptAndEmplIdDto("1234","aaaa", 'N', date, "admin", date, "admin");
        int rowCnt = 0;
        try{
            rowCnt = blngDeptDao.insertOneBlngDept(bded);
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
        }
        assertEquals(0, rowCnt);
    }
}