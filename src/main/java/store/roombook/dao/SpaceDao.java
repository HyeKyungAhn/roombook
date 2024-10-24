package store.roombook.dao;

import store.roombook.domain.SpaceDto;
import store.roombook.domain.SpaceInfoAndTimeslotDto;

import java.util.List;
import java.util.Map;

public interface SpaceDao {
    int deleteAll();

    int deleteWithSpaceNo(int spaceNo);

    int insertSpace(SpaceDto spaceDto);

    List<SpaceDto> selectAllSpace();

    SpaceDto selectOne(int spaceNo);

    int selectAllCnt();

    int selectCntAllNotHiddenSpace();

    List<SpaceInfoAndTimeslotDto> selectSpaceList(SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto);

    List<SpaceInfoAndTimeslotDto> selectLimitedSpaces(SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto);

    List<SpaceInfoAndTimeslotDto> selectOneSpaceAndRescAndFIle(Map<String, Object> spaceData);

    int update(SpaceDto spaceDto);
}
