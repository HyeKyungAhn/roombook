package site.roombook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminEmplController {

    @GetMapping("/admin/empls")
    public ModelAndView getAdminEmplListPage() {
        return new ModelAndView("admin/empl/list");
    }
}
