package site.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.dao.DeptDao;
import site.roombook.domain.DeptDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    DeptDao deptDao;

    @Transactional
    @Override
    public boolean saveOneDept(DeptDto deptDto){
        int rowCnt = 0;
        try {
            rowCnt = deptDao.insertDept(deptDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowCnt == 1;
    }

    @Transactional
    @Override
    public List<DeptDto> getAllDept(){
        try {
            return deptDao.selectAllDept();
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
            rowCnt = deptDao.updateAllDeptTreeData(list);
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
}
