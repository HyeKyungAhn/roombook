package site.roombook.dao;

import site.roombook.domain.DeptDto;

import java.util.List;
import java.util.Map;

public interface DeptDao {
    List<DeptDto> selectAllDept() throws Exception;

    DeptDto selectDept(String deptCd) throws Exception;

    int selectAllDeptCnt();

    int insertDept(DeptDto deptDto) throws Exception;

    int updateManager(Map<String, String> map);

    int updateAllDeptTreeData(List<DeptDto> list) throws Exception;

    int deleteAll();
}
