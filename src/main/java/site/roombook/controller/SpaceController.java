package site.roombook.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import site.roombook.CmnCode;
import site.roombook.FileStorageProperties;
import site.roombook.domain.*;
import site.roombook.service.FileService;
import site.roombook.service.RescService;
import site.roombook.service.SpaceService;
import site.roombook.service.SpaceTransactionService;

import java.util.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Controller
public class SpaceController {

    @Autowired
    FileService fileService;

    @Autowired
    SpaceService spaceService;

    @Autowired
    RescService rescService;

    @Autowired
    SpaceTransactionService spaceTransactionService;

    @Autowired
    FileStorageProperties properties;

    static final int SPACE_CNT_OF_LIST = 5;
    static final int RESC_CNT_OF_LIST = 3;

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException e){
        return "space/notfound";
    }

    @GetMapping("/admin-spaces/new")
    public ModelAndView getSpaceCreationPage(ModelAndView mv){
        mv.setViewName("space/adminSpaceInsert");
        mv.addObject("spaceSaveRequestUrl", WebMvcLinkBuilder.linkTo(methodOn(SpaceRestController.class).saveNewSpace(null, null, null)).toUri().toString());
        return mv;
    }

    @GetMapping("/admin-spaces/{spaceNo:[0-9]{1,9}}/edit")
    public ModelAndView getModifySpacePage(@PathVariable("spaceNo") Integer spaceNo, ModelAndView mv){
        try{
            getDetailPage(spaceNo, mv, false);

            String spaceModificationUri = linkTo(methodOn(SpaceRestController.class).modifySpace(null, spaceNo, null, null, null)).toUri().toString();
            mv.addObject("modificationRequestUrl", spaceModificationUri);
            mv.setViewName("space/adminSpaceMod");
        } catch (IllegalArgumentException e){
            mv.setViewName("space/notfound");
        }

      return mv;
    }

    @GetMapping("/admin-spaces")
    public ModelAndView getAdminSpaceList(@RequestParam(required = false, name = "page", defaultValue = "1") Integer page, ModelAndView mv){
        PageHandler ph = new PageHandler(spaceService.getSpaceAllCnt(), page);

        if (page < 0 || page > ph.getTotalPage()) {
            return new ModelAndView("space/notfound");
        }

        List<SpaceRescFileDto> list = spaceService.getSpaceList(SPACE_CNT_OF_LIST, ph.getOffset(), RESC_CNT_OF_LIST, CmnCode.ATCH_LOC_CD_SPACE, false);
        mv.addObject("list", list);
        mv.addObject("thumbnailPath", properties.getThumbnailUploadPath());
        mv.addObject("ph", ph);
        mv.setViewName("space/adminSpaceList");
        return mv;
    }

    @GetMapping("/admin-spaces/{spaceNo:[0-9]{1,9}}")
    public ModelAndView getAdminSpaceDetailPage(@PathVariable("spaceNo") int spaceNo, ModelAndView mv){
        try{
            getDetailPage(spaceNo, mv, false);
            mv.addObject("imgPath", properties.getOriginalUploadPath());
            mv.setViewName("space/adminSpaceDetail");
        } catch (IllegalArgumentException e){
            mv.setViewName("space/notfound");
        }

        return mv;
    }

    @GetMapping("/spaces")
    public ModelAndView getSpaceList(@RequestParam(required = false, name = "page", defaultValue = "1") int page, ModelAndView mv){
        PageHandler ph = new PageHandler(spaceService.getNotHiddenSpaceCnt(), page);

        if(page<0 || page>ph.getTotalPage()){
            return new ModelAndView("space/notfound");
        }

        List<SpaceRescFileDto> list = spaceService.getSpaceList(SPACE_CNT_OF_LIST, ph.getOffset(), RESC_CNT_OF_LIST, CmnCode.ATCH_LOC_CD_SPACE, true);
        mv.addObject("list", list);
        mv.addObject("thumbnailPath", properties.getThumbnailUploadPath());
        mv.addObject("ph", ph);
        mv.setViewName("space/spaceList");
        return mv;
    }

    @GetMapping("/spaces/{spaceNo:[0-9]{1,9}}")
    public ModelAndView getSpaceDetailPage(@PathVariable("spaceNo") int spaceNo, ModelAndView mv){
        try{
            getDetailPage(spaceNo, mv, true);
            mv.addObject("imgPath", properties.getOriginalUploadPath());
            mv.setViewName("space/spaceDetail");
        } catch (IllegalArgumentException e){
            mv.setViewName("space/notfound");
            return mv;
        }
        return mv;
    }

    private void getDetailPage(int spaceNo, ModelAndView mv, boolean isHiddenSpaceInvisible) throws IllegalArgumentException{
        Map<String, Object> spaceDetails = spaceService.getOneSpaceAndDetails(spaceNo, CmnCode.ATCH_LOC_CD_SPACE, isHiddenSpaceInvisible);

        if(spaceDetails.isEmpty()){
            throw new IllegalArgumentException("Non-existing or hidden space");
        }

        String jsonRescs = null;
        String jsonFiles = null;

        try {
            jsonRescs = convertToJson(spaceDetails, "rescs");
            jsonFiles = convertToJson(spaceDetails, "files");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        mv.addObject("space", spaceDetails.get("space"));
        mv.addObject("files", jsonFiles);
        mv.addObject("resources", jsonRescs);
    }

    private String convertToJson(Map<String, Object> spaceDetail, String targetDataName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return objectMapper.writeValueAsString(spaceDetail.get(targetDataName));
    }
}
