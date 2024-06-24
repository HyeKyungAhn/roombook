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
    private Integer RESC_NO;
    @JsonProperty("value")
    private String RESC_NM;
    @JsonIgnore
    private Integer SPACE_NO;
    @JsonIgnore
    private Date FST_REG_DTM;
    @JsonIgnore
    private String FST_REGR_IDNF_NO;
    @JsonIgnore
    private Date LAST_UPD_DTM;
    @JsonIgnore
    private String LAST_UPDR_IDNF_NO;

    public static RescDtoBuilder builder(String RESC_NM) {
        if (RESC_NM == null) {
            throw new IllegalArgumentException("파라미터 누락: RESC_NM");
        }
        return RescDtoBuilder().RESC_NM(RESC_NM);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RescDto rescDto = (RescDto) o;
        return Objects.equals(RESC_NO, rescDto.RESC_NO) && RESC_NM.equals(rescDto.RESC_NM) && SPACE_NO.equals(rescDto.SPACE_NO) && Objects.equals(FST_REG_DTM, rescDto.FST_REG_DTM) && Objects.equals(FST_REGR_IDNF_NO, rescDto.FST_REGR_IDNF_NO) && Objects.equals(LAST_UPD_DTM, rescDto.LAST_UPD_DTM) && Objects.equals(LAST_UPDR_IDNF_NO, rescDto.LAST_UPDR_IDNF_NO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(RESC_NO, RESC_NM, SPACE_NO, FST_REG_DTM, FST_REGR_IDNF_NO, LAST_UPD_DTM, LAST_UPDR_IDNF_NO);
    }
}
