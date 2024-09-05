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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import site.roombook.domain.ServiceResult;
import site.roombook.domain.SignupInput;
import site.roombook.service.EmplService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class EmplRestControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    @Autowired
    EmplRestController emplRestController;

    @Mock
    EmplService emplService;

    @Mock
    ServiceResult serviceResult;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {

        @BeforeEach
        void setup(){
            MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(emplRestController).build();
        }

        @Test
        @DisplayName("입력값이 null일 때")
        void testNullInput() throws Exception {
            mockMvc.perform(post("/api/signup").
                            content("").
                            contentType(MediaType.APPLICATION_JSON)).
                    andExpect(status().isBadRequest()).andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")));
        }

        @Test
        @DisplayName("유효한 입력값 성공 테스트")
        void testSuccess() throws Exception {
            String inputs = "{\"name\":\"관리자\",\"id\":\"admin\",\"pwd\":\"djfudnsqlalfqjsgh\",\"email\":\"fear.wise.01@gmail.com\",\"verificationCode\":\"160466\",\"emplno\":\"123123\"}";

            when(emplService.processSignup(any(SignupInput.class), anyString())).thenReturn(serviceResult);
            when(serviceResult.getMsg()).thenReturn(null);
            when(serviceResult.isSuccessful()).thenReturn(true);

            mockMvc.perform(post("/api/signup").
                            content(inputs).
                            contentType(MediaType.APPLICATION_JSON)).
                    andExpect(status().isOk()).andExpect(jsonPath("$.serverState.result", is("SUCCESS")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "\"id\":\"$%#id\",", "\"id\":\"\","})
        @DisplayName("유효하지 않은 아이디 값 테스트")
        void testInvalidId(String idInput) throws Exception {
            String inputs = "{\"name\":\"관리자\","+idInput+"\"pwd\":\"djfudnsqlalfqjsgh\",\"email\":\"fear.wise.01@gmail.com\",\"verificationCode\":\"160466\",\"emplno\":\"123123\"}";

            mockMvc.perform(post("/api/signup").
                            content(inputs).
                            contentType(MediaType.APPLICATION_JSON)).
                    andExpect(status().isBadRequest()).andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")));
        }


        @Test
        @DisplayName("회원가입 입력값 중 이름 제외 테스트")
        void testInputWithoutName() throws Exception {
            String inputs = "{\"id\":\"admin\",\"pwd\":\"djfudnsqlalfqjsgh\",\"email\":\"fear.wise.01@gmail.com\",\"verificationCode\":\"160466\",\"emplno\":\"123123\"}";

            mockMvc.perform(post("/api/signup").
                            content(inputs).
                            contentType(MediaType.APPLICATION_JSON)).
                    andExpect(status().isBadRequest()).andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")));

        }

        @ParameterizedTest
        @ValueSource(strings = {"", "\"name\":\"관\",", "\"name\":\"\","})
        @DisplayName("유효하지 않은 이름 값 테스트")
        void testInvalidName(String nameInput) throws Exception {
            String inputs = "{"+nameInput+"\"id\":\"admin\",\"pwd\":\"djfudnsqlalfqjsgh\",\"email\":\"fear.wise.01@gmail.com\",\"verificationCode\":\"160466\",\"emplno\":\"123123\"}";

            mockMvc.perform(post("/api/signup").
                            content(inputs).
                            contentType(MediaType.APPLICATION_JSON)).
                    andExpect(status().isBadRequest()).andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")));
        }

        @Test
        @DisplayName("이름이 입력값이 없는 경우 테스트")
        void testNullName() throws Exception {
            String inputs = "{\"name\":,\"id\":\"admin\",\"pwd\":\"djfudnsqlalfqjsgh\",\"email\":\"fear.wise.01@gmail.com\",\"verificationCode\":\"160466\",\"emplno\":\"123123\"}";

            mockMvc.perform(post("/api/signup").
                            content(inputs).
                            contentType(MediaType.APPLICATION_JSON)).
                    andExpect(status().isBadRequest()).andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"\"email\":\"asdf.amail.com\",", "\"email\":\"\",", ""})
        @DisplayName("유효하지 않은 이메일 값 테스트")
        void testInvalidEmail(String emailInput) throws Exception {
            String inputs = "{\"name\":\"관리자\",\"id\":\"admin\",\"pwd\":\"djfudnsqlalfqjsgh\","+emailInput+"\"verificationCode\":\"160466\",\"emplno\":\"123123\"}";

            mockMvc.perform(post("/api/signup").
                            content(inputs).
                            contentType(MediaType.APPLICATION_JSON)).
                    andExpect(status().isBadRequest()).andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")));

        }

        @ParameterizedTest
        @ValueSource(strings = {"\"pwd\":\"admin\",", "\"pwd\":\"\",", ""})
        @DisplayName("유효하지 않은 비밀번호 테스트")
        void testInvalidPassword(String pwdInput) throws Exception {
            String inputs = "{\"name\":\"관리자\",\"id\":\"admin\","+pwdInput+"\"email\":\"fea.gmail.com\",\"verificationCode\":\"160466\",\"emplno\":\"123123\"}";

            mockMvc.perform(post("/api/signup")
                            .content(inputs)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")))
                    .andExpect(jsonPath("$.pwdSafe", is(false)));
        }

        @ParameterizedTest
        @ValueSource(strings = {",\"emplno\":\"123\"", ",\"emplno\":\"\"", ""})
        @DisplayName("유효하지 않은 사원번호 테스트")
        void testInvalidEmplno(String emplnoInput) throws Exception {
            String inputs = "{\"name\":\"관리자\",\"id\":\"admin\",\"pwd\":\"admin\",\"email\":\"fear@gmail.com\",\"verificationCode\":\"160466\""+emplnoInput+"}";

            mockMvc.perform(post("/api/signup")
                            .content(inputs)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")))
                    .andExpect(jsonPath("$.emplnoValid", is(false)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "\"verificationCode\":\"\","})
        @DisplayName("인증번호 미입력 테스트")
        void testEmptyVerificationCode(String vCodeInput) throws Exception {
            String inputs = "{\"name\":\"관리자\",\"id\":\"admin\",\"pwd\":\"admin\",\"email\":\"fear@gmail.com\","+vCodeInput+"\"emplno\":\"123123\"}";

            mockMvc.perform(post("/api/signup")
                            .content(inputs)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")))
                    .andExpect(jsonPath("$.verificationCodeEmpty", is(true)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "\"id\":\"\",", "\"id\":\"a\",", "\"id\":\"aaaaaaaaaaaaaaaaaaaaaa\","})
        @DisplayName("인증번호 미입력 테스트")
        void testInValidId(String idInput) throws Exception {
            String inputs = "{\"name\":\"관리자\","+idInput+"\"pwd\":\"admin\",\"email\":\"fear@gmail.com\",\"verificationCode\":\"\",\"emplno\":\"123123\"}";

            mockMvc.perform(post("/api/signup")
                            .content(inputs)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.serverState.result", is("INVALID_INPUTS")))
                    .andExpect(jsonPath("$.verificationCodeEmpty", is(true)));
        }
    }
}