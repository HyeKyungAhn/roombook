package site.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.roombook.ExceptionMsg;
import site.roombook.FileStorageProperties;
import site.roombook.domain.DeptAndEmplDto;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;
import site.roombook.service.DeptService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping("/dept")
public class DeptController {
    @Autowired
    DeptService deptService;

    @Autowired
    private FileStorageProperties properties;

    @ExceptionHandler({IllegalArgumentException.class, NoSuchElementException.class})
    public String catcher(IllegalArgumentException e){
        return "error";
    }

    @GetMapping("/dept")
    public String getDeptDetailPage(@RequestParam String deptCd, Model m){
        if(Strings.isEmpty(deptCd)){
            return "notFound.adminTiles";
        }

        DeptAndEmplDto deptAndMngrData = deptService.getDeptDetailInfo(deptCd);

        if(Objects.isNull(deptAndMngrData)){
            return "notFound.adminTiles";
        }

        String deptList = linkTo(methodOn(DeptController.class).getDeptListPage()).toUri().toString();
        m.addAttribute("deptListUri", deptList);
        m.addAttribute("noImgPath", properties.getNoImgPath());
        m.addAttribute("deptAndMngrData", deptAndMngrData);
        m.addAttribute("memberInfo", deptService.getDeptMembers(deptCd));
        return "dept/deptDetail.adminTiles";
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

        String mngrEmplNo = deptDto.getDeptMngrEmplNo();

        if(!Strings.isEmpty(mngrEmplNo)){
            m.addAttribute("mngr", deptService.getDeptMngr(mngrEmplNo));
        }

        m.addAttribute("noImgPath", properties.getNoImgPath());
        m.addAttribute("deptInfo", deptDto);
        return "dept/deptMod.adminTiles";
    }

    @GetMapping("/list")
    public ModelAndView getDeptListPage(){
        return new ModelAndView("dept/deptList.adminTiles");
    }

    @GetMapping("/move")
    public String getDeptMovePage(){
        return "dept/deptMove.adminTiles";
    }

