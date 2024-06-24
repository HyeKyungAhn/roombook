package site.roombook.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.CmnCode;
import site.roombook.domain.FileDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class FileDaoImplTest {

    @Autowired
    FileDao fileDao;

    @Test
    @Transactional
    @DisplayName("파일 정보 저장 테스트")
    void insertFilesTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").ATCH_LOC_NO(1).ATCH_LOC_CD("asdf").FILE_ORGL_NM("진짜이름")
                .FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();
        FileDto fileDto2 = FileDto.builder("파일이름2").ATCH_LOC_NO(1).ATCH_LOC_CD("asdf").FILE_ORGL_NM("진짜이름")
                .FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();

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

        Exception exception = assertThrows(BadSqlGrammarException.class, () -> fileDao.insertFiles(list));
        assertTrue(exception.getMessage().contains("''"));
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

        FileDto fileDto1 = FileDto.builder("파일이름1").ATCH_LOC_NO(1).ATCH_LOC_CD("asdf").FILE_ORGL_NM("진짜이름")
                .FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();
        FileDto fileDto2 = FileDto.builder("파일이름2").ATCH_LOC_NO(1).ATCH_LOC_CD("asdf").FILE_ORGL_NM("진짜이름")
                .FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();

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

        FileDto fileDto1 = FileDto.builder("파일이름1").ATCH_LOC_NO(1).ATCH_LOC_CD("asdf").FILE_ORGL_NM("진짜이름")
                .FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();
        FileDto fileDto2 = FileDto.builder("파일이름2").ATCH_LOC_NO(1).ATCH_LOC_CD("asdf").FILE_ORGL_NM("진짜이름")
                .FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();

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

        FileDto fileDto1 = FileDto.builder("파일이름1").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();
        FileDto fileDto2 = FileDto.builder("파일이름2").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));

        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("ATCH_LOC_CD", CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("ATCH_LOC_NO", 1);

        List<FileDto> list2 = fileDao.selectFilesWithSpaceData(spaceData);
        assertEquals(2, list2.size());
    }

    @Test
    @Transactional
    @DisplayName("제일 먼저 저장된 파일 한 개 조회 테스트")
    void selectOneRepresentFileTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름1").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();
        FileDto fileDto2 = FileDto.builder("파일이름2").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름2").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();
        FileDto fileDto3 = FileDto.builder("파일이름3").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름3").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);
        list.add(fileDto3);

        assertEquals(3, fileDao.insertFiles(list));

        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("ATCH_LOC_CD", CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("ATCH_LOC_NO", fileDto1.getATCH_LOC_NO());

        assertEquals(fileDto1.getFILE_NM(),fileDao.selectOneFileWithSpaceData(spaceData).getFILE_NM());
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 공간 정보로 제일 먼저 저장된 파일 한 개 조회 테스트")
    void selectOneRepresentFileTest2(){
        fileDao.deleteAll();

        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put("ATCH_LOC_CD", CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("ATCH_LOC_NO", 12);

        assertNull(fileDao.selectOneFileWithSpaceData(spaceData));
    }

    @Test
    @Transactional
    @DisplayName("파일 이름으로 복수개 삭제 테스트")
    void deleteWithNamesTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();
        FileDto fileDto2 = FileDto.builder("파일이름2").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));

        List<String> names = new ArrayList<>();
        names.add(fileDto1.getFILE_NM());
        names.add(fileDto2.getFILE_NM());

        assertEquals(2, fileDao.deleteWithNames(names));
        assertEquals(0, fileDao.selectALlFilesCnt());
    }

    @Test
    @Transactional
    @DisplayName("파일 최대 갯수 넘는지 확인")
    void checkMaxFileCntTest(){
        fileDao.deleteAll();

        FileDto fileDto1 = FileDto.builder("파일이름1").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();
        FileDto fileDto2 = FileDto.builder("파일이름2").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .FILE_ORGL_NM("진짜이름").FILE_TYP_NM("img").FILE_SIZE(123L).FST_REG_DTM(LocalDateTime.now()).FST_REGR_IDNF_NO("admin").build();

        List<FileDto> list = new ArrayList<>();
        list.add(fileDto1);
        list.add(fileDto2);

        assertEquals(2, fileDao.insertFiles(list));

        FileDto fileDto3 = FileDto.builder("").ATCH_LOC_NO(1).ATCH_LOC_CD(CmnCode.ATCH_LOC_CD_SPACE.getCode()).maxFileCnt(5).build();

        int result = fileDao.checkExceedingMaxFileCnt(fileDto3);
        assertEquals(1, result);
    }
}