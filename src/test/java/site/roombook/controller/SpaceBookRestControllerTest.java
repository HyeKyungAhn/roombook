package site.roombook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.EmplDto;
import site.roombook.domain.ServiceResult;
import site.roombook.domain.SpaceBookDto;
import site.roombook.resolver.UserArgumentResolver;
import site.roombook.service.SpaceBookService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class SpaceBookRestControllerTest {
    @InjectMocks
    @Autowired
    private SpaceBookRestController spaceBookRestController;

    @Mock
    private SpaceBookService mockSpaceBookService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(spaceBookRestController)
                .setCustomArgumentResolvers(new UserArgumentResolver())
                .build();
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("대여 정보 저장 컨트롤러 호출 성공")
    void timeslotBookingTest() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", 123123);
        map.put("spaceBookCn", "회의");
        map.put("date", "2024/10/10");
        map.put("beginTime", "10:00");
        map.put("endTime", "12:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                .spaceBookSpaceNo(123123)
                .spaceBookCn("회의")
                .spaceBookDate(LocalDate.of(2024, 10, 10))
                .spaceBookBgnTm(LocalTime.of(10, 0))
                .spaceBookEndTm(LocalTime.of(12, 0))
                .build();

        EmplDto emplDto = EmplDto.EmplDtoBuilder().emplId("user").emplAuthNm("ROLE_RSC_ADMIN").build();
        when(mockSpaceBookService.bookTimeslot(spaceBookDto, emplDto)).thenReturn(new ServiceResult(true));

        mockMvc.perform(post("/api/book/timeslots")
                .content(jsonInput)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("유효하지 않은 날짜 데이터")
    void invalidDate() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", 123123);
        map.put("spaceBookCn", "회의");
        map.put("date", "20241010");
        map.put("beginTime", "10:00");
        map.put("endTime", "12:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("날짜 및 시간을 형식에 맞게 입력해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("유효하지 않은 시간 데이터")
    @Transactional
    void invalidDate2() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", 123123);
        map.put("spaceBookCn", "회의");
        map.put("date", "2024/10/10");
        map.put("beginTime", "99:00");
        map.put("endTime", "12:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("날짜 및 시간을 형식에 맞게 입력해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("공간번호가 없을 때")
    void notExistingSpaceNumber() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceBookCn", "회의");
        map.put("date", "2024/10/10");
        map.put("beginTime", "10:00");
        map.put("endTime", "12:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("예약정보가 잘못되었습니다.\n새로고침 후 다시 예약해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("공간번호가 유효하지 않을 때")
    void invalidSpaceNumber() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", "asdf");
        map.put("spaceBookCn", "회의");
        map.put("date", "2024/10/10");
        map.put("beginTime", "10:00");
        map.put("endTime", "12:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("잘못된 예약정보가 입력되었습니다.\n새로고침 후 다시 예약해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("유효하지 않은 예약 시작 시간")
    void invalidBeginTime() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", "1234");
        map.put("spaceBookCn", "회의");
        map.put("date", "2024/10/10");
        map.put("beginTime", "aaa");
        map.put("endTime", "12:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("날짜 및 시간을 형식에 맞게 입력해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("유효하지 않은 예약 종료 시간")
    void invalidEndTime() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", "1234");
        map.put("spaceBookCn", "회의");
        map.put("date", "2024/10/10");
        map.put("beginTime", "10:00");
        map.put("endTime", "asdf");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("날짜 및 시간을 형식에 맞게 입력해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("예약 시작 시간이 없을 때")
    void notExistingBeginTime() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", "1234");
        map.put("spaceBookCn", "회의");
        map.put("date", "2024/10/10");
        map.put("endTime", "12:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("예약 시간을 입력해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("예약 종료 시간이 없을 때")
    void notExistingEndTime() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", "1234");
        map.put("spaceBookCn", "회의");
        map.put("date", "2024/10/10");
        map.put("beginTime", "10:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("예약 시간을 입력해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "RSC_ADMIN")
    @DisplayName("예약 내용이 없을 때")
    void notExistingBookContent() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("spaceNo", "1234");
        map.put("date", "2024/10/10");
        map.put("beginTime", "10:00");
        map.put("endTime", "12:00");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(map);

        mockMvc.perform(post("/api/book/timeslots")
                        .content(jsonInput)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("예약 내용을 입력해주세요.")))
                .andExpect(jsonPath("$.result", is("FAIL")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("예약 내역이 없을 때")
    void noBookedTimeslots() throws Exception {
        when(mockSpaceBookService.getPersonalTimeslotsCount(anyString())).thenReturn(0);

        mockMvc.perform(get("/api/mybook/timeslots")
                .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("예약 내역이 있을 때")
    void hasBookedTimeslots() throws Exception {
        when(mockSpaceBookService.getPersonalTimeslotsCount(anyString())).thenReturn(10);
        when(mockSpaceBookService.getPersonalTimeslots(anyString(), anyInt(), anyInt())).thenReturn(List.of(SpaceBookDto.spaceBookDtoBuilder().build()));

        mockMvc.perform(get("/api/mybook/timeslots")
                        .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}