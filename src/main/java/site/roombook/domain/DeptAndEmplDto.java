package site.roombook.domain;

import java.util.Date;
import java.util.Objects;

public class DeptAndEmplDto {
    private String DEPT_CD;
    private String DEPT_NM;
    private String ENG_DEPT_NM;
    private String EMPL_ID;
    private String PRF_PHOTO_PATH;
    private String RNM;
    private String ENG_NM;
    private String EMPNO;
    private String EMAIL;
    private int CDR_DEPT_CNT;
    private Date FST_REG_DTM;
    private String FST_REGR_IDNF_NO;
    private Date LAST_UPD_DTM;
    private String LAST_UPDR_IDNF_NO;

    public DeptAndEmplDto(){}

    public DeptAndEmplDto(String DEPT_CD, String DEPT_NM, String ENG_DEPT_NM, String EMPL_ID, String PRF_PHOTO_PATH, String RNM, String ENG_NM, String EMPNO, String EMAIL, int CDR_DEPT_CNT, Date FST_REG_DTM, String FST_REGR_IDNF_NO, Date LAST_UPD_DTM, String LAST_UPDR_IDNF_NO) {
        this.DEPT_CD = DEPT_CD;
        this.DEPT_NM = DEPT_NM;
        this.ENG_DEPT_NM = ENG_DEPT_NM;
        this.EMPL_ID = EMPL_ID;
        this.PRF_PHOTO_PATH = PRF_PHOTO_PATH;
        this.RNM = RNM;
        this.ENG_NM = ENG_NM;
        this.EMPNO = EMPNO;
        this.EMAIL = EMAIL;
        this.CDR_DEPT_CNT = CDR_DEPT_CNT;
        this.FST_REG_DTM = FST_REG_DTM;
        this.FST_REGR_IDNF_NO = FST_REGR_IDNF_NO;
        this.LAST_UPD_DTM = LAST_UPD_DTM;
        this.LAST_UPDR_IDNF_NO = LAST_UPDR_IDNF_NO;
    }

    public String getDEPT_CD() {
        return DEPT_CD;
    }

    public void setDEPT_CD(String DEPT_CD) {
        this.DEPT_CD = DEPT_CD;
    }

    public String getDEPT_NM() {
        return DEPT_NM;
    }

    public void setDEPT_NM(String DEPT_NM) {
        this.DEPT_NM = DEPT_NM;
    }

    public String getENG_DEPT_NM() {
        return ENG_DEPT_NM;
    }

    public void setENG_DEPT_NM(String ENG_DEPT_NM) {
        this.ENG_DEPT_NM = ENG_DEPT_NM;
    }

    public String getEMPL_ID() {
        return EMPL_ID;
    }

    public void setEMPL_ID(String EMPL_ID) {
        this.EMPL_ID = EMPL_ID;
    }

    public String getPRF_PHOTO_PATH() {
        return PRF_PHOTO_PATH;
    }

    public void setPRF_PHOTO_PATH(String PRF_PHOTO_PATH) {
        this.PRF_PHOTO_PATH = PRF_PHOTO_PATH;
    }

    public String getRNM() {
        return RNM;
    }

    public void setRNM(String RNM) {
        this.RNM = RNM;
    }

    public String getENG_NM() {
        return ENG_NM;
    }

    public void setENG_NM(String ENG_NM) {
        this.ENG_NM = ENG_NM;
    }

    public String getEMPNO() {
        return EMPNO;
    }

    public void setEMPNO(String EMPNO) {
        this.EMPNO = EMPNO;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public int getCDR_DEPT_CNT() {
        return CDR_DEPT_CNT;
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

    public void setCDR_DEPT_CNT(int CDR_DEPT_CNT) {
        this.CDR_DEPT_CNT = CDR_DEPT_CNT;
    }

    @Override
    public String toString() {
        return "DeptAndEmplDto{" +
                "DEPT_CD='" + DEPT_CD + '\'' +
                ", DEPT_NM='" + DEPT_NM + '\'' +
                ", ENG_DEPT_NM='" + ENG_DEPT_NM + '\'' +
                ", EMPL_ID='" + EMPL_ID + '\'' +
                ", PRF_PHOTO_PATH='" + PRF_PHOTO_PATH + '\'' +
                ", RNM='" + RNM + '\'' +
                ", ENG_NM='" + ENG_NM + '\'' +
                ", EMPNO='" + EMPNO + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                ", CDR_DEPT_CNT=" + CDR_DEPT_CNT +
                ", FST_REG_DTM=" + FST_REG_DTM +
                ", FST_REGR_IDNF_NO='" + FST_REGR_IDNF_NO + '\'' +
                ", LAST_UPD_DTM=" + LAST_UPD_DTM +
                ", LAST_UPDR_IDNF_NO='" + LAST_UPDR_IDNF_NO + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeptAndEmplDto that = (DeptAndEmplDto) o;
        return CDR_DEPT_CNT == that.CDR_DEPT_CNT && DEPT_CD.equals(that.DEPT_CD) && DEPT_NM.equals(that.DEPT_NM) && Objects.equals(ENG_DEPT_NM, that.ENG_DEPT_NM) && EMPL_ID.equals(that.EMPL_ID) && Objects.equals(PRF_PHOTO_PATH, that.PRF_PHOTO_PATH) && RNM.equals(that.RNM) && Objects.equals(ENG_NM, that.ENG_NM) && Objects.equals(EMPNO, that.EMPNO) && EMAIL.equals(that.EMAIL) && Objects.equals(FST_REG_DTM, that.FST_REG_DTM) && Objects.equals(FST_REGR_IDNF_NO, that.FST_REGR_IDNF_NO) && Objects.equals(LAST_UPD_DTM, that.LAST_UPD_DTM) && Objects.equals(LAST_UPDR_IDNF_NO, that.LAST_UPDR_IDNF_NO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(DEPT_CD, DEPT_NM, ENG_DEPT_NM, EMPL_ID, PRF_PHOTO_PATH, RNM, ENG_NM, EMPNO, EMAIL, CDR_DEPT_CNT, FST_REG_DTM, FST_REGR_IDNF_NO, LAST_UPD_DTM, LAST_UPDR_IDNF_NO);
    }
}
