package site.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    private List<EmplDto> sixEmpls;

    @Autowired
    EmplDao emplDao;

    @BeforeEach
    void setUp(){
        emplDao.deleteAll();

        sixEmpls = createSixEmplList();
    }

    @Test
    @Transactional
    void selectOneEmplTest(){
        EmplDto empl = sixEmpls.get(0);

        emplDao.insertEmpl(empl);

        EmplDto emplDto = emplDao.selectOneEmpl("0000001");
        assertNotNull(emplDto);
        assertEquals("01123123", emplDto.getWncomTelno());
    }

    @Test
    @Transactional
    void selectOneEmplProfileTest(){
        EmplDto oldEmplDto = sixEmpls.get(0);

        emplDao.insertEmpl(oldEmplDto);

        EmplDto newEmplDto = emplDao.selectOneEmplProfile(oldEmplDto.getEmplNo());

        assertNotNull(newEmplDto);
        assertEquals(oldEmplDto.getEmplId(), newEmplDto.getEmplId());
        assertNull(newEmplDto.getPrfPhotoPath());
    }

    @Test
    void insertEmplTest() {
        emplDao.insertEmpl(sixEmpls.get(0));
        emplDao.insertEmpl(sixEmpls.get(1));
        emplDao.insertEmpl(sixEmpls.get(2));
        emplDao.insertEmpl(sixEmpls.get(3));
        emplDao.insertEmpl(sixEmpls.get(4));
        emplDao.insertEmpl(sixEmpls.get(5));

        assertEquals(6, emplDao.selectAllEmplCnt());
    }

    @Test
    @Transactional
    void selectEmplProfilesTest(){
        emplDao.insertEmpl(sixEmpls.get(0));
        emplDao.insertEmpl(sixEmpls.get(1));
        emplDao.insertEmpl(sixEmpls.get(2));
        emplDao.insertEmpl(sixEmpls.get(3));
        emplDao.insertEmpl(sixEmpls.get(4));
        emplDao.insertEmpl(sixEmpls.get(5));

        assertEquals(6, emplDao.selectAllEmplCnt());

        List<EmplDto> list = emplDao.selectEmplProfilesWithRnmOrEmail("asdf");
        assertEquals(6, list.size());

        List<EmplDto> list2 = emplDao.selectEmplProfilesWithRnmOrEmail("ccc");
        assertEquals(1, list2.size());
    }

    @Nested
    class SelectEmplWtiEmailTest{

        @Test
        @Transactional
        void success(){
            emplDao.insertEmpl(sixEmpls.get(0));

            assertEquals(1, emplDao.selectEmplByEmail(sixEmpls.get(0).getEmail()));
        }

        @Test
        @Transactional
        void fail(){
            emplDao.insertEmpl(sixEmpls.get(0));

            assertEquals(0, emplDao.selectEmplByEmail("nonexistentEmail@mail.com"));
        }
    }


    @Nested
    class HasEmplTest{
        @Test
        @Transactional
        void success() {
            EmplDto oldEmplDto = sixEmpls.get(0);
            emplDao.insertEmpl(oldEmplDto);

            EmplDto selectedEmpl = emplDao.selectEmplById(oldEmplDto.getEmplId());
            assertNotNull(selectedEmpl);
        }

        @Test
        @Transactional
        void fail() {
            EmplDto selectedEmpl = emplDao.selectEmplById("nonExistentId");
            assertNull(selectedEmpl);
        }
    }

    List<EmplDto> createSixEmplList(){
        return List.of(
                new EmplDto("0000001", "aaaa", "aaaa", "aaaa@asdf.com",
                        0, "aaa", "aaa", "2024-01-01", "2000-01-01",
                        "01123123", 1111, null, null, 'Y',
                        'Y', 'Y', 'N'),
                new EmplDto("0000002", "bbbb", "bbbb", "bbbb@asdf.com",
                        0, "bbb", "bbb", "2024-01-01", "2000-01-01",
                        "01123123", 2222, null, null, 'Y',
                        'Y', 'Y', 'N'),
                new EmplDto("0000003", "cccc", "cccc", "cccc@asdf.com",
                        0, "ccc", "ccc", "2024-01-01", "2000-01-01",
                        "01123123", 3333, null, null, 'Y',
                        'Y', 'Y', 'N'),
                new EmplDto("0000004", "dddd", "dddd", "dddd@asdf.com",
                        0, "ddd", "ddd", "2024-01-01", "2000-01-01",
                        "01123123", 4444, null, null, 'Y',
                        'Y', 'Y', 'N'),
                new EmplDto("0000005", "eeee", "eeee", "eeee@asdf.com",
                        0, "eee", "eee", "2024-01-01", "2000-01-01",
                        "01123123", 5555, null, null, 'Y',
                        'Y', 'Y', 'N'),
                new EmplDto("0000006", "ffff", "ffff", "ffff@asdf.com",
                        0, "fff", "fff", "2024-01-01", "2000-01-01",
                        "01123123", 6666, null, null, 'Y',
                        'Y', 'Y', 'N')
        );
    }
}