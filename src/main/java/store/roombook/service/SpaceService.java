package store.roombook.service;

import store.roombook.domain.PageHandler;
import store.roombook.domain.SpaceDto;
import store.roombook.domain.SpaceInfoAndTimeslotDto;

import store.roombook.domain.SpaceListDto;

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
