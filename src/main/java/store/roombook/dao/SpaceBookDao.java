package store.roombook.dao;

import store.roombook.domain.SpaceBookAndSpaceDto;
import store.roombook.domain.SpaceBookDto;

import java.util.List;

public interface SpaceBookDao {

    int insert(SpaceBookDto spaceBookDto);

    List<SpaceBookDto> selectTimeSlotsAtTheDate(SpaceBookDto spaceBookDto);

    SpaceBookAndSpaceDto selectTimeslot(SpaceBookDto spaceBookDto);

    List<SpaceBookDto> selectPersonalTimeslots(SpaceBookDto spaceBookDto);

    int selectPersonalTimeslotsCount(SpaceBookDto spaceBookDto);

    int updateTimeslot(SpaceBookDto spaceBookDto);

    int updateTimeslotStatus(SpaceBookDto spaceBookDto);

    int deleteAll();
}
