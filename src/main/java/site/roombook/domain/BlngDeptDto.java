package site.roombook.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "BlngDeptDtoBuilder")
public class BlngDeptDto {
    private String blngDeptCd;
    private String blngEmplNo;
    private String blngEmplId;
    private char repDeptYn;
    private LocalDateTime fstRegDtm;
    private String fstRegrIdnfNo;
    private String registerId;
    private LocalDateTime lastUpdDtm;
    private String lastUpdrIdnfNo;
    private String modifierId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlngDeptDto that = (BlngDeptDto) o;
        return repDeptYn == that.repDeptYn && blngDeptCd.equals(that.blngDeptCd) && blngEmplNo.equals(that.blngEmplNo) && fstRegDtm.equals(that.fstRegDtm) && fstRegrIdnfNo.equals(that.fstRegrIdnfNo) && lastUpdDtm.equals(that.lastUpdDtm) && lastUpdrIdnfNo.equals(that.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blngDeptCd, blngEmplNo, repDeptYn, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }
}
