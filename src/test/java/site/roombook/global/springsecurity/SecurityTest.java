package site.roombook.global.springsecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import site.roombook.controller.AdminEmplController;
import site.roombook.controller.EmplRestController;
import site.roombook.dao.EmplDao;
import site.roombook.domain.EmplDto;
import site.roombook.service.JwtService;

import javax.servlet.http.Cookie;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Transactional
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebAppConfiguration("web")
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml", "file:web/WEB-INF/spring/**/dispatcher-servlet.xml"})
class SecurityTest {

    private MockMvc mockMvc;

    @InjectMocks
    @Autowired
    EmplRestController emplRestController;

    @Autowired
    private EmplDao emplDao;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERNAME_KEY = "id";
    private static final String PASSWORD_KEY = "password";
    private static final String USERNAME = "testId";
    private static final String PASSWORD = "testpasswordlong";

    @BeforeEach
    void setup(WebApplicationContext wac) {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        EmplDto empl = EmplDto.EmplDtoBuilder()
                .emplNo("1010101010")
                .emplId(USERNAME)
                .pwd(passwordEncoder.encode(PASSWORD))
                .email("aaaa@asdf.com")
                .pwdErrTms(0)
                .rnm("aaa")
                .engNm("aaa")
                .entDt("2024-01-01")
                .emplAuthNm("ROLE_SUPER_ADMIN")
                .brdt("2000-01-01")
                .wncomTelno("01123123")
                .empno(1111)
                .msgrId(null)
                .prfPhotoPath(null)
                .subsCertiYn('Y')
                .termsAgreYn('Y')
                .subsAprvYn('Y')
                .secsnYn('N')
                .build();

        assertEquals(1, emplDao.insertEmpl(empl));
    }

    @Nested
    @DisplayName("로그인")
    class DaoAuthenticationProviderTest {

        @Nested
        @DisplayName("실패")
        class FailureTest {

            @Test
            @DisplayName("존재하지 않는 아이디")
            void nonExistingId() throws Exception {
                Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                        , PASSWORD_KEY + "123123", PASSWORD);

                mockMvc.perform(post("/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.result", is("SIGNIN_FAIL")))
                        .andReturn();
            }

            @Test
            @DisplayName("유효하지 않은 비밀번호")
            void invalidPassword() throws Exception {
                Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                        , PASSWORD_KEY, PASSWORD + "123123");

                mockMvc.perform(post("/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.result", is("SIGNIN_FAIL")))
                        .andReturn();
            }

            @Test
            @DisplayName("지원하지 않는 요청타입 - text/plain")
            void notSupportedContentType() throws Exception {
                Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                        , PASSWORD_KEY, PASSWORD);

