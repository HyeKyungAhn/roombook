package site.roombook.service;

import org.springframework.dao.DataIntegrityViolationException;
import site.roombook.domain.DeptAndEmplDto;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;
import site.roombook.domain.ServiceResult;

import java.util.List;

public interface DeptService {
    String NO_DEPT_CD = "0";

    boolean haveIdenticalDeptNm(String deptNm);

    boolean saveOneDept(List<DeptDto> list, String emplNo) throws NullPointerException;

    List<DeptDto> getAllDeptTreeData();

    int modifyDeptOdr(List<DeptDto> list);

    void deleteAll();

    void deleteDept(String deptCd) throws IllegalArgumentException;

    List<DeptDto> getDeptCdAndNm();

    DeptDto getOneDept(String deptCd);

    List<EmplDto> getDeptMembers(String deptCd);

    EmplDto getDeptMngr(String emplNo);

    List<EmplDto> searchEmplWithRnmOrEmail(String keyword);

    boolean modifyOneDept(DeptDto deptDataAndEmplId);

    List<DeptAndEmplDto> getProfilesOfMemberAndDeptName(String deptCd);

    ServiceResult modifyDeptMem(String deptCd, List<String> list, String modifierId) throws DataIntegrityViolationException;

    DeptAndEmplDto getDeptDetailInfo(String deptCd);
}
