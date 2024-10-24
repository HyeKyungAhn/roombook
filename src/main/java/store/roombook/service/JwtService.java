package store.roombook.service;

import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface JwtService {
    String createAccessToken(String username, String[] authorities);

    String createRefreshToken();

    /*
    * 사용자의 만료되지 않은 토큰이 1개를 초과하지 않게
    * 기존의 만료되지 않은 토큰이 있다면 만료를 시키고 새 토큰을 저장
    * */
    void saveRefreshToken(String username, String refreshToken);

    /*
    * 만료시점이 가장 최근인 만료되지 않은 토큰을 만료
    * */
    void expireRefreshToken(String username);

    void sendToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException;

    String extractAccessToken(HttpServletRequest request) throws IOException, ServletException;

    String extractRefreshToken(HttpServletRequest request) throws IOException, ServletException;

    String extractUsername(String accessToken);

    boolean isValidAccessToken(String accessToken);

    Authentication getAuthentication(String accessToken);

    String getAccessCookieName();

    String getRefreshCookieName();
}
