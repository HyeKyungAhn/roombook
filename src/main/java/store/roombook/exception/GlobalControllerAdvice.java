package store.roombook.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import store.roombook.controller.HomeController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<String> handleMaxUploadExceedException(MaxUploadSizeExceededException e) {
        return ResponseEntity.badRequest().header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8).body("파일은 총 5MB까지 업로드할 수 있습니다");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(){
        ModelAndView mv = new ModelAndView();

        Map<String, String> linkMap = new HashMap<>();
        linkMap.put("home", linkTo(methodOn(HomeController.class).getHomePage()).withRel("home").toUri().toString());

        mv.addObject("links", stringifyMap(linkMap));
        mv.setViewName("notFound.tiles");
        return mv;
    }

    private String stringifyMap(Map<String,String> map) {
        String stringifiedLinkMap = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            stringifiedLinkMap = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return stringifiedLinkMap;
    }

}
