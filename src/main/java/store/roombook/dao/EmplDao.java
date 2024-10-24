package store.roombook.dao;

import store.roombook.domain.EmplDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmplDao {
    List<EmplDto> selectDeptMembers(String deptCd);

    EmplDto selectOneEmpl(String emplNo);

    EmplDto selectOneEmplProfile(String emplNo);

    List<EmplDto> selectEmplProfilesWithRnmOrEmail(String keyword);

    int insertEmpl(EmplDto emplDto);

    List<EmplDto> selectAllEmpl();

    List<EmplDto> selectAllForAuthAdmin();

    int selectAllEmplCnt();

    Optional<EmplDto> selectEmplById(String emplId);

    int selectEmplByEmail(String email);

    List<EmplDto> selectLimitedEmplList(Map<String, Object> map);

    int selectSearchedEmplsCnt(Map<String, String> map);

    int updateAuthName(EmplDto emplDto);

    int deleteAll();
}
