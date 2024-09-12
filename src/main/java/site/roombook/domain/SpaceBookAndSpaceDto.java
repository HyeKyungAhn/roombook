package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import site.roombook.serializer.DateDeserializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "Builder")
public class SpaceBookAndSpaceDto {
    @JsonProperty("spaceNo")
    private Integer spaceNo;
    @JsonProperty("spaceNm")
    private String spaceNm;
    @JsonProperty("maxCapacity")
    private Integer spaceMaxPsonCnt;
    @JsonProperty("spaceLoc")
    private String spaceLocDesc;
    @JsonProperty("maxRsvsTms")
    private Integer spaceMaxRsvdTms;
    @JsonProperty("startTm")
    private LocalTime spaceUsgPosblBgnTm;
    @JsonProperty("finishTm")
    private LocalTime spaceUsgPosblEndTm;
    @JsonProperty("weekend")
    private Character spaceWkendUsgPosblYn = 'N';
    @JsonProperty("bookId")
    private String spaceBookId;
    @JsonDeserialize(using = DateDeserializer.class)
    @JsonProperty("date")
    private LocalDate spaceBookDate;
    @JsonProperty("beginTime")
    private LocalTime spaceBookBgnTm;
    @JsonProperty("endTime")
    private LocalTime spaceBookEndTm;
    @JsonProperty("content")
    private String spaceBookCn;
    @JsonProperty("statusCode")
    private String spaceBookStusCd;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceBookAndSpaceDto that = (SpaceBookAndSpaceDto) o;
        return Objects.equals(spaceNo, that.spaceNo) && Objects.equals(spaceNm, that.spaceNm) && Objects.equals(spaceMaxPsonCnt, that.spaceMaxPsonCnt) && Objects.equals(spaceLocDesc, that.spaceLocDesc) && Objects.equals(spaceMaxRsvdTms, that.spaceMaxRsvdTms) && Objects.equals(spaceUsgPosblBgnTm, that.spaceUsgPosblBgnTm) && Objects.equals(spaceUsgPosblEndTm, that.spaceUsgPosblEndTm) && Objects.equals(spaceWkendUsgPosblYn, that.spaceWkendUsgPosblYn) && Objects.equals(spaceBookId, that.spaceBookId) && Objects.equals(spaceBookDate, that.spaceBookDate) && Objects.equals(spaceBookBgnTm, that.spaceBookBgnTm) && Objects.equals(spaceBookEndTm, that.spaceBookEndTm) && Objects.equals(spaceBookCn, that.spaceBookCn) && Objects.equals(spaceBookStusCd, that.spaceBookStusCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaceNo, spaceNm, spaceMaxPsonCnt, spaceLocDesc, spaceMaxRsvdTms, spaceUsgPosblBgnTm, spaceUsgPosblEndTm, spaceWkendUsgPosblYn, spaceBookId, spaceBookDate, spaceBookBgnTm, spaceBookEndTm, spaceBookCn, spaceBookStusCd);
    }
}
