package site.roombook.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "JwtDtoBuilder")
@Getter
@ToString
public class JwtDto {
    private String tknId;
    private String tkn;
    private String creEmplNo;
    private String creEmplId;
    private LocalDateTime creDtm;
    private LocalDateTime expiDtm;
    private String emplAuthNm;
}
