package site.roombook.controller.dept;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.roombook.domain.DeptDto;
import site.roombook.service.DeptService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/dept")
public class DeptController {
    // TODO : 모든 메서드 권환 확인 필요

    @Autowired
    DeptService deptService;

    @GetMapping("/move")
    public String getDeptMovePage(){
        return "/dept/deptMove";
    }

    @GetMapping("/tree")
    @ResponseBody
    public String getDeptTreeDataForMove(){
        //권한 확인 필요
        //TODO : mngr -> empl id로 대체 고려해보기
        List<DeptDto> deptDtoList = deptService.getAllDeptTreeData();
        ObjectMapper objMapper = new ObjectMapper();

        String deptData = "";
        try {
            deptData = objMapper.writeValueAsString(deptDtoList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return deptData;
    }

    @GetMapping("/tree2")
    @ResponseBody
    public String getDeptTreeDataForInsert(String deptNm, String parent, String mngr){
        List<DeptDto> deptDtoList = deptService.getAllDeptTreeData();

        DeptDto newDept = new DeptDto();
        newDept.setDEPT_CD(DeptService.NO_DEPT_CD);
        newDept.setUPP_DEPT_CD(parent);
        newDept.setDEPT_NM(deptNm);
        newDept.setDEPT_MNGR_EMPL_NO(mngr);

        int max = getMaxOdrInSameParent(newDept.getUPP_DEPT_CD(), deptDtoList);
        newDept.setDEPT_SORT_ODR(max+1);

        deptDtoList.add(newDept);

        ObjectMapper objMapper = new ObjectMapper();

        String deptData = "";
        try {
            deptData = objMapper.writeValueAsString(deptDtoList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return deptData;
    }

    private int getMaxOdrInSameParent(String parent, List<DeptDto> deptDtoList) {
        int max = 0;
        int odr;
        for (DeptDto deptDto : deptDtoList) {
            if(deptDto.getUPP_DEPT_CD().equals(parent)){
                odr = deptDto.getDEPT_SORT_ODR();
                max = Math.max(odr, max);
            }
        }

        return max;
    }

    @PostMapping("/move")
    @ResponseBody
    public String modifyDeptDataForMove(@RequestBody List<DeptDto> list){
        list.forEach( deptDto -> deptDto.setLAST_UPDR_IDNF_NO("ahk")); // TODO : 실제 변경 직원 이름 넣기
        return deptService.modifyDeptOdr(list)+"";
    }

    @GetMapping("/save")
    public String getDeptInsertPage(Model m){
        List<DeptDto> result = deptService.getDeptCdAndNm();

        m.addAttribute("CdAndNm", result);
        return "/dept/deptInsert";
    }

    @PostMapping("/save")
    public String getDeptInsertPage2(String deptNm
            , String engDeptNm
            , String parent
            , String mngr
            , HttpServletRequest req
            , RedirectAttributes rattr){

        if(deptNm.isEmpty() || engDeptNm.isEmpty() || mngr.isEmpty()){
            rattr.addFlashAttribute("msg", "NO_INPUT");
            addAttribute(deptNm, engDeptNm, parent, mngr, rattr);
            return "redirect:"+req.getHeader("Referer");
        } else if(deptService.haveIdenticalDeptNm(deptNm)){
            rattr.addFlashAttribute("msg", "SAME_NM");//부서이름 중복
            addAttribute(deptNm, engDeptNm, parent, mngr, rattr);
            return "redirect:"+req.getHeader("Referer");
        }
        return "/dept/deptInsert2";
    }

    @PostMapping("/save2")
    @ResponseBody
    public String insertOneDeptAndModifyDeptTreeData(HttpServletRequest req
            , RedirectAttributes rattr
            , @RequestBody List<DeptDto> list){
        String emplNo = "asdf"; // TODO : 직원테이블 관련 기능 구현 후 실제 작성자 번호로 수정
        boolean result;
        try {
            result = deptService.saveOneDept(list, emplNo);
        } catch(DuplicateKeyException e){
            result = deptService.saveOneDept(list, emplNo);
        } catch(NullPointerException e){
            e.printStackTrace();
            rattr.addFlashAttribute("msg", "NO_DEPT");
            return "redirect:" + req.getHeader("Referer");
        }

        if(!result){
            rattr.addFlashAttribute("msg", "WRONG_INPUT");
            return "redirect:" + req.getHeader("Referer");
        }

        return req.getHeader("origin")+"/dept/save";
    }

    private void addAttribute(String deptNm, String engDeptNm, String parent, String mngr, RedirectAttributes rattr) {
        rattr.addFlashAttribute("deptNm", deptNm);
        rattr.addFlashAttribute("engDeptNm", engDeptNm);
        rattr.addFlashAttribute("parent", parent);
        rattr.addFlashAttribute("mngr", mngr);
    }
}
