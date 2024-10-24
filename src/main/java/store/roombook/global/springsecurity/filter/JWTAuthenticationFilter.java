package store.roombook.global.springsecurity.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import store.roombook.dao.EmplDao;
import store.roombook.dao.JwtDao;
import store.roombook.domain.JwtDto;
import store.roombook.service.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    JwtDao jwtDao;

    @Autowired
    EmplDao emplDao;

    @Autowired
    UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtService.extractAccessToken(request);
        String refreshToken = jwtService.extractRefreshToken(request);

        if (accessToken == null && refreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (accessToken != null) {
                if (jwtService.isValidAccessToken(accessToken)) {
                    saveAuthentication(accessToken);
                } else {
                    throw new PreAuthenticatedCredentialsNotFoundException("Invalid Access Token");
                }
            } else {
                JwtDto refreshTokenData = jwtDao.selectUnexpiredTokenAndAuthority(refreshToken);

                if (refreshTokenData != null) {
                    accessToken = jwtService.createAccessToken(refreshTokenData.getCreEmplId(), new String[]{refreshTokenData.getEmplAuthNm()});
                    jwtService.sendToken(response, accessToken, refreshToken);

                    saveAuthentication(accessToken);
                } else {
                    expireInvalidTokenCookie(jwtService.getRefreshCookieName(), refreshToken, response);

                    throw new PreAuthenticatedCredentialsNotFoundException("Invalid Refresh Token");
                }
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(String accessToken) {
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void expireInvalidTokenCookie(String tokenName, String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
