package site.roombook.dao;

import site.roombook.domain.FileDto;

import java.util.List;
import java.util.Map;

public interface FileDao {

    int insertFiles(List<FileDto> list);
    List<FileDto> selectAllFiles();
    List<FileDto> selectFilesWithSpaceData(Map<String, Object> spaceData);
    int selectALlFilesCnt();
    FileDto selectOneFileWithSpaceData(Map<String, Object> spaceData);
    int checkExceedingMaxFileCnt(FileDto filedto);
    int deleteAll();
    int deleteWithNames(List<String> fileNames);
}
