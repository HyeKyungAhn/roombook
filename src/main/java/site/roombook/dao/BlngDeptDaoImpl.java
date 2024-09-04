package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import site.roombook.domain.BlngDeptDto;

import java.util.List;
import java.util.Map;

@Repository
public class BlngDeptDaoImpl implements BlngDeptDao {
    String namespace = "site.roombook.dao.blngDeptMapper.";

    @Autowired
    SqlSession session;

    @Override
    public int insertBlngDept(BlngDeptDto blngDept) { return session.insert(namespace+"insertDeptMember", blngDept); }

    @Override
    public List<BlngDeptDto> selectAllBlngDept() { return session.selectList(namespace+"selectAllBlngDept"); }

    @Override
    public int deleteAllBlngDept() { return session.delete(namespace+"deleteAll"); }

    @Override
    public int deleteBlngDepts(Map<String, Object> blngDepts) { return session.delete(namespace+"deleteBlngDepts", blngDepts); }

    @Override
    public int insertBlngDepts(List<BlngDeptDto> blngDepts) throws DataIntegrityViolationException  {
        return session.insert(namespace+"insertBlngDepts", blngDepts);
    }

    @Override
    public int insertOneBlngDept(BlngDeptDto blngDept) throws DataIntegrityViolationException{ return session.insert(namespace+"insertOneBlngDept", blngDept); }
}
