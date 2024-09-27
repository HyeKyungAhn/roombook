package site.roombook.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import site.roombook.domain.PageHandler;
import site.roombook.domain.SpaceInfoAndTimeslotDto;
import site.roombook.domain.SpaceListDto;
import site.roombook.resolver.StringDateArgumentResolver;
import site.roombook.service.SpaceService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class SpaceRestControllerTest {

    @InjectMocks
    @Autowired
    private SpaceRestController spaceRestController;

    @Mock
    private SpaceService mockSpaceService;

    private MockMvc mockMvc;

    @Nested
    @DisplayName("사용자 공간 목록 조회 테스트")
    class SpaceListSelectinTest {

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(spaceRestController)
                    .setCustomArgumentResolvers(new StringDateArgumentResolver())
                    .build();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "-1"})
        @WithMockUser(roles = "USER")
        @DisplayName("유효하지 않은 page 숫자")
        void invalidPageNumberTest(String page) throws Exception {
            when(mockSpaceService.getNotHiddenSpaceCnt()).thenReturn(10);
            when(mockSpaceService.getSpaceList(any(PageHandler.class), any(SpaceInfoAndTimeslotDto.class))).thenReturn(SpaceListDto.SpaceListDto().build());

            mockMvc.perform(get("/api/spaces")
                            .param("page", page))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("유효하지 않은 page 문자")
        void invalidPageStringTest() throws Exception {
            when(mockSpaceService.getNotHiddenSpaceCnt()).thenReturn(10);
            when(mockSpaceService.getSpaceList(any(PageHandler.class), any(SpaceInfoAndTimeslotDto.class))).thenReturn(SpaceListDto.SpaceListDto().build());

            mockMvc.perform(get("/api/spaces")
                            .param("page", "ㅁ"))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("유효하지 않은 page 문자")
        void nullPageTest() throws Exception {
            when(mockSpaceService.getNotHiddenSpaceCnt()).thenReturn(10);
            when(mockSpaceService.getSpaceList(any(PageHandler.class), any(SpaceInfoAndTimeslotDto.class))).thenReturn(SpaceListDto.SpaceListDto().build());

            mockMvc.perform(get("/api/spaces"))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("공간 목록 데이터가 없을 때")
        void emptySpaceListSelection() throws Exception {
            when(mockSpaceService.getNotHiddenSpaceCnt()).thenReturn(0);

            mockMvc.perform(get("/api/spaces"))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("존재 하지 않는 공간 목록 페이지 요청 시")
        void requestNotExistingPage() throws Exception {
            when(mockSpaceService.getNotHiddenSpaceCnt()).thenReturn(10);

            mockMvc.perform(get("/api/spaces")
                    .param("page", "1000"))
                    .andExpect(MockMvcResultMatchers.status().isNotModified())
                    .andExpect(MockMvcResultMatchers.header().string("location", "http://localhost/not-found"));
        }


        @ParameterizedTest
        @ValueSource(strings = {"a", "123123123333", "aaaaaaaa", "-9999999", "00000000", "20240431"})
        @WithMockUser(roles = "USER")
        @DisplayName("유효하지 않은 date")
        void testInvalidDate(String date) throws Exception {
            when(mockSpaceService.getNotHiddenSpaceCnt()).thenReturn(10);
            when(mockSpaceService.getSpaceList(any(PageHandler.class), any(SpaceInfoAndTimeslotDto.class))).thenReturn(SpaceListDto.SpaceListDto().build());

            mockMvc.perform(get("/api/spaces")
                            .param("date", date))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("존재하지 않는 date")
        void testNullDate() throws Exception {
            when(mockSpaceService.getNotHiddenSpaceCnt()).thenReturn(10);
            when(mockSpaceService.getSpaceList(any(PageHandler.class), any(SpaceInfoAndTimeslotDto.class))).thenReturn(SpaceListDto.SpaceListDto().build());

            mockMvc.perform(get("/api/spaces"))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }

}