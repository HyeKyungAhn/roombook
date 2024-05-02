package site.roombook.service;

import org.springframework.dao.DuplicateKeyException;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;

import java.util.List;

public interface DeptService {
    String NO_DEPT_CD = "0";

    boolean haveIdenticalDeptNm(String deptNm);

    boolean saveOneDept(List<DeptDto> list, String emplNo) throws DuplicateKeyException, NullPointerException;

    List<DeptDto> getAllDeptTreeData();

    int modifyDeptOdr(List<DeptDto> list);

    void deleteAll();

    void deleteDeptWithNoEmpl(String deptCd);

    List<DeptDto> getDeptCdAndNm();

    DeptDto getOneDept(String deptCd);

    List<EmplDto> getDeptMembers(String deptCd);

    EmplDto getDeptMngr(String emplNo);
}
