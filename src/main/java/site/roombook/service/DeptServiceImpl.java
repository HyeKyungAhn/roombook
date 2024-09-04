package site.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.ExceptionMsg;
import site.roombook.dao.BlngDeptDao;
import site.roombook.dao.DeptDao;
import site.roombook.dao.EmplDao;
import site.roombook.domain.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DeptServiceImpl implements DeptService {

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
    public boolean saveOneDept(List<DeptDto> deptList, String registerId) throws NullPointerException {
        DeptDto newDept = findNewDept(deptList);
        if(Objects.isNull(newDept)) return false;

        newDept.setRegisterId(registerId);
        newDept.setDeptCd(getUniqueDeptCode());

        int insertedDeptCount = deptDao.insertDept(newDept);
        if(insertedDeptCount == 0) return false;

        deptList.forEach( deptDto -> {
            deptDto.setModifierId(registerId);
            deptDto.setLastUpdDtm(LocalDateTime.now());
        });

        deptDao.updateAllDeptTreeOdrData(deptList);

        return true;
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
    public boolean modifyOneDept(DeptDto deptDataAndEmplId){
        return deptDao.updateDept(deptDataAndEmplId) > 0;
    }

    @Override
    public List<DeptAndEmplDto> getProfilesOfMemberAndDeptName(String deptCd){
        return deptDao.selectMemberProfilesAndDeptName(deptCd);
    }

    @Override
    @Transactional
    public ServiceResult modifyDeptMem(String deptCd, List<String> memIDs, String modifierId) throws DataIntegrityViolationException {
        List<EmplDto> oldMemList = emplDao.selectDeptMembers(deptCd);

        HashSet<String> newMemIDs = new HashSet<>(memIDs);
        HashSet<String> oldMemIDs = new HashSet<>();

        for (EmplDto de : oldMemList) {
            oldMemIDs.add(de.getEmplId());
        }

        HashSet<String> memForRemoval = new HashSet<>(oldMemIDs);
        memForRemoval.removeAll(newMemIDs);

        if(!memForRemoval.isEmpty()){
            Map<String, Object> blngDepts = new HashMap<>();
            blngDepts.put("blngDeptCd", deptCd);
            blngDepts.put("emplIDs", memForRemoval.toArray());

            int affectedRows = blngDeptDao.deleteBlngDepts(blngDepts);

            if (memForRemoval.size() != affectedRows) {
                return new ServiceResult(false, ExceptionMsg.DEPT_MEM_DELETE_FAIL);
            }
        }

        newMemIDs.removeAll(oldMemIDs);

        if(!newMemIDs.isEmpty()){
            List<BlngDeptAndEmplIdDto> newMemList = new ArrayList<>();

            for (String newMemID : newMemIDs) {
                newMemList.add(new BlngDeptAndEmplIdDto(deptCd, newMemID, modifierId, modifierId));
            }

            blngDeptDao.insertBlngDepts(newMemList);
        }

        return new ServiceResult(true);
    }

    @Override
    public DeptAndEmplDto getDeptDetailInfo(String deptCd){
        return deptDao.selectOneDeptAndMngrAndCdrDeptCnt(deptCd);
    }


    private String getUniqueDeptCode() {
        String generateDeptCd;
        DeptDto selectedDept;
        do {
            generateDeptCd = generateDeptCd();
            selectedDept = deptDao.selectDept(generateDeptCd);
        } while(selectedDept!=null);

        return generateDeptCd;
    }

    private DeptDto findNewDept(List<DeptDto> deptList) {
        for (DeptDto deptDto : deptList) {
            if(Objects.equals(deptDto.getDeptCd(), NO_DEPT_CD)){
                return deptDto;
            }
        }
        return null;
    }

    private String generateDeptCd() {
        return ((ThreadLocalRandom.current().nextInt(9999))+1)+""; // 1-9999
    }
}
