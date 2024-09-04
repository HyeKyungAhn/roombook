package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import site.roombook.serializer.DeptOdrDeserializer;
import site.roombook.serializer.DeptOdrSerializer;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "DeptDtoBuilder")
@Getter
public class DeptDto {
    @JsonProperty("id")
    private String deptCd;
    @JsonProperty("parent")
    private String uppDeptCd;
    private String deptMngrEmplNo;
    @JsonProperty("text")
    private String deptNm;
    @JsonProperty("engDeptNm")
    private String engDeptNm;
    @JsonProperty("data")
    @JsonSerialize(using = DeptOdrSerializer.class)
    @JsonDeserialize(using = DeptOdrDeserializer.class)
    private Integer deptSortOdr;
    @JsonIgnore
    private LocalDateTime fstRegDtm;
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private LocalDateTime lastUpdDtm;
    @JsonIgnore
    private String lastUpdrIdnfNo;
    @JsonProperty("mngrId")
    private String emplId; //부서 저장 시 입력받는 관리자 아이디
    @JsonIgnore
    private String registerId;
    @JsonIgnore
    private String modifierId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeptDto deptDto = (DeptDto) o;
        return deptCd.equals(deptDto.deptCd) && uppDeptCd.equals(deptDto.uppDeptCd) && Objects.equals(deptMngrEmplNo, deptDto.deptMngrEmplNo) && deptNm.equals(deptDto.deptNm) && engDeptNm.equals(deptDto.engDeptNm) && deptSortOdr.equals(deptDto.deptSortOdr) && Objects.equals(fstRegDtm, deptDto.fstRegDtm) && fstRegrIdnfNo.equals(deptDto.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, deptDto.lastUpdDtm) && lastUpdrIdnfNo.equals(deptDto.lastUpdrIdnfNo) && Objects.equals(emplId, deptDto.emplId) && Objects.equals(registerId, deptDto.registerId) && Objects.equals(modifierId, deptDto.modifierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptCd, uppDeptCd, deptMngrEmplNo, deptNm, engDeptNm, deptSortOdr, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo, emplId, registerId, modifierId);
    }
}
