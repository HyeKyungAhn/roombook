package store.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import store.roombook.FileStorageProperties;
import store.roombook.annotation.StringDate;
import store.roombook.domain.SpaceDto;
import store.roombook.service.FileService;
import store.roombook.service.RescService;
import store.roombook.service.SpaceService;
import store.roombook.service.SpaceTransactionService;

import java.time.LocalDate;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException e){
        return "space/notfound";
    }

    @GetMapping("/admin/spaces/new")
    public ModelAndView getSpaceCreationPage(ModelAndView mv){
        mv.setViewName("space/adminSpaceInsert.adminTiles");
        mv.addObject("spaceSaveRequestUrl", WebMvcLinkBuilder.linkTo(methodOn(SpaceRestController.class).saveNewSpace(null, null, null, null)).toUri().toString());
        return mv;
    }

    @GetMapping("/admin/spaces/{spaceNo:[0-9]{1,9}}/edit")
    public ModelAndView getModifySpacePage(@PathVariable("spaceNo") Integer spaceNo){
        ModelAndView mv = new ModelAndView();
        SpaceDto spaceDto = spaceService.getOneSpaceAndDetails(spaceNo, false);
        String stringifiedSpaceDto = stringifyObject(spaceDto);

        String spaceModificationUri = linkTo(methodOn(SpaceRestController.class).modifySpace(spaceNo,null, null, null, null, null)).toUri().toString();
        mv.addObject("modificationRequestUrl", spaceModificationUri);
        mv.addObject("jsonSpace", stringifiedSpaceDto);
        mv.setViewName("space/adminSpaceMod.adminTiles");

        return mv;
    }

    @GetMapping("/admin/spaces")
    public ModelAndView getAdminSpaceList(@RequestParam(required = false, name = "page", defaultValue = "1") String page
            , @StringDate LocalDate date){
        ModelAndView mv = new ModelAndView();
        mv.addObject("page", page);
        mv.addObject("date", convertToBasicIsoDate(date));
        mv.addObject("spaceListRequestUrl"
                , linkTo(methodOn(SpaceRestController.class).getSpaceListForAdmin(null, null, null)).toUri().toString());
        mv.setViewName("space/spaceList.adminTiles");
        return mv;
    }

    @GetMapping("/admin/spaces/{spaceNo:[0-9]{1,9}}")
    public ModelAndView getAdminSpaceDetailPage(@PathVariable("spaceNo") Integer spaceNo){
        ModelAndView mv = new ModelAndView();
        SpaceDto spaceDto = spaceService.getOneSpaceAndDetails(spaceNo, false);

        if (spaceDto.getSpaceNo() == null) {
            mv.setViewName("space/notfound.adminTiles");
            return mv;
        }

        String stringifiedSpaceDto = stringifyObject(spaceDto);

        String editUri = linkTo(methodOn(SpaceController.class).getModifySpacePage(spaceNo)).toUri().toString();
        String spaceListUri = linkTo(methodOn(SpaceController.class).getAdminSpaceList(null, null)).toUri().toString();
        String bookingUri = linkTo(methodOn(SpaceBookController.class).getSpaceBookingPage(spaceNo, null, null, null)).toUri().toString();
        String requestTimeslotsUri = linkTo(methodOn(SpaceBookRestController.class).getTimeslotOfTheDay(spaceNo, null, null)).toUri().toString();

        mv.addObject("noImgPath", properties.getNoImgPath());
        mv.addObject("imgPath", properties.getOriginalUploadPath());
        mv.addObject("bookingUri", bookingUri);
        mv.addObject("editUri", editUri);
        mv.addObject("spaceListUri", spaceListUri);
        mv.addObject("jsonSpace", stringifiedSpaceDto);
        mv.addObject("requestTimeslots", requestTimeslotsUri);
        mv.setViewName("space/adminSpaceDetail.adminTiles");

        return mv;
    }

    @GetMapping("/spaces")
    public ModelAndView getSpaceList(@RequestParam(required = false, name = "page", defaultValue = "1") String page
    , @StringDate LocalDate date){
        ModelAndView mv = new ModelAndView();
        mv.addObject("page", page);
        mv.addObject("date", convertToBasicIsoDate(date));
        mv.addObject("spaceListRequestUrl"
                , linkTo(methodOn(SpaceRestController.class).getSpaceList(null, null, null)).toUri().toString());
        mv.setViewName("space/spaceList.tiles");
        return mv;
    }

    @GetMapping("/spaces/{spaceNo:[0-9]{1,9}}")
    public ModelAndView getSpaceDetailPage(@PathVariable("spaceNo") Integer spaceNo){
        ModelAndView mv = new ModelAndView();
        SpaceDto spaceDto = spaceService.getOneSpaceAndDetails(spaceNo, true);

        if (spaceDto.getSpaceNo() == null) {
            mv.setViewName("space/notfound.tiles");
            return mv;
        }

        String stringifiedSpaceDto = stringifyObject(spaceDto);

        String bookingUri = linkTo(methodOn(SpaceBookController.class).getSpaceBookingPage(spaceNo, null, null, null)).toUri().toString();
        String spaceListUri = linkTo(methodOn(SpaceController.class).getSpaceList(null, null)).toUri().toString();
        String requestTimeslotsUri = linkTo(methodOn(SpaceBookRestController.class).getTimeslotOfTheDay(spaceNo, null, null)).toUri().toString();

        mv.addObject("noImgPath", properties.getNoImgPath());
        mv.addObject("imgPath", properties.getOriginalUploadPath());
        mv.addObject("bookingUri", bookingUri);
        mv.addObject("spaceListUri", spaceListUri);
        mv.addObject("jsonSpace", stringifiedSpaceDto);
        mv.addObject("requestTimeslots", requestTimeslotsUri);
        mv.setViewName("space/spaceDetail.tiles");
        return mv;
    }

    private String stringifyObject(Object object) {
        String stringifiedObject;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            stringifiedObject = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            stringifiedObject = "";
        }

        return stringifiedObject;
    }

    private String convertToBasicIsoDate(LocalDate date) {
        return String.format("%d%2d%2d", date.getYear(),date.getMonthValue(),date.getDayOfMonth()).replace(' ', '0');
    }
}
