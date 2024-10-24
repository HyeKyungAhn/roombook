package store.roombook.dao;

import org.springframework.dao.DataIntegrityViolationException;
import store.roombook.domain.BlngDeptDto;

import java.util.List;
import java.util.Map;

public interface BlngDeptDao {

    int insertBlngDept(BlngDeptDto blngDeptDto);
    List<BlngDeptDto> selectAllBlngDept();
    int deleteAllBlngDept();

    int deleteBlngDepts(Map<String, Object> blngDepts);

    /* insertBlngDepts
     * - BlngDeptDto 의 blngEmplId(사원 아이디)가 반드시 empl 테이블 내에 하나 존재해야한다.
     *   그렇지 않을 경우에 DataIntegrityViolationException 발생
     * */
    int insertBlngDepts(List<BlngDeptDto> blngDepts) throws DataIntegrityViolationException;

    /* insertOneBlngDept
     * - BlngDeptDto 의 blngEmplId(사원 아이디)가 반드시 empl 테이블 내에 하나 존재해야한다.
     *   그렇지 않을 경우에 DataIntegrityViolationException 발생
     * */
    int insertOneBlngDept(BlngDeptDto blngDept) throws DataIntegrityViolationException;
}
