package site.roombook.service;

import site.roombook.domain.PageHandler;
import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceInfoAndTimeslotDto;

import site.roombook.domain.SpaceListDto;

import java.util.List;

public interface SpaceService {

    boolean saveSpace(SpaceDto spaceDto, int spaceNo, String emplId);

    SpaceDto getOneSpace(int spaceNo);

    SpaceDto getSpaceDataForBooking(int spaceNo);

    boolean updateSpace(int spaceNo, String lastUpdrIdnfNo, SpaceDto spaceDto);

    SpaceListDto getSpaceList(PageHandler ph, SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto);

    List<SpaceInfoAndTimeslotDto> getSpaceList(SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto);

    SpaceDto getOneSpaceAndDetails(int spaceNo, boolean isHiddenSpaceInvisible);

    int getSpaceAllCnt();

    int getNotHiddenSpaceCnt();
}
