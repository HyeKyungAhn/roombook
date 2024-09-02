package site.roombook.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import site.roombook.dao.JwtDao;
import site.roombook.domain.JwtDto;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class JwtServiceImpl implements JwtService{

    @Autowired
    private JwtDao jwtDao;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "JAT"; //AccessToken
    private static final String REFRESH_TOKEN_COOKIE_NAME = "JRT"; //RefreshToken
    private static final String REFRESH_TOKEN_SUBJECT = "refreshToken";

    @Override
    public String createAccessToken(String username, String[] authorities) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                .withArrayClaim("authorities", authorities)
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public void saveRefreshToken(String username, String refreshToken) {
        jwtDao.expireTokenByEmplId(username);

        try {
            jwtDao.insertToken(JwtDto.JwtDtoBuilder()
                    .tknId(UUID.randomUUID().toString())
                    .tkn(refreshToken)
                    .creEmplId(username)
                    .creDtm(LocalDateTime.now())
                    .expiDtm(LocalDateTime.now().plusMinutes(1L)).build());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("해당 사용자가 없습니다.");
        }
    }

    @Override
    public void expireRefreshToken(String username) {
        if (jwtDao.expireTokenByEmplId(username) == 0) {
            throw new IllegalArgumentException("해당 사용자가 없거나 만료할 토큰이 없습니다.");
        }
    }

    @Override
    public void sendToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        setTokenInHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken);
        setTokenInHttpOnlyCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        setCookieSameSite(response);
    }

    @Override
    public String extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                        .orElse(null);
    }

    @Override
    public String extractRefreshToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElse(null);
    }

    @Override
    public String extractUsername(String accessToken) {
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();
    }

    @Override
    public boolean isValidAccessToken(String accessToken) {
        try {
            JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(accessToken);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(extractUsername(accessToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public String getAccessCookieName() {
        return ACCESS_TOKEN_COOKIE_NAME;
    }

    @Override
    public String getRefreshCookieName() {
        return REFRESH_TOKEN_COOKIE_NAME;
    }

    private void setCookieSameSite(HttpServletResponse response) {
        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        for (String header : headers) {
            if (firstHeader) {
                response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Strict"));
                firstHeader = false;
                continue;
            }
            response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Strict"));
        }
    }

    private void setTokenInHttpOnlyCookie(HttpServletResponse response, String cookeName, String token) {
        Cookie cookie = new Cookie(cookeName, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        if (REFRESH_TOKEN_SUBJECT.equals(cookeName)) {
            cookie.setMaxAge(259200);
        } else {
            cookie.setMaxAge(86400);
        }
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
    }
}
