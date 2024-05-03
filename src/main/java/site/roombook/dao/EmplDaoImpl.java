package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.EmplDto;

import java.util.List;

@Repository
public class EmplDaoImpl implements EmplDao{
    String namespace = "site.roombook.dao.emplMapper.";

    @Autowired
    SqlSession session;

    @Override
    public List<EmplDto> selectDeptMembers(String deptCd) { return session.selectList(namespace + "selectDeptMemberEmplProfile", deptCd); }

    @Override
    public EmplDto selectOneEmpl(String emplNo) { return session.selectOne(namespace+"selectOneEmpl", emplNo); }

    @Override
    public EmplDto selectOneEmplProfile(String emplNo) { return session.selectOne(namespace+"selectOneEmplProfile", emplNo); }

    @Override
    public List<EmplDto> selectEmplProfilesWithRnmOrEmail(String keyword) { return session.selectList(namespace+"selectEmplProfiles", keyword); }

    @Override
    public int insertEmpl(EmplDto emplDto) { return session.insert(namespace+"insertEmpl", emplDto); }

    @Override
    public List<EmplDto> selectAllEmpl() { return session.selectList(namespace+"selectAllEmpl"); }

    @Override
    public int selectAllEmplCnt() { return session.selectOne(namespace + "selectAllEmplCnt"); }

    @Override
    public int deleteAll() { return session.delete(namespace + "deleteAll"); }
}
