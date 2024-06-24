package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.RescDto;

import java.util.List;

@Repository
public class RescDaoImpl implements RescDao{
    String namespace = "site.roombook.dao.rescMapper.";

    @Autowired
    SqlSession session;

    @Override
    public List<RescDto> selectRescsWithKeyword(String keyword){
        return session.selectList(namespace+"selectNoAndNmListWithKeyword", keyword);
    }

    @Override
    public int insertRescs(List<RescDto> list){
        return session.insert(namespace+"insertRescs", list);
    }

    @Override
    public int deleteAll() {
        return session.delete(namespace+"deleteAll");
    }

    @Override
    public List<RescDto> selectAllResc() {
        return session.selectList(namespace+"selectAllResc");
    }

    @Override
    public int selectAllRescCnt() {
        return session.selectOne(namespace+"selectAllRescCnt");
    }

    @Override
    public List<RescDto> selectSpaceResc(int spaceNo) {
        return session.selectList(namespace+"selectSpaceResc", spaceNo);
    }
}
