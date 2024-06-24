package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.RescDto;
import site.roombook.domain.SpaceRescDto;

import java.util.List;
import java.util.Map;

@Repository
public class SpaceRescDaoImpl implements SpaceRescDao{

    @Autowired
    SqlSession session;

    private static final String namespace = "site.roombook.dao.spaceRescMapper.";

    @Override
    public int insertSpaceRescs(List<RescDto> rescs){
        return session.insert(namespace + "insertSpaceResc", rescs);
    }

    @Override
    public int deleteAll(){ return session.delete(namespace + "deleteAll");}

    @Override
    public int deleteSpaceRescs(Map<String, Object> rescsAndSpaceNo) {
        return session.delete(namespace+"deleteSpaceRescs", rescsAndSpaceNo);
    }

    @Override
    public List<SpaceRescDto> selectAll() {
        return session.selectList(namespace+"selectAll");
    }
}
