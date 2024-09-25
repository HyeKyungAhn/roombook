package site.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.CmnCode;
import site.roombook.FileStorageProperties;
import site.roombook.dao.SpaceDao;
import site.roombook.domain.*;

import java.util.*;

@Service
public class SpaceServiceImpl implements SpaceService {

    @Autowired
    SpaceDao spaceDao;

    @Autowired
    FileStorageProperties properties;

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
    public SpaceListDto getSpaceList(PageHandler ph, SpaceInfoAndTimeslotDto dataForSelection) {
        List<SpaceInfoAndTimeslotDto> SpaceInfoAndTimeslotDtos = spaceDao.selectSpaceList(dataForSelection);
        Set<SpaceDto> spaces = new HashSet<>();
        Set<RescDto> resources = new HashSet<>();
        Set<FileDto> files = new HashSet<>();
        Set<SpaceBookDto> bookings = new HashSet<>();

        for (SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto : SpaceInfoAndTimeslotDtos) {
            SpaceDto spaceDto = new SpaceDto.Builder()
                    .spaceNo(spaceInfoAndTimeslotDto.getSpaceNo())
                    .spaceNm(spaceInfoAndTimeslotDto.getSpaceNm())
                    .spaceMaxPsonCnt(spaceInfoAndTimeslotDto.getSpaceMaxPsonCnt())
                    .spaceLocDesc(spaceInfoAndTimeslotDto.getSpaceLocDesc())
                    .spaceAdtnDesc(spaceInfoAndTimeslotDto.getSpaceAdtnDesc())
                    .spaceMaxRsvdTms(spaceInfoAndTimeslotDto.getSpaceMaxRsvdTms())
                    .spaceUsgPosblBgnTm(spaceInfoAndTimeslotDto.getSpaceUsgPosblBgnTm())
                    .spaceUsgPosblEndTm(spaceInfoAndTimeslotDto.getSpaceUsgPosblEndTm())
                    .spaceWkendUsgPosblYn(spaceInfoAndTimeslotDto.getSpaceWkendUsgPosblYn())
                    .spaceHideYn(spaceInfoAndTimeslotDto.getSpaceHideYn())
                    .build();

            FileDto fileDto = FileDto.FileDtoBuilder()
                    .fileNo(spaceInfoAndTimeslotDto.getFileNo())
                    .atchLocNo(spaceInfoAndTimeslotDto.getSpaceNo())
                    .atchLocCd(spaceInfoAndTimeslotDto.getAtchLocCd())
                    .fileNm(spaceInfoAndTimeslotDto.getFileNm())
                    .fileOrglNm(spaceInfoAndTimeslotDto.getFileOrglNm())
                    .fileTypNm(spaceInfoAndTimeslotDto.getFileTypNm())
                    .fileSize(spaceInfoAndTimeslotDto.getFileSize())
                    .build();

            RescDto rescDto = RescDto.RescDtoBuilder()
                    .rescNo(spaceInfoAndTimeslotDto.getRescNo())
                    .rescNm(spaceInfoAndTimeslotDto.getRescNm())
                    .spaceNo(spaceInfoAndTimeslotDto.getSpaceNo())
                    .build();

            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(spaceInfoAndTimeslotDto.getSpaceBookId())
                    .spaceBookSpaceNo(spaceInfoAndTimeslotDto.getSpaceNo())
                    .spaceBookDate(spaceInfoAndTimeslotDto.getSpaceBookDate())
                    .spaceBookBgnTm(spaceInfoAndTimeslotDto.getSpaceBookBgnTm())
                    .spaceBookEndTm(spaceInfoAndTimeslotDto.getSpaceBookEndTm())
                    .spaceBookStusCd(spaceInfoAndTimeslotDto.getSpaceBookStusCd())
                    .build();

            spaces.add(spaceDto);
            files.add(fileDto);

            if (!isResourceEmpty(rescDto)) {
                resources.add(rescDto);
            }

            if (!isSpaceBookEmpty(spaceBookDto)) {
                bookings.add(spaceBookDto);
            }
        }

        return SpaceListDto.SpaceListDto()
                .bookings(bookings)
                .spaces(spaces)
                .files(files)
                .resources(resources)
                .thumbnailPath(properties.getThumbnailUploadPath())
                .noImgPath("/img/noImg.png")
                .pageHandler(ph)
                .build();
    }

