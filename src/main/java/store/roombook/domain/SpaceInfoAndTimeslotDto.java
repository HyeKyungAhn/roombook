package store.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import store.roombook.serializer.DateDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "SpaceRescFileDtoBuilder")
public class SpaceInfoAndTimeslotDto {
    //space
    @JsonProperty("spaceNo")
    private Integer spaceNo;
    @JsonProperty("spaceNm")
    private String spaceNm;
    @JsonProperty("maxCapacity")
    private Integer spaceMaxPsonCnt;
    @JsonProperty("spaceLoc")
    private String spaceLocDesc;
    @JsonProperty("spaceDesc")
    private String spaceAdtnDesc;
    @JsonProperty("maxRsvsTm")
    private Integer spaceMaxRsvdTms;
    @JsonProperty("startTm")
    private LocalTime spaceUsgPosblBgnTm;
    @JsonProperty("finishTm")
    private LocalTime spaceUsgPosblEndTm;
    @JsonProperty("weekend")
    private Character spaceWkendUsgPosblYn = 'N';
    @JsonProperty("hide")
    private Character spaceHideYn = 'N';
    //resc
    @JsonProperty("id")
    private Integer rescNo;
    @JsonProperty("value")
    private String rescNm;
    //file
    @JsonProperty("fileNo")
    private Integer fileNo;
    @JsonProperty("fileLocNo")
    private Integer atchLocNo;
    @JsonProperty("fileLocCd")
    private String atchLocCd;
    @JsonProperty("fileName")
    private String fileNm;
    @JsonProperty("fileOrgName")
    private String fileOrglNm;
    @JsonProperty("fileTyp")
    private String fileTypNm;
    @JsonProperty("fileSize")
    private Long fileSize;
    //spaceBook
    @JsonProperty("bookId")
    private String spaceBookId;
    @JsonProperty("date")
    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate spaceBookDate;
    @JsonProperty("beginTime")
    private LocalTime spaceBookBgnTm;
    @JsonProperty("endTime")
    private LocalTime spaceBookEndTm;
    @JsonProperty("statusCode")
    private String spaceBookStusCd;
    //pagination
    @JsonIgnore
    private Integer spaceCnt;
    @JsonIgnore
    private Integer offset;
    @JsonIgnore
    private Integer rescCnt;
    @JsonIgnore
    private Boolean isHiddenSpaceInvisible;

    @JsonIgnore
    private LocalDateTime fstRegDtm;
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private LocalDateTime lastUpdDtm;
    @JsonIgnore
    private String lastUpdrIdnfNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceInfoAndTimeslotDto that = (SpaceInfoAndTimeslotDto) o;
        return Objects.equals(spaceNo, that.spaceNo) && Objects.equals(spaceNm, that.spaceNm) && Objects.equals(spaceMaxPsonCnt, that.spaceMaxPsonCnt) && Objects.equals(spaceLocDesc, that.spaceLocDesc) && Objects.equals(spaceAdtnDesc, that.spaceAdtnDesc) && Objects.equals(spaceMaxRsvdTms, that.spaceMaxRsvdTms) && Objects.equals(spaceUsgPosblBgnTm, that.spaceUsgPosblBgnTm) && Objects.equals(spaceUsgPosblEndTm, that.spaceUsgPosblEndTm) && Objects.equals(spaceWkendUsgPosblYn, that.spaceWkendUsgPosblYn) && Objects.equals(spaceHideYn, that.spaceHideYn) && Objects.equals(rescNo, that.rescNo) && Objects.equals(rescNm, that.rescNm) && Objects.equals(fileNo, that.fileNo) && Objects.equals(atchLocNo, that.atchLocNo) && Objects.equals(atchLocCd, that.atchLocCd) && Objects.equals(fileNm, that.fileNm) && Objects.equals(fileOrglNm, that.fileOrglNm) && Objects.equals(fileTypNm, that.fileTypNm) && Objects.equals(fileSize, that.fileSize) && Objects.equals(spaceBookId, that.spaceBookId) && Objects.equals(spaceBookDate, that.spaceBookDate) && Objects.equals(spaceBookBgnTm, that.spaceBookBgnTm) && Objects.equals(spaceBookEndTm, that.spaceBookEndTm) && Objects.equals(spaceBookStusCd, that.spaceBookStusCd) && Objects.equals(spaceCnt, that.spaceCnt) && Objects.equals(offset, that.offset) && Objects.equals(rescCnt, that.rescCnt) && Objects.equals(isHiddenSpaceInvisible, that.isHiddenSpaceInvisible) && Objects.equals(fstRegDtm, that.fstRegDtm) && Objects.equals(fstRegrIdnfNo, that.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, that.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, that.lastUpdrIdnfNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaceNo, spaceNm, spaceMaxPsonCnt, spaceLocDesc, spaceAdtnDesc, spaceMaxRsvdTms, spaceUsgPosblBgnTm, spaceUsgPosblEndTm, spaceWkendUsgPosblYn, spaceHideYn, rescNo, rescNm, fileNo, atchLocNo, atchLocCd, fileNm, fileOrglNm, fileTypNm, fileSize, spaceBookId, spaceBookDate, spaceBookBgnTm, spaceBookEndTm, spaceBookStusCd, spaceCnt, offset, rescCnt, isHiddenSpaceInvisible, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo);
    }
}
