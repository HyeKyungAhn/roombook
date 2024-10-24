package store.roombook.service;

import org.springframework.dao.DataIntegrityViolationException;
import store.roombook.domain.DeptAndEmplDto;
import store.roombook.domain.DeptDto;
import store.roombook.domain.EmplDto;
import store.roombook.domain.ServiceResult;

import java.util.List;

public interface DeptService {
    String NO_DEPT_CD = "0";

    boolean haveIdenticalDeptNm(String deptNm);

    boolean saveOneDept(List<DeptDto> list, String emplNo) throws NullPointerException;

    List<DeptDto> getAllDeptTreeData();

    int modifyDeptOdr(List<DeptDto> list, String modifier);

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
