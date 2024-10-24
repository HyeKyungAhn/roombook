package store.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import store.roombook.domain.SpaceDto;
import store.roombook.domain.SpaceInfoAndTimeslotDto;

import java.util.List;
import java.util.Map;

@Repository
public class SpaceDaoImpl implements SpaceDao{

    String namespace = "site.roombook.dao.spaceMapper.";

    @Autowired
    SqlSession session;

    @Override
    public int deleteAll(){
        return session.delete(namespace + "deleteAll");
    }

    @Override
    public int deleteWithSpaceNo(int spaceNo) {
        return session.delete(namespace+"deleteWithSpaceNo", spaceNo);
    }

    @Override
    public int insertSpace(SpaceDto spaceDto){
        return session.insert(namespace + "insertSpace", spaceDto);
    }

    @Override
    public List<SpaceDto> selectAllSpace(){
        return session.selectList(namespace + "selectAll");
    }

    @Override
    public SpaceDto selectOne(int spaceNo) {
        return session.selectOne(namespace+"selectOne", spaceNo);
    }

    @Override
    public int selectAllCnt() {
        return session.selectOne(namespace+"selectCntAll");
    }

    @Override
    public int selectCntAllNotHiddenSpace() {
        return session.selectOne(namespace + "selectCntAllNotHiddenSpace");
    }

    @Override
    public List<SpaceInfoAndTimeslotDto> selectSpaceList(SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto) {
        return session.selectList(namespace+"selectSpaceInfoAndTimeslots", spaceInfoAndTimeslotDto);
    }

    @Override
    public List<SpaceInfoAndTimeslotDto> selectLimitedSpaces(SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto) {
        return session.selectList(namespace+"selectSpacesAndFiles", spaceInfoAndTimeslotDto);
    }

    @Override
    public List<SpaceInfoAndTimeslotDto> selectOneSpaceAndRescAndFIle(Map<String, Object> spaceData) {
        return session.selectList(namespace+"selectOneSpaceAndRescAndFIle", spaceData);
    }

    @Override
    public int update(SpaceDto spaceDto) {
        return session.update(namespace+"updateSpace", spaceDto);
    }
}
