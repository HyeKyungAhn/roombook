package site.roombook.dao;

import org.springframework.dao.DataIntegrityViolationException;
import site.roombook.domain.BlngDeptAndEmplIdDto;
import site.roombook.domain.BlngDeptDto;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface BlngDeptDao {

    int insertBlngDept(BlngDeptDto blngDeptDto);
    List<BlngDeptDto> selectAllBlngDept();
    int selectAllBlngDeptCnt();
    int deleteAllBlngDept();

    /*deleteBlngDepts
    * - 파라미터인 List<BlngDeptAndEmplIdDto>의 size 반드시 1이상이어야한다.
    *   0일 경우 InvocationTargetException 발생.
    * */
    int deleteBlngDepts(Map<String, Object> blngDepts);

    /* insertBlngDepts
     * - BlngDeptAndEmplIdDto 의 emplId(사원 아이디)가 반드시 empl 테이블 내에 하나 존재해야한다.
     *   그렇지 않을 경우에 DataIntegrityViolationException 발생
     * - 파라미터인 List<BlngDeptAndEmplIdDto>의 size 반드시 1이상이어야한다.
     *   0일 경우 InvocationTargetException 발생.
     * */
    int insertBlngDepts(List<BlngDeptAndEmplIdDto> blngDepts) throws DataIntegrityViolationException;

    /* insertOneBlngDept
     * - BlngDeptAndEmplIdDto 의 emplId(사원 아이디)가 반드시 empl 테이블 내에 하나 존재해야한다.
     *   그렇지 않을 경우에 DataIntegrityViolationException 발생
     * */
    int insertOneBlngDept(BlngDeptAndEmplIdDto blngDept) throws DataIntegrityViolationException;
}
