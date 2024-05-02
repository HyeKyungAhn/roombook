package site.roombook.dao;

import site.roombook.domain.BlngDeptDto;

import java.util.List;

public interface BlngDeptDao {

    int insertBlngDept(BlngDeptDto blngDeptDto);
    List<BlngDeptDto> selectAllBlngDept();
    int selectAllBlngDeptCnt();
    int deleteAllBlngDept();
}
