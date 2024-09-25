package site.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.CmnCode;
import site.roombook.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("공간")
@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class SpaceDaoImplTest {
    @Autowired
    private SpaceDao spaceDao;

    @Autowired
    private RescDao rescDao;

    @Autowired
    private SpaceRescDao spaceRescDao;

    @Autowired
    private EmplDao emplDao;

    @Autowired
    private SpaceBookDao spaceBookDao;

    @BeforeEach
    void setUp(){
        spaceDao.deleteAll();
    }

    SpaceDto createSpaceDto() {
        return createSpaceDto(false, false);
    }

    SpaceDto createSpaceDto(boolean isHide, boolean isWkendPosbl) {
        int ranNum = (int)(Math.random()*899999999)+100000000;
        return new SpaceDto.Builder()
                .spaceNo(ranNum)
                .spaceNm("회의실" + ranNum)
                .spaceMaxPsonCnt(10)
                .spaceLocDesc("1층 정수기 옆")
                .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                .spaceMaxRsvdTms(5)
                .spaceUsgPosblBgnTm(LocalTime.of(9,0))
                .spaceUsgPosblEndTm(LocalTime.of(12,0))
                .spaceWkendUsgPosblYn(isWkendPosbl?'Y':'N')
                .spaceHideYn(isHide?'Y':'N')
                .fstRegDtm(LocalDateTime.now())
                .fstRegrIdnfNo("admin").build();
    }

    @Nested
    @DisplayName("삽입 시")
    class WhenNewSpaceInsertTest{
        @Test
        @DisplayName("정상으로 처리될 때")
        void isInserted(){
            assertEquals(1, spaceDao.insertSpace(createSpaceDto()));
        }
    }

    @Test
    @DisplayName("전체 삭제 테스트")
    void deleteAllTest(){
        spaceDao.deleteAll();

        assertEquals(1, spaceDao.insertSpace(createSpaceDto()));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto()));

        List<SpaceDto> list = spaceDao.selectAllSpace();
        assertEquals(2, list.size());
        spaceDao.deleteAll();
        assertEquals(0, spaceDao.selectAllSpace().size());
    }

    @Test
    @DisplayName("전체 공간 수 조회 테스트")
    void deleteAllCntTest(){
        spaceDao.deleteAll();

        assertEquals(1, spaceDao.insertSpace(createSpaceDto()));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto()));

        assertEquals(2, spaceDao.selectAllCnt());
        spaceDao.deleteAll();
        assertEquals(0, spaceDao.selectAllCnt());
    }

    @Test
    @DisplayName("spaceNo로 공간 한 개 조회 테스트")
    void selectOneTest(){
        spaceDao.deleteAll();

        assertEquals(1, spaceDao.insertSpace(createSpaceDto()));
    }

    @Test
    @DisplayName("공간 수정 테스트")
    void updateTest(){
        spaceDao.deleteAll();

        SpaceDto spaceDto1 = new SpaceDto.Builder()
                .spaceNo(1)
                .spaceNm("회의실A")
                .spaceMaxPsonCnt(10)
                .spaceLocDesc("2층 정수기 옆")
                .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                .spaceMaxRsvdTms(5)
                .spaceUsgPosblBgnTm(LocalTime.of(9,0))
                .spaceUsgPosblEndTm(LocalTime.of(12,0))
                .spaceWkendUsgPosblYn('N')
                .spaceHideYn('N')
                .fstRegDtm(LocalDateTime.now())
                .fstRegrIdnfNo("admin").build();

        assertEquals(1, spaceDao.insertSpace(spaceDto1));

        SpaceDto spaceDto2 = new SpaceDto.Builder()
                .spaceNo(1)
                .spaceNm("회의실B")
                .spaceMaxPsonCnt(20)
                .spaceLocDesc("2층 정수기 옆")
                .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                .spaceMaxRsvdTms(5)
                .spaceUsgPosblBgnTm(LocalTime.of(9,0))
                .spaceUsgPosblEndTm(LocalTime.of(12,0))
                .spaceWkendUsgPosblYn('N')
                .spaceHideYn('N')
                .lastUpdrIdnfNo("admin").build();

        assertEquals(1,spaceDao.update(spaceDto2));
        SpaceDto selectedSpace = spaceDao.selectOne(spaceDto2.getSpaceNo());
        assertEquals(spaceDto2.getSpaceMaxPsonCnt(), selectedSpace.getSpaceMaxPsonCnt());
    }


    @Test
    @DisplayName("숨겨지지 않은 공간 갯수 조회 테스트")
    void selectCntAllNotHiddenSpaceTest(){
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(true, false)));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(false, false)));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(false, false)));

        assertEquals(2, spaceDao.selectCntAllNotHiddenSpace());
    }

    @Test
    @DisplayName("공간 갯수 조회 테스트")
    void selectAllTest(){
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(true, true)));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(false, true)));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(true, true)));

        assertEquals(3, spaceDao.selectAllCnt());
    }

    @Nested
    @DisplayName("공간 목록 조회 테스트")
    class SpaceListTest {

        @BeforeEach
        void setup() {
            spaceDao.deleteAll();
            rescDao.deleteAll();
            emplDao.deleteAll();
            //관리자 저장
            EmplDto dummyRscAdminEmpl = getRscAdmin();
            assertEquals(1, emplDao.insertEmpl(dummyRscAdminEmpl));

            //공간 저장
            SpaceDto dummySpace = new SpaceDto.Builder()
                    .spaceNo(999999)
                    .spaceNm("회의실A")
                    .spaceMaxPsonCnt(10)
                    .spaceLocDesc("2층 정수기 옆")
                    .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                    .spaceMaxRsvdTms(2)
                    .spaceUsgPosblBgnTm(LocalTime.of(9,0))
                    .spaceUsgPosblEndTm(LocalTime.of(18,0))
                    .spaceWkendUsgPosblYn('Y')
                    .spaceHideYn('N')
                    .fstRegDtm(LocalDateTime.now())
                    .fstRegrIdnfNo(dummyRscAdminEmpl.getEmplNo()).build();

            assertEquals(1, spaceDao.insertSpace(dummySpace));
            //물품 저장

            RescDto rescDto1 = RescDto.builder("wifi").spaceNo(dummySpace.getSpaceNo()).fstRegrIdnfNo(dummyRscAdminEmpl.getEmplNo()).lastUpdrIdnfNo(dummyRscAdminEmpl.getEmplNo()).build();
            RescDto rescDto2 = RescDto.builder("의자").spaceNo(dummySpace.getSpaceNo()).fstRegrIdnfNo(dummyRscAdminEmpl.getEmplNo()).lastUpdrIdnfNo(dummyRscAdminEmpl.getEmplNo()).build();
            RescDto rescDto3 = RescDto.builder("pc").spaceNo(dummySpace.getSpaceNo()).fstRegrIdnfNo(dummyRscAdminEmpl.getEmplNo()).lastUpdrIdnfNo(dummyRscAdminEmpl.getEmplNo()).build();

            List<RescDto> list = new ArrayList<>();
            list.add(rescDto1);
            list.add(rescDto2);
            list.add(rescDto3);

            assertEquals(3,rescDao.insertRescs(list));
            assertEquals(3, spaceRescDao.insertSpaceRescs(list));


            //예약 저장
            String spaceBookId1 = UUID.randomUUID().toString();
            SpaceBookDto existingSpaceBooking1 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(spaceBookId1)
                    .emplId(dummyRscAdminEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 11, 11))
                    .spaceBookBgnTm(LocalTime.of(9, 0))
                    .spaceBookEndTm(LocalTime.of(11, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(existingSpaceBooking1));


            String spaceBookId2 = UUID.randomUUID().toString();
            SpaceBookDto existingSpaceBooking2 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(spaceBookId2)
                    .emplId(dummyRscAdminEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 11, 11))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(existingSpaceBooking2));
        }


        @Test
        @DisplayName("공간 목록 조회")
        void selectList() {
            SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto = SpaceInfoAndTimeslotDto.SpaceRescFileDtoBuilder()
                    .spaceCnt(5)
                    .offset(0)
                    .rescCnt(3)
                    .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .spaceBookDate(LocalDate.of(2024,11,11))
                    .isHiddenSpaceInvisible(true)
                    .build();

            List<SpaceInfoAndTimeslotDto> list = spaceDao.selectSpaceList(spaceInfoAndTimeslotDto);
            assertEquals(6, list.size());
        }

        private EmplDto getRscAdmin() {
            return EmplDto.EmplDtoBuilder().emplNo("8457294").emplId("adminId").pwd("1234").email("testid1@gmail.com")
                    .pwdErrTms(0).rnm("감자영").engNm("gamja").entDt("2024-01-01").emplAuthNm("ROLE_RSC_ADMIN").brdt("2000-01-01")
                    .wncomTelno("111111").empno(1111).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
        }
    }
}
