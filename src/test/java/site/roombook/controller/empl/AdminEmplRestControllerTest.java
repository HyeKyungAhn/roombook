package site.roombook.controller.empl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import site.roombook.ExceptionMsg;
import site.roombook.domain.EmplDto;
import site.roombook.domain.ServiceResult;
import site.roombook.service.EmplService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
@DisplayName("사원 관리자 API 단위 테스트")
class AdminEmplRestControllerTest {

    @InjectMocks
    @Autowired
    private AdminEmplRestController adminEmplRestController;

    @Mock
    private EmplService mockEmplService;

    private MockMvc mockMvc;
    private final Logger logger = LoggerFactory.getLogger(AdminEmplRestController.class);

    private List<EmplDto> sixDummyEmpls;

    @Nested
    @DisplayName("사용자 목록 조회")
    class AdminEmplListTest {

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(adminEmplRestController).build();
            sixDummyEmpls = createSixEmplList();
        }

        @Test
        void searchByAuthNameOption() throws Exception {
            List<EmplDto> list = new ArrayList<>();
            list.add(sixDummyEmpls.get(0));
            list.add(sixDummyEmpls.get(1));

            when(mockEmplService.getSearchedEmplsCount(anyString(), anyString())).thenReturn(105);
            when(mockEmplService.getEmplListForEmplAuthChange(anyString(), anyString(), anyInt(), anyInt())).thenReturn(list);


            MvcResult result = mockMvc.perform(get("/api/admin/empls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("page", "2")
                            .param("option", "authName")
                            .param("optionValue", "사원"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ph.totalCnt", is(105)))
                    .andExpect(jsonPath("$.ph.offset", is(10)))
                    .andExpect(jsonPath("$.ph.pageSize", is(10)))
                    .andReturn();

            logger.info(result.getResponse().getContentAsString());
        }
    }

    @Nested
    @DisplayName("권한 업데이트")
    class EmplAuthUpdateTest {
        private final String emplId = "testId";
        private final String validAuthNm = "사원";
        private final String invalidAuthNm = "사사원";

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(adminEmplRestController).build();
            sixDummyEmpls = createSixEmplList();
        }

        @Test
        @WithMockUser(roles = "SUPER_ADMIN")
        @DisplayName("유효하지 않은 권한명 사용으로 실패")
        void convertToOriginalAuthNameFailTest() throws Exception {
            Map<String, String> role = new HashMap<>();
            role.put("authNm", invalidAuthNm);
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(role);

            mockMvc.perform(patch("/api/admin/empls/" + emplId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result", is("FAIL")))
                    .andExpect(jsonPath("$.errorMessage", is(ExceptionMsg.EMPL_AUTH_INVALID_AUTH_NAME.getContent())))
                    .andReturn();
        }

        @Test
        @WithMockUser(roles = "SUPER_ADMIN")
        @DisplayName("업데이트 정보 저장 실패")
        void updateFailTest() throws Exception {
            when(mockEmplService.updateEmplAuth(anyString(), anyString(), anyString())).thenReturn(new ServiceResult(false));

            Map<String, String> role = new HashMap<>();
            role.put("authNm", validAuthNm);
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(role);

            mockMvc.perform(patch("/api/admin/empls/" + emplId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result", is("FAIL")))
                    .andExpect(jsonPath("$.errorMessage", is(ExceptionMsg.EMPL_AUTH_UPDATE_FAIL.getContent())))
                    .andReturn();
        }

        @Test
        @WithMockUser(roles = "SUPER_ADMIN")
        @DisplayName("성공")
        void updateSuccessTest() throws Exception {
            when(mockEmplService.updateEmplAuth(anyString(), anyString(),anyString())).thenReturn(new ServiceResult(true));

            Map<String, String> role = new HashMap<>();
            role.put("authNm", validAuthNm);
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(role);

            mockMvc.perform(patch("/api/admin/empls/" + emplId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result", is("SUCCESS")))
                    .andReturn();
        }
    }

    List<EmplDto> createSixEmplList(){
        return List.of(
                EmplDto.EmplDtoBuilder().emplNo("0000001").emplId("aaaa").pwd("aaaa").email("aaaa@asdf.com")
                        .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(1111).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000002").emplId("bbbb").pwd("bbbb").email("bbbb@asdf.com")
                        .pwdErrTms(0).rnm("bbb").engNm("bbb").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(2222).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000003").emplId("cccc").pwd("cccc").email("cccc@asdf.com")
                        .pwdErrTms(0).rnm("ccc").engNm("ccc").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(3333).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000004").emplId("dddd").pwd("dddd").email("dddd@asdf.com")
                        .pwdErrTms(0).rnm("ddd").engNm("ddd").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(4444).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000005").emplId("eeee").pwd("eeee").email("eeee@asdf.com")
                        .pwdErrTms(0).rnm("eee").engNm("eee").entDt("2024-01-01").emplAuthNm("ROLE_EMPL_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(5555).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000006").emplId("ffff").pwd("ffff").email("ffff@asdf.com")
                        .pwdErrTms(0).rnm("fff").engNm("fff").entDt("2024-01-01").emplAuthNm("ROLE_RSC_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(6666).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build()
        );
    }
}