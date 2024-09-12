package site.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.CmnCode;
import site.roombook.dao.SpaceDao;
import site.roombook.domain.FileDto;
import site.roombook.domain.RescDto;
import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceRescFileDto;

import java.util.*;

@Service
public class SpaceServiceImpl implements SpaceService {

    @Autowired
    SpaceDao spaceDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = DuplicateKeyException.class)
    public boolean saveSpace(SpaceDto spaceDto, int spaceNo, String fstRegrIdnfNo) {
        SpaceDto space = new SpaceDto.Builder().spaceNo(spaceNo)
                .spaceNm(spaceDto.getSpaceNm())
                .spaceMaxPsonCnt(spaceDto.getSpaceMaxPsonCnt())
                .spaceLocDesc(spaceDto.getSpaceLocDesc())
                .spaceAdtnDesc(spaceDto.getSpaceAdtnDesc())
                .spaceMaxRsvdTms(spaceDto.getSpaceMaxRsvdTms())
                .spaceUsgPosblBgnTm(spaceDto.getSpaceUsgPosblBgnTm())
                .spaceUsgPosblEndTm(spaceDto.getSpaceUsgPosblEndTm())
                .spaceWkendUsgPosblYn(spaceDto.getSpaceWkendUsgPosblYn())
                .spaceHideYn(spaceDto.getSpaceHideYn())
                .fstRegrIdnfNo(fstRegrIdnfNo).build();

        return spaceDao.insertSpace(space)==1;
    }

    @Override
    public SpaceDto getOneSpace(int spaceNo) {
        return spaceDao.selectOne(spaceNo);
    }

    @Override
    public SpaceDto getSpaceDataForBooking(int spaceNo) {
        SpaceDto selectedSpace = spaceDao.selectOne(spaceNo);
        if (selectedSpace == null) {
            return null;
        }
        return new SpaceDto.Builder()
                .spaceNo(selectedSpace.getSpaceNo())
                .spaceNm(selectedSpace.getSpaceNm())
                .spaceMaxPsonCnt(selectedSpace.getSpaceMaxPsonCnt())
                .spaceMaxRsvdTms(selectedSpace.getSpaceMaxRsvdTms())
                .spaceWkendUsgPosblYn(selectedSpace.getSpaceWkendUsgPosblYn())
                .spaceUsgPosblBgnTm(selectedSpace.getSpaceUsgPosblBgnTm())
                .spaceUsgPosblEndTm(selectedSpace.getSpaceUsgPosblEndTm())
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateSpace(int spaceNo, String lastUpdrIdnfNo, SpaceDto spaceDto){
        SpaceDto space = new SpaceDto.Builder().spaceNo(spaceNo)
                .spaceNm(spaceDto.getSpaceNm())
                .spaceMaxPsonCnt(spaceDto.getSpaceMaxPsonCnt())
                .spaceLocDesc(spaceDto.getSpaceLocDesc())
                .spaceAdtnDesc(spaceDto.getSpaceAdtnDesc())
                .spaceMaxRsvdTms(spaceDto.getSpaceMaxRsvdTms())
                .spaceUsgPosblBgnTm(spaceDto.getSpaceUsgPosblBgnTm())
                .spaceUsgPosblEndTm(spaceDto.getSpaceUsgPosblEndTm())
                .spaceWkendUsgPosblYn(spaceDto.getSpaceWkendUsgPosblYn())
                .spaceHideYn(spaceDto.getSpaceHideYn())
                .lastUpdrIdnfNo(lastUpdrIdnfNo).build();

        return spaceDao.update(space) == 1;
    }

    @Override
    public List<SpaceRescFileDto> getSpaceList(int spaceCnt, int offset, int rescCnt, CmnCode atchLocCd, boolean isHiddenSpaceInvisible) {
        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("spaceCnt", spaceCnt);
        spaceData.put("offset", offset);
        spaceData.put("rescCnt", rescCnt);
        spaceData.put("atchLocCd", atchLocCd.getCode());
        spaceData.put("isHiddenSpaceInvisible", isHiddenSpaceInvisible);

        return spaceDao.selectSpaceList(spaceData);
    }

    @Override
    public Map<String, Object> getOneSpaceAndDetails(int spaceNo, CmnCode atchLocCd, boolean isHiddenSpaceInvisible) {
        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("spaceNo", spaceNo);
        spaceData.put("atchLocCd", CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("isHiddenSpaceInvisible", isHiddenSpaceInvisible);

        List<SpaceRescFileDto> spaceDetails = spaceDao.selectOneSpaceAndRescAndFIle(spaceData);

        if(spaceDetails.isEmpty()){
            return Collections.emptyMap();
        }

        SpaceDto space = setSpaceData(spaceDetails.get(0), spaceNo, isHiddenSpaceInvisible);

        //file & resc
        Set<String> fileNmSet = new HashSet<>();
        Set<Integer> rescNoSet = new HashSet<>();

        List<FileDto> files = new ArrayList<>();
        List<RescDto> rescs = new ArrayList<>();

        for (SpaceRescFileDto detail : spaceDetails) {
            fileNmSet.add(detail.getFileNm());
            rescNoSet.add(detail.getRescNo());
        }

        filterFileData(fileNmSet, files, spaceDetails);
        filterRescData(rescNoSet, rescs, spaceDetails);

        Map<String, Object> result = new HashMap<>();
        result.put("space", space);
        result.put("files", files);
        result.put("rescs", rescs);

        return result;
    }

    private void filterRescData(Set<Integer> rescNoSet, List<RescDto> rescs, List<SpaceRescFileDto> spaceDetails) {
        if(rescNoSet.size()==1&&rescNoSet.contains(null)) return;

        Iterator<Integer> rescIterator = rescNoSet.stream().iterator();

        while (rescIterator.hasNext()) {
            Integer rescNo = rescIterator.next();

            for (SpaceRescFileDto detail : spaceDetails) {
                if (detail.getRescNo().equals(rescNo)) {
                    RescDto resc = RescDto.builder(detail.getRescNm()).
                            rescNo(detail.getRescNo()).build();
                    rescs.add(resc);
                    break;
                }
            }
        }

    }

    private void filterFileData(Set<String> fileNmSet, List<FileDto> files, List<SpaceRescFileDto> spaceDetails) {
        if(fileNmSet.size()==1&&fileNmSet.contains(null)) return;

        Iterator<String> fileIterator = fileNmSet.stream().iterator();

        while (fileIterator.hasNext()) {
            String fileNm = fileIterator.next();

            for (SpaceRescFileDto detail : spaceDetails) {
                if (detail.getFileNm().equals(fileNm)) {
                    FileDto file = FileDto.builder(fileNm).
                            fileOrglNm(detail.getFileOrglNm()).
                            fileTypNm(detail.getFileTypNm()).
                            fileSize(detail.getFileSize()).build();
                    files.add(file);
                    break;
                }
            }
        }
    }

    private SpaceDto setSpaceData(SpaceRescFileDto spaceInfo, int spaceNo, boolean isHiddenSpaceInvisible) {
        SpaceDto.Builder build = new SpaceDto.Builder()
                .spaceNo(spaceNo)
                .spaceNm(spaceInfo.getSpaceNm())
                .spaceMaxPsonCnt(spaceInfo.getSpaceMaxPsonCnt())
                .spaceLocDesc(spaceInfo.getSpaceLocDesc())
                .spaceAdtnDesc(spaceInfo.getSpaceAdtnDesc())
                .spaceMaxRsvdTms(spaceInfo.getSpaceMaxRsvdTms())
                .spaceUsgPosblBgnTm(spaceInfo.getSpaceUsgPosblBgnTm())
                .spaceUsgPosblEndTm(spaceInfo.getSpaceUsgPosblEndTm())
                .spaceWkendUsgPosblYn(spaceInfo.getSpaceWkendUsgPosblYn())
                .spaceHideYn(spaceInfo.getSpaceHideYn());

        if(!isHiddenSpaceInvisible){
            build.spaceHideYn(spaceInfo.getSpaceHideYn());
        }

        return build.build();
    }

    @Override
    public int getSpaceAllCnt() {
        return spaceDao.selectAllCnt();
    }

    @Override
    public int getNotHiddenSpaceCnt() {
        return spaceDao.selectCntAllNotHiddenSpace();
    }
}
