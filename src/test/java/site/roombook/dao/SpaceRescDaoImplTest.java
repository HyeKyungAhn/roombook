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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class SpaceRescDaoImplTest {

    @Autowired
    SpaceRescDao spaceRescDao;

    @Autowired
    RescDao rescDao;

    @Test
    @Transactional
    @DisplayName("공간 물품 삽입 테스트")
    void insertSpaceRescsTest(){
        spaceRescDao.deleteAll();
        rescDao.deleteAll();

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> list = new ArrayList<>();
        list.add(rescDto1);
        list.add(rescDto2);

        assertEquals(2, rescDao.insertRescs(list));

        assertEquals(2, spaceRescDao.insertSpaceRescs(list));
    }

    @Test
    @Transactional
    @DisplayName("중복된 공간 물품 삽입 테스트")
    void insertDuplicateSpaceRescsTest(){
        spaceRescDao.deleteAll();
        rescDao.deleteAll();

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto newRescDto = RescDto.builder("빗자루").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> list = new ArrayList<>();
        list.add(rescDto1);
        list.add(rescDto2);
        list.add(newRescDto);

        assertEquals(3, rescDao.insertRescs(list));

        list.remove(newRescDto);

        assertEquals(2, spaceRescDao.insertSpaceRescs(list));

        List<RescDto> dupList = new ArrayList<>();
        dupList.add(rescDto1);
        dupList.add(rescDto2);
        dupList.add(newRescDto);

        assertEquals(1, spaceRescDao.insertSpaceRescs(dupList));
    }

    @Test
    @Transactional
    @DisplayName("공간 물품 삭제 테스트")
    void deleteSpaceRescsTest(){
        spaceRescDao.deleteAll();
        rescDao.deleteAll();

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화분").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("화이트보드").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> rescs = new ArrayList<>();
        rescs.add(rescDto1);
        rescs.add(rescDto2);
        rescs.add(rescDto3);
        rescs.add(rescDto4);

        assertEquals(4, rescDao.insertRescs(rescs));
        assertEquals(4, spaceRescDao.insertSpaceRescs(rescs));

        List<RescDto> updatedRescs = new ArrayList<>();
        updatedRescs.add(rescDto1);
        updatedRescs.add(rescDto2);
        updatedRescs.add(rescDto3);

        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", 12);
        map.put("rescs", updatedRescs);

        assertEquals(1, spaceRescDao.deleteSpaceRescs(map));
    }

    @Test
    @Transactional
    @DisplayName("입력값이 없을 때 공간 물품 삭제 테스트")
    void deleteSpaceRescsWithNoInputTest(){
        spaceRescDao.deleteAll();
        rescDao.deleteAll();

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화분").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("화이트보드").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> rescs = new ArrayList<>();
        rescs.add(rescDto1);
        rescs.add(rescDto2);
        rescs.add(rescDto3);
        rescs.add(rescDto4);

        assertEquals(4, rescDao.insertRescs(rescs));
        assertEquals(4, spaceRescDao.insertSpaceRescs(rescs));

        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", 12);
        map.put("rescs", null);

        assertEquals(4, spaceRescDao.deleteSpaceRescs(map));
    }

    @Test
    @Transactional
    @DisplayName("잘못된 공간 번호로 공간 물품 삭제 테스트")
    void deleteSpaceRescsWithWrongSpaceNoTest(){
        spaceRescDao.deleteAll();
        rescDao.deleteAll();

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화분").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto4 = RescDto.builder("화이트보드").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> rescs = new ArrayList<>();
        rescs.add(rescDto1);
        rescs.add(rescDto2);
        rescs.add(rescDto3);
        rescs.add(rescDto4);

        assertEquals(4, rescDao.insertRescs(rescs));
        assertEquals(4, spaceRescDao.insertSpaceRescs(rescs));

        List<RescDto> updatedRescs = new ArrayList<>();
        updatedRescs.add(rescDto1);
        updatedRescs.add(rescDto2);
        updatedRescs.add(rescDto3);

        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", 13);
        map.put("rescs", updatedRescs);

        assertEquals(0, spaceRescDao.deleteSpaceRescs(map));
    }

    @Test
    @Transactional
    @DisplayName("목록에 새 물품 추가 시 공간 물품 삭제 테스트")
    void deleteSpaceRescsWithNewRescTest(){
        spaceRescDao.deleteAll();
        rescDao.deleteAll();

        RescDto rescDto1 = RescDto.builder("wifi").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto2 = RescDto.builder("에어컨").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();
        RescDto rescDto3 = RescDto.builder("화분").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> rescs = new ArrayList<>();
        rescs.add(rescDto1);
        rescs.add(rescDto2);
        rescs.add(rescDto3);

        assertEquals(3, rescDao.insertRescs(rescs));
        assertEquals(3, spaceRescDao.insertSpaceRescs(rescs));

        RescDto rescDto4 = RescDto.builder("화이트보드").spaceNo(12).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build();

        List<RescDto> updatedRescs = new ArrayList<>();
        updatedRescs.add(rescDto1);
        updatedRescs.add(rescDto2);
        updatedRescs.add(rescDto4);

        assertEquals(1, rescDao.insertRescs(updatedRescs));
        assertEquals(1, spaceRescDao.insertSpaceRescs(updatedRescs));

        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", rescDto1.getSpaceNo());
        map.put("rescs", updatedRescs);

        assertEquals(1, spaceRescDao.deleteSpaceRescs(map));
    }
}