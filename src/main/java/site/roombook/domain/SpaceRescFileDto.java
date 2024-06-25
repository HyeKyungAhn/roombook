package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "SpaceRescFileDtoBuilder")
public class SpaceRescFileDto {
    //space
    @JsonIgnore
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
    @JsonProperty("no")
    private Integer fileNo;
    @JsonProperty("loc_no")
    private Integer atchLocNo;
    @JsonProperty("loc_cd")
    private String atchLocCd;
    @JsonProperty("rename")
    private String fileNm;
    @JsonProperty("name")
    private String fileOrglNm;
    @JsonProperty("typ")
    private String fileTypNm;
    @JsonProperty("size")
    private Long fileSize;
    //common
    @JsonIgnore
    private LocalDateTime fstRegDtm;
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private LocalDateTime lastUpdDtm;
    @JsonIgnore
    private String lastUpdrIdnfNo;

    public static SpaceRescFileDtoBuilder builder(Integer spaceNo){
        if (spaceNo == null) {
            throw new IllegalArgumentException("필수 파라미터 누락: SPACE_NO");
        }
        return SpaceRescFileDtoBuilder().spaceNo(spaceNo);
    }
}
