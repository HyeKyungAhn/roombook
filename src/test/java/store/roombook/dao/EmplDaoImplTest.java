package store.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import store.roombook.domain.EmplDto;
import store.roombook.service.JwtService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class EmplDaoImplTest {

    private List<EmplDto> sixEmpls;

    @Autowired
    EmplDao emplDao;

    @Autowired
    JwtService jwtService;

    @BeforeEach
    void setUp(){
        emplDao.deleteAll();
        sixEmpls = createSixEmplList();
    }

    @Test
    @DisplayName("사원 한 명 조회")
    void selectOneEmplTest(){
        EmplDto empl = sixEmpls.get(0);

        emplDao.insertEmpl(empl);

        EmplDto emplDto = emplDao.selectOneEmpl("0000001");
        assertNotNull(emplDto);
        assertEquals("01123123", emplDto.getWncomTelno());
    }

    @Test
    @DisplayName("사원 한 명 프로필 조회")
    void selectOneEmplProfileTest(){
        EmplDto oldEmplDto = sixEmpls.get(0);

        emplDao.insertEmpl(oldEmplDto);

        EmplDto newEmplDto = emplDao.selectOneEmplProfile(oldEmplDto.getEmplNo());

        assertNotNull(newEmplDto);
        assertEquals(oldEmplDto.getEmplId(), newEmplDto.getEmplId());
        assertNull(newEmplDto.getPrfPhotoPath());
    }

    @Test
    @DisplayName("사원 추가")
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
    @DisplayName("사원 복수개 프로필 조회")
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

    @Test
    @DisplayName("권한 변경을 위한 복수개 사원 정보 조회")
    void selectAllForAuthAdminTest() {
        emplDao.insertEmpl(sixEmpls.get(0));
        emplDao.insertEmpl(sixEmpls.get(1));
        emplDao.insertEmpl(sixEmpls.get(2));
        emplDao.insertEmpl(sixEmpls.get(3));
        emplDao.insertEmpl(sixEmpls.get(4));
        emplDao.insertEmpl(sixEmpls.get(5));

        assertEquals(6, emplDao.selectAllEmplCnt());

        List<EmplDto> list = emplDao.selectAllForAuthAdmin();
        assertEquals(6, list.size());
        assertNull(list.get(0).getEmplNo());
    }

    @Test
    @DisplayName("제한된 갯수의 정렬된 사원 목록 조회")
    void selectLimitedEmplListTest() {
        emplDao.insertEmpl(sixEmpls.get(0));
        emplDao.insertEmpl(sixEmpls.get(1));
        emplDao.insertEmpl(sixEmpls.get(2));
        emplDao.insertEmpl(sixEmpls.get(3));
        emplDao.insertEmpl(sixEmpls.get(4));
        emplDao.insertEmpl(sixEmpls.get(5));

        Map<String, Object> map = new HashMap<>();
        map.put("option", "authName");
        map.put("optionValue", "ROLE_USER");
        map.put("limit", 3);
        map.put("offset", 0);

        List<EmplDto> list = emplDao.selectLimitedEmplList(map);
        assertEquals(3, list.size());

        map = new HashMap<>();
        map.put("option", "authName");
        map.put("optionValue", "ROLE_USER");
        map.put("limit", 3);
        map.put("offset", 3);

        list = emplDao.selectLimitedEmplList(map);
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("option에 맞는 사원 수 조회")
    void selectLimitedEmplListCntTest() {
        emplDao.insertEmpl(sixEmpls.get(0));
        emplDao.insertEmpl(sixEmpls.get(1));
        emplDao.insertEmpl(sixEmpls.get(2));
        emplDao.insertEmpl(sixEmpls.get(3));
        emplDao.insertEmpl(sixEmpls.get(4));
        emplDao.insertEmpl(sixEmpls.get(5));

        Map<String, String> map = new HashMap<>();
        map.put("option", "authName");
        map.put("optionValue", "ROLE_USER");

        assertEquals(5, emplDao.selectSearchedEmplsCnt(map));
    }

    @Test
    @DisplayName("권한 정보 업데이트")
    void updateEmplAuthNameTest() {
        emplDao.insertEmpl(sixEmpls.get(0));

        EmplDto emplDto = EmplDto.EmplDtoBuilder()
                .emplId(sixEmpls.get(0).getEmplId())
                .emplAuthNm("ROLE_SUPER_ADMIN")
                .build();

        assertEquals(1, emplDao.updateAuthName(emplDto));
    }

    @Nested
    @DisplayName("이메일로 사원 조회 테스트")
    class SelectEmplWithEmailTest{

        @Test
        @DisplayName("성공")
        void success(){
            emplDao.insertEmpl(sixEmpls.get(0));

            assertEquals(1, emplDao.selectEmplByEmail(sixEmpls.get(0).getEmail()));
        }

        @Test
        @DisplayName("실패")
        void fail(){
            emplDao.insertEmpl(sixEmpls.get(0));

            assertEquals(0, emplDao.selectEmplByEmail("nonexistentEmail@mail.com"));
        }
    }


    @Nested
    @DisplayName("사원 아이디 존해 여부 확인 테스트")
    class HasEmplTest{
        @Test
        @DisplayName("성공")
        void success() {
            EmplDto oldEmplDto = sixEmpls.get(0);
            emplDao.insertEmpl(oldEmplDto);

            assertTrue(emplDao.selectEmplById(oldEmplDto.getEmplId()).isPresent());
        }

        @Test
        @DisplayName("실패")
        void fail() {
            assertFalse(emplDao.selectEmplById("nonExistentId").isPresent());
        }
    }

    List<EmplDto> createSixEmplList(){
        return List.of(
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("science").pwd("science").email("science@asdf.com")
                        .pwdErrTms(0).rnm("science").engNm("science").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("6543321").empno(584393).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("mathematical").pwd("mathematical").email("mathematical@asdf.com")
                        .pwdErrTms(0).rnm("mathematical").engNm("mathematical").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(385737).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("geometry").pwd("geometry").email("geometry@asdf.com")
                        .pwdErrTms(0).rnm("geometry").engNm("geometry").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(102947).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("architecture").pwd("architecture").email("architecture@asdf.com")
                        .pwdErrTms(0).rnm("architecture").engNm("architecture").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(382947).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("musical").pwd("musical").email("musical@asdf.com")
                        .pwdErrTms(0).rnm("musical").engNm("musical").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(8228384).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("electronic").pwd("electronic").email("electronic@asdf.com")
                        .pwdErrTms(0).rnm("electronic").engNm("electronic").entDt("2024-01-01").emplAuthNm("ROLE_SUPER_ADMIN").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(747586).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build()
        );
    }
}