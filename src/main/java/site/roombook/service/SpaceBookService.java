package site.roombook.service;

import site.roombook.domain.ServiceResult;
import site.roombook.domain.SpaceBookAndSpaceDto;
import site.roombook.domain.SpaceBookDto;

import java.util.List;

public interface SpaceBookService {

    List<SpaceBookDto> getBookedTimeslotsOfTheDay(int spaceNo, String date, String emplId);

    ServiceResult bookTimeslot(SpaceBookDto spaceBookDto, String emplId, String emplRole);

    SpaceBookAndSpaceDto getTimeslot(String spaceBookId, String emplId);

    List<SpaceBookDto> getPersonalTimeslots(String emplId, int offset, int limit);

    int getPersonalTimeslotsCount(String emplId);

    ServiceResult modifyBooking(SpaceBookDto spaceBookDto, String bookId, String emplId, String emplRole);

    ServiceResult cancelBooking(String emplId, String emplRole, String bookId);
}