                mockMvc.perform(post("/signin")
                                .contentType(MediaType.TEXT_PLAIN_VALUE)
                                .content(objectMapper.writeValueAsString(map)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.result", is("SIGNIN_FAIL")))
                        .andReturn();
            }

            @Test
            @DisplayName("지원하지 않는 메서드 타입")
            void notSupportedMethod() throws Exception {
                Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                        , PASSWORD_KEY, PASSWORD);

                mockMvc.perform(put("/signin")
                                .contentType(MediaType.TEXT_PLAIN_VALUE)
                                .content(objectMapper.writeValueAsString(map)))
                        .andExpect(status().isMethodNotAllowed())
                        .andReturn();
            }
        }

        @Nested
        @DisplayName("성공")
        class SuccessTest {
            @Test
            @DisplayName("상태코드 200")
            void status200Test() throws Exception {
                Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                        , PASSWORD_KEY, PASSWORD);

                MvcResult result = mockMvc.perform(post("/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.redirect", is("/")))
                        .andReturn();

                assertNotNull(result.getResponse().getCookie(jwtService.getAccessCookieName()));
            }

            @Test
            @DisplayName("응답에 JWT 토큰 보유 여부 확인")
            void hasTokenTest() throws Exception {
                Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                        , PASSWORD_KEY, PASSWORD);

                MvcResult result = mockMvc.perform(post("/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.redirect", is("/")))
                        .andReturn();

                assertNotNull(result.getResponse().getCookie(jwtService.getAccessCookieName()));
                assertNotNull(result.getResponse().getCookie(jwtService.getRefreshCookieName()));
            }

            @Test
            @DisplayName("로그인 성공시 이전 요청 리소스로 리다이렉트")
            void replayRequestTest() throws Exception {
                Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                        , PASSWORD_KEY, PASSWORD);

                mockMvc.perform(post("/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("referer", "localhost/signin?redirect=/dept/list?emplid=123")
                                .content(objectMapper.writeValueAsString(map)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.redirect", is("/dept/list?emplid=123")))
                        .andReturn();
            }

        }
    }

    @Nested
    @DisplayName("로그아웃")
    class SignoutTest {

        @BeforeEach
        void setup() throws Exception {
            Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                    , PASSWORD_KEY, PASSWORD);

            mockMvc.perform(post("/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(map)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.redirect", is("/")))
                    .andReturn();
        }

        @Test
        void success() throws Exception {
            mockMvc.perform(get("/signout"))
                    .andExpect(status().isFound())
                    .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
                    .andReturn();

            String adminEmplListPageUrl = linkTo(methodOn(AdminEmplController.class).getAdminEmplListPage()).toUri().toString();

            mockMvc.perform(get(adminEmplListPageUrl))
                    .andExpect(status().isFound())
                    .andReturn();
        }

    }
    @Nested
    @DisplayName("Access Dined 요청 테스트")
    class AccessDinedRequestTest {

        @Nested
        @DisplayName("인증 페이지로 리디렉션")
        class AuthenticationEntryPointTest {

            @Test
            @DisplayName("/dept/list 요청")
            void getDeptListPageTest() throws Exception {
                mockMvc.perform(get("/dept/list"))
                        .andExpect(status().isFound())
                        .andReturn();
            }

            @Test
            @DisplayName("/dept/move 요청")
            void getDeptMovePageTest() throws Exception {
                mockMvc.perform(get("/dept/move"))
                        .andExpect(status().isFound())
                        .andReturn();
            }

            @Test
            @DisplayName("이전 요청 URL queryString으로 보내기")
            void rememberReuqestURL() throws Exception {
                mockMvc.perform(get("/dept/move?empl=123"))
                        .andExpect(status().isFound())
                        .andReturn();
            }
        }
    }

    @Nested
    @DisplayName("로그인 후 JWT 인증 확인 테스트")
    class JWTAuthenticationFilterTest {

        private Cookie accessTokenCookie;
        private Cookie refreshTokenCookie;

        @BeforeEach
        void setup() throws Exception {
            Map<String, String> map = Map.of(USERNAME_KEY, USERNAME
                    , PASSWORD_KEY, PASSWORD);

            MvcResult signInResult = mockMvc.perform(post("/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(map)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.redirect", is("/")))
                    .andReturn();

            MockHttpServletResponse response = signInResult.getResponse();
            accessTokenCookie = response.getCookie(jwtService.getAccessCookieName());
            refreshTokenCookie = response.getCookie(jwtService.getRefreshCookieName());
        }

        @Test
        @DisplayName("Access, Refresh token 둘 다 없는 경우")
        void noAccessAndRefreshTokenTest() throws Exception {
            mockMvc.perform(get("/dept/list"))
                    .andExpect(status().isFound())
                    .andReturn();
        }

        @Test
        @DisplayName("Access, Refresh token 둘 다 유효한 경우")
        void validAccessAndRefreshTokenTest() throws Exception {
            mockMvc.perform(get("/dept/list")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(accessTokenCookie, refreshTokenCookie))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("Access token이 변조된 경우")
        void invalidAccessTokenTest() throws Exception {
            String forgedAccessToken = forgeToken(accessTokenCookie.getValue());
            String refreshToken = refreshTokenCookie.getValue();

            Cookie accessTokenCookie = getCookie(forgedAccessToken, jwtService.getAccessCookieName());
            Cookie refreshTokenCookie = getCookie(refreshToken, jwtService.getRefreshCookieName());


            mockMvc.perform(get("/dept/list")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(accessTokenCookie, refreshTokenCookie))
                    .andExpect(status().isFound())
                    .andExpect(MockMvcResultMatchers.redirectedUrl("/signin?redirect=/dept/list"))
                    .andReturn();
        }

        @Test
        @DisplayName("Access token이 없고 Refresh token만 있는 경우")
        void noAccessTokenTest() throws Exception {
            mockMvc.perform(get("/dept/list")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(refreshTokenCookie))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("Access token이 없고 Refresh token이 위조된 경우")
        void noAccessTokenAndForgedRefreshTokenTest() throws Exception {
            String forgedRefreshToken = forgeToken(refreshTokenCookie.getValue());
            Cookie refreshTokenCookie = getCookie(forgedRefreshToken, jwtService.getRefreshCookieName());

            mockMvc.perform(get("/dept/list")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(refreshTokenCookie))
                    .andExpect(status().isFound())
                    .andReturn();
        }

        private String forgeToken(String token) {
            String modifiedToken = token.replace(token.charAt(36), 'Z');
            modifiedToken = modifiedToken.replace(modifiedToken.charAt(40), 'A');

            return modifiedToken;
        }

        private Cookie getCookie(String token, String tokenName) {
            MockHttpServletRequest mockedRequst = new MockHttpServletRequest();

            mockedRequst.setCookies(getHttpOnlyCookie(tokenName, token));

            setCookieSameSite(mockedRequst);

            return Objects.requireNonNull(mockedRequst.getCookies())[0];
        }

        private void setCookieSameSite(MockHttpServletRequest mockedRequest) {
            Enumeration<String> headers = mockedRequest.getHeaders(HttpHeaders.SET_COOKIE);

            if (headers.hasMoreElements()) {
                mockedRequest.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", headers.nextElement(), "SameSite=Strict"));
            }
        }

        private Cookie getHttpOnlyCookie(String cookeName, String token) {
            Cookie cookie = new Cookie(cookeName, token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60); //1분
            cookie.setHttpOnly(true);

            return cookie;
        }
    }

    @Nested
    @DisplayName("권한")
    class AuthorizationTest {

        @Test
        @DisplayName("불일치로 리다이렉트")
        @WithMockUser(roles = "USER")
        void inValidRoleTest() throws Exception {
            mockMvc.perform(get("/api/admin/empls"))
                    .andExpect(status().isFound()).andReturn();
        }

        @Test
        @WithMockUser(roles = "SUPER_ADMIN")
        void validRoleTest() throws Exception {
            mockMvc.perform(get("/api/admin/empls"))
                    .andExpect(status().isOk())
                    .andReturn();
        }
    }
}
