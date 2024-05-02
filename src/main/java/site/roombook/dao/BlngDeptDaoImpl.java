package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.BlngDeptDto;

import java.util.List;

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
    public int selectAllBlngDeptCnt() { return session.selectOne(namespace+"selectAllBlngDeptCnt"); }

    @Override
    public int deleteAllBlngDept() { return session.delete(namespace+"deleteAll"); }
}
