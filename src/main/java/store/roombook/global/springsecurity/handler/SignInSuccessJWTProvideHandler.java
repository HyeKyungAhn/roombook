package store.roombook.global.springsecurity.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import store.roombook.service.JwtService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignInSuccessJWTProvideHandler implements AuthenticationSuccessHandler {

    @Autowired
    JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String accessToken = jwtService.createAccessToken(userDetails.getUsername(), authorities.toArray(new String[0]));
        String refreshToken = jwtService.createRefreshToken();

        jwtService.saveRefreshToken(userDetails.getUsername(), refreshToken);
        jwtService.sendToken(response, accessToken, refreshToken);

        String referer = request.getHeader("referer");
        String redirectUrl;
        if (referer != null && referer.contains("?redirect=")) {
            redirectUrl = request.getHeader("referer").split("\\?redirect=")[1];
        } else {
            redirectUrl = "/";
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "SUCCESS");
        resultMap.put("redirect", redirectUrl);

        ObjectMapper objectMapper = new ObjectMapper();
        String redirectUrlJson = objectMapper.writeValueAsString(resultMap);
        response.getWriter().write(redirectUrlJson);
    }
}
