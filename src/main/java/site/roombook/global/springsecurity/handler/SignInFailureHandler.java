package site.roombook.global.springsecurity.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignInFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            Map<String, String> map = new HashMap<>();
            map.put("result", "SIGNIN_FAIL");

            ObjectMapper objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(map);

            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
