package site.roombook.service;

import site.roombook.domain.DeptDto;

import java.util.List;

public interface DeptService {
    boolean saveOneDept(DeptDto deptDto);
    List<DeptDto> getAllDept();

    int modifyDeptOdr(List<DeptDto> list);

    void deleteAll();
}
