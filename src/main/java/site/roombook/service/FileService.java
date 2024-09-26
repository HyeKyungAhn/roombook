package site.roombook.service;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import site.roombook.CmnCode;
import site.roombook.domain.FileDto;
import site.roombook.domain.FileServiceResult;

import java.io.IOException;
import java.util.List;

public interface FileService {
    FileServiceResult saveFiles(MultipartFile[] files, int atchLocNum, String emplId) throws IOException;
    List<FileDto> getFileData(int spaceNo, CmnCode code);
    FileServiceResult deleteFiles(List<String> deletedFileNames) throws IOException, MultipartException;
    FileDto getFile(int ATCH_LOC_NO, CmnCode atchLocCd);
}
