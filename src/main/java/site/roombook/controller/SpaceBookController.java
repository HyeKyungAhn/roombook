package site.roombook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import site.roombook.domain.SpaceBookAndSpaceDto;
import site.roombook.domain.SpaceDto;
import site.roombook.service.SpaceBookService;
import site.roombook.service.SpaceService;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping("/book")
public class SpaceBookController {

    @Autowired
    SpaceService spaceService;

    @Autowired
    SpaceBookService spaceBookService;

    @GetMapping("/timeslots/{space-no}")
    public ModelAndView getSpaceBookingPage(@PathVariable("space-no") Integer spaceNo, @RequestParam(required = false) String date, ModelAndView mv, HttpServletResponse response) {
        SpaceDto spaceDto = spaceService.getSpaceDataForBooking(spaceNo);
        if (spaceDto == null) {
            mv.setViewName("redirect:/not-found");
        } else {
            String result;
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                objectMapper.registerModule(new JavaTimeModule());
                result = objectMapper.writeValueAsString(spaceDto);
            } catch (JsonProcessingException e) {
                mv.setViewName("redirect:/error");
                return mv;
            }

            String bookingUrl = linkTo(methodOn(SpaceBookRestController.class).bookTimeslot(null)).toUri().toString();
            String bookedTimeslotsUrl = linkTo(methodOn(SpaceBookRestController.class).getTimeslotOfTheDay(spaceNo, null)).toUri().toString();
            String spaceDetailPageUrl = linkTo(methodOn(SpaceController.class).getSpaceDetailPage(spaceNo, null)).toUri().toString();
            mv.addObject("bookedTimeslotsUrl", bookedTimeslotsUrl);
            mv.addObject("bookingUrl", bookingUrl);
            mv.addObject("spaceDetailPageUrl", spaceDetailPageUrl);
            mv.addObject("date", date);
            mv.addObject("spaceData", result);
            mv.setViewName("spaceBook/book");
        }
        return mv;
    }

    @GetMapping("/timeslots/{spaceBookId}/edit")
    public ModelAndView getModifyingTimeslotPage(@PathVariable("spaceBookId") String spaceBookId, ModelAndView mv) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        SpaceBookAndSpaceDto spaceAndBookData = spaceBookService.getTimeslot(spaceBookId, userDetails.getUsername());
        if (spaceAndBookData == null) {
            mv.setViewName("redirect:/not-found");
            return mv;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonSpaceAndBookData;

        try {
            jsonSpaceAndBookData = objectMapper.writeValueAsString(spaceAndBookData);
        } catch (JsonProcessingException e) {
            mv.setViewName("redirect:/error");
            return mv;
        }
        // TODO:  location은 내 예약 페이지 목록 구현 후 추가할 것
        String modifyingTimeslotUrl = linkTo(methodOn(SpaceBookRestController.class).modifyTimeslot(spaceBookId, null)).toUri().toString();
        String bookedTimeslotsUrl = linkTo(methodOn(SpaceBookRestController.class).getTimeslotOfTheDay(spaceAndBookData.getSpaceNo(), null)).toUri().toString();
        String spaceDetailPageUrl = linkTo(methodOn(SpaceController.class).getSpaceDetailPage(spaceAndBookData.getSpaceNo(), null)).toUri().toString();
        mv.addObject("modifyingUrl", modifyingTimeslotUrl);
        mv.addObject("bookedTimeslotsUrl", bookedTimeslotsUrl);
        mv.addObject("spaceDetailPageUrl", spaceDetailPageUrl);
        mv.addObject("spaceAndBookData", jsonSpaceAndBookData);
        mv.setViewName("spaceBook/bookEdit");
        return mv;
    }
}
