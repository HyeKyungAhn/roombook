package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import site.roombook.serializer.DateDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "spaceBookDtoBuilder")
@Getter @ToString
public class SpaceBookDto {
    @JsonProperty("bookId")
    private String spaceBookId;
    @JsonIgnore
    private String spaceBookEmplNo;
    @JsonIgnore
    private String emplId;
    @JsonIgnore
    private String emplRole;
    @JsonProperty("selfBook")
    private Boolean selfBookYN;
    @JsonProperty("spaceNo")
    private Integer spaceBookSpaceNo;
    @JsonProperty("spaceName")
    private String spaceBookSpaceNm;
    @JsonIgnore
    private String spaceBookLocDesc;
    @JsonProperty("date")
    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate spaceBookDate;
    @JsonProperty("beginTime")
    private LocalTime spaceBookBgnTm;
    @JsonProperty("endTime")
    private LocalTime spaceBookEndTm;
    private String spaceBookCn;
    @JsonIgnore
    private String spaceCnclRsn;
    @JsonProperty("statusCode")
    private String spaceBookStusCd;
    @JsonIgnore
    private LocalDateTime fstRegDtm;
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private String registerId;
    @JsonIgnore
    private LocalDateTime lastUpdDtm;
    @JsonIgnore
    private String lastUpdrIdnfNo;
    @JsonIgnore
    private String modifierId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceBookDto that = (SpaceBookDto) o;
        return Objects.equals(spaceBookId, that.spaceBookId) && Objects.equals(spaceBookEmplNo, that.spaceBookEmplNo) && Objects.equals(emplId, that.emplId) && Objects.equals(selfBookYN, that.selfBookYN) && Objects.equals(spaceBookSpaceNo, that.spaceBookSpaceNo) && Objects.equals(spaceBookSpaceNm, that.spaceBookSpaceNm) && Objects.equals(spaceBookLocDesc, that.spaceBookLocDesc) && Objects.equals(spaceBookDate, that.spaceBookDate) && Objects.equals(spaceBookBgnTm, that.spaceBookBgnTm) && Objects.equals(spaceBookEndTm, that.spaceBookEndTm) && Objects.equals(spaceBookCn, that.spaceBookCn) && Objects.equals(spaceCnclRsn, that.spaceCnclRsn) && Objects.equals(spaceBookStusCd, that.spaceBookStusCd) && Objects.equals(fstRegDtm, that.fstRegDtm) && Objects.equals(fstRegrIdnfNo, that.fstRegrIdnfNo) && Objects.equals(registerId, that.registerId) && Objects.equals(lastUpdDtm, that.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, that.lastUpdrIdnfNo) && Objects.equals(modifierId, that.modifierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaceBookId, spaceBookEmplNo, emplId, selfBookYN, spaceBookSpaceNo, spaceBookSpaceNm, spaceBookLocDesc, spaceBookDate, spaceBookBgnTm, spaceBookEndTm, spaceBookCn, spaceCnclRsn, spaceBookStusCd, fstRegDtm, fstRegrIdnfNo, registerId, lastUpdDtm, lastUpdrIdnfNo, modifierId);
    }
}
