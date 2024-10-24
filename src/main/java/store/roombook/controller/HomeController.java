package store.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import store.roombook.CmnCode;
import store.roombook.FileStorageProperties;
import store.roombook.domain.SpaceInfoAndTimeslotDto;
import store.roombook.service.SpaceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
public class HomeController {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private FileStorageProperties properties;

    @GetMapping("/")
    public ModelAndView getHomePage() {
        ModelAndView mv = new ModelAndView();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String spaceList;

        try {
            SpaceInfoAndTimeslotDto spaceInfoAndTimeslotDto = SpaceInfoAndTimeslotDto.SpaceRescFileDtoBuilder()
                    .atchLocCd(CmnCode.ATCH_LOC_CD_SPACE.getCode())
                    .spaceHideYn('N')
                    .spaceCnt(6)
                    .offset(0)
                    .build();
            List<SpaceInfoAndTimeslotDto> list = spaceService.getSpaceList(spaceInfoAndTimeslotDto);
            spaceList = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            spaceList = "";
        }

        String spaceDetailUri = linkTo(methodOn(SpaceController.class).getSpaceDetailPage(null)).toString();
        String moreSpaceUri = linkTo(methodOn(SpaceController.class).getSpaceList(null, null)).toString().replace("{?page}", "");
        mv.addObject("spaceDetailUri", spaceDetailUri);
        mv.addObject("moreSpaceUri", moreSpaceUri);
        mv.addObject("imgPath", properties.getThumbnailUploadPath());
        mv.addObject("noImgPath", "/img/noImg.png");
        mv.addObject("spaces", spaceList);
        mv.setViewName("home.tiles");
        return mv;
    }

    @GetMapping("/invalid-access")
    public ModelAndView getInvalidAccessPage() {
        ModelAndView mv = new ModelAndView();

        Map<String, String> linkMap = new HashMap<>();
        linkMap.put("home", linkTo(methodOn(HomeController.class).getHomePage()).withRel("home").toUri().toString());

        mv.addObject("links", stringifyLinkMap(linkMap));
        mv.setViewName("invalidAccess.tiles");

        return mv;
    }

    @GetMapping("/not-found")
    public ModelAndView getNotFoundPage() {
        ModelAndView mv = new ModelAndView();

        Map<String, String> linkMap = new HashMap<>();
        linkMap.put("home", linkTo(methodOn(HomeController.class).getHomePage()).withRel("home").toUri().toString());

        mv.addObject("links", stringifyLinkMap(linkMap));
        mv.setViewName("notFound.tiles");

        return mv;
    }

    @GetMapping("/error")
    public ModelAndView getServerErrorPage(){
        return new ModelAndView("error500.tiles");
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
