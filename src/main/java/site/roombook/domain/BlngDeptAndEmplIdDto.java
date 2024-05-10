package site.roombook.domain;

import java.util.Date;
import java.util.Objects;

public class BlngDeptAndEmplIdDto {
    private String BLNG_DEPT_CD;
    private String BLNG_EMPL_NO;
    private String EMPL_ID;
    private char REP_DEPT_YN;
    private Date FST_REG_DTM;
    private String FST_REGR_IDNF_NO;
    private Date LAST_UPD_DTM;
    private String LAST_UPDR_IDNF_NO;

    public BlngDeptAndEmplIdDto() {}

    public BlngDeptAndEmplIdDto(String BLNG_DEPT_CD, String BLNG_EMPL_NO, String EMPL_ID, char REP_DEPT_YN, Date FST_REG_DTM, String FST_REGR_IDNF_NO, Date LAST_UPD_DTM, String LAST_UPDR_IDNF_NO) {
        this.BLNG_DEPT_CD = BLNG_DEPT_CD;
        this.BLNG_EMPL_NO = BLNG_EMPL_NO;
        this.EMPL_ID = EMPL_ID;
        this.REP_DEPT_YN = REP_DEPT_YN;
        this.FST_REG_DTM = FST_REG_DTM;
        this.FST_REGR_IDNF_NO = FST_REGR_IDNF_NO;
        this.LAST_UPD_DTM = LAST_UPD_DTM;
        this.LAST_UPDR_IDNF_NO = LAST_UPDR_IDNF_NO;
    }

    public BlngDeptAndEmplIdDto(String BLNG_DEPT_CD, String EMPL_ID, char REP_DEPT_YN, Date FST_REG_DTM, String FST_REGR_IDNF_NO, Date LAST_UPD_DTM, String LAST_UPDR_IDNF_NO) {
        this.BLNG_DEPT_CD = BLNG_DEPT_CD;
        this.EMPL_ID = EMPL_ID;
        this.REP_DEPT_YN = REP_DEPT_YN;
        this.FST_REG_DTM = FST_REG_DTM;
        this.FST_REGR_IDNF_NO = FST_REGR_IDNF_NO;
        this.LAST_UPD_DTM = LAST_UPD_DTM;
        this.LAST_UPDR_IDNF_NO = LAST_UPDR_IDNF_NO;
    }

    public BlngDeptAndEmplIdDto(String BLNG_DEPT_CD, String EMPL_ID, String FST_REGR_IDNF_NO, String LAST_UPDR_IDNF_NO) {
        this.BLNG_DEPT_CD = BLNG_DEPT_CD;
        this.EMPL_ID = EMPL_ID;
        this.FST_REGR_IDNF_NO = FST_REGR_IDNF_NO;
        this.LAST_UPDR_IDNF_NO = LAST_UPDR_IDNF_NO;
    }

    public String getBLNG_DEPT_CD() {
        return BLNG_DEPT_CD;
    }

    public void setBLNG_DEPT_CD(String BLNG_DEPT_CD) {
        this.BLNG_DEPT_CD = BLNG_DEPT_CD;
    }

    public String getBLNG_EMPL_NO() {
        return BLNG_EMPL_NO;
    }

    public void setBLNG_EMPL_NO(String BLNG_EMPL_NO) {
        this.BLNG_EMPL_NO = BLNG_EMPL_NO;
    }

    public String getEMPL_ID() {
        return EMPL_ID;
    }

    public void setEMPL_ID(String EMPL_ID) {
        this.EMPL_ID = EMPL_ID;
    }

    public char getREP_DEPT_YN() {
        return REP_DEPT_YN;
    }

    public void setREP_DEPT_YN(char REP_DEPT_YN) {
        this.REP_DEPT_YN = REP_DEPT_YN;
    }

    public Date getFST_REG_DTM() {
        return FST_REG_DTM;
    }

    public void setFST_REG_DTM(Date FST_REG_DTM) {
        this.FST_REG_DTM = FST_REG_DTM;
    }

    public String getFST_REGR_IDNF_NO() {
        return FST_REGR_IDNF_NO;
    }

    public void setFST_REGR_IDNF_NO(String FST_REGR_IDNF_NO) {
        this.FST_REGR_IDNF_NO = FST_REGR_IDNF_NO;
    }

    public Date getLAST_UPD_DTM() {
        return LAST_UPD_DTM;
    }

    public void setLAST_UPD_DTM(Date LAST_UPD_DTM) {
        this.LAST_UPD_DTM = LAST_UPD_DTM;
    }

    public String getLAST_UPDR_IDNF_NO() {
        return LAST_UPDR_IDNF_NO;
    }

    public void setLAST_UPDR_IDNF_NO(String LAST_UPDR_IDNF_NO) {
        this.LAST_UPDR_IDNF_NO = LAST_UPDR_IDNF_NO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlngDeptAndEmplIdDto that = (BlngDeptAndEmplIdDto) o;
        return REP_DEPT_YN == that.REP_DEPT_YN && BLNG_DEPT_CD.equals(that.BLNG_DEPT_CD) && Objects.equals(BLNG_EMPL_NO, that.BLNG_EMPL_NO) && EMPL_ID.equals(that.EMPL_ID) && FST_REG_DTM.equals(that.FST_REG_DTM) && FST_REGR_IDNF_NO.equals(that.FST_REGR_IDNF_NO) && LAST_UPD_DTM.equals(that.LAST_UPD_DTM) && LAST_UPDR_IDNF_NO.equals(that.LAST_UPDR_IDNF_NO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(BLNG_DEPT_CD, BLNG_EMPL_NO, EMPL_ID, REP_DEPT_YN, FST_REG_DTM, FST_REGR_IDNF_NO, LAST_UPD_DTM, LAST_UPDR_IDNF_NO);
    }
}
