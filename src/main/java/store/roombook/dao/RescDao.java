package store.roombook.dao;

import store.roombook.domain.RescDto;

import java.util.List;

public interface RescDao {
    List<RescDto> selectRescsWithKeyword(String keyword);

    int insertRescs(List<RescDto> list);

    int deleteAll();

    List<RescDto> selectAllResc();

    int selectAllRescCnt();

    List<RescDto> selectSpaceResc(int spaceNo);
}
