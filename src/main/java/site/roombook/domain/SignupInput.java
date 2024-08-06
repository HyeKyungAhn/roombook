package site.roombook.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import site.roombook.serializer.SignupInputDeserializer;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@JsonDeserialize(using = SignupInputDeserializer.class)
@Builder(builderMethodName = "Builder")
public class SignupInput {
    private String name;
    private String id;
    private String pwd;
    private String email;
    private String verificationCode;
    private String emplno;
}
