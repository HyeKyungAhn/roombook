package site.roombook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @ExceptionHandler(Exception.class)
    public String home(Exception ex){
        return "error";
    }

    @GetMapping("/home")
    public String home(@RequestParam int age){
        return "home/home";
    }
}
