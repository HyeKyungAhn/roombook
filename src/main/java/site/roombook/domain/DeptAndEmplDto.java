package site.roombook.domain;

import java.util.Date;
import java.util.Objects;

public class DeptAndEmplDto {
    private String deptCd;
    private String deptNm;
    private String engDeptNm;
    private String emplId;
    private String prfPhotoPath;
    private String rnm;
    private String engNm;
    private String empno;
    private String email;
    private int cdrDeptCnt;
    private Date fstRegDtm;
    private String fstRegrIdnfNo;
    private Date lastUpdDtm;
    private String lastUpdrIdnfNo;

    public DeptAndEmplDto(){}

    public DeptAndEmplDto(String emplId){
        this.emplId = emplId;
    }

    public DeptAndEmplDto(String deptCd, String deptNm, String engDeptNm, String emplId, String prfPhotoPath, String rnm, String engNm, String empno, String email, int cdrDeptCnt, Date fstRegDtm, String fstRegrIdnfNo, Date lastUpdDtm, String lastUpdrIdnfNo) {
        this.deptCd = deptCd;
        this.deptNm = deptNm;
        this.engDeptNm = engDeptNm;
        this.emplId = emplId;
        this.prfPhotoPath = prfPhotoPath;
        this.rnm = rnm;
        this.engNm = engNm;
        this.empno = empno;
        this.email = email;
        this.cdrDeptCnt = cdrDeptCnt;
        this.fstRegDtm = fstRegDtm;
        this.fstRegrIdnfNo = fstRegrIdnfNo;
        this.lastUpdDtm = lastUpdDtm;
        this.lastUpdrIdnfNo = lastUpdrIdnfNo;
    }

    public String getDeptCd() {
        return deptCd;
    }

    public void setDeptCd(String deptCd) {
        this.deptCd = deptCd;
    }

    public String getDeptNm() {
        return deptNm;
    }

    public void setDeptNm(String deptNm) {
        this.deptNm = deptNm;
    }

    public String getEngDeptNm() {
        return engDeptNm;
    }

    public void setEngDeptNm(String engDeptNm) {
        this.engDeptNm = engDeptNm;
    }

    public String getEmplId() {
        return emplId;
    }

    public void setEmplId(String emplId) {
        this.emplId = emplId;
    }

    public String getPrfPhotoPath() {
        return prfPhotoPath;
    }

    public void setPrfPhotoPath(String prfPhotoPath) {
        this.prfPhotoPath = prfPhotoPath;
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

    public String getEmpno() {
        return empno;
    }

    public void setEmpno(String empno) {
        this.empno = empno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCdrDeptCnt() {
        return cdrDeptCnt;
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

    public void setCdrDeptCnt(int cdrDeptCnt) {
        this.cdrDeptCnt = cdrDeptCnt;
    }

    @Override
    public String toString() {
        return "DeptAndEmplDto{" +
                "deptCd='" + deptCd + '\'' +
                ", deptNm='" + deptNm + '\'' +
                ", engDeptNm='" + engDeptNm + '\'' +
                ", emplId='" + emplId + '\'' +
                ", prfPhotoPath='" + prfPhotoPath + '\'' +
                ", rnm='" + rnm + '\'' +
                ", engNm='" + engNm + '\'' +
                ", empno='" + empno + '\'' +
                ", email='" + email + '\'' +
                ", cdrDeptCnt=" + cdrDeptCnt +
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
        DeptAndEmplDto that = (DeptAndEmplDto) o;
        return cdrDeptCnt == that.cdrDeptCnt && deptCd.equals(that.deptCd) && deptNm.equals(that.deptNm) && Objects.equals(engDeptNm, that.engDeptNm) && emplId.equals(that.emplId) && Objects.equals(prfPhotoPath, that.prfPhotoPath) && rnm.equals(that.rnm) && Objects.equals(engNm, that.engNm) && Objects.equals(empno, that.empno) && email.equals(that.email) && Objects.equals(fstRegDtm, that.fstRegDtm) && Objects.equals(fstRegrIdnfNo, that.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, that.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, that.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptCd, deptNm, engDeptNm, emplId, prfPhotoPath, rnm, engNm, empno, email, cdrDeptCnt, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }
}
