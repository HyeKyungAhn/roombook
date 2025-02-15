package store.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import store.roombook.domain.FileServiceResult;
import store.roombook.domain.RescDto;
import store.roombook.domain.SpaceDto;
import store.roombook.domain.SpaceTransactionServiceResult;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SpaceTransactionServiceImpl implements SpaceTransactionService{
    private static final long MIN = 100000000L;
    private static final long MAX = 999999999L;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private FileService fileService;

    @Autowired
    private RescService rescService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public SpaceTransactionServiceResult saveSpace(SpaceDto spaceDto, MultipartFile[] files, String emplId, List<RescDto> rescs) throws DuplicateKeyException, IllegalArgumentException, IOException {
        SpaceTransactionServiceResult saveResult = new SpaceTransactionServiceResult();
        int spaceNo = generateSpaceNo();

        if(spaceService.saveSpace(spaceDto, spaceNo, emplId)){
            saveResult.setSpaceSaved(true);
        } else {
            saveResult.setSpaceSaved(false);
            return saveResult;
        }

        FileServiceResult fileSaveResult;

        if(!Objects.isNull(files) && files.length!=0){
            fileSaveResult = fileService.saveFiles(files, spaceNo, emplId);
        } else {
            fileSaveResult = new FileServiceResult();
            fileSaveResult.setSaved(true);
        }

        saveResult.setFileSaveResult(fileSaveResult);

        if(Objects.nonNull(rescs) && !rescs.isEmpty()){
            rescService.saveRescs(rescs, spaceNo, emplId);
        }

        return saveResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {IOException.class})
    public SpaceTransactionServiceResult modifySpace(int spaceNo,
                                                     String emplId,
                                                     SpaceDto space,
                                                     MultipartFile[] newFiles,
                                                     ArrayList<String> deletedFilesNames,
                                                     List<RescDto> rescs) throws MultipartException, IllegalArgumentException, IOException{
        SpaceTransactionServiceResult modifyResult = new SpaceTransactionServiceResult();

        if (spaceService.updateSpace(spaceNo, emplId, space)) {
            modifyResult.setSpaceSaved(true);
        } else {
            modifyResult.setSpaceSaved(false);
            return modifyResult;
        }

        FileServiceResult saveResult;
        FileServiceResult deleteResult;

        if (Objects.nonNull(newFiles) && newFiles.length != 0) {
            saveResult = fileService.saveFiles(newFiles, spaceNo, emplId);
        } else {
            saveResult = new FileServiceResult();
            saveResult.setSaved(true);
        }

        if (Objects.nonNull(deletedFilesNames) && !deletedFilesNames.isEmpty()) {
            deleteResult = fileService.deleteFiles(deletedFilesNames);
        } else {
            deleteResult = new FileServiceResult();
            deleteResult.setDeleted(true);
        }

        modifyResult.setFileSaveResult(saveResult);
        modifyResult.setFileDeleteResult(deleteResult);

        if(Objects.nonNull(rescs) && !rescs.isEmpty()){
            rescService.updateRescs(spaceNo, emplId, rescs);
        }

        return modifyResult;
    }

    private int generateSpaceNo(){  //generate 8-digit spaceNo
        return (int) ThreadLocalRandom.current().nextLong(MIN, MAX + 1);
    }
}
