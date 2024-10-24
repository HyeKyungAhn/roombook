package store.roombook.controller;

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
public class EmplController {
    @GetMapping("/signup")
    public ModelAndView getSignupPage() {
        return new ModelAndView("empl/signup.tiles");
    }

    @GetMapping("/signup/success")
    public ModelAndView getSignupSuccessPage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("empl/signupSuccess.tiles");

        String signInUri = linkTo(methodOn(EmplController.class).getSignInPage()).toUri().toString();
        String homeUri = linkTo(methodOn(HomeController.class).getHomePage()).toUri().toString();

        Map<String, String> linkMap = new HashMap<>();
        linkMap.put("signin", signInUri);
        linkMap.put("home", homeUri);

        ObjectMapper mapper = new ObjectMapper();
        try {
            mv.addObject("links", mapper.writeValueAsString(linkMap));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            mv.setViewName("redirect:/");
            return mv;
        }
        return mv;
    }

    @GetMapping("/signin")
    public ModelAndView getSignInPage() {
        return new ModelAndView("empl/signin.tiles");
    }
}
