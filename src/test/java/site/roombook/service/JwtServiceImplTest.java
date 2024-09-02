package site.roombook.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.dao.EmplDao;
import site.roombook.dao.JwtDao;
import site.roombook.domain.EmplDto;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml", "file:web/WEB-INF/spring/**/dispatcher-servlet.xml"})
class JwtServiceImplTest {

    @Autowired
    JwtService jwtService;
    @Autowired
    EmplDao emplDao;
    @Autowired
    JwtDao jwtDao;

    @Value("${jwt.secret}")
    private String secret;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "JAT"; //AccessToken
    private static final String REFRESH_TOKEN_COOKIE_NAME = "JRT"; //RefreshToken
    private static final String USERNAME_CLAIM = "id";
    private static final String REFRESH_TOKEN_SUBJECT = "refreshToken";

    private EmplDto dummyEmpl;

    @BeforeEach
    void setup(){
        emplDao.deleteAll();

        dummyEmpl = EmplDto.EmplDtoBuilder().emplNo("0000001").emplId("aaaa").pwd("aaaa").email("aaaa@asdf.com")
                .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                .wncomTelno("01123123").empno(1111).msgrId(null).prfPhotoPath(null)
                .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();

        emplDao.insertEmpl(dummyEmpl);
    }

    @Test
    @DisplayName("access 토큰 발급 테스트")
    void createAccessToken() {
        String accessToken = jwtService.createAccessToken(dummyEmpl.getEmplId(), new String[]{dummyEmpl.getEmplAuthNm()});

        DecodedJWT verify = getVerify(accessToken);

        String subject = verify.getSubject();
        String username = verify.getSubject();

        assertEquals(dummyEmpl.getEmplId(), username);
    }

    @Test
    @DisplayName("refresh 토큰 발급 테스트")
    void createRefreshToken() {
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();

        assertEquals(REFRESH_TOKEN_SUBJECT, subject);
    }

    @Test
    @DisplayName("refresh 토큰 생성 및 저장 테스트")
    void updateRefreshToken() throws InterruptedException {
        String refreshToken = jwtService.createRefreshToken();
        jwtService.saveRefreshToken(dummyEmpl.getEmplId(), refreshToken);

        Thread.sleep(1000);

        String reissuedRefreshToken = jwtService.createRefreshToken();
        jwtService.saveRefreshToken(dummyEmpl.getEmplId(), reissuedRefreshToken);

        assertNull(jwtDao.selectUnexpiredTokenByToken(refreshToken));
        assertEquals(dummyEmpl.getEmplNo(), jwtDao.selectUnexpiredTokenByToken(reissuedRefreshToken).getCreEmplNo());
    }

    @Test
    @DisplayName("없는 사용자의 refresh 토큰 저장 테스트")
    void updateInvalidEmplRefreshToken() {
        String refreshToken = jwtService.createRefreshToken();
        assertThrows(IllegalArgumentException.class, () ->
                jwtService.saveRefreshToken("INVALID_ID", refreshToken));
}

    @Test
    @DisplayName("refresh 토큰 삭제 테스트")
    void destroyRefreshToken() {
        String refreshToken = jwtService.createRefreshToken();
        jwtService.saveRefreshToken(dummyEmpl.getEmplId(), refreshToken);

        jwtService.expireRefreshToken(dummyEmpl.getEmplId());

        assertNull(jwtDao.selectUnexpiredTokenByToken(refreshToken));
    }

    @Test
    @DisplayName("없는 사용자의 refresh 토큰 삭제 테스트")
    void destroyInvalidEmplRefreshToken() {
        assertThrows(IllegalArgumentException.class, () -> jwtService.expireRefreshToken("INVALID_ID"));
    }

    @Test
    @DisplayName("access, refresh token 헤더 설정")
    void setAccessRefreshTokenHeader() throws IOException {
        //given
        MockHttpServletResponse mockedResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(dummyEmpl.getEmplId(), new String[]{dummyEmpl.getEmplAuthNm()});
        String refreshToken = jwtService.createRefreshToken();

        //when
        jwtService.sendToken(mockedResponse,accessToken,refreshToken);

        //then
        String accessTokenCookieValue = Objects.requireNonNull(mockedResponse.getCookie(ACCESS_TOKEN_COOKIE_NAME)).getValue();
        String refreshTokenCookieValue = Objects.requireNonNull(mockedResponse.getCookie(REFRESH_TOKEN_COOKIE_NAME)).getValue();

        assertNotNull(accessTokenCookieValue);
        assertNotNull(refreshTokenCookieValue);

        assertEquals(accessToken, accessTokenCookieValue);
        assertEquals(refreshToken, refreshTokenCookieValue);
    }

    @Test
    @DisplayName("access token 쿠키에서 추출 테스트")
    void extractAccessTokenTest() throws IOException, ServletException {
        String accessToken = jwtService.createAccessToken(dummyEmpl.getEmplId(), new String[]{dummyEmpl.getEmplAuthNm()});
        String refreshToken = jwtService.createRefreshToken();

        HttpServletRequest request = setRequest(accessToken, refreshToken);

        String extractAccessToken = jwtService.extractAccessToken(request);

        assertEquals(accessToken, extractAccessToken);
        assertEquals(dummyEmpl.getEmplId(), getVerify(extractAccessToken).getSubject());
    }

    @Test
    @DisplayName("refresh token 쿠키에서 추출 테스트")
    void extractRefreshTokenTest() throws IOException, ServletException {
        String accessToken = jwtService.createAccessToken(dummyEmpl.getEmplId(), new String[]{dummyEmpl.getEmplAuthNm()});
        String refreshToken = jwtService.createRefreshToken();

        HttpServletRequest request = setRequest(accessToken, refreshToken);

        String extractRefreshToken = jwtService.extractRefreshToken(request);

        assertEquals(refreshToken, extractRefreshToken);
        assertNull(getVerify(extractRefreshToken).getClaim(USERNAME_CLAIM).asString());
    }

    @Test
    @DisplayName("username(id) 추출 테스트")
    void extractUsernameTest() throws IOException, ServletException {
        String accessToken = jwtService.createAccessToken(dummyEmpl.getEmplId(), new String[]{dummyEmpl.getEmplAuthNm()});
        String refreshToken = jwtService.createRefreshToken();

        HttpServletRequest request = setRequest(accessToken, refreshToken);

        String extractedAccessToken = jwtService.extractAccessToken(request);
        String username = jwtService.extractUsername(extractedAccessToken);

        assertEquals(dummyEmpl.getEmplId(), username);
    }

    @Test
    @DisplayName("위조 토큰 테스트")
    void forgedTokenTest() {
        String accessToken = jwtService.createAccessToken(dummyEmpl.getEmplId(), new String[]{dummyEmpl.getEmplAuthNm()});

        String modifiedToken = accessToken.replace(accessToken.charAt(36), 'Z');
        modifiedToken = modifiedToken.replace(modifiedToken.charAt(40), 'A');

        assertFalse(jwtService.isValidAccessToken(modifiedToken));
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken) {
        MockHttpServletRequest mockedRequest = new MockHttpServletRequest();

        mockedRequest.setCookies(getHttpOnlyCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken)
                , getHttpOnlyCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken));

        setCookieSameSite(mockedRequest);

        return mockedRequest;
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

    private DecodedJWT getVerify(String token){
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
    }
}