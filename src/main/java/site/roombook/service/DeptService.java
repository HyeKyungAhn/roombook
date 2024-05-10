package site.roombook.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import site.roombook.domain.DeptAndEmplDto;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface DeptService {
    String NO_DEPT_CD = "0";

    boolean haveIdenticalDeptNm(String deptNm);

    boolean saveOneDept(List<DeptDto> list, String emplNo) throws DuplicateKeyException, NullPointerException;

    List<DeptDto> getAllDeptTreeData();

    int modifyDeptOdr(List<DeptDto> list);

    void deleteAll();

    void deleteDept(String deptCd) throws IllegalArgumentException;

    List<DeptDto> getDeptCdAndNm();

    DeptDto getOneDept(String deptCd);

    List<EmplDto> getDeptMembers(String deptCd);

    EmplDto getDeptMngr(String emplNo);

    List<EmplDto> searchEmplWithRnmOrEmail(String keyword);

    boolean modifyOneDept(Map<String, String> deptDataAndEmplId);

    List<DeptAndEmplDto> getProfilesOfMemberAndDeptName(String deptCd);

    void modifyDeptMem(String deptCd, List<String> list, String modifier) throws DataIntegrityViolationException;

    DeptAndEmplDto getDeptDetailInfo(String deptCd);
}
