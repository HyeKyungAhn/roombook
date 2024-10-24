package store.roombook.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import store.roombook.domain.SpaceBookAndSpaceDto;
import store.roombook.domain.SpaceDto;
import store.roombook.service.SpaceBookService;
import store.roombook.service.SpaceService;

import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
public class SpaceBookController {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private SpaceBookService spaceBookService;

    @GetMapping("/book/timeslots/{space-no}")
    public ModelAndView getSpaceBookingPage(@PathVariable("space-no") Integer spaceNo, @RequestParam(required = false) String date, ModelAndView mv, HttpServletResponse response) {
        SpaceDto spaceDto = spaceService.getSpaceDataForBooking(spaceNo);
        if (spaceDto == null) {
            mv.setViewName("redirect:/not-found.tiles");
        } else {
            String result;
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                objectMapper.registerModule(new JavaTimeModule());
                result = objectMapper.writeValueAsString(spaceDto);
            } catch (JsonProcessingException e) {
                mv.setViewName("redirect:/error.tiles");
                return mv;
            }

            String bookingUrl = linkTo(methodOn(SpaceBookRestController.class).bookTimeslot(null, null)).toUri().toString();
            String bookedTimeslotsUrl = linkTo(methodOn(SpaceBookRestController.class).getTimeslotOfTheDay(spaceNo, null, null)).toUri().toString();
            String spaceDetailPageUrl = linkTo(methodOn(SpaceController.class).getSpaceDetailPage(spaceNo)).toUri().toString();
            mv.addObject("bookedTimeslotsUrl", bookedTimeslotsUrl);
            mv.addObject("bookingUrl", bookingUrl);
            mv.addObject("spaceDetailPageUrl", spaceDetailPageUrl);
            mv.addObject("date", date);
            mv.addObject("spaceData", result);
            mv.setViewName("spaceBook/book.tiles");
        }
        return mv;
    }

    @GetMapping("/book/timeslots/{bookId}/edit")
    public ModelAndView getChangingBookingPage(@PathVariable("bookId") String spaceBookId) {
        ModelAndView mv = new ModelAndView();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        SpaceBookAndSpaceDto spaceAndBookData = spaceBookService.getTimeslot(spaceBookId, userDetails.getUsername());
        if (spaceAndBookData == null) {
            mv.setViewName("redirect:/not-found.tiles");
            return mv;
        }

        LocalDate bookDate = spaceAndBookData.getSpaceBookDate();
        LocalTime bookTime = spaceAndBookData.getSpaceBookBgnTm();

        if (LocalDate.now().isAfter(bookDate)
                || LocalDate.now().isEqual(bookDate) && LocalTime.now().isAfter(bookTime)) {
            mv.setViewName("redirect:/invalid-access");
            return mv;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonSpaceAndBookData;

        try {
            jsonSpaceAndBookData = objectMapper.writeValueAsString(spaceAndBookData);
        } catch (JsonProcessingException e) {
            mv.setViewName("redirect:/error.tiles");
            return mv;
        }

        String modifyingTimeslotUrl = linkTo(methodOn(SpaceBookRestController.class).modifyTimeslot(spaceBookId, null, null)).toUri().toString();
        String bookedTimeslotsUrl = linkTo(methodOn(SpaceBookRestController.class).getTimeslotOfTheDay(spaceAndBookData.getSpaceNo(), null, null)).toUri().toString();
        String spaceDetailPageUrl = linkTo(methodOn(SpaceController.class).getSpaceDetailPage(spaceAndBookData.getSpaceNo())).toUri().toString();
        mv.addObject("modifyingUrl", modifyingTimeslotUrl);
        mv.addObject("bookedTimeslotsUrl", bookedTimeslotsUrl);
        mv.addObject("spaceDetailPageUrl", spaceDetailPageUrl);
        mv.addObject("spaceAndBookData", jsonSpaceAndBookData);
        mv.setViewName("spaceBook/bookEdit.tiles");
        return mv;
    }

    @GetMapping("/mybook")
    public ModelAndView getMyBookPage() {
        ModelAndView mv = new ModelAndView();
        String timeslotsRequestUri = linkTo(methodOn(SpaceBookRestController.class).getMyTimeslots(null, null, null)).toUri().toString();
        String selfUri = linkTo(methodOn(SpaceBookController.class).getMyBookPage()).toUri().toString();
        mv.addObject("self", selfUri);
        mv.addObject("timeslotsRequestUri", timeslotsRequestUri);
        mv.setViewName("spaceBook/myBook.tiles");
        return mv;
    }
}