    @Override
    public SpaceDto getOneSpaceAndDetails(int spaceNo, boolean isHiddenSpaceInvisible) {
        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("spaceNo", spaceNo);
        spaceData.put("atchLocCd", CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("isHiddenSpaceInvisible", isHiddenSpaceInvisible);

        List<SpaceInfoAndTimeslotDto> spaceDetails = spaceDao.selectOneSpaceAndRescAndFIle(spaceData);

        if(spaceDetails.isEmpty()){
            return new SpaceDto.Builder().build();
        }

        //file & resc
        Set<String> fileNmSet = new HashSet<>();
        Set<Integer> rescNoSet = new HashSet<>();

        List<FileDto> files = new ArrayList<>();
        List<RescDto> rescs = new ArrayList<>();

        for (SpaceInfoAndTimeslotDto detail : spaceDetails) {
            fileNmSet.add(detail.getFileNm());
            rescNoSet.add(detail.getRescNo());
        }

        filterFileData(fileNmSet, files, spaceDetails);
        filterRescData(rescNoSet, rescs, spaceDetails);

        SpaceDto.Builder build = new SpaceDto.Builder()
                .spaceNo(spaceNo)
                .spaceNm(spaceDetails.get(0).getSpaceNm())
                .spaceMaxPsonCnt(spaceDetails.get(0).getSpaceMaxPsonCnt())
                .spaceLocDesc(spaceDetails.get(0).getSpaceLocDesc())
                .spaceAdtnDesc(spaceDetails.get(0).getSpaceAdtnDesc())
                .spaceMaxRsvdTms(spaceDetails.get(0).getSpaceMaxRsvdTms())
                .spaceUsgPosblBgnTm(spaceDetails.get(0).getSpaceUsgPosblBgnTm())
                .spaceUsgPosblEndTm(spaceDetails.get(0).getSpaceUsgPosblEndTm())
                .spaceWkendUsgPosblYn(spaceDetails.get(0).getSpaceWkendUsgPosblYn())
                .files(files)
                .resources(rescs);

        if(!isHiddenSpaceInvisible){
            build.spaceHideYn(spaceDetails.get(0).getSpaceHideYn());
        }

        return build.build();
    }

    private void filterRescData(Set<Integer> rescNoSet, List<RescDto> rescs, List<SpaceInfoAndTimeslotDto> spaceDetails) {
        if(rescNoSet.size()==1&&rescNoSet.contains(null)) return;

        Iterator<Integer> rescIterator = rescNoSet.stream().iterator();

        while (rescIterator.hasNext()) {
            Integer rescNo = rescIterator.next();

            for (SpaceInfoAndTimeslotDto detail : spaceDetails) {
                if (detail.getRescNo().equals(rescNo)) {
                    RescDto resc = RescDto.builder(detail.getRescNm()).
                            rescNo(detail.getRescNo()).build();
                    rescs.add(resc);
                    break;
                }
            }
        }

    }

    private void filterFileData(Set<String> fileNmSet, List<FileDto> files, List<SpaceInfoAndTimeslotDto> spaceDetails) {
        if(fileNmSet.size()==1&&fileNmSet.contains(null)) return;

        Iterator<String> fileIterator = fileNmSet.stream().iterator();

        while (fileIterator.hasNext()) {
            String fileNm = fileIterator.next();

            for (SpaceInfoAndTimeslotDto detail : spaceDetails) {
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

    @Override
    public int getSpaceAllCnt() {
        return spaceDao.selectAllCnt();
    }

    @Override
    public int getNotHiddenSpaceCnt() {
        return spaceDao.selectCntAllNotHiddenSpace();
    }

    private boolean isSpaceBookEmpty(SpaceBookDto spaceBookDto) {
        return spaceBookDto.getSpaceBookId() == null;
    }

    private boolean isResourceEmpty(RescDto rescDto) {
        return rescDto.getRescNo() == null;
    }
}
