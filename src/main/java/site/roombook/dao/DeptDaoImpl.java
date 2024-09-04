package site.roombook.dao;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import site.roombook.domain.DeptAndEmplDto;
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
    public DeptDto selectDept(String deptCd){ return session.selectOne(namespace+"select", deptCd); }

    @Override
    public int selectAllDeptCnt() { return session.selectOne(namespace+"selectAllDeptCnt");}

    @Override
    public List<DeptDto> selectAllDeptForTree(){ return session.selectList(namespace+"selectAllForTree"); }

    @Override
    public int insertDept(DeptDto deptDto) throws DuplicateKeyException {
        return session.insert(namespace + "insert", deptDto);
    }

    @Override
    public int selectDeptCntWithNm(String deptNm) { return session.selectOne(namespace + "selectDeptCntWithNm", deptNm); }

    @Override
    public int updateManager(Map<String, String> map) { return session.update(namespace + "updateManager", map); }

    @Override
    public int updateAllDeptTreeOdrData(List<DeptDto> list) {
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

    @Override
    public List<DeptDto> selectDeptCdAndNm(){
        return session.selectList(namespace+"selectDeptCdAndNm");
    }

    @Override
    public int deleteDeptWithNoEmpl(String deptCd) { return session.delete(namespace+"deleteDeptWithNoEmpl", deptCd); }

    @Override
    public int updateDept(DeptDto deptDataAndEmplId) {
        return session.update(namespace+"updateDept",deptDataAndEmplId);
    }

    @Override
    public List<DeptAndEmplDto> selectMemberProfilesAndDeptName(String deptCd){
        return session.selectList(namespace + "selectMemberProfilesAndDeptName", deptCd);
    }

    @Override
    public DeptAndEmplDto selectOneDeptAndMngrAndCdrDeptCnt(String deptCd){
        return session.selectOne(namespace + "selectOneDeptAndMngrAndCdrDeptCnt", deptCd);
    }

    @Override
    public int selectCdrDeptCnt(String deptCd) {
        return session.selectOne(namespace + "selectCdrDeptCnt", deptCd);
    }
}
