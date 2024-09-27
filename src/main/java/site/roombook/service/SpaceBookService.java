package site.roombook.service;

import site.roombook.domain.EmplDto;
import site.roombook.domain.ServiceResult;
import site.roombook.domain.SpaceBookAndSpaceDto;
import site.roombook.domain.SpaceBookDto;

import java.time.LocalDate;
import java.util.List;

public interface SpaceBookService {

    List<SpaceBookDto> getBookedTimeslotsOfTheDay(int spaceNo, LocalDate date, String emplId);

    ServiceResult bookTimeslot(SpaceBookDto spaceBookDto, EmplDto emplDto);

    SpaceBookAndSpaceDto getTimeslot(String spaceBookId, String emplId);

    List<SpaceBookDto> getPersonalTimeslots(String emplId, int offset, int limit);

    int getPersonalTimeslotsCount(String emplId);

    ServiceResult modifyBooking(SpaceBookDto spaceBookDto, String bookId, EmplDto emplDto);

    ServiceResult cancelBooking(EmplDto emplDto, String bookId);
}
