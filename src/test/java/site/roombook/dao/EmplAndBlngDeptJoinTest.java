package site.roombook.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.BlngDeptDto;
import site.roombook.domain.EmplDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class EmplAndBlngDeptJoinTest {

    @Autowired
    EmplDao emplDao;

    @Autowired
    BlngDeptDao blngDeptDao;

    @Test
    @Transactional
    void selectDeptMemberTest() {
        emplDao.deleteAll();

        EmplDto emplDto1 = new EmplDto("00000001", "aaaa", "aaaa", "aaaa@asdf.com",
                0, "aaa", "aaa", "2024-01-01", "2000-01-01",
                "01123123", 1111, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto2 = new EmplDto("00000002", "bbbb", "bbbb", "bbbb@asdf.com",
                0, "bbb", "bbb", "2024-01-01", "2000-01-01",
                "01123123", 2222, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto3 = new EmplDto("00000003", "cccc", "cccc", "cccc@asdf.com",
                0, "ccc", "ccc", "2024-01-01", "2000-01-01",
                "01123123", 3333, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto4 = new EmplDto("00000004", "dddd", "dddd", "dddd@asdf.com",
                0, "ddd", "ddd", "2024-01-01", "2000-01-01",
                "01123123", 4444, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto5 = new EmplDto("00000005", "eeee", "eeee", "eeee@asdf.com",
                0, "eee", "eee", "2024-01-01", "2000-01-01",
                "01123123", 5555, null, null, 'Y',
                'Y', 'Y', 'N');
        EmplDto emplDto6 = new EmplDto("00000006", "ffff", "ffff", "ffff@asdf.com",
                0, "fff", "fff", "2024-01-01", "2000-01-01",
                "01123123", 6666, null, null, 'Y',
                'Y', 'Y', 'N');

        emplDao.insertEmpl(emplDto1);
        emplDao.insertEmpl(emplDto2);
        emplDao.insertEmpl(emplDto3);
        emplDao.insertEmpl(emplDto4);
        emplDao.insertEmpl(emplDto5);
        emplDao.insertEmpl(emplDto6);

        String deptCd = "1234";
        BlngDeptDto blngDeptDto1 = new BlngDeptDto(deptCd, "00000001", 'N', "10000000", "10000000");
        BlngDeptDto blngDeptDto2 = new BlngDeptDto(deptCd, "00000002", 'N', "10000000", "10000000");
        BlngDeptDto blngDeptDto3 = new BlngDeptDto(deptCd, "00000003", 'N', "10000000", "10000000");
        BlngDeptDto blngDeptDto4 = new BlngDeptDto(deptCd, "00000004", 'N', "10000000", "10000000");

        blngDeptDao.insertBlngDept(blngDeptDto1);
        blngDeptDao.insertBlngDept(blngDeptDto2);
        blngDeptDao.insertBlngDept(blngDeptDto3);
        blngDeptDao.insertBlngDept(blngDeptDto4);

        List<EmplDto> list = emplDao.selectDeptMembers(deptCd);

        assertEquals(4, list.size());
    }

}
