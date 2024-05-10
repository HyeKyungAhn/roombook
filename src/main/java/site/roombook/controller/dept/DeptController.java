package site.roombook.controller.dept;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.roombook.ExceptionMsg;
import site.roombook.domain.DeptAndEmplDto;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;
import site.roombook.service.DeptService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/dept")
public class DeptController {
    private static final Logger logger = LogManager.getLogger();
    // TODO : 모든 메서드 권환 확인 필요

    @Autowired
    DeptService deptService;

    @ExceptionHandler(IllegalArgumentException.class)
    public String catcher(IllegalArgumentException e){
        return "error";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String catcher2(NoSuchElementException e){
        return "error";
    }


    @GetMapping("/dept")
    public String getDeptDetailPage(@RequestParam String deptCd, Model m){
        if(Strings.isEmpty(deptCd)){
            throw new IllegalArgumentException("/dept/dept GET method, no DeptCd");
        }

        DeptAndEmplDto deptAndMngrData = deptService.getDeptDetailInfo(deptCd);

        if(Objects.isNull(deptAndMngrData)){
            throw new NoSuchElementException("Not Existing dept");
        }

        m.addAttribute("deptAndMngrData", deptAndMngrData);
        m.addAttribute("memberInfo", deptService.getDeptMembers(deptCd));
        return "dept/deptDetail";
    }

    @GetMapping("/mod")
    public String getDeptModPage(@RequestParam String deptCd, Model m){
        if(Strings.isEmpty(deptCd)){
            throw new IllegalArgumentException("/dept/mod GET method, no DeptCd");
        }

        DeptDto deptDto = deptService.getOneDept(deptCd);

        if(Objects.isNull(deptDto)){
            throw new NoSuchElementException("Not Existing dept");
        }

        String mngrEmplNo = deptDto.getDEPT_MNGR_EMPL_NO();

        if(!Strings.isEmpty(mngrEmplNo)){
            m.addAttribute("mngr", deptService.getDeptMngr(mngrEmplNo));
        }

        m.addAttribute("deptInfo", deptDto);
        return "dept/deptMod";
    }

    @GetMapping("/list")
    public String getDeptListPage(){
        return "/dept/deptList";
    }

    @GetMapping("/move")
    public String getDeptMovePage(){
        return "/dept/deptMove";
    }

    @GetMapping(value = "/tree", produces = "application/json;charset=UTF-8")
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

    @GetMapping(value = "/tree2", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getDeptTreeDataForInsert(String deptNm, String parent, String mngrId){
        List<DeptDto> deptDtoList = deptService.getAllDeptTreeData();

        DeptDto newDept = new DeptDto();
        newDept.setDEPT_CD(DeptService.NO_DEPT_CD);
        newDept.setUPP_DEPT_CD(parent);
        newDept.setDEPT_NM(deptNm);
        newDept.setDEPT_MNGR_EMPL_NO(mngrId);

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

    @PostMapping(value = "/move", produces = "application/json;charset=UTF-8")
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
            , String mngrId
            , HttpServletRequest req
            , RedirectAttributes rattr){

        if(deptNm.isEmpty() || engDeptNm.isEmpty()){
            rattr.addFlashAttribute("msg", "NO_INPUT");
            addAttribute(deptNm, engDeptNm, parent, mngrId, rattr);
            return "redirect:"+req.getHeader("Referer");
        } else if(deptService.haveIdenticalDeptNm(deptNm)){
            rattr.addFlashAttribute("msg", "SAME_NM");//부서이름 중복
            addAttribute(deptNm, engDeptNm, parent, mngrId, rattr);
            return "redirect:"+req.getHeader("Referer");
        }
        return "/dept/deptInsert2";
    }

    @PostMapping(value = "/save2", produces = "application/json;charset=UTF-8")
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

    @PostMapping(value = "/del", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteDeptWithNoMember(@RequestBody String deptCd){
        try{
            deptService.deleteDept(deptCd);
        } catch (IllegalArgumentException e){
            String msg;
            if(e.getMessage().equals(ExceptionMsg.DEPT_DELETE_MEMBER.getCode())){
                msg = "MEM";
            } else if(e.getMessage().equals(ExceptionMsg.DEPT_DELETE_SUB_DEPT.getCode())){
                msg = "SUB_DEPT";
            } else {
                msg = "DEL_FAIL";
            }
            return msg;
        }
        return "DEL_OK";
    }

    @GetMapping("/memSrch")
    @ResponseBody
    public String searchEmplWithKeyword(String keyword){
        List<EmplDto> emplProfilelist = deptService.searchEmplWithRnmOrEmail(keyword);
        ObjectMapper objMapper = new ObjectMapper();

        String emplProfile = "";

        try {
            emplProfile = objMapper.writeValueAsString(emplProfilelist);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return emplProfile;
    }

    @PostMapping("/mod")
    public String modifyDept(@RequestParam(value = "deptCd", required = false) String DEPT_CD,
                             @RequestParam(value = "deptNm", required = false) String DEPT_NM,
                             @RequestParam(value = "engDeptNm", required = false) String ENG_DEPT_NM,
                             String mngrId,
                             RedirectAttributes rattr,
                             HttpServletRequest req){
        if(Strings.isEmpty(DEPT_CD)||Strings.isEmpty(DEPT_NM)){
            rattr.addFlashAttribute("msg", "DEPTNAME_REQUIRED");
            return "redirect:"+req.getHeader("referer");
        }

        String LAST_UPDR_IDNF_NO = "asdf"; // TODO : 실제 수정자 EmplNo로 번경하기
        Map<String, String> deptData = new HashMap<>();
        deptData.put("EMPL_ID", mngrId);
        deptData.put("DEPT_NM",DEPT_NM);
        deptData.put("ENG_DEPT_NM",ENG_DEPT_NM);
        deptData.put("LAST_UPDR_IDNF_NO",LAST_UPDR_IDNF_NO);
        deptData.put("DEPT_CD",DEPT_CD);

        boolean isModifySuccess = deptService.modifyOneDept(deptData);

        if(isModifySuccess){
            rattr.addFlashAttribute("msg", "MOD_SUCCESS");
            return "redirect:/dept/dept?deptCd="+DEPT_CD;
        } else {
            rattr.addFlashAttribute("msg", "MOD_FAIL");
            return "redirect:/dept/mod?deptCd="+DEPT_CD;
        }
    }

    @GetMapping("/mem")
    public String getDeptMemPage(String deptCd, RedirectAttributes rattr, Model m){
        if(Strings.isEmpty(deptCd)){
            rattr.addFlashAttribute("msg", "INVALID_REQUEST");
            return "error";
        }

        List<DeptAndEmplDto> deptMembersAndName = deptService.getProfilesOfMemberAndDeptName(deptCd);
        m.addAttribute("deptCd", deptMembersAndName.get(0).getDEPT_CD());
        m.addAttribute("deptNm", deptMembersAndName.get(0).getDEPT_NM());
        m.addAttribute("engDeptNm", deptMembersAndName.get(0).getENG_DEPT_NM());
        m.addAttribute("deptMemAndDeptNm", deptMembersAndName);
        return "/dept/deptMemMod";
    }

    @PostMapping("/mem")
    @ResponseBody
    public String updateDeptMem(@RequestBody Map<String, Object> deptMemData){
        String emplNo = "admin"; //TODO: 관리자 emplNo로 추후 변경
        List<String> memIDs = (List<String>) deptMemData.get("memIDs");
        String deptCd = (String) deptMemData.get("deptCd");
        try{
            deptService.modifyDeptMem(deptCd, memIDs, emplNo);
        } catch (DataIntegrityViolationException e){
            logger.warn("empl에 없는 empl_id값으로 blng_dept update 시도");
            return "INVALID_INPUT";
        }
        return "SUCCESS";
    }

    private void addAttribute(String deptNm, String engDeptNm, String parent, String mngr, RedirectAttributes rattr) {
        rattr.addFlashAttribute("deptNm", deptNm);
        rattr.addFlashAttribute("engDeptNm", engDeptNm);
        rattr.addFlashAttribute("parent", parent);
        rattr.addFlashAttribute("mngr", mngr);
    }
}
