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
                .spaceNm(spaceDto.getSPACE_NM())
                .spaceMaxPsonCnt(spaceDto.getSPACE_MAX_PSON_CNT())
                .spaceLocDesc(spaceDto.getSPACE_LOC_DESC())
                .spaceAdtnDesc(spaceDto.getSPACE_ADTN_DESC())
                .spaceMaxRsvdTms(spaceDto.getSPACE_MAX_RSVD_TMS())
                .spaceUsgPosblBgnTm(spaceDto.getSPACE_USG_POSBL_BGN_TM())
                .spaceUsgPosblEndTm(spaceDto.getSPACE_USG_POSBL_END_TM())
                .spaceWkendUsgPosblYn(spaceDto.getSPACE_WKEND_USG_POSBL_YN())
                .spaceHideYn(spaceDto.getSPACE_HIDE_YN())
                .fstRegrIdnfNo(fstRegrIdnfNo).build();

        return spaceDao.insertSpace(space)==1;
    }

    @Override
    public SpaceDto getOneSpace(int spaceNo) {
        return spaceDao.selectOne(spaceNo);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateSpace(int spaceNo, String lastUpdrIdnfNo, SpaceDto spaceDto){
        SpaceDto space = new SpaceDto.Builder().spaceNo(spaceNo)
                .spaceNm(spaceDto.getSPACE_NM())
                .spaceMaxPsonCnt(spaceDto.getSPACE_MAX_PSON_CNT())
                .spaceLocDesc(spaceDto.getSPACE_LOC_DESC())
                .spaceAdtnDesc(spaceDto.getSPACE_ADTN_DESC())
                .spaceMaxRsvdTms(spaceDto.getSPACE_MAX_RSVD_TMS())
                .spaceUsgPosblBgnTm(spaceDto.getSPACE_USG_POSBL_BGN_TM())
                .spaceUsgPosblEndTm(spaceDto.getSPACE_USG_POSBL_END_TM())
                .spaceWkendUsgPosblYn(spaceDto.getSPACE_WKEND_USG_POSBL_YN())
                .spaceHideYn(spaceDto.getSPACE_HIDE_YN())
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
            fileNmSet.add(detail.getFILE_NM());
            rescNoSet.add(detail.getRESC_NO());
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
                if (detail.getRESC_NO().equals(rescNo)) {
                    RescDto resc = RescDto.builder(detail.getRESC_NM()).
                            RESC_NO(detail.getRESC_NO()).build();
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
                if (detail.getFILE_NM().equals(fileNm)) {
                    FileDto file = FileDto.builder(fileNm).
                            FILE_ORGL_NM(detail.getFILE_ORGL_NM()).
                            FILE_TYP_NM(detail.getFILE_TYP_NM()).
                            FILE_SIZE(detail.getFILE_SIZE()).build();
                    files.add(file);
                    break;
                }
            }
        }
    }

    private SpaceDto setSpaceData(SpaceRescFileDto spaceInfo, int spaceNo, boolean isHiddenSpaceInvisible) {
        SpaceDto.Builder build = new SpaceDto.Builder()
                .spaceNo(spaceNo)
                .spaceNm(spaceInfo.getSPACE_NM())
                .spaceMaxPsonCnt(spaceInfo.getSPACE_MAX_PSON_CNT())
                .spaceLocDesc(spaceInfo.getSPACE_LOC_DESC())
                .spaceAdtnDesc(spaceInfo.getSPACE_ADTN_DESC())
                .spaceMaxRsvdTms(spaceInfo.getSPACE_MAX_RSVD_TMS())
                .spaceUsgPosblBgnTm(spaceInfo.getSPACE_USG_POSBL_BGN_TM())
                .spaceUsgPosblEndTm(spaceInfo.getSPACE_USG_POSBL_END_TM())
                .spaceWkendUsgPosblYn(spaceInfo.getSPACE_WKEND_USG_POSBL_YN())
                .spaceHideYn(spaceInfo.getSPACE_HIDE_YN());

        if(!isHiddenSpaceInvisible){
            build.spaceHideYn(spaceInfo.getSPACE_HIDE_YN());
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
