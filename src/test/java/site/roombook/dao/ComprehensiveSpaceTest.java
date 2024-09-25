package site.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.CmnCode;
import site.roombook.domain.FileDto;
import site.roombook.domain.RescDto;
import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceInfoAndTimeslotDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("공간")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class ComprehensiveSpaceTest {

    private final SpaceDao spaceDao;
    private final RescDao rescDao;
    private final SpaceRescDao spaceRescDao;
    private final FileDao fileDao;

    @Autowired
    public ComprehensiveSpaceTest(SpaceDao spaceDao, RescDao rescDao, SpaceRescDao spaceRescDao, FileDao fileDao){
        this.spaceDao = spaceDao;
        this.rescDao = rescDao;
        this.spaceRescDao = spaceRescDao;
        this.fileDao = fileDao;
    }

    protected SpaceDto spaceDto;
    protected List<SpaceDto> spaces;

    @BeforeEach
    void setUp(){
        spaceDao.deleteAll();
        rescDao.deleteAll();
        spaceRescDao.deleteAll();
        fileDao.deleteAll();
        spaces = createSpaceList();
    }

    @Nested
    @DisplayName("공간 목록 조회 시")
    class WhenSelectSpaceListTest{
        @Test
        @Transactional
        @DisplayName("공간 물품 없는 공간 목록 관리자 조회 테스트")
        void selectAdminSpaceWithNoRescTest(){
            //insert space
            spaces.forEach(space -> assertEquals(1, spaceDao.insertSpace(space)));

            //insert files
            List<FileDto> fileOfSpace1 = createFiles(spaces.get(0).getSpaceNo());
            List<FileDto> fileOfSpace2 = createFiles(spaces.get(1).getSpaceNo());
            List<FileDto> fileOfSpace3 = createFiles(spaces.get(2).getSpaceNo());

            assertEquals(fileOfSpace1.size(), fileDao.insertFiles(fileOfSpace1));
            assertEquals(fileOfSpace2.size(), fileDao.insertFiles(fileOfSpace2));
            assertEquals(fileOfSpace3.size(), fileDao.insertFiles(fileOfSpace3));

            //select spaceList
            SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto = SpaceInfoAndTimeslotDto.SpaceRescFileDtoBuilder()
                    .spaceCnt(5)
                    .offset(0)
                    .rescCnt(3)
                    .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                    .isHiddenSpaceInvisible(false)
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .spaceBookDate(LocalDate.now())
                    .build();

            assertEquals(3, spaceDao.selectSpaceList(spaceInfoAndTimeslotDto).size());
        }

        @Test
        @Transactional
        @DisplayName("공간 물품만 있고 파일은 없는 공간 2개와 둘 다 없는 공간 1개 관리자 조회 테스트")
        void selectAdminSpaceWithRescAndNoFileTest(){
            //insert space
            spaces.forEach(space -> assertEquals(1, spaceDao.insertSpace(space)));

            //insert resc
            List<RescDto> resc = createRescDtos(0);
            List<RescDto> rescOfSpace1 = createRescDtos(spaces.get(0).getSpaceNo());
            List<RescDto> rescOfSpace2 = createRescDtos(spaces.get(1).getSpaceNo());

            assertEquals(resc.size(), rescDao.insertRescs(resc));

            //insert space-resc
            assertEquals(rescOfSpace1.size(), spaceRescDao.insertSpaceRescs(rescOfSpace1));
            assertEquals(rescOfSpace2.size(), spaceRescDao.insertSpaceRescs(rescOfSpace2));

            //select spaceList
            SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto = SpaceInfoAndTimeslotDto.SpaceRescFileDtoBuilder()
                    .spaceCnt(5)
                    .offset(0)
                    .rescCnt(3)
                    .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                    .isHiddenSpaceInvisible(false)
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .spaceBookDate(LocalDate.now())
                    .build();

            int spaceCnt = spaces.size();
            int spaceWithNoResc = spaceCnt - 2;
            int rescCnt = countResc(rescOfSpace1) + countResc(rescOfSpace2);

            assertEquals(rescCnt + spaceWithNoResc, spaceDao.selectSpaceList(spaceInfoAndTimeslotDto).size());
        }

        @Test
        @Transactional
        @DisplayName("공간 물품과 파일 정보가 있는 공간 3개 관리자 조회 테스트")
        void selectAdminSpaceWithRescAndFileTest(){
            //insert space
            spaces.forEach(space -> assertEquals(1, spaceDao.insertSpace(space)));

            //insert resc
            List<RescDto> resc = createRescDtos(0);

            assertEquals(resc.size(), rescDao.insertRescs(resc));

            //insert space-resc
            List<RescDto> rescOfSpace1 = createRescDtos(spaces.get(0).getSpaceNo());
            List<RescDto> rescOfSpace2 = createRescDtos(spaces.get(1).getSpaceNo());
            List<RescDto> rescOfSpace3 = createRescDtos(spaces.get(2).getSpaceNo());

            assertEquals(rescOfSpace1.size(), spaceRescDao.insertSpaceRescs(rescOfSpace1));
            assertEquals(rescOfSpace2.size(), spaceRescDao.insertSpaceRescs(rescOfSpace2));
            assertEquals(rescOfSpace3.size(), spaceRescDao.insertSpaceRescs(rescOfSpace3));

            //insert files
            List<FileDto> fileOfSpace1 = createFiles(spaces.get(0).getSpaceNo());
            List<FileDto> fileOfSpace2 = createFiles(spaces.get(1).getSpaceNo());
            List<FileDto> fileOfSpace3 = createFiles(spaces.get(2).getSpaceNo());

            assertEquals(fileOfSpace1.size(), fileDao.insertFiles(fileOfSpace1));
            assertEquals(fileOfSpace2.size(), fileDao.insertFiles(fileOfSpace2));
            assertEquals(fileOfSpace3.size(), fileDao.insertFiles(fileOfSpace3));

            //select spaceList
            SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto = SpaceInfoAndTimeslotDto.SpaceRescFileDtoBuilder()
                    .spaceCnt(5)
                    .offset(0)
                    .rescCnt(3)
                    .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                    .isHiddenSpaceInvisible(false)
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .spaceBookDate(LocalDate.now())
                    .build();

            int spaceCnt = spaces.size();
            int spaceWithNoResc = spaceCnt - 3;
            int rescCnt = countResc(rescOfSpace1) + countResc(rescOfSpace2) + countResc(rescOfSpace3);

            assertEquals(rescCnt + spaceWithNoResc, spaceDao.selectSpaceList(spaceInfoAndTimeslotDto).size());
        }

        @Nested
        @DisplayName("한 공간과 관련된 물품, 파일 정보")
        class SelectOneSpaceAndRescAndFileTest {

            @BeforeEach
            void insertSpaceAndRescAndFiles(){
                //insert space
                spaces.forEach(space -> assertEquals(1, spaceDao.insertSpace(space)));

                //insert resc
                List<RescDto> resc = createRescDtos(0);

                assertEquals(resc.size(), rescDao.insertRescs(resc));

                //insert space rescs
                List<RescDto> rescOfSpace1 = createRescDtos(spaces.get(0).getSpaceNo());
                List<RescDto> rescOfSpace2 = createRescDtos(spaces.get(1).getSpaceNo());
                List<RescDto> rescOfSpace3 = createRescDtos(spaces.get(2).getSpaceNo());

                assertEquals(rescOfSpace1.size(), spaceRescDao.insertSpaceRescs(rescOfSpace1));
                assertEquals(rescOfSpace2.size(), spaceRescDao.insertSpaceRescs(rescOfSpace2));
                assertEquals(rescOfSpace3.size(), spaceRescDao.insertSpaceRescs(rescOfSpace3));

                List<FileDto> fileOfSpace1 = createFiles(spaces.get(0).getSpaceNo());
                List<FileDto> fileOfSpace2 = createFiles(spaces.get(1).getSpaceNo());
                List<FileDto> fileOfSpace3 = createFiles(spaces.get(2).getSpaceNo());

                assertEquals(fileOfSpace1.size(), fileDao.insertFiles(fileOfSpace1));
                assertEquals(fileOfSpace2.size(), fileDao.insertFiles(fileOfSpace2));
                assertEquals(fileOfSpace3.size(), fileDao.insertFiles(fileOfSpace3));
            }

            @Test
            @Transactional
            @DisplayName("숨겨진 공간 조회 성공 테스트")
            void selectHiddenPlaceSuccessTest(){
                Map<String, Object> map = new HashMap<>();
                map.put("spaceNo", spaces.get(0).getSpaceNo());                //숨겨진 공간 번호
                map.put("atchLocCd", CmnCode.ATCH_LOC_CD_SPACE.getCode());      //파일 첨부 위치 유형
                map.put("isHiddenSpaceInvisible", false);                       //숨겨진 공간 비조회 여부

                List<SpaceInfoAndTimeslotDto> space = spaceDao.selectOneSpaceAndRescAndFIle(map);

                int fileCnt = 2, rescCnt = 4;
                assertEquals(fileCnt*rescCnt, space.size());
            }

            @Test
            @Transactional
            @DisplayName("숨겨진 공간 조회 불가 테스트")
            void selectHiddenPlaceFailTest(){
                Map<String, Object> map = new HashMap<>();
                map.put("spaceNo", spaces.get(0).getSpaceNo());                //숨겨진 공간 번호
                map.put("atchLocCd", CmnCode.ATCH_LOC_CD_SPACE.getCode());      //파일 첨부 위치 유형
                map.put("isHiddenSpaceInvisible", true);                       //숨겨진 공간 비조회 여부

                List<SpaceInfoAndTimeslotDto> space = spaceDao.selectOneSpaceAndRescAndFIle(map);

                assertEquals(0, space.size());
            }
        }

        private int countResc(List<RescDto> list){
            return Math.min(list.size(), 3);
        }
    }

    List<SpaceDto> createSpaceList(){
        return List.of(
                new SpaceDto.Builder().spaceNo(1).spaceNm("회의실A").spaceMaxPsonCnt(10).spaceLocDesc("1층 정수기 옆")
                        .spaceAdtnDesc("1").spaceMaxRsvdTms(5).spaceUsgPosblBgnTm(LocalTime.of(9,30))
                        .spaceUsgPosblEndTm(LocalTime.of(12,0)).spaceWkendUsgPosblYn('N')
                        .spaceHideYn('Y').fstRegDtm(LocalDateTime.now()).fstRegrIdnfNo("admin").build(),
                new SpaceDto.Builder().spaceNo(2).spaceNm("회의실B").spaceMaxPsonCnt(10).spaceLocDesc("2층 정수기 옆")
                        .spaceAdtnDesc("2").spaceMaxRsvdTms(5).spaceUsgPosblBgnTm(LocalTime.of(9,30))
                        .spaceUsgPosblEndTm(LocalTime.of(12,0)).spaceWkendUsgPosblYn('N')
                        .spaceHideYn('N').fstRegDtm(LocalDateTime.now()).fstRegrIdnfNo("admin").build(),
                new SpaceDto.Builder().spaceNo(3).spaceNm("회의실C").spaceMaxPsonCnt(10).spaceLocDesc("3층 정수기 옆")
                        .spaceAdtnDesc("3").spaceMaxRsvdTms(5).spaceUsgPosblBgnTm(LocalTime.of(9,30))
                        .spaceUsgPosblEndTm(LocalTime.of(12,0)).spaceWkendUsgPosblYn('N')
                        .spaceHideYn('N').fstRegDtm(LocalDateTime.now()).fstRegrIdnfNo("admin").build()
        );
    }

    List<FileDto> createFiles(int spaceNo){
        return List.of(
                FileDto.builder("파일이름"+spaceNo).atchLocNo(spaceNo).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).fileOrglNm("진짜이름")
                        .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).fstRegrIdnfNo("admin").build(),
                FileDto.builder("파일이름"+spaceNo).atchLocNo(spaceNo).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).fileOrglNm("진짜이름")
                        .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).fstRegrIdnfNo("admin").build());
    }

    List<RescDto> createRescDtos(int spaceNo){
        return List.of(
                RescDto.builder("wifi").spaceNo(spaceNo).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build(),
                RescDto.builder("에어컨").spaceNo(spaceNo).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build(),
                RescDto.builder("화이트보드").spaceNo(spaceNo).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build(),
                RescDto.builder("모니터").spaceNo(spaceNo).fstRegrIdnfNo("admin").lastUpdrIdnfNo("admin").build()
        );
    }
}
