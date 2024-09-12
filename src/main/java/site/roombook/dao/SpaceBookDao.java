package site.roombook.dao;

import site.roombook.domain.SpaceBookAndSpaceDto;
import site.roombook.domain.SpaceBookDto;

import java.util.List;

public interface SpaceBookDao {

    int insert(SpaceBookDto spaceBookDto);
    List<SpaceBookDto> selectTimeSlotsAtTheDate(SpaceBookDto spaceBookDto);
    SpaceBookAndSpaceDto selectTimeslot(SpaceBookDto spaceBookDto);
    int updateTimeslot(SpaceBookDto spaceBookDto);
    int updateTimeslotStatus(SpaceBookDto spaceBookDto);
    int deleteAll();
}
