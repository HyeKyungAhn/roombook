package store.roombook.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "EmailVerfDtoBuilder")
public class EmailVerfDto {
    private String emailVerfId;
    private String email;
    private String ip;
    private String verificationCode;
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime creDtm;
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime expiDtm;
    private Integer authRequestCnt;
    private Integer maxAuthRequestCnt;
    private Integer authAttemptsCnt;
    private Integer maxAuthAttemptsCnt;
    private Boolean isVerfScss;
    private Boolean isBlocked;

    @Override
    public String toString() {
        return "EmailVerfDto{" +
                "emailVerfId='" + emailVerfId + '\'' +
                ", email='" + email + '\'' +
                ", ip='" + ip + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", creDtm=" + creDtm +
                ", expiDtm=" + expiDtm +
                ", authRequestCnt=" + authRequestCnt +
                ", maxAuthRequestCnt=" + maxAuthRequestCnt +
                ", authAttemptsCnt=" + authAttemptsCnt +
                ", maxAuthAttemptsCnt=" + maxAuthAttemptsCnt +
                ", isVerfScss=" + isVerfScss +
                ", isBlocked=" + isBlocked +
                '}';
    }
}
