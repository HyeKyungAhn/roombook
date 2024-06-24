package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.SpaceDto;
import site.roombook.domain.SpaceRescFileDto;

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
    public List<SpaceRescFileDto> selectSpaceList(Map<String, Object> spaceData) {
        return session.selectList(namespace+"selectSpacesAndRescAndFile", spaceData);
    }

    @Override
    public List<SpaceRescFileDto> selectOneSpaceAndRescAndFIle(Map<String, Object> spaceData) {
        return session.selectList(namespace+"selectOneSpaceAndRescAndFIle", spaceData);
    }

    @Override
    public int update(SpaceDto spaceDto) {
        return session.update(namespace+"updateSpace", spaceDto);
    }
}
