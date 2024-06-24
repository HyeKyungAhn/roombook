package site.roombook.service;

import site.roombook.CmnCode;
import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceRescFileDto;

import java.util.List;
import java.util.Map;

public interface SpaceService {

    boolean saveSpace(SpaceDto spaceDto, int spaceNo, String fstRegrIdnfNo);

    SpaceDto getOneSpace(int spaceNo);

    boolean updateSpace(int spaceNo, String lastUpdrIdnfNo, SpaceDto spaceDto);

    List<SpaceRescFileDto> getSpaceList(int spaceCnt, int offset, int rescCnt, CmnCode atchLocCd, boolean isHiddenSpaceInvisible);

    Map<String, Object> getOneSpaceAndDetails(int spaceNo, CmnCode atchLocCd, boolean isHiddenSpaceInvisible);

    int getSpaceAllCnt();

    int getNotHiddenSpaceCnt();
}
