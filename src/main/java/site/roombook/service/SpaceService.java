package site.roombook.service;

import site.roombook.domain.PageHandler;
import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceInfoAndTimeslotDto;

import site.roombook.domain.SpaceListDto;

public interface SpaceService {

    boolean saveSpace(SpaceDto spaceDto, int spaceNo, String fstRegrIdnfNo);

    SpaceDto getOneSpace(int spaceNo);

    SpaceDto getSpaceDataForBooking(int spaceNo);

    boolean updateSpace(int spaceNo, String lastUpdrIdnfNo, SpaceDto spaceDto);

    SpaceListDto getSpaceList(PageHandler ph, SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto);

    SpaceDto getOneSpaceAndDetails(int spaceNo, boolean isHiddenSpaceInvisible);

    int getSpaceAllCnt();

    int getNotHiddenSpaceCnt();
}
