package store.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import store.roombook.serializer.RescDeserializer;

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
    @JsonProperty("spaceNo")
    private Integer spaceNo;
    @JsonIgnore
    private Date fstRegDtm;
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private Date lastUpdDtm;
    @JsonIgnore
    private String lastUpdrIdnfNo;
    @JsonIgnore
    private String emplId;

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
        return Objects.equals(rescNo, rescDto.rescNo) && Objects.equals(rescNm, rescDto.rescNm) && Objects.equals(spaceNo, rescDto.spaceNo) && Objects.equals(fstRegDtm, rescDto.fstRegDtm) && Objects.equals(fstRegrIdnfNo, rescDto.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, rescDto.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, rescDto.lastUpdrIdnfNo) && Objects.equals(emplId, rescDto.emplId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rescNo, rescNm, spaceNo, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo, emplId);
    }
}
