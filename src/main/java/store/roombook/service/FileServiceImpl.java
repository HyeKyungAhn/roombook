package store.roombook.service;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import store.roombook.CmnCode;
import store.roombook.FileStorageProperties;
import store.roombook.dao.FileDao;
import store.roombook.domain.FileDto;
import store.roombook.domain.FileServiceResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

@Service
public class FileServiceImpl implements FileService{

    @Autowired
    private FileDao fileDao;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    private static final int THUMBNAIL_WIDTH = 230;
    private static final int THUMBNAIL_HEIGHT = 230;
    private static final boolean SUCCESS = true;
    private static final boolean FAIL = false;
    private static final String FILE_DIRECTORY_ORIGINAL = "originals";
    private static final String FILE_DIRECTORY_THUMBNAIL = "thumbnails";
    private static final String INFO = "INFO";
    private static final int MAX_FILE_CNT = 5;
    private static final int CAPACITY_PER_FILE = 1024*1024; //1MB
    private static final List<String> IMAGE_TYPES = Arrays.asList("jpeg", "jpg", "png");
    private static final Logger logger = LogManager.getLogger(FileServiceImpl.class);
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png");
    private static final String ATTACHED_LOCATION_CODE = "atchLocCd";


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileServiceResult saveFiles(MultipartFile[] newFiles, int atchLocNum, String emplId) throws MultipartException, IllegalStateException {
        FileServiceResult result = new FileServiceResult();

        if (isExceedCapacity(newFiles)) {
            throw new IllegalArgumentException("a file size exceeds the limit.");
        }

        List<FileDto> fileDtoList = new ArrayList<>();

        FileDto fileData = FileDto.builder("")
                .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                .atchLocNo(atchLocNum)
                .maxFileCnt(MAX_FILE_CNT).build();

        result.setFileCntExceeded(fileDao.checkExceedingMaxFileCnt(fileData));

        if(result.isFileCntExceeded()) {
            throw new IllegalArgumentException("The number of files exceeds the maximum limit");
        }

        for (MultipartFile file : newFiles) {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if(extension != null && extension.equals("jfif")) extension = "jpg";

            String uniqueFileName = UUID.randomUUID() + "." + extension;

            boolean isSaveSuccess;

            if(isValidFileType(file)){
                isSaveSuccess = saveImg(file, fileStorageProperties.getUploadDir(), uniqueFileName, extension);
            } else {
                throw new MultipartException("Unsupported file format");
            }

            if (!isSaveSuccess) continue;

            FileDto fileDto = FileDto.builder(uniqueFileName)
                        .atchLocNo(atchLocNum)
                        .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                        .fileOrglNm(file.getOriginalFilename())
                        .fileTypNm(file.getContentType())
                        .fileSize(file.getSize())
                        .emplId(emplId)
                        .build();

            fileDtoList.add(fileDto);
        }

        int savedFileCnt = 0;

        if (!fileDtoList.isEmpty()) {
            savedFileCnt = fileDao.insertFiles(fileDtoList); //length 0일경우
        }

        if(newFiles.length==savedFileCnt){
            result.setSaved(true);
        } else {
            result.setSaved(false);
            result.setUnsavedFileCnt(newFiles.length-savedFileCnt);
        }

        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileServiceResult deleteFiles(List<String> fileNamesToDelete) throws MultipartException {
        FileServiceResult result = new FileServiceResult();
        List<String> deletedFileNames = new ArrayList<>();

        for(String name : fileNamesToDelete){
            String extension = FilenameUtils.getExtension(name);

            if(IMAGE_TYPES.contains(extension.toLowerCase())){
                if(!removeFile(name, FILE_DIRECTORY_ORIGINAL, fileStorageProperties.getUploadDir())
                        || !removeFile(name, FILE_DIRECTORY_THUMBNAIL, fileStorageProperties.getUploadDir())){
                    logger.error("file deletion failed");
                    continue;
                }
                deletedFileNames.add(name);
            } else {
                throw new MultipartException("Unsupported file format");
            }
        }

        int deletedFileCnt = 0;

        if (!deletedFileNames.isEmpty()) {
            deletedFileCnt = fileDao.deleteWithNames(deletedFileNames);
        }

        if (fileNamesToDelete.size() == deletedFileCnt) {
            result.setDeleted(true);
        } else {
            result.setDeleted(false);
            result.setUndeletedFileCnt(fileNamesToDelete.size()-deletedFileNames.size());
        }

        return result;
    }

    @Override
    public List<FileDto> getFileData(int spaceNo, CmnCode locCode) {
        Map<String, Object> spaceData = new HashMap<>();

        if (locCode.equals(CmnCode.ATCH_LOC_CD_SPACE)) {
            spaceData.put(ATTACHED_LOCATION_CODE, CmnCode.ATCH_LOC_CD_SPACE.getCode());
        } else if (locCode.equals(CmnCode.ATCH_LOC_CD_NOTICE)) {
            spaceData.put(ATTACHED_LOCATION_CODE, CmnCode.ATCH_LOC_CD_NOTICE.getCode());
        }

        spaceData.put("ATCH_LOC_NO", spaceNo);
        return fileDao.selectFilesWithSpaceData(spaceData);
    }

    @Override
    public FileDto getFile(int ATCH_LOC_NO, CmnCode atchLocCd) {
        Map<String, Object> spaceData = new HashMap<>();
        spaceData.put(ATTACHED_LOCATION_CODE, CmnCode.ATCH_LOC_CD_SPACE.getCode());
        spaceData.put("atchLocNo", ATCH_LOC_NO);
        return fileDao.selectOneFileWithSpaceData(spaceData);
    }

    private boolean removeFile(String fileName, String directoryName, String uploadDir) {
        Path path = Paths.get(uploadDir + File.separator + directoryName + File.separator + fileName);
        boolean isDeleteSuccess = false;
        try {
            isDeleteSuccess = Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error(String.format("file deletion error: %s", e.getMessage()), e);
        }

        return isDeleteSuccess;
    }

    private boolean saveImg(MultipartFile file, String uploadDir, String newFileName, String format) {
        File originalImgFile = new File(uploadDir + File.separator + FILE_DIRECTORY_ORIGINAL + File.separator + newFileName);
        File thumbnailFile = new File(uploadDir + File.separator + FILE_DIRECTORY_THUMBNAIL + File.separator + newFileName);

        try{
            file.transferTo(originalImgFile);
        } catch (IOException e){
            logger.error(String.format("original image save error:  %s", e.getMessage()), e);
            return FileServiceImpl.FAIL;
        }

        boolean isThumbnailSaveSuccess = generateAndSaveThumbnail(thumbnailFile, originalImgFile, format);

        if(!isThumbnailSaveSuccess){
            removeFile(newFileName, FILE_DIRECTORY_ORIGINAL, uploadDir);
            return FileServiceImpl.FAIL;
        }

        return FileServiceImpl.SUCCESS;
    }

    private boolean generateAndSaveThumbnail(File thumbnailFile, File originalImgFile, String format){

        Thumbnails.Builder<File> thumbnailBuilder = Thumbnails.of(originalImgFile)
                .crop(Positions.CENTER)
                .size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                .outputFormat(format);

        try{
            if (format.equals("png")) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                thumbnailBuilder.toOutputStream(outputStream);

                optimizePng(originalImgFile.getPath(), outputStream, thumbnailFile.getPath());
            } else {
                thumbnailBuilder.outputQuality(1.0)
                        .toFile(thumbnailFile);
            }
        } catch (IOException e){
            logger.error(String.format("thumbnail save error:  %s", e.getMessage()), e);
            return FileServiceImpl.FAIL;
        }
        return FileServiceImpl.SUCCESS;
    }

    private void optimizePng(String originalFilePath, ByteArrayOutputStream outputStream, String outputImagePath) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        PngImage image = new PngImage(inputStream, FileServiceImpl.INFO);
        image.setFileName(originalFilePath);

        PngOptimizer optimizer = new PngOptimizer();
        try {
            optimizer.optimize(image, outputImagePath, false, 9);
        } catch (IOException e) {
            throw new IOException("fail to optimize a png file");
        }
    }

    private boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    private boolean isExceedCapacity(MultipartFile[] files) {
        for (MultipartFile file : files) {
            if (file.getSize() > CAPACITY_PER_FILE) {
                return true;
            }
        }
        return false;
    }
}
