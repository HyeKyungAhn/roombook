package site.roombook.dao;

import org.springframework.dao.DuplicateKeyException;
import site.roombook.domain.DeptDto;

import java.util.List;
import java.util.Map;

public interface DeptDao {
    List<DeptDto> selectAllDept() throws Exception;

    DeptDto selectDept(String deptCd);

    int selectAllDeptCnt();

    List<DeptDto> selectAllDeptForTree();

    int selectDeptCntWithNm(String deptNm);

    int insertDept(DeptDto deptDto) throws DuplicateKeyException;

    int updateManager(Map<String, String> map);

    int updateAllDeptTreeOdrData(List<DeptDto> list);

    int deleteAll();

    List<DeptDto> selectDeptCdAndNm();

    int deleteDeptWithNoEmpl(String deptCd);
}
