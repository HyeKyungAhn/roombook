package site.roombook.dao;

import site.roombook.domain.EmplDto;

import java.util.List;

public interface EmplDao {
    List<EmplDto> selectDeptMembers(String deptCd);

    EmplDto selectOneEmpl(String emplNo);

    EmplDto selectOneEmplProfile(String emplNo);

    List<EmplDto> selectEmplProfilesWithRnmOrEmail(String keyword);

    int insertEmpl(EmplDto emplDto);

    List<EmplDto> selectAllEmpl();

    int selectAllEmplCnt();

    EmplDto selectEmplById(String emplId);

    int selectEmplByEmail(String email);

    int deleteAll();
}
