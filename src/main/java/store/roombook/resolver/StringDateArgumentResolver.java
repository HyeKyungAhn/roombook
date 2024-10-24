package store.roombook.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import store.roombook.annotation.StringDate;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringDateArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LocalDate.class)
                && parameter.hasParameterAnnotation(StringDate.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String dateParam = webRequest.getParameter(parameter.getParameterName());
        LocalDate localDate;

        if (isValidDate(dateParam)) {
            try {
                localDate = LocalDate.parse(dateParam, DateTimeFormatter.BASIC_ISO_DATE);
            } catch (DateTimeException e){
                localDate = LocalDate.now();
            }
        } else {
            localDate = LocalDate.now();
        }

        return localDate;
    }

    private boolean isValidDate(String str) {
        return str != null && str.matches("^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$");
    }
}
