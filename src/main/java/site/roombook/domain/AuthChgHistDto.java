package site.roombook.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "authChgHistDto")
@Getter
@ToString
public class AuthChgHistDto {
    private String authChgHistId;
    private String emplNo;
    private String emplId; //emplNo 조회 위한 값
    private String authNm;
    private Character authYn;
    private LocalDateTime regDtm;
    private String regrIdnfNo;
    private String regrIdnfId; //regrIdnfNo 조회 위한 값
}
