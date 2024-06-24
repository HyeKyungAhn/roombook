package site.roombook.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import site.roombook.domain.RescDto;
import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceTransactionServiceResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface SpaceTransactionService {
    SpaceTransactionServiceResult saveSpace(SpaceDto spaceDto, MultipartFile[] files, String fstRegrIdnfNo, List<RescDto> rescs) throws DuplicateKeyException, IllegalArgumentException, IOException;
    SpaceTransactionServiceResult modifySpace(int spaceNo, String lastUpdrIdnfNo, SpaceDto spaceDto, MultipartFile[] files, ArrayList<String> deletedFileNames, List<RescDto> rescs) throws MultipartException, IllegalArgumentException, IOException;
}
