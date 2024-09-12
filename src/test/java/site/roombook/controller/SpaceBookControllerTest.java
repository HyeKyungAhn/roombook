package site.roombook.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import site.roombook.domain.SpaceDto;
import site.roombook.service.SpaceBookService;
import site.roombook.service.SpaceService;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class SpaceBookControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    @Autowired
    private SpaceBookController spaceBookController;

    @Mock
    private SpaceService spaceService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(spaceBookController).build();
    }

    @Test
    @DisplayName("check")
    void test() throws Exception {
        when(spaceService.getSpaceDataForBooking(123123)).thenReturn(new SpaceDto.Builder().build());

        MvcResult mvcResult = mockMvc.perform(get("/book/timeslots/123123")
                .param("date", "20240910"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mv = mvcResult.getModelAndView();

        assertNotNull(mv);
        assertEquals("http://localhost/api/spaces/123123/timeslots", mv.getModel().get("bookedTimeslotsUrl"));
    }
}