package site.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
public class HomeController {

    @GetMapping("/")
    public ModelAndView getHomePage() {
        return new ModelAndView("home");
    }

    @GetMapping("/invalid-access")
    public ModelAndView getInvalidAccessPage() {
        ModelAndView mv = new ModelAndView();

        Map<String, String> linkMap = new HashMap<>();
        linkMap.put("home", linkTo(methodOn(HomeController.class).getHomePage()).withRel("home").toUri().toString());

        mv.addObject("links", stringifyLinkMap(linkMap));
        mv.setViewName("invalidAccess");

        return mv;
    }

    @GetMapping("/not-found")
    public ModelAndView getNotFoundPage() {
        ModelAndView mv = new ModelAndView();

        Map<String, String> linkMap = new HashMap<>();
        linkMap.put("home", linkTo(methodOn(HomeController.class).getHomePage()).withRel("home").toUri().toString());

        mv.addObject("links", stringifyLinkMap(linkMap));
        mv.setViewName("notFound");

        return mv;
    }

    @GetMapping("/error")
    public ModelAndView getServerErrorPage(){
        return new ModelAndView("error500");
    }

    private String stringifyLinkMap(Map<String,String> linkMap) {
        String stringifiedLinkMap = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            stringifiedLinkMap = objectMapper.writeValueAsString(linkMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return stringifiedLinkMap;
    }
}
