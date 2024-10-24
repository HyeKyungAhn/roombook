package store.roombook.dao;

import store.roombook.domain.RescDto;
import store.roombook.domain.SpaceRescDto;

import java.util.List;
import java.util.Map;

public interface SpaceRescDao {
    int insertSpaceRescs(List<RescDto> rescs);
    int deleteAll();
    int deleteSpaceRescs(Map<String,Object> rescsAndSpaceNo);
    List<SpaceRescDto> selectAll();
}
