package store.roombook.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import store.roombook.CmnCode;
import store.roombook.domain.EmplDto;
import store.roombook.domain.FileDto;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class FileDaoImplTest {

    @Autowired
    private FileDao fileDao;

    @Autowired
    private EmplDao emplDao;

    private EmplDto dummyEmpl;

    @BeforeEach
    void setup() {
        fileDao.deleteAll();

        dummyEmpl = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("dummyEmpl").pwd("password").email("dummyEmpl@asdf.com")
                .pwdErrTms(0).rnm("dummyEmpl").engNm("dummyEmpl").entDt("2024-01-01").emplAuthNm("ROLE_SUPER_ADMIN").brdt("2000-01-01")
                .wncomTelno("1111111").empno(747586).msgrId(null).prfPhotoPath(null)
                .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();

        assertEquals(1, emplDao.insertEmpl(dummyEmpl));
    }

    @Test
    @Transactional
    @DisplayName("파일 정보 저장 테스트")
    void insertFilesTest(){

        FileDto fileDto1 = FileDto.builder("파일이름1").atchLocNo(1).atchLocCd("asdf").fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();
        FileDto fileDto2 = FileDto.builder("파일이름2").atchLocNo(1).atchLocCd("asdf").fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));
    }

    @Test
    @Transactional
    @DisplayName("빈 값 저장 테스트")
    void insertEmptyListTest(){
        fileDao.deleteAll();

        List<FileDto> list = new ArrayList<>();

        Exception exception = assertThrows(MyBatisSystemException.class, () -> fileDao.insertFiles(list));
        assertTrue(exception.getMessage().contains("Index 0 out of bounds for length 0"));
    }

    @Test
    @Transactional
    @DisplayName("빈 값 저장 테스트")
    void insertNullTest(){
        fileDao.deleteAll();

        Exception exception = assertThrows(MyBatisSystemException.class, () -> fileDao.insertFiles(null));

        assertTrue(exception.getMessage().contains("null value"));
    }

    @Test
    @Transactional
    @DisplayName("파일 갯수 조회 테스트")
    void selectAllFilesCntTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").atchLocNo(1).atchLocCd("asdf").fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();
        FileDto fileDto2 = FileDto.builder("파일이름2").atchLocNo(1).atchLocCd("asdf").fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));
        assertEquals(2, fileDao.selectALlFilesCnt());
    }

    @Test
    @Transactional
    @DisplayName("모든 파일 조회 테스트")
    void selectAllTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").atchLocNo(1).atchLocCd("asdf").fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();
        FileDto fileDto2 = FileDto.builder("파일이름2").atchLocNo(1).atchLocCd("asdf").fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));
        assertEquals(2, fileDao.selectAllFiles().size());
    }

    @Test
    @Transactional
    @DisplayName("스페이스 정보로 복수 개의 파일 조회 테스트")
    void selectFilesWithSpaceDataTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();
        FileDto fileDto2 = FileDto.builder("파일이름2").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));

        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("atchLocCd", CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("atchLocNo", 1);

        List<FileDto> list2 = fileDao.selectFilesWithSpaceData(spaceData);
        assertEquals(2, list2.size());
    }

    @Test
    @Transactional
    @DisplayName("제일 먼저 저장된 파일 한 개 조회 테스트")
    void selectOneRepresentFileTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .fileOrglNm("진짜이름1").fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();
        FileDto fileDto2 = FileDto.builder("파일이름2").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .fileOrglNm("진짜이름2").fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();
        FileDto fileDto3 = FileDto.builder("파일이름3").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .fileOrglNm("진짜이름3").fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);
        list.add(fileDto3);

        assertEquals(3, fileDao.insertFiles(list));

        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("atchLocCd", CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("atchLocNo", fileDto1.getAtchLocNo());

        Assertions.assertEquals(fileDto1.getFileNm(),fileDao.selectOneFileWithSpaceData(spaceData).getFileNm());
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 공간 정보로 제일 먼저 저장된 파일 한 개 조회 테스트")
    void selectOneRepresentFileTest2(){
        fileDao.deleteAll();

        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("atchLocCd", CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("atchLocNo", 12);

        assertNull(fileDao.selectOneFileWithSpaceData(spaceData));
    }

    @Test
    @Transactional
    @DisplayName("파일 이름으로 복수개 삭제 테스트")
    void deleteWithNamesTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();
        FileDto fileDto2 = FileDto.builder("파일이름2").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));

        List<String> names = new ArrayList<>();
        names.add(fileDto1.getFileNm());
        names.add(fileDto2.getFileNm());

        assertEquals(2, fileDao.deleteWithNames(names));
        assertEquals(0, fileDao.selectALlFilesCnt());
    }

    @Test
    @Transactional
    @DisplayName("파일 최대 갯수 넘는지 확인")
    void checkMaxFileCntTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();
        FileDto fileDto2 = FileDto.builder("파일이름2").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).fileOrglNm("진짜이름")
                .fileTypNm("img").fileSize(123L).fstRegDtm(LocalDateTime.now()).emplId(dummyEmpl.getEmplId()).build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));

        FileDto fileDto3 = FileDto.builder("").atchLocNo(1).atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode()).maxFileCnt(5).build();

        int result = fileDao.checkExceedingMaxFileCnt(fileDto3);
        assertEquals(1, result);
    }
}