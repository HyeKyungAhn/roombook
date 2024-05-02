package site.roombook.domain;

import java.util.Date;
import java.util.Objects;

public class EmplDto {
    private String EMPL_NO;
    private String EMPL_ID;
    private String PWD;
    private String EMAIL;
    private int PWD_ERR_TMS;
    private String RNM;
    private String ENG_NM;
    private Date SUBS_DTM;
    private String ENT_DT;
    private String BRDT;
    private String WNCOM_TELNO;
    private int EMPNO;
    private String MSGR_ID;
    private String PRF_PHOTO_PATH;
    private char SUBS_CERTI_YN;
    private char TERMS_AGRE_YN;
    private char SUBS_APRV_YN;
    private char SECSN_YN;
    private Date ACCT_SECSN_DTM;
    private Date FST_REG_DTM;
    private String FST_REGR_IDNF_NO;
    private Date LAST_UPD_DTM;
    private String LAST_UPDR_IDNF_NO;

    public EmplDto(){}

    public EmplDto(String EMPL_NO, String EMPL_ID, String PWD, String EMAIL, int PWD_ERR_TMS, String RNM, String ENG_NM,
                   String ENT_DT, String BRDT, String WNCOM_TELNO, int EMPNO, String MSGR_ID, String PRF_PHOTO_PATH,
                   char SUBS_CERTI_YN, char TERMS_AGRE_YN, char SUBS_APRV_YN, char SECSN_YN) {
        this.EMPL_NO = EMPL_NO;
        this.EMPL_ID = EMPL_ID;
        this.PWD = PWD;
        this.EMAIL = EMAIL;
        this.PWD_ERR_TMS = PWD_ERR_TMS;
        this.RNM = RNM;
        this.ENG_NM = ENG_NM;
        this.ENT_DT = ENT_DT;
        this.BRDT = BRDT;
        this.WNCOM_TELNO = WNCOM_TELNO;
        this.EMPNO = EMPNO;
        this.MSGR_ID = MSGR_ID;
        this.PRF_PHOTO_PATH = PRF_PHOTO_PATH;
        this.SUBS_CERTI_YN = SUBS_CERTI_YN;
        this.TERMS_AGRE_YN = TERMS_AGRE_YN;
        this.SUBS_APRV_YN = SUBS_APRV_YN;
        this.SECSN_YN = SECSN_YN;
    }

    public String getEMPL_NO() {
        return EMPL_NO;
    }

    public void setEMPL_NO(String EMPL_NO) {
        this.EMPL_NO = EMPL_NO;
    }

    public String getEMPL_ID() {
        return EMPL_ID;
    }

