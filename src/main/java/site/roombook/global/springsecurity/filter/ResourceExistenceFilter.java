package site.roombook.global.springsecurity.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class ResourceExistenceFilter extends OncePerRequestFilter {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if(jsonUsernamePasswordAuthenticationFilter.getDefaultLoginRequestUrl().equals(request.getRequestURI())
                    || Objects.nonNull(requestMappingHandlerMapping.getHandler(request))){
                filterChain.doFilter(request, response);
            } else {
                response.sendRedirect("/not-found");
            }
        } catch (Exception e) {
            response.sendRedirect("/not-found");
        }
    }
}
