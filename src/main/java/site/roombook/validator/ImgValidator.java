package site.roombook.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class ImgValidator implements Validator {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");

    @Override
    public boolean supports(Class<?> clazz) {
        return MultipartFile[].class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MultipartFile[] files = (MultipartFile[]) target;

        for (MultipartFile file : files) {
            String mimeType = file.getContentType();
            if(mimeType== null || !ALLOWED_IMAGE_TYPES.contains(mimeType.toLowerCase())){
                errors.rejectValue("contentType", "invalidImgType");
            }
        }
    }
}
