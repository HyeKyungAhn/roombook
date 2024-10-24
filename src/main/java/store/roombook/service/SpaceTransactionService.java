package store.roombook.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import store.roombook.domain.RescDto;
import store.roombook.domain.SpaceDto;
import store.roombook.domain.SpaceTransactionServiceResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface SpaceTransactionService {
    SpaceTransactionServiceResult saveSpace(SpaceDto spaceDto, MultipartFile[] files, String emplId, List<RescDto> rescs) throws DuplicateKeyException, IllegalArgumentException, IOException;
    SpaceTransactionServiceResult modifySpace(int spaceNo, String lastUpdrIdnfNo, SpaceDto spaceDto, MultipartFile[] files, ArrayList<String> deletedFileNames, List<RescDto> rescs) throws MultipartException, IllegalArgumentException, IOException;
}
