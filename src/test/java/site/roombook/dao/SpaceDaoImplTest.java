package site.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.SpaceDto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("공간")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class SpaceDaoImplTest {
    @Autowired
    SpaceDao spaceDao;

    @Autowired
    RescDao rescDao;

    @Autowired
    SpaceRescDao spaceRescDao;

    @Autowired
    FileDao fileDao;

    protected SpaceDto spaceDto;

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
    class whenNewSpaceInsertTest{
        @Test
        @Transactional
        @DisplayName("정상으로 처리될 때")
        void isInserted(){
            assertEquals(1, spaceDao.insertSpace(createSpaceDto()));
        }
    }

    @Test
    @Transactional
    @DisplayName("필수 파라미터 중 일부를 제외한 공간 데이터 삽입 테스트")
    void insertSpaceTest2(){
        spaceDao.deleteAll();

        SpaceDto spaceDto = new SpaceDto.Builder()
                .spaceNo(1)
                .spaceNm("회의실")
                .spaceMaxPsonCnt(10)
                .spaceLocDesc("1층 정수기 옆")
                .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                .fstRegDtm(LocalDateTime.now())
                .fstRegrIdnfNo("admin").build();


        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> spaceDao.insertSpace(spaceDto));
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    @Transactional
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
    @Transactional
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
    @Transactional
    @DisplayName("spaceNo로 공간 한 개 조회 테스트")
    void selectOneTest(){
        spaceDao.deleteAll();

        assertEquals(1, spaceDao.insertSpace(createSpaceDto()));
    }

    @Test
    @Transactional
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
        SpaceDto selectedSpace = spaceDao.selectOne(spaceDto2.getSPACE_NO());
        assertEquals(spaceDto2.getSPACE_MAX_PSON_CNT(), selectedSpace.getSPACE_MAX_PSON_CNT());
    }


    @Test
    @Transactional
    @DisplayName("숨겨지지 않은 공간 갯수 조회 테스트")
    void selectCntAllNotHiddenSpaceTest(){
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(true, false)));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(false, false)));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(false, false)));

        assertEquals(2, spaceDao.selectCntAllNotHiddenSpace());
    }

    @Test
    @Transactional
    @DisplayName("")
    void selectAllTest(){
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(true, true)));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(false, true)));
        assertEquals(1, spaceDao.insertSpace(createSpaceDto(true, true)));

        assertEquals(3, spaceDao.selectAllCnt());
    }
}
