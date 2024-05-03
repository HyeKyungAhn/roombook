package site.roombook.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.EmplDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class EmplDaoImplTest {

    @Autowired
    EmplDao emplDao;

    @Test
    @Transactional
    void selectOneEmplTest(){
        emplDao.deleteAll();

        String emplNo = "0000001";
        EmplDto emplDto1 = new EmplDto(emplNo, "aaaa", "aaaa", "aaaa@asdf.com",
                0, "aaa", "aaa", "2024-01-01", "2000-01-01",
                "01123123", 1111, null, null, 'Y',
                'Y', 'Y', 'N');

        emplDao.insertEmpl(emplDto1);
        EmplDto emplDto = emplDao.selectOneEmpl(emplNo);
        assertNotNull(emplDto);
        assertEquals("01123123", emplDto.getWNCOM_TELNO());
    }

    @Test
    @Transactional
    void selectOneEmplProfileTest(){
        emplDao.deleteAll();

        String emplNo = "0000001";
        EmplDto oldEmplDto = new EmplDto(emplNo, "aaaa", "aaaa", "aaaa@asdf.com",
                0, "aaa", "aaa", "2024-01-01", "2000-01-01",
                "01123123", 1111, null, null, 'Y',
                'Y', 'Y', 'N');

        emplDao.insertEmpl(oldEmplDto);
        EmplDto newEmplDto = emplDao.selectOneEmplProfile(emplNo);

        assertNotNull(newEmplDto);

        assertNull(newEmplDto.getEMPL_NO());

        assertEquals(oldEmplDto.getEMPL_ID(), newEmplDto.getEMPL_ID());
        assertNull(newEmplDto.getPRF_PHOTO_PATH());
        assertEquals(oldEmplDto.getRNM(), newEmplDto.getRNM());
        assertEquals(oldEmplDto.getENG_NM(), newEmplDto.getENG_NM());
        assertEquals(oldEmplDto.getEMPNO(), newEmplDto.getEMPNO());
        assertEquals(oldEmplDto.getEMAIL(), newEmplDto.getEMAIL());
    }

    @Test
    void insertEmplTest(){
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

        emplDao.insertEmpl(emplDto1);
        emplDao.insertEmpl(emplDto2);
        emplDao.insertEmpl(emplDto3);
        emplDao.insertEmpl(emplDto4);
        emplDao.insertEmpl(emplDto5);
        emplDao.insertEmpl(emplDto6);

        assertEquals(6, emplDao.selectAllEmplCnt());
    }

    @Test
    @Transactional
    void selectEmplProfilesTest(){
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

        emplDao.insertEmpl(emplDto1);
        emplDao.insertEmpl(emplDto2);
        emplDao.insertEmpl(emplDto3);
        emplDao.insertEmpl(emplDto4);
        emplDao.insertEmpl(emplDto5);
        emplDao.insertEmpl(emplDto6);

        assertEquals(6, emplDao.selectAllEmplCnt());

        List<EmplDto> list = emplDao.selectEmplProfilesWithRnmOrEmail("asdf");
        assertEquals(6, list.size());

        List<EmplDto> list2 = emplDao.selectEmplProfilesWithRnmOrEmail("ccc");
        assertEquals(1, list2.size());
    }
}