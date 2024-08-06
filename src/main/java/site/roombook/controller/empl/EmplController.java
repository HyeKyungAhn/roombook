package site.roombook.controller.empl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class EmplController {
    @GetMapping("/signup")
    public ModelAndView getSignupPage() {
        return new ModelAndView("empl/signup");
    }

    @GetMapping("/signup-success")
    public ModelAndView getSignupSuccessPage() {
        return new ModelAndView("empl/signupSuccess");
    }

    @GetMapping("/error")
    public ModelAndView getServerErrorPage(){
        return new ModelAndView("error500");
    }
}
