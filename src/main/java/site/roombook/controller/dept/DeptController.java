package site.roombook.controller.dept;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.roombook.domain.DeptDto;
import site.roombook.service.DeptService;

import java.util.List;

@Controller
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    DeptService deptService;

    @GetMapping("/dept")
    public String getDeptMovePage(Model m){
        return "/dept/deptMove";
    }

    @GetMapping("/data")
    @ResponseBody
    public String getDeptDataForTree(){
        //권한 확인 필요
        List<DeptDto> deptDtoList = deptService.getAllDept();
        ObjectMapper objMapper = new ObjectMapper();

        String deptData = "";
        try {
            deptData = objMapper.writeValueAsString(deptDtoList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return deptData;
    }

    @PostMapping("/data")
    @ResponseBody
    public String saveDeptTreeData(@RequestBody List<DeptDto> list){
        list.forEach( deptDto -> deptDto.setLAST_UPDR_IDNF_NO("ahk"));
        return deptService.modifyDeptOdr(list)+"";
    }
}
