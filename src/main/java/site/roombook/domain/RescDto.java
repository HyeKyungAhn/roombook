package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import site.roombook.serializer.RescDeserializer;

import java.util.Date;
import java.util.Objects;

@JsonDeserialize(using = RescDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter(onMethod = @__({@JsonIgnore}))
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "RescDtoBuilder")
public class RescDto {
    @JsonProperty("rescNo")
    private Integer rescNo;
    @JsonProperty("value")
    private String rescNm;
    @JsonIgnore
    private Integer spaceNo;
    @JsonIgnore
    private Date fstRegDtm;
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private Date lastUpdDtm;
    @JsonIgnore
    private String lastUpdrIdnfNo;

    public static RescDtoBuilder builder(String rescNm) {
        if (rescNm == null) {
            throw new IllegalArgumentException("물품 번호 누락");
        }
        return RescDtoBuilder().rescNm(rescNm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RescDto rescDto = (RescDto) o;
        return Objects.equals(rescNo, rescDto.rescNo) && rescNm.equals(rescDto.rescNm) && spaceNo.equals(rescDto.spaceNo) && Objects.equals(fstRegDtm, rescDto.fstRegDtm) && Objects.equals(fstRegrIdnfNo, rescDto.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, rescDto.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, rescDto.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rescNo, rescNm, spaceNo, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }
}
