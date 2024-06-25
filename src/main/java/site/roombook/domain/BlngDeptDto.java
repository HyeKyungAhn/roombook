package site.roombook.domain;

import java.util.Date;
import java.util.Objects;

public class BlngDeptDto {
    private String blngDeptCd;
    private String blngEmplNo;
    private char repDeptYn;
    private Date fstRegDtm;
    private String fstRegrIdnfNo;
    private Date lastUpdDtm;
    private String lastUpdrIdnfNo;

    public BlngDeptDto() {}

    public BlngDeptDto(String blngDeptCd, String blngEmplNo, char repDeptYn, Date fstRegDtm, String fstRegrIdnfNo, Date lastUpdDtm, String lastUpdrIdnfNo) {
        this.blngDeptCd = blngDeptCd;
        this.blngEmplNo = blngEmplNo;
        this.repDeptYn = repDeptYn;
        this.fstRegDtm = fstRegDtm;
        this.fstRegrIdnfNo = fstRegrIdnfNo;
        this.lastUpdDtm = lastUpdDtm;
        this.lastUpdrIdnfNo = lastUpdrIdnfNo;
    }

    public BlngDeptDto(String blngDeptCd, String blngEmplNo, char repDeptYn, String fstRegrIdnfNo, String lastUpdrIdnfNo) {
        this.blngDeptCd = blngDeptCd;
        this.blngEmplNo = blngEmplNo;
        this.repDeptYn = repDeptYn;
        this.fstRegrIdnfNo = fstRegrIdnfNo;
        this.lastUpdrIdnfNo = lastUpdrIdnfNo;
    }

    public BlngDeptDto(String blngDeptCd, String blngEmplNo, Date fstRegDtm, String fstRegrIdnfNo, Date lastUpdDtm, String lastUpdrIdnfNo) {
        this.blngDeptCd = blngDeptCd;
        this.blngEmplNo = blngEmplNo;
        this.fstRegDtm = fstRegDtm;
        this.fstRegrIdnfNo = fstRegrIdnfNo;
        this.lastUpdDtm = lastUpdDtm;
        this.lastUpdrIdnfNo = lastUpdrIdnfNo;
    }

    public String getBlngDeptCd() {
        return blngDeptCd;
    }

    public void setBlngDeptCd(String blngDeptCd) {
        this.blngDeptCd = blngDeptCd;
    }

    public String getBlngEmplNo() {
        return blngEmplNo;
    }

    public void setBlngEmplNo(String blngEmplNo) {
        this.blngEmplNo = blngEmplNo;
    }

    public char getRepDeptYn() {
        return repDeptYn;
    }

    public void setRepDeptYn(char repDeptYn) {
        this.repDeptYn = repDeptYn;
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
        return "BlngDeptDto{" +
                "blngDeptCd='" + blngDeptCd + '\'' +
                ", blngEmplNo='" + blngEmplNo + '\'' +
                ", repDeptYn=" + repDeptYn +
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
        BlngDeptDto that = (BlngDeptDto) o;
        return repDeptYn == that.repDeptYn && blngDeptCd.equals(that.blngDeptCd) && blngEmplNo.equals(that.blngEmplNo) && fstRegDtm.equals(that.fstRegDtm) && fstRegrIdnfNo.equals(that.fstRegrIdnfNo) && lastUpdDtm.equals(that.lastUpdDtm) && lastUpdrIdnfNo.equals(that.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blngDeptCd, blngEmplNo, repDeptYn, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }
}