    public void setEMPL_ID(String EMPL_ID) {
        this.EMPL_ID = EMPL_ID;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public int getPWD_ERR_TMS() {
        return PWD_ERR_TMS;
    }

    public void setPWD_ERR_TMS(int PWD_ERR_TMS) {
        this.PWD_ERR_TMS = PWD_ERR_TMS;
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

    public Date getSUBS_DTM() {
        return SUBS_DTM;
    }

    public void setSUBS_DTM(Date SUBS_DTM) {
        this.SUBS_DTM = SUBS_DTM;
    }

    public String getENT_DT() {
        return ENT_DT;
    }

    public void setENT_DT(String ENT_DT) {
        this.ENT_DT = ENT_DT;
    }

    public String getBRDT() {
        return BRDT;
    }

    public void setBRDT(String BRDT) {
        this.BRDT = BRDT;
    }

    public String getWNCOM_TELNO() {
        return WNCOM_TELNO;
    }

    public void setWNCOM_TELNO(String WNCOM_TELNO) {
        this.WNCOM_TELNO = WNCOM_TELNO;
    }

    public int getEMPNO() {
        return EMPNO;
    }

    public void setEMPNO(int EMPNO) {
        this.EMPNO = EMPNO;
    }

    public String getMSGR_ID() {
        return MSGR_ID;
    }

    public void setMSGR_ID(String MSGR_ID) {
        this.MSGR_ID = MSGR_ID;
    }

    public String getPRF_PHOTO_PATH() {
        return PRF_PHOTO_PATH;
    }

    public void setPRF_PHOTO_PATH(String PRF_PHOTO_PATH) {
        this.PRF_PHOTO_PATH = PRF_PHOTO_PATH;
    }

    public char getSUBS_CERTI_YN() {
        return SUBS_CERTI_YN;
    }

    public void setSUBS_CERTI_YN(char SUBS_CERTI_YN) {
        this.SUBS_CERTI_YN = SUBS_CERTI_YN;
    }

    public char getTERMS_AGRE_YN() {
        return TERMS_AGRE_YN;
    }

    public void setTERMS_AGRE_YN(char TERMS_AGRE_YN) {
        this.TERMS_AGRE_YN = TERMS_AGRE_YN;
    }

    public char getSUBS_APRV_YN() {
        return SUBS_APRV_YN;
    }

    public void setSUBS_APRV_YN(char SUBS_APRV_YN) {
        this.SUBS_APRV_YN = SUBS_APRV_YN;
    }

    public char getSECSN_YN() {
        return SECSN_YN;
    }

    public void setSECSN_YN(char SECSN_YN) {
        this.SECSN_YN = SECSN_YN;
    }

    public Date getACCT_SECSN_DTM() {
        return ACCT_SECSN_DTM;
    }

    public void setACCT_SECSN_DTM(Date ACCT_SECSN_DTM) {
        this.ACCT_SECSN_DTM = ACCT_SECSN_DTM;
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
    public String toString() {
        return "EmplDto{" +
                "EMPL_NO='" + EMPL_NO + '\'' +
                ", EMPL_ID='" + EMPL_ID + '\'' +
                ", PWD='" + PWD + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                ", PWD_ERR_TMS=" + PWD_ERR_TMS +
                ", RNM='" + RNM + '\'' +
                ", ENG_NM='" + ENG_NM + '\'' +
                ", SUBS_DTM=" + SUBS_DTM +
                ", ENT_DT='" + ENT_DT + '\'' +
                ", BRDT='" + BRDT + '\'' +
                ", WNCOM_TELNO='" + WNCOM_TELNO + '\'' +
                ", EMPNO=" + EMPNO +
                ", MSGR_ID='" + MSGR_ID + '\'' +
                ", PRF_PHOTO_PATH='" + PRF_PHOTO_PATH + '\'' +
                ", SUBS_CERTI_YN=" + SUBS_CERTI_YN +
                ", TERMS_AGRE_YN=" + TERMS_AGRE_YN +
                ", SUBS_APRV_YN=" + SUBS_APRV_YN +
                ", SECSN_YN=" + SECSN_YN +
                ", ACCT_SECSN_DTM=" + ACCT_SECSN_DTM +
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
        EmplDto emplDto = (EmplDto) o;
        return PWD_ERR_TMS == emplDto.PWD_ERR_TMS && EMPNO == emplDto.EMPNO && SUBS_CERTI_YN == emplDto.SUBS_CERTI_YN && TERMS_AGRE_YN == emplDto.TERMS_AGRE_YN && SUBS_APRV_YN == emplDto.SUBS_APRV_YN && SECSN_YN == emplDto.SECSN_YN && EMPL_NO.equals(emplDto.EMPL_NO) && EMPL_ID.equals(emplDto.EMPL_ID) && PWD.equals(emplDto.PWD) && EMAIL.equals(emplDto.EMAIL) && RNM.equals(emplDto.RNM) && Objects.equals(ENG_NM, emplDto.ENG_NM) && SUBS_DTM.equals(emplDto.SUBS_DTM) && Objects.equals(ENT_DT, emplDto.ENT_DT) && Objects.equals(BRDT, emplDto.BRDT) && Objects.equals(WNCOM_TELNO, emplDto.WNCOM_TELNO) && Objects.equals(MSGR_ID, emplDto.MSGR_ID) && Objects.equals(PRF_PHOTO_PATH, emplDto.PRF_PHOTO_PATH) && Objects.equals(ACCT_SECSN_DTM, emplDto.ACCT_SECSN_DTM) && FST_REG_DTM.equals(emplDto.FST_REG_DTM) && FST_REGR_IDNF_NO.equals(emplDto.FST_REGR_IDNF_NO) && LAST_UPD_DTM.equals(emplDto.LAST_UPD_DTM) && LAST_UPDR_IDNF_NO.equals(emplDto.LAST_UPDR_IDNF_NO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EMPL_NO, EMPL_ID, PWD, EMAIL, PWD_ERR_TMS, RNM, ENG_NM, SUBS_DTM, ENT_DT, BRDT, WNCOM_TELNO, EMPNO, MSGR_ID, PRF_PHOTO_PATH, SUBS_CERTI_YN, TERMS_AGRE_YN, SUBS_APRV_YN, SECSN_YN, ACCT_SECSN_DTM, FST_REG_DTM, FST_REGR_IDNF_NO, LAST_UPD_DTM, LAST_UPDR_IDNF_NO);
    }
}
