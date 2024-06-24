package site.roombook.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "SpaceRescDtoBuilder")
public class SpaceRescDto {
    private Integer RESC_NO;
    private Integer SPACE_NO;
    private LocalDateTime FST_REG_DTM;
    private String FST_REGR_IDNF_NO;
    private LocalDateTime LAST_UPD_DTM;
    private String LAST_UPDR_IDNF_NO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceRescDto that = (SpaceRescDto) o;
        return RESC_NO.equals(that.RESC_NO) && SPACE_NO.equals(that.SPACE_NO) && Objects.equals(FST_REG_DTM, that.FST_REG_DTM) && Objects.equals(FST_REGR_IDNF_NO, that.FST_REGR_IDNF_NO) && Objects.equals(LAST_UPD_DTM, that.LAST_UPD_DTM) && Objects.equals(LAST_UPDR_IDNF_NO, that.LAST_UPDR_IDNF_NO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(RESC_NO, SPACE_NO, FST_REG_DTM, FST_REGR_IDNF_NO, LAST_UPD_DTM, LAST_UPDR_IDNF_NO);
    }
}
