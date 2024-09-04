package site.roombook.controller.dept;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import site.roombook.domain.DeptDto;
import site.roombook.domain.ServiceResult;
import site.roombook.service.DeptService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class DeptControllerTest {

    @Autowired
    @InjectMocks
    private DeptController deptController;

    @Mock
    private DeptService deptService;

    private MockMvc mockMvc;

    @Nested
    @DisplayName("부서 구성원 수정 테스트")
    class DeptMemberModificationTest {

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(deptController).build();
        }

        @Test
        @WithMockUser(roles = "EMPL_ADMIN")
        @DisplayName("성공")
        void success() throws Exception {
            when(deptService.modifyDeptMem(anyString(), ArgumentMatchers.any(), anyString())).thenReturn(new ServiceResult(true));

            Map<String, String> deptDataMap = new HashMap<>();
            deptDataMap.put("memIDs", "[\"aaaa\",\"bbbb\",\"cccc\"]");
            deptDataMap.put("deptCd", "000001");

            ObjectMapper objectMapper = new ObjectMapper();
            String stringifiedMap = objectMapper.writeValueAsString(deptDataMap);

            mockMvc.perform(post("/dept/mem")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(stringifiedMap))
                    .andExpect(MockMvcResultMatchers.content().string("SUCCESS"))
                    .andReturn();
        }

        @Test
        @WithMockUser(roles = "EMPL_ADMIN")
        @DisplayName("memIDs 파라미터가 없을 때")
        void noDeptCd() throws Exception {
            Map<String, String> deptDataMap = new HashMap<>();
            deptDataMap.put("memIDs", "[\"aaaa\",\"bbbb\",\"cccc\"]");

            ObjectMapper objectMapper = new ObjectMapper();
            String stringifiedMap = objectMapper.writeValueAsString(deptDataMap);

            mockMvc.perform(post("/dept/mem")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(stringifiedMap))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("INPUT_REQUIRED"))
                    .andReturn();
        }
    }


    @Nested
    @DisplayName("부서 구성원 수정 테스트")
    class DeptModificationTest {

        Logger logger = LoggerFactory.getLogger(DeptController.class);
        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(deptController).build();
        }

        @Test
        @WithMockUser(roles = "EMPL_ADMIN")
        @DisplayName("")
        void success() throws Exception {
            when(deptService.modifyOneDept(any(DeptDto.class))).thenReturn(true);
            MvcResult result = mockMvc.perform(post("/dept/mod")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .param("deptCd", "111111")
                            .param("deptNm", "test부서이름")
                            .param("engDeptNm", "testDeptName")
                            .param("mngrId", "managerId"))
                    .andExpect(MockMvcResultMatchers.status().isFound())
                    .andReturn();

            assertEquals("redirect:/dept/dept?deptCd=111111", result.getModelAndView().getViewName());
        }
    }
}