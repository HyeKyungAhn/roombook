package site.roombook.dao;

import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceRescFileDto;

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

    List<SpaceRescFileDto> selectSpaceList(Map<String, Object> spaceData);

    List<SpaceRescFileDto> selectOneSpaceAndRescAndFIle(Map<String, Object> spaceData);

    int update(SpaceDto spaceDto);
}
