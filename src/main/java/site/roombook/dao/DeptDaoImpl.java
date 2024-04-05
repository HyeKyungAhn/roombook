package site.roombook.dao;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.DeptDto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class DeptDaoImpl implements DeptDao {
    String namespace = "site.roombook.dao.deptMapper.";

    @Autowired SqlSession session;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Override
    public List<DeptDto> selectAllDept() { return session.selectList(namespace + "selectAll"); }

    @Override
    public DeptDto selectDept(String deptCd) throws Exception{ return session.selectOne(namespace+"select", deptCd); }

    @Override
    public int selectAllDeptCnt() { return session.selectOne(namespace+"selectAllDeptCnt");}

    @Override
    public int insertDept(DeptDto deptDto) throws Exception{ return session.insert(namespace + "insert", deptDto); }

    @Override
    public int updateManager(Map<String, String> map) { return session.update(namespace + "updateManager", map); }

    @Override
    public int updateAllDeptTreeData(List<DeptDto> list) throws Exception {
        int totalNumberOfAffectedRows;
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            for (DeptDto deptDto : list) {
                sqlSession.update(namespace + "updateAllDeptTreeOdrData", deptDto);
            }
            List<BatchResult> results = sqlSession.flushStatements();
            totalNumberOfAffectedRows = Arrays.stream(results.get(0).getUpdateCounts()).sum();
        }
        return totalNumberOfAffectedRows;
    }

    @Override
    public int deleteAll(){ return session.delete(namespace + "deleteAll"); }
}
