package store.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import store.roombook.domain.RescDto;
import store.roombook.service.RescService;

import java.util.List;

@Controller
public class RescController {

    @Autowired
    RescService rescService;

    @GetMapping(value = "/spaces/rescs", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getResourceSuggestions(String keyword){
        List<RescDto> suggestions = rescService.getRescsSuggestions(keyword);
        ObjectMapper objMapper = new ObjectMapper();

        String jsonResponse = "";
        try {
            jsonResponse = objMapper.writeValueAsString(suggestions);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
}
