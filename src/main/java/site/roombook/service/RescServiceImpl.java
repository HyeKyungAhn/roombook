package site.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.dao.RescDao;
import site.roombook.dao.SpaceRescDao;
import site.roombook.domain.RescDto;

import java.util.*;

@Service
public class RescServiceImpl implements RescService{
    @Autowired
    RescDao rescDao;

    @Autowired
    SpaceRescDao spaceRescDao;

    @Override
    public List<RescDto> getRescsSuggestions(String keyword) {
        return rescDao.selectRescsWithKeyword(keyword);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRescs(List<RescDto> rescs, int spaceNo, String emplId){
        List<RescDto> rescsAndMetaData = new ArrayList<>();

        for(RescDto resc : rescs){
            rescsAndMetaData.add(RescDto
                    .builder(resc.getRescNm())
                    .rescNo(resc.getRescNo()) // TODO 없으면?
                    .emplId(emplId)
                    .spaceNo(spaceNo).build());
        }

        rescDao.insertRescs(rescsAndMetaData);
        spaceRescDao.insertSpaceRescs(rescsAndMetaData);
    }

    @Override
    public List<RescDto> getRescs(int spaceNo) {
        return rescDao.selectSpaceResc(spaceNo);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRescs(int spaceNo, String emplId, List<RescDto> rescs){
        List<RescDto> rescsWithSpaceData = new ArrayList<>();

        for (RescDto resc : rescs) {
            rescsWithSpaceData.add(RescDto
                    .builder(resc.getRescNm())
                    .spaceNo(spaceNo)
                    .emplId(emplId).build());
        }

        rescDao.insertRescs(rescsWithSpaceData);
        spaceRescDao.insertSpaceRescs(rescsWithSpaceData);

        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", spaceNo);
        map.put("rescs", rescsWithSpaceData);

        spaceRescDao.deleteSpaceRescs(map);
    }
}
