package site.roombook.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.RescDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class RescDaoImplTest {
    @Autowired
    RescDao rescDao;
    @Autowired
    SpaceRescDao spaceRescDao;

    @Test
    @Transactional
    @DisplayName("복수개 물품 삽입 테스트")
    void insertRescsTest(){
        rescDao.deleteAll();
        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화이트보드").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("모니터").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> list = new ArrayList<>();
        list.add(rescDto1);
        list.add(rescDto2);
        list.add(rescDto3);
        list.add(rescDto4);

        assertEquals(4,rescDao.insertRescs(list));
    }

    @Test
    @Transactional
    @DisplayName("전체 삭제 테스트")
    void deleteAllRescsTest(){
        rescDao.deleteAll();
        assertEquals(0, rescDao.selectAllRescCnt());

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화이트보드").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("모니터").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> list = new ArrayList<>();
        list.add(rescDto1);
        list.add(rescDto2);
        list.add(rescDto3);
        list.add(rescDto4);

        assertEquals(4,rescDao.insertRescs(list));
        rescDao.deleteAll();
        assertEquals(0, rescDao.selectAllRescCnt());
    }

    @Test
    @Transactional
    @DisplayName("전체 물품 수 조회 테스트")
    void selectCntRescsTest(){
        rescDao.deleteAll();
        assertEquals(0, rescDao.selectAllRescCnt());

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화이트보드").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("모니터").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> list = new ArrayList<>();
        list.add(rescDto1);
        list.add(rescDto2);
        list.add(rescDto3);
        list.add(rescDto4);

        assertEquals(4,rescDao.insertRescs(list));
        assertEquals(4, rescDao.selectAllRescCnt());
    }

    @Test
    @Transactional
    @DisplayName("전체 물품 목록 조회 테스트")
    void selectAllRescListTest(){
        rescDao.deleteAll();
        assertEquals(0, rescDao.selectAllRescCnt());

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화이트보드").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("모니터").spaceNo(1).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> list = new ArrayList<>();
        list.add(rescDto1);
        list.add(rescDto2);
        list.add(rescDto3);
        list.add(rescDto4);

        assertEquals(4,rescDao.insertRescs(list));
        assertEquals(4, rescDao.selectAllResc().size());
    }

    @Test
    @Transactional
    @DisplayName("키워드로 물품 목록 조회 테스트")
    void selectRescsWithKeywordTest(){
        rescDao.deleteAll();

        RescDto rescDto1 = RescDto.builder("wifi").fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화이트보드").fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("모니터").fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> list = new ArrayList<>();
        list.add(rescDto1);
        list.add(rescDto2);
        list.add(rescDto3);
        list.add(rescDto4);

        assertEquals(4,rescDao.insertRescs(list));
        assertEquals("wifi", rescDao.selectRescsWithKeyword("wi").get(0).getRescNm());
        assertEquals("화이트보드", rescDao.selectRescsWithKeyword("보").get(0).getRescNm());
    }

    @Test
    @Transactional
    @DisplayName("공간 물품 조회")
    void selectSpaceRescTest(){
        rescDao.deleteAll();

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화이트보드").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("모니터").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> list = new ArrayList<>();
        list.add(rescDto1);
        list.add(rescDto2);
        list.add(rescDto3);
        list.add(rescDto4);

        assertEquals(4,rescDao.insertRescs(list));
        assertEquals(4,spaceRescDao.insertSpaceRescs(list));
        assertEquals(4,rescDao.selectSpaceResc(rescDto1.getSpaceNo()).size());
    }
}