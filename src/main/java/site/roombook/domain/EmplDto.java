package site.roombook.domain;

import java.util.Date;
import java.util.Objects;

public class EmplDto {
    private String emplNo;
    private String emplId;
    private String pwd;
    private String email;
    private int pwdErrTms;
    private String rnm;
    private String engNm;
    private Date subsDtm;
    private String entDt;
    private String brdt;
    private String wncomTelno;
    private int empno;
    private String msgrId;
    private String prfPhotoPath;
    private char subsCertiYn;
    private char termsAgreYn;
    private char subsAprvYn;
    private char secsnYn;
    private Date acctSecsnDtm;
    private Date fstRegDtm;
    private String fstRegrIdnfNo;
    private Date lastUpdDtm;
    private String lastUpdrIdnfNo;

    public EmplDto(){}

    public EmplDto(String emplNo, String emplId, String pwd, String email, int pwdErrTms, String rnm, String engNm,
                   String entDt, String brdt, String wncomTelno, int empno, String msgrId, String prfPhotoPath,
                   char subsCertiYn, char termsAgreYn, char subsAprvYn, char secsnYn) {
        this.emplNo = emplNo;
        this.emplId = emplId;
        this.pwd = pwd;
        this.email = email;
        this.pwdErrTms = pwdErrTms;
        this.rnm = rnm;
        this.engNm = engNm;
        this.entDt = entDt;
        this.brdt = brdt;
        this.wncomTelno = wncomTelno;
        this.empno = empno;
        this.msgrId = msgrId;
        this.prfPhotoPath = prfPhotoPath;
        this.subsCertiYn = subsCertiYn;
        this.termsAgreYn = termsAgreYn;
        this.subsAprvYn = subsAprvYn;
        this.secsnYn = secsnYn;
    }

    public String getEmplNo() {
        return emplNo;
    }

    public void setEmplNo(String emplNo) {
        this.emplNo = emplNo;
    }

    public String getEmplId() {
        return emplId;
    }

