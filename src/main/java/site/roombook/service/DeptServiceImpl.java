package site.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.dao.DeptDao;
import site.roombook.dao.EmplDao;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DeptServiceImpl implements DeptService {


    @Autowired
    DeptDao deptDao;

    @Autowired
    EmplDao emplDao;

    @Override
    public boolean haveIdenticalDeptNm(String deptNm) {
        return deptDao.selectDeptCntWithNm(deptNm)==1;
    }

    @Transactional
    @Override
    public boolean saveOneDept(List<DeptDto> list, String emplNo) throws DuplicateKeyException, NullPointerException{
        // TODO : test하기, service test 돌아가게 수정, test갯수는 필요한 것만 최소화, 중복 제거하기
        DeptDto newDept = null;
        int rowCnt;

        for (DeptDto deptDto : list) {
            if(Objects.equals(deptDto.getDEPT_CD(), NO_DEPT_CD)){
                newDept = deptDto;
                break;
            }
        }

        if(Objects.isNull(newDept)) return false;

        newDept.setFST_REGR_IDNF_NO(emplNo);
        newDept.setLAST_UPDR_IDNF_NO(emplNo);
        newDept.setDEPT_CD(generateDeptCd());
        rowCnt = deptDao.insertDept(newDept);
        if(rowCnt == 0){
            return false;
        }

        list.forEach( deptDto -> deptDto.setLAST_UPDR_IDNF_NO(emplNo));

        try{
            deptDao.updateAllDeptTreeOdrData(list);
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    private String generateDeptCd() {
        return ((ThreadLocalRandom.current().nextInt(9999))+1)+""; // 1-9999
    }

    @Transactional
    @Override
    public List<DeptDto> getAllDeptTreeData(){
        try {
            return deptDao.selectAllDeptForTree();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public int modifyDeptOdr(List<DeptDto> list) {
        int rowCnt = 0;
        try{
            rowCnt = deptDao.updateAllDeptTreeOdrData(list);
        } catch (Exception e){
            e.printStackTrace();
        }
        return rowCnt;
    }

    @Transactional
    @Override
    public void deleteAll(){
        try {
            deptDao.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public void deleteDeptWithNoEmpl(String deptCd) throws RuntimeException{
        int deletedRowCnt = deptDao.deleteDeptWithNoEmpl(deptCd);

        if(deletedRowCnt==0){
            throw new RuntimeException("Dept with members cannot be deleted");
        }

    }

    @Override
    public List<DeptDto> getDeptCdAndNm(){
        return deptDao.selectDeptCdAndNm();
    }

    @Override
    public DeptDto getOneDept(String deptCd){
        return deptDao.selectDept(deptCd);
    }

    @Override
    public List<EmplDto> getDeptMembers(String deptCd){
        return emplDao.selectDeptMembers(deptCd);
    }

    @Override
    public EmplDto getDeptMngr(String emplNo){
        return emplDao.selectOneEmplProfile(emplNo);
    }
}
