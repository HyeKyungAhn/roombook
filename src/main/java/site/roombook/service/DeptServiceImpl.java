package site.roombook.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.ExceptionMsg;
import site.roombook.dao.BlngDeptDao;
import site.roombook.dao.DeptDao;
import site.roombook.dao.EmplDao;
import site.roombook.domain.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DeptServiceImpl implements DeptService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    DeptDao deptDao;

    @Autowired
    EmplDao emplDao;

    @Autowired
    BlngDeptDao blngDeptDao;

    @Override
    public boolean haveIdenticalDeptNm(String deptNm) {
        return deptDao.selectDeptCntWithNm(deptNm)==1;
    }

    @Transactional
    @Override
    public boolean saveOneDept(List<DeptDto> depts, String emplNo) throws DuplicateKeyException, NullPointerException{
        // TODO : test하기, service test 돌아가게 수정, test갯수는 필요한 것만 최소화, 중복 제거하기
        DeptDto newDept = null;
        int rowCnt;

        for (DeptDto deptDto : depts) {
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

        depts.forEach( deptDto -> deptDto.setLAST_UPDR_IDNF_NO(emplNo));

        try{
            deptDao.updateAllDeptTreeOdrData(depts);
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
    public void deleteDept(String deptCd) throws IllegalArgumentException{
        if(deptDao.selectCdrDeptCnt(deptCd)!=0){
            throw new IllegalArgumentException(ExceptionMsg.DEPT_DELETE_SUB_DEPT.getCode());
        }

        int deletedRowCnt = deptDao.deleteDeptWithNoEmpl(deptCd);

        if(deletedRowCnt==0){
            throw new IllegalArgumentException(ExceptionMsg.DEPT_DELETE_MEMBER.getCode());
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

    @Transactional
    @Override
    public List<EmplDto> searchEmplWithRnmOrEmail(String keyword){
        return emplDao.selectEmplProfilesWithRnmOrEmail(keyword);
    }

    @Transactional
    @Override
    public boolean modifyOneDept(Map<String, String> deptDataAndEmplId){
        return deptDao.updateDept(deptDataAndEmplId)>0;
    }

    @Override
    public List<DeptAndEmplDto> getProfilesOfMemberAndDeptName(String deptCd){
        return deptDao.selectMemberProfilesAndDeptName(deptCd);
    }

    @Override
    @Transactional
    public void modifyDeptMem(String deptCd, List<String> memIDs, String modifier) throws DataIntegrityViolationException {
        List<DeptAndEmplDto> oldMemList = deptDao.selectMemberProfilesAndDeptName(deptCd);

        HashSet<String> newMemIDs = new HashSet<>(memIDs);
        HashSet<String> oldMemIDs = new HashSet<>();
        for (DeptAndEmplDto de : oldMemList) {
            oldMemIDs.add(de.getEMPL_ID());
        }

        HashSet<String> memForRemoval = new HashSet<>(oldMemIDs);
        memForRemoval.removeAll(newMemIDs);

        if(!memForRemoval.isEmpty()){
            Map<String, Object> blngDepts = new HashMap<>();
            blngDepts.put("BLNG_DEPT_CD", deptCd);
            blngDepts.put("emplIDs", memForRemoval.toArray());

            try{
                blngDeptDao.deleteBlngDepts(blngDepts);
            } catch (InvocationTargetException e){
                logger.info("dept 구성원 수정 중 delete value 없음");
            }
        }

        newMemIDs.removeAll(oldMemIDs);

        if(!newMemIDs.isEmpty()){
            List<BlngDeptAndEmplIdDto> newMemList = new ArrayList<>();

            for (String newMemID : newMemIDs) {
                newMemList.add(new BlngDeptAndEmplIdDto(deptCd, newMemID, modifier, modifier));
            }

            try{
                blngDeptDao.insertBlngDepts(newMemList);
            } catch (InvocationTargetException e){
                logger.info("dept 구성원 수정 중 insert value 없음");
            }
        }
    }

    @Override
    public DeptAndEmplDto getDeptDetailInfo(String deptCd){
        return deptDao.selectOneDeptAndMngrAndCdrDeptCnt(deptCd);
    }
}
