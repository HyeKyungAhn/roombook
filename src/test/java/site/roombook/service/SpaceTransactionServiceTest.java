package site.roombook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import site.roombook.domain.RescDto;
import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceTransactionServiceResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
public class SpaceTransactionServiceTest {

    @Autowired
    @InjectMocks
    SpaceTransactionService sts = new SpaceTransactionServiceImpl();

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    SpaceService spaceService;
    @Mock
    SpaceDto spaceDto;
    @Mock
    List<RescDto> rescs;
    @Mock
    RescService rescService;

    @Nested
    class SaveTest{

        @Nested
        @DisplayName("파일 입력값")
        class FileServiceTest{

            @BeforeEach
            void setUp(){
                MockitoAnnotations.openMocks(this);
                when(spaceService.saveSpace(any(SpaceDto.class), anyInt(), anyString())).thenReturn(true);
            }

            @Test
            @DisplayName("null일 때")
            void fileArrayNullTest() throws IOException {
                SpaceTransactionServiceResult stsr = sts.saveSpace(spaceDto, null, "fstRegrIdnfNo", rescs);
                assertTrue(stsr.isSpaceSaved());
                assertTrue(stsr.getFileSaveResult().isSaved());
            }

            @Test
            @DisplayName("빈 배열일 때")
            void fileEmptyArrayTest() throws IOException {
                SpaceTransactionServiceResult stsr = sts.saveSpace(spaceDto, new MultipartFile[]{}, "fstRegrIdnfNo", rescs);
                assertTrue(stsr.isSpaceSaved());
                assertTrue(stsr.getFileSaveResult().isSaved());
            }
        }

        @Nested
        @DisplayName("파일 저장 시")
        class SpaceServiceTest{

            @BeforeEach
            void setUp() {
                MockitoAnnotations.openMocks(this);
                when(spaceService.saveSpace(any(SpaceDto.class), anyInt(), anyString())).thenReturn(false);
            }

            @Test
            @DisplayName("실패")
            void fail() throws IOException {
                SpaceTransactionServiceResult stsr = sts.saveSpace(spaceDto, new MultipartFile[]{}, "fstRegrIdnfNo", rescs);
                assertFalse(stsr.isSpaceSaved());
            }
        }
    }

    @Nested
    @DisplayName("수정")
    class SpaceModificationTest{
        @Nested
        @DisplayName("공간")
        class SpaceServiceTest{
            @BeforeEach
            void setUp(){
                MockitoAnnotations.openMocks(this);
            }

            @Test
            @DisplayName("수정 실패")
            void failTest() throws IOException {
                when(spaceService.updateSpace(anyInt(), anyString(), any(SpaceDto.class))).thenReturn(false);
                SpaceTransactionServiceResult stsr = sts.modifySpace(1234, "admin", spaceDto, new MultipartFile[]{}, new ArrayList<>(), rescs);
                assertFalse(stsr.isSpaceSaved());
            }

            @Test
            @DisplayName("수정 성공")
            void successTest() throws IOException {
                doNothing().when(rescService).updateRescs(anyInt(), anyString(), any());
                when(spaceService.updateSpace(anyInt(), anyString(), any(SpaceDto.class))).thenReturn(true);

                SpaceTransactionServiceResult stsr = sts.modifySpace(1234, "admin", spaceDto, new MultipartFile[]{}, new ArrayList<>(), rescs);
                assertTrue(stsr.isSpaceSaved());
            }
        }
    }




//    @Autowired
//    SpaceTransactionService sts;
//
//    @Autowired
//    SpaceDao spaceDao;
//
//    @Autowired
//    RescDao rescDao;
//
//    @Autowired
//    SpaceRescDao spaceRescDao;
//
//    @Autowired
//    FileDao fileDao;
//
//
//
//    @Nested
//    @DisplayName("공간 삽입 시")
//    class SpaceSaveTest{
//
//        @BeforeEach
//        void setUp(){
//            spaceDao.deleteAll();
//            rescDao.deleteAll();
//            spaceRescDao.deleteAll();
//            fileDao.deleteAll();
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("정상 작동 트랜잭션 테스트")
//        void saveSpaceTest() {
//            SpaceDto spaceDto = getSpace("회의실입니다");
//            try{
//                sts.saveSpace(spaceDto, null, "admin", new ArrayList<>());
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//
//            List<SpaceDto> list = spaceDao.selectAllSpace();
//            assertEquals(1, list.size());
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("같은 이름을 가진 공간 삽입 트랜잭션 테스트")
//        void saveSameNameSpaceTest() {
//            SpaceDto spaceDto = getSpace("회의실입니다");
//            try{
//                sts.saveSpace(spaceDto, null, "admin", new ArrayList<>());
//                sts.saveSpace(spaceDto, null, "admin", new ArrayList<>());
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//
//            List<SpaceDto> list = spaceDao.selectAllSpace();
//            assertEquals(1, list.size());
//        }
//    }
//
//    @Nested
//    @DisplayName("공간 수정 시")
//    class SpaceModificationTest {
//        SpaceDto originalSpace;
//        int spaceNo;
//
//        @BeforeEach
//        void setUp(){
//            originalSpace = getSpace("새회의실22");
//
//            try {
//                sts.saveSpace(originalSpace, null, "admin", new ArrayList<>());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            List<SpaceDto> list = spaceDao.selectAllSpace();
//            for (SpaceDto space : list) {
//                if (Objects.equals(space.getSPACE_NM(), originalSpace.getSPACE_NM())) {
//                    spaceNo = space.getSPACE_NO();
//                }
//            }
//        }
//
//        @Test
//        @DisplayName("space 필수 값 없어서 실패")
//        void modifySpaceWithNoRescsTest(){
//            SpaceDto UpdatedSpace = new SpaceDto.Builder().spaceNm("다른이름 회의실").build();
//
//            assertThrows(BadSqlGrammarException.class, () -> {
//                sts.modifySpace(spaceNo, "admin", UpdatedSpace, null, new ArrayList<>(), new ArrayList<>());
//            });
//
//            SpaceDto updatedSpace = spaceDao.selectOne(spaceNo);
//
//            assertEquals(originalSpace.getSPACE_NM(), updatedSpace.getSPACE_NM());
//            spaceDao.deleteWithSpaceNo(spaceNo);
//        }
//    }
//
//    private SpaceDto getSpace(String name){
//        return new SpaceDto.Builder()
//                .spaceNm(name)
//                .spaceMaxPsonCnt(10)
//                .spaceLocDesc("2층 정수기 옆")
//                .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
//                .spaceMaxRsvdTms(5)
//                .spaceUsgPosblBgnTm(LocalTime.of(9,0))
//                .spaceUsgPosblEndTm(LocalTime.of(12,0))
//                .spaceWkendUsgPosblYn('N')
//                .spaceHideYn('N')
//                .fstRegDtm(LocalDateTime.now())
//                .fstRegrIdnfNo("admin").build();
//    }
}
