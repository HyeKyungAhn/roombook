package store.roombook.global.springsecurity.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import store.roombook.service.JwtService;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    JwtService jwtService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (hasInvalidCookie(request)) {
            response.addCookie(expireJwtCookie(jwtService.getAccessCookieName()));
            response.addCookie(expireJwtCookie(jwtService.getRefreshCookieName()));
        }
        String redirectQueryString = generateRedirectQueryString(request);
        response.sendRedirect("/signin" + redirectQueryString);
    }

    private boolean hasInvalidCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;

        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getName().equals(jwtService.getAccessCookieName()));
    }

    private Cookie expireJwtCookie(String cookieName){
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        return cookie;
    }

    private String generateRedirectQueryString(HttpServletRequest request){
        String redirectQueryString = "";
        String redirectUrl = request.getRequestURI();
        String queryString = request.getQueryString();

        if (redirectUrl != null) {
            redirectQueryString = "?redirect="+redirectUrl;
            if (queryString != null) redirectQueryString += "?"+queryString;
        }

        return redirectQueryString;
    }
}
