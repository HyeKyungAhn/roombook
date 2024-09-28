package site.roombook.dao;

import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceInfoAndTimeslotDto;

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