    @GetMapping(value = "/tree", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getDeptTreeDataForMove(){
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

        DeptDto newDept = DeptDto.DeptDtoBuilder()
                .deptCd(DeptService.NO_DEPT_CD)
                .uppDeptCd(parent)
                .deptNm(deptNm)
                .deptMngrEmplNo(mngrId).build();

        int max = getMaxOdrInSameParent(newDept.getUppDeptCd(), deptDtoList);
        newDept = modifyDeptSortOdr(newDept, max + 1);

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

    private DeptDto modifyDeptSortOdr(DeptDto deptDto, int maxOrderInSameParent){
        return DeptDto.DeptDtoBuilder()
                .deptCd(deptDto.getDeptCd())
                .uppDeptCd(deptDto.getUppDeptCd())
                .deptNm(deptDto.getDeptNm())
                .deptMngrEmplNo(deptDto.getDeptMngrEmplNo())
                .deptSortOdr(maxOrderInSameParent)
                .build();
    }

    @PostMapping(value = "/move", produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> modifyDeptDataForMove(@RequestBody List<DeptDto> list){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, String> map = new HashMap<>();
        map.put("result", "SUCCESS");
        map.put("redirectUrl", "/dept/list");
        try {
            int modifiedDeptCount = deptService.modifyDeptOdr(list, userDetails.getUsername());
            map.put("msg", modifiedDeptCount == 0 ? "수정사항이 없습니다." : "부서 이동이 완료되었습니다.");
        } catch (Exception e) {
            map.put("msg", "부서이동이 정상적으로 처리되지 않았습니다.");
        }

        String result = "";
        try {
            result = new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @GetMapping("/save")
    public String getDeptInsertPage(Model m){
        List<DeptDto> result = deptService.getDeptCdAndNm();

        m.addAttribute("CdAndNm", result);
        return "dept/deptInsert.adminTiles";
    }

    @PostMapping("/save")
    public String getDeptInsertPage2(String deptNm
            , String engDeptNm
            , String parent
            , String mngrId
            , HttpServletRequest req
            , RedirectAttributes rattr
            , Model m){

        if(deptNm.isEmpty() || engDeptNm.isEmpty()){
            rattr.addFlashAttribute("msg", "NO_INPUT");
            addAttribute(deptNm, engDeptNm, parent, mngrId, rattr);
            return "redirect:"+req.getHeader("Referer");
        } else if(deptService.haveIdenticalDeptNm(deptNm)){
            rattr.addFlashAttribute("msg", "SAME_NM");//부서이름 중복
            addAttribute(deptNm, engDeptNm, parent, mngrId, rattr);
            return "redirect:"+req.getHeader("Referer");
        }

        m.addAttribute("noImgPath", properties.getNoImgPath());
        return "dept/deptInsert2.adminTiles";
    }

    @PostMapping(value = "/save2", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String insertOneDeptAndModifyDeptTreeData(HttpServletRequest req
            , RedirectAttributes rattr
            , @RequestBody List<DeptDto> list){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        boolean result;
        try {
            result = deptService.saveOneDept(list, userDetails.getUsername());
        } catch(NullPointerException e){
            e.printStackTrace();
            rattr.addFlashAttribute("msg", "NO_DEPT");
            return "redirect:" + req.getHeader("Referer");
        }

        if(!result){
            rattr.addFlashAttribute("msg", "WRONG_INPUT");
            return "redirect:" + req.getHeader("Referer");
        }

        return req.getHeader("origin")+"/dept/list";
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

    @GetMapping(value = "/memSrch", produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    @ResponseBody
    public String searchEmplWithKeyword(String keyword){
        List<EmplDto> emplProfilelist = deptService.searchEmplWithRnmOrEmail(keyword);

        String emplProfile = "";

        try {
            emplProfile =  new ObjectMapper().writeValueAsString(emplProfilelist);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return emplProfile;
    }

    @PostMapping("/mod")
    public String modifyDept(@RequestParam(value = "deptCd", required = false) String deptCd,
                             @RequestParam(value = "deptNm", required = false) String deptName,
                             @RequestParam(value = "engDeptNm", required = false) String engDeptName,
                             String mngrId,
                             RedirectAttributes rattr,
                             HttpServletRequest req){
        if(Strings.isEmpty(deptCd)||Strings.isEmpty(deptName)){
            rattr.addFlashAttribute("msg", "DEPTNAME_REQUIRED");
            return "redirect:"+req.getHeader("referer");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        DeptDto deptDto = DeptDto.DeptDtoBuilder()
                .deptCd(deptCd)
                .emplId(mngrId)
                .deptNm(deptName)
                .engDeptNm(engDeptName)
                .lastUpdDtm(LocalDateTime.now())
                .modifierId(userDetails.getUsername())
                .build();

        boolean isModifySuccess = deptService.modifyOneDept(deptDto);

        if(isModifySuccess){
            rattr.addFlashAttribute("msg", "MOD_SUCCESS");
            return "redirect:/dept/dept?deptCd="+deptCd;
        } else {
            rattr.addFlashAttribute("msg", "MOD_FAIL");
            return "redirect:/dept/mod?deptCd="+deptCd;
        }
    }

    @GetMapping("/mem")
    public String getDeptMemPage(String deptCd, RedirectAttributes rattr, Model m){
        if(Strings.isEmpty(deptCd)){
            rattr.addFlashAttribute("msg", "INVALID_REQUEST");
            return "error.adminTiles";
        }

        List<DeptAndEmplDto> deptMembersAndName = deptService.getProfilesOfMemberAndDeptName(deptCd);
        m.addAttribute("deptCd", deptMembersAndName.get(0).getDeptCd());
        m.addAttribute("deptNm", deptMembersAndName.get(0).getDeptNm());
        m.addAttribute("engDeptNm", deptMembersAndName.get(0).getEngDeptNm());
        m.addAttribute("noImgPath", properties.getNoImgPath());

        if (!(deptMembersAndName.size() == 1 && deptMembersAndName.get(0).getEmplId() == null)) {
            m.addAttribute("deptMemAndDeptNm", deptMembersAndName);
        }
        return "/dept/deptMemMod.adminTiles";
    }

    @PostMapping("/mem")
    @ResponseBody
    public ResponseEntity<String> updateDeptMem(@RequestBody String body){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        JsonNode jsonNode;
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> memIds = new ArrayList<>();
        String deptCd;

        try {
            jsonNode  = objectMapper.readTree(body);

            if (!jsonNode.has("deptCd")) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("INPUT_REQUIRED");
            }

            if (jsonNode.has("memIds") && jsonNode.get("memIds").isArray()) {
                for(final JsonNode objNode : jsonNode.get("memIds")){
                    memIds.add(objNode.asText());
                }
            }
            deptCd = jsonNode.get("deptCd").asText();

            deptService.modifyDeptMem(deptCd, memIds, userDetails.getUsername());
        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("INVALID_INPUT");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("SUCCESS");
    }

    private void addAttribute(String deptNm, String engDeptNm, String parent, String mngr, RedirectAttributes rattr) {
        rattr.addFlashAttribute("deptNm", deptNm);
        rattr.addFlashAttribute("engDeptNm", engDeptNm);
        rattr.addFlashAttribute("parent", parent);
        rattr.addFlashAttribute("mngr", mngr);
    }

    private int getMaxOdrInSameParent(String parent, List<DeptDto> deptDtoList) {
        int max = 0;
        int odr;
        for (DeptDto deptDto : deptDtoList) {
            if(deptDto.getUppDeptCd().equals(parent)){
                odr = deptDto.getDeptSortOdr();
                max = Math.max(odr, max);
            }
        }

        return max;
    }
}
