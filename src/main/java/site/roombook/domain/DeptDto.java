package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import site.roombook.serializer.DeptOdrDeserializer;
import site.roombook.serializer.DeptOdrSerializer;

import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class DeptDto {
    @NonNull
    @JsonProperty("id")
    private String deptCd;
    @NonNull
    @JsonProperty("parent")
    private String uppDeptCd;
//    @JsonProperty("mngrId")
    private String deptMngrEmplNo;
    @NonNull
    @JsonProperty("text")
    private String deptNm;
    @NonNull
    @JsonProperty("engDeptNm")
    private String engDeptNm;
    @NonNull
    @JsonProperty("data")
    @JsonSerialize(using = DeptOdrSerializer.class)
    @JsonDeserialize(using = DeptOdrDeserializer.class)
    private Integer deptSortOdr;
    @JsonIgnore
    private Date fstRegDtm;
    @NonNull
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private Date lastUpdDtm;
    @NonNull
    @JsonIgnore
    private String lastUpdrIdnfNo;
    @JsonProperty("mngrId")
    private String emplId; //부서 저장 시 입력받는 관리자 아이디

    public DeptDto(String deptCd, String uppDeptCd, String deptMngrEmplNo, String deptNm, String engDeptNm, Integer deptSortOdr, String fstRegrIdnfNo, String lastUpdrIdnfNo) {
        this.deptCd = deptCd;
        this.uppDeptCd = uppDeptCd;
        this.deptMngrEmplNo = deptMngrEmplNo;
        this.deptNm = deptNm;
        this.engDeptNm = engDeptNm;
        this.deptSortOdr = deptSortOdr;
        this.fstRegrIdnfNo = fstRegrIdnfNo;
        this.lastUpdrIdnfNo = lastUpdrIdnfNo;
    }

    public DeptDto(String deptCd, String uppDeptCd, String deptMngrEmplNo, String deptNm, String engDeptNm, Integer deptSortOdr, String fstRegrIdnfNo, String lastUpdrIdnfNo, String emplId) {
        this.deptCd = deptCd;
        this.uppDeptCd = uppDeptCd;
        this.deptMngrEmplNo = deptMngrEmplNo;
        this.deptNm = deptNm;
        this.engDeptNm = engDeptNm;
        this.deptSortOdr = deptSortOdr;
        this.fstRegrIdnfNo = fstRegrIdnfNo;
        this.lastUpdrIdnfNo = lastUpdrIdnfNo;
        this.emplId = emplId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeptDto deptDto = (DeptDto) o;
        return deptCd.equals(deptDto.deptCd) && uppDeptCd.equals(deptDto.uppDeptCd) && deptMngrEmplNo.equals(deptDto.deptMngrEmplNo) && deptNm.equals(deptDto.deptNm) && Objects.equals(engDeptNm, deptDto.engDeptNm) && deptSortOdr.equals(deptDto.deptSortOdr) && Objects.equals(fstRegDtm, deptDto.fstRegDtm) && fstRegrIdnfNo.equals(deptDto.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, deptDto.lastUpdDtm) && lastUpdrIdnfNo.equals(deptDto.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptCd, uppDeptCd, deptMngrEmplNo, deptNm, engDeptNm, deptSortOdr, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }

    @Override
    public String toString() {
        return "DeptDto{" +
                "deptCd='" + deptCd + '\'' +
                ", uppDeptCd='" + uppDeptCd + '\'' +
                ", deptMngrEmplNo='" + deptMngrEmplNo + '\'' +
                ", deptNm='" + deptNm + '\'' +
                ", engDeptNm='" + engDeptNm + '\'' +
                ", deptSortOdr=" + deptSortOdr +
                ", FST_REG_DTM=" + fstRegDtm +
                ", fstRegrIdnfNo='" + fstRegrIdnfNo + '\'' +
                ", LAST_UPD_DTM=" + lastUpdDtm +
                ", lastUpdrIdnfNo='" + lastUpdrIdnfNo + '\'' +
                ", emplId='" + emplId + '\'' +
                '}';
    }
}
