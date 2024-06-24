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
    public void saveRescs(List<RescDto> rescs, int spaceNo, String fstRegrIdnfNo){
        List<RescDto> rescsWithSpaceNoAndIdnfNo = new ArrayList<>();

        for(RescDto resc : rescs){
            rescsWithSpaceNoAndIdnfNo.add(RescDto
                    .builder(resc.getRESC_NM())
                    .RESC_NO(resc.getRESC_NO()) // TODO 없으면?
                    .FST_REGR_IDNF_NO(fstRegrIdnfNo)
                    .SPACE_NO(spaceNo).build());
        }

        rescDao.insertRescs(rescsWithSpaceNoAndIdnfNo);
        spaceRescDao.insertSpaceRescs(rescsWithSpaceNoAndIdnfNo);
    }

    @Override
    public List<RescDto> getRescs(int spaceNo) {
        return rescDao.selectSpaceResc(spaceNo);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRescs(int spaceNo, String idnfNo, List<RescDto> rescs){
        List<RescDto> rescsWithSpaceData = new ArrayList<>();

        for (RescDto resc : rescs) {
            rescsWithSpaceData.add(RescDto
                    .builder(resc.getRESC_NM())
                    .SPACE_NO(spaceNo)
                    .FST_REGR_IDNF_NO(idnfNo).build());
        }

        rescDao.insertRescs(rescsWithSpaceData);
        spaceRescDao.insertSpaceRescs(rescsWithSpaceData);

        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", spaceNo);
        map.put("rescs", rescsWithSpaceData);

        spaceRescDao.deleteSpaceRescs(map);
    }
}