    public void setEmplId(String emplId) {
        this.emplId = emplId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPwdErrTms() {
        return pwdErrTms;
    }

    public void setPwdErrTms(int pwdErrTms) {
        this.pwdErrTms = pwdErrTms;
    }

    public String getRnm() {
        return rnm;
    }

    public void setRnm(String rnm) {
        this.rnm = rnm;
    }

    public String getEngNm() {
        return engNm;
    }

    public void setEngNm(String engNm) {
        this.engNm = engNm;
    }

    public Date getSubsDtm() {
        return subsDtm;
    }

    public void setSubsDtm(Date subsDtm) {
        this.subsDtm = subsDtm;
    }

    public String getEntDt() {
        return entDt;
    }

    public void setEntDt(String entDt) {
        this.entDt = entDt;
    }

    public String getBrdt() {
        return brdt;
    }

    public void setBrdt(String brdt) {
        this.brdt = brdt;
    }

    public String getWncomTelno() {
        return wncomTelno;
    }

    public void setWncomTelno(String wncomTelno) {
        this.wncomTelno = wncomTelno;
    }

    public int getEmpno() {
        return empno;
    }

    public void setEmpno(int empno) {
        this.empno = empno;
    }

    public String getMsgrId() {
        return msgrId;
    }

    public void setMsgrId(String msgrId) {
        this.msgrId = msgrId;
    }

    public String getPrfPhotoPath() {
        return prfPhotoPath;
    }

    public void setPrfPhotoPath(String prfPhotoPath) {
        this.prfPhotoPath = prfPhotoPath;
    }

    public char getSubsCertiYn() {
        return subsCertiYn;
    }

    public void setSubsCertiYn(char subsCertiYn) {
        this.subsCertiYn = subsCertiYn;
    }

    public char getTermsAgreYn() {
        return termsAgreYn;
    }

    public void setTermsAgreYn(char termsAgreYn) {
        this.termsAgreYn = termsAgreYn;
    }

    public char getSubsAprvYn() {
        return subsAprvYn;
    }

    public void setSubsAprvYn(char subsAprvYn) {
        this.subsAprvYn = subsAprvYn;
    }

    public char getSecsnYn() {
        return secsnYn;
    }

    public void setSecsnYn(char secsnYn) {
        this.secsnYn = secsnYn;
    }

    public Date getAcctSecsnDtm() {
        return acctSecsnDtm;
    }

    public void setAcctSecsnDtm(Date acctSecsnDtm) {
        this.acctSecsnDtm = acctSecsnDtm;
    }

    public Date getFstRegDtm() {
        return fstRegDtm;
    }

    public void setFstRegDtm(Date fstRegDtm) {
        this.fstRegDtm = fstRegDtm;
    }

    public String getFstRegrIdnfNo() {
        return fstRegrIdnfNo;
    }

    public void setFstRegrIdnfNo(String fstRegrIdnfNo) {
        this.fstRegrIdnfNo = fstRegrIdnfNo;
    }

    public Date getLastUpdDtm() {
        return lastUpdDtm;
    }

    public void setLastUpdDtm(Date lastUpdDtm) {
        this.lastUpdDtm = lastUpdDtm;
    }

    public String getLastUpdrIdnfNo() {
        return lastUpdrIdnfNo;
    }

    public void setLastUpdrIdnfNo(String lastUpdrIdnfNo) {
        this.lastUpdrIdnfNo = lastUpdrIdnfNo;
    }

    @Override
    public String toString() {
        return "EmplDto{" +
                "emplNo='" + emplNo + '\'' +
                ", emplId='" + emplId + '\'' +
                ", pwd='" + pwd + '\'' +
                ", email='" + email + '\'' +
                ", pwdErrTms=" + pwdErrTms +
                ", rnm='" + rnm + '\'' +
                ", engNm='" + engNm + '\'' +
                ", subsDtm=" + subsDtm +
                ", entDt='" + entDt + '\'' +
                ", brdt='" + brdt + '\'' +
                ", wncomTelno='" + wncomTelno + '\'' +
                ", empno=" + empno +
                ", msgrId='" + msgrId + '\'' +
                ", prfPhotoPath='" + prfPhotoPath + '\'' +
                ", subsCertiYn=" + subsCertiYn +
                ", termsAgreYn=" + termsAgreYn +
                ", subsAprvYn=" + subsAprvYn +
                ", secsnYn=" + secsnYn +
                ", acctSecsnDtm=" + acctSecsnDtm +
                ", fstRegDtm=" + fstRegDtm +
                ", fstRegrIdnfNo='" + fstRegrIdnfNo + '\'' +
                ", lastUpdDtm=" + lastUpdDtm +
                ", lastUpdrIdnfNo='" + lastUpdrIdnfNo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmplDto emplDto = (EmplDto) o;
        return pwdErrTms == emplDto.pwdErrTms && empno == emplDto.empno && subsCertiYn == emplDto.subsCertiYn && termsAgreYn == emplDto.termsAgreYn && subsAprvYn == emplDto.subsAprvYn && secsnYn == emplDto.secsnYn && emplNo.equals(emplDto.emplNo) && emplId.equals(emplDto.emplId) && pwd.equals(emplDto.pwd) && email.equals(emplDto.email) && rnm.equals(emplDto.rnm) && Objects.equals(engNm, emplDto.engNm) && subsDtm.equals(emplDto.subsDtm) && Objects.equals(entDt, emplDto.entDt) && Objects.equals(brdt, emplDto.brdt) && Objects.equals(wncomTelno, emplDto.wncomTelno) && Objects.equals(msgrId, emplDto.msgrId) && Objects.equals(prfPhotoPath, emplDto.prfPhotoPath) && Objects.equals(acctSecsnDtm, emplDto.acctSecsnDtm) && fstRegDtm.equals(emplDto.fstRegDtm) && fstRegrIdnfNo.equals(emplDto.fstRegrIdnfNo) && lastUpdDtm.equals(emplDto.lastUpdDtm) && lastUpdrIdnfNo.equals(emplDto.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emplNo, emplId, pwd, email, pwdErrTms, rnm, engNm, subsDtm, entDt, brdt, wncomTelno, empno, msgrId, prfPhotoPath, subsCertiYn, termsAgreYn, subsAprvYn, secsnYn, acctSecsnDtm, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }
}
