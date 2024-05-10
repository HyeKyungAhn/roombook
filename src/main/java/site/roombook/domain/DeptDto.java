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
    private String DEPT_CD;
    @NonNull
    @JsonProperty("parent")
    private String UPP_DEPT_CD;
//    @JsonProperty("mngrId")
    private String DEPT_MNGR_EMPL_NO;
    @NonNull
    @JsonProperty("text")
    private String DEPT_NM;
    @NonNull
    @JsonProperty("engDeptNm")
    private String ENG_DEPT_NM;
    @NonNull
    @JsonProperty("data")
    @JsonSerialize(using = DeptOdrSerializer.class)
    @JsonDeserialize(using = DeptOdrDeserializer.class)
    private Integer DEPT_SORT_ODR;
    @JsonIgnore
    private Date FST_REG_DTM;
    @NonNull
    @JsonIgnore
    private String FST_REGR_IDNF_NO;
    @JsonIgnore
    private Date LAST_UPD_DTM;
    @NonNull
    @JsonIgnore
    private String LAST_UPDR_IDNF_NO;
    @JsonProperty("mngrId")
    private String EMPL_ID; //부서 저장 시 입력받는 관리자 아이디

    public DeptDto(String DEPT_CD, String UPP_DEPT_CD, String DEPT_MNGR_EMPL_NO, String DEPT_NM, String ENG_DEPT_NM, Integer DEPT_SORT_ODR, String FST_REGR_IDNF_NO, String LAST_UPDR_IDNF_NO) {
        this.DEPT_CD = DEPT_CD;
        this.UPP_DEPT_CD = UPP_DEPT_CD;
        this.DEPT_MNGR_EMPL_NO = DEPT_MNGR_EMPL_NO;
        this.DEPT_NM = DEPT_NM;
        this.ENG_DEPT_NM = ENG_DEPT_NM;
        this.DEPT_SORT_ODR = DEPT_SORT_ODR;
        this.FST_REGR_IDNF_NO = FST_REGR_IDNF_NO;
        this.LAST_UPDR_IDNF_NO = LAST_UPDR_IDNF_NO;
    }

    public DeptDto(String DEPT_CD, String UPP_DEPT_CD, String DEPT_MNGR_EMPL_NO, String DEPT_NM, String ENG_DEPT_NM, Integer DEPT_SORT_ODR, String FST_REGR_IDNF_NO, String LAST_UPDR_IDNF_NO, String EMPL_ID) {
        this.DEPT_CD = DEPT_CD;
        this.UPP_DEPT_CD = UPP_DEPT_CD;
        this.DEPT_MNGR_EMPL_NO = DEPT_MNGR_EMPL_NO;
        this.DEPT_NM = DEPT_NM;
        this.ENG_DEPT_NM = ENG_DEPT_NM;
        this.DEPT_SORT_ODR = DEPT_SORT_ODR;
        this.FST_REGR_IDNF_NO = FST_REGR_IDNF_NO;
        this.LAST_UPDR_IDNF_NO = LAST_UPDR_IDNF_NO;
        this.EMPL_ID = EMPL_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeptDto deptDto = (DeptDto) o;
        return DEPT_CD.equals(deptDto.DEPT_CD) && UPP_DEPT_CD.equals(deptDto.UPP_DEPT_CD) && DEPT_MNGR_EMPL_NO.equals(deptDto.DEPT_MNGR_EMPL_NO) && DEPT_NM.equals(deptDto.DEPT_NM) && Objects.equals(ENG_DEPT_NM, deptDto.ENG_DEPT_NM) && DEPT_SORT_ODR.equals(deptDto.DEPT_SORT_ODR) && Objects.equals(FST_REG_DTM, deptDto.FST_REG_DTM) && FST_REGR_IDNF_NO.equals(deptDto.FST_REGR_IDNF_NO) && Objects.equals(LAST_UPD_DTM, deptDto.LAST_UPD_DTM) && LAST_UPDR_IDNF_NO.equals(deptDto.LAST_UPDR_IDNF_NO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(DEPT_CD, UPP_DEPT_CD, DEPT_MNGR_EMPL_NO, DEPT_NM, ENG_DEPT_NM, DEPT_SORT_ODR, FST_REG_DTM, FST_REGR_IDNF_NO, LAST_UPD_DTM, LAST_UPDR_IDNF_NO);
    }

    @Override
    public String toString() {
        return "DeptDto{" +
                "DEPT_CD='" + DEPT_CD + '\'' +
                ", UPP_DEPT_CD='" + UPP_DEPT_CD + '\'' +
                ", DEPT_MNGR_EMPL_NO='" + DEPT_MNGR_EMPL_NO + '\'' +
                ", DEPT_NM='" + DEPT_NM + '\'' +
                ", ENG_DEPT_NM='" + ENG_DEPT_NM + '\'' +
                ", DEPT_SORT_ODR=" + DEPT_SORT_ODR +
                ", FST_REG_DTM=" + FST_REG_DTM +
                ", FST_REGR_IDNF_NO='" + FST_REGR_IDNF_NO + '\'' +
                ", LAST_UPD_DTM=" + LAST_UPD_DTM +
                ", LAST_UPDR_IDNF_NO='" + LAST_UPDR_IDNF_NO + '\'' +
                ", EMPL_ID='" + EMPL_ID + '\'' +
                '}';
    }
}
