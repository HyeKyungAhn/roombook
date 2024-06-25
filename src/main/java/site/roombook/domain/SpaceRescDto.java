package site.roombook.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "SpaceRescDtoBuilder")
public class SpaceRescDto {
    private Integer rescNo;
    private Integer spaceNo;
    private LocalDateTime fstRegDtm;
    private String fstRegrIdnfNo;
    private LocalDateTime lastUpdDtm;
    private String lastUpdrIdnfNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceRescDto that = (SpaceRescDto) o;
        return rescNo.equals(that.rescNo) && spaceNo.equals(that.spaceNo) && Objects.equals(fstRegDtm, that.fstRegDtm) && Objects.equals(fstRegrIdnfNo, that.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, that.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, that.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rescNo, spaceNo, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }
}
