package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.EmplDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public List<EmplDto> selectAllForAuthAdmin() { return session.selectList(namespace+"selectAllForAuthAdmin"); }

    @Override
    public int selectAllEmplCnt() { return session.selectOne(namespace + "selectAllEmplCnt"); }

    @Override
    public Optional<EmplDto> selectEmplById(String emplId) {
        return Optional.ofNullable(session.selectOne(namespace + "selectEmplById", emplId));
    }

    @Override
    public int selectEmplByEmail(String email) {
        return session.selectOne(namespace + "selectEmplByEmail", email); }

    @Override
    public List<EmplDto> selectLimitedEmplList(Map<String, Object> map) {
        return session.selectList(namespace+"selectLimitedEmplList", map);
    }

    @Override
    public int selectSearchedEmplsCnt(Map<String, String> map) {
        return session.selectOne(namespace+"selectLimitedEmplListCnt", map);
    }

    @Override
    public int updateAuthName(EmplDto emplDto) {
        return session.update(namespace+"updateEmplAuthNm", emplDto);
    }

    @Override
    public int deleteAll() { return session.delete(namespace + "deleteAll"); }
}
