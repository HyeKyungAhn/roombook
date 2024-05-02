package site.roombook.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.BlngDeptDto;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class BlngDeptImplTest {

    @Autowired
    BlngDeptDao blngDeptDao;

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
}