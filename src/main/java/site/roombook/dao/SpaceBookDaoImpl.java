package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.SpaceBookAndSpaceDto;
import site.roombook.domain.SpaceBookDto;

import java.util.List;

@Repository
public class SpaceBookDaoImpl implements SpaceBookDao{

    private static final String namespace = "site.roombook.dao.spaceBookMapper.";

    @Autowired
    SqlSession session;


    @Override
    public int insert(SpaceBookDto spaceBookDto) {
        return session.insert(namespace+"insert", spaceBookDto);
    }

    @Override
    public List<SpaceBookDto> selectTimeSlotsAtTheDate(SpaceBookDto spaceBookDto) {
        return session.selectList(namespace+"selectBookedTimeslotsAtTheDate", spaceBookDto);
    }

    @Override
    public SpaceBookAndSpaceDto selectTimeslot(SpaceBookDto spaceBookDto) {
        return session.selectOne(namespace+"selectOneTimeslot", spaceBookDto);
    }

    @Override
    public int updateTimeslot(SpaceBookDto spaceBookDto) {
        return session.update(namespace+"updateTimeslot", spaceBookDto);
    }

    @Override
    public int updateTimeslotStatus(SpaceBookDto spaceBookDto) {
        return session.update(namespace + "updateTimeslotStatus", spaceBookDto);
    }

    @Override
    public int deleteAll() {
        return session.delete(namespace+"deleteAll");
    }
}
