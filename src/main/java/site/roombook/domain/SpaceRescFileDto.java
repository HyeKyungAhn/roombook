package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private Integer SPACE_NO;
    @JsonProperty("spaceNm")
    private String SPACE_NM;
    @JsonProperty("maxCapacity")
    private Integer SPACE_MAX_PSON_CNT;
    @JsonProperty("spaceLoc")
    private String SPACE_LOC_DESC;
    @JsonProperty("spaceDesc")
    private String SPACE_ADTN_DESC;
    @JsonProperty("maxRsvsTm")
    private Integer SPACE_MAX_RSVD_TMS;
    @JsonProperty("startTm")
    private LocalTime SPACE_USG_POSBL_BGN_TM;
    @JsonProperty("finishTm")
    private LocalTime SPACE_USG_POSBL_END_TM;
    @JsonProperty("weekend")
    private Character SPACE_WKEND_USG_POSBL_YN = 'N';
    @JsonProperty("hide")
    private Character SPACE_HIDE_YN = 'N';
    //resc
    @JsonProperty("id")
    private Integer RESC_NO;
    @JsonProperty("value")
    private String RESC_NM;
    //file
    @JsonProperty("no")
    private Integer FILE_NO;
    @JsonProperty("loc_no")
    private Integer ATCH_LOC_NO;
    @JsonProperty("loc_cd")
    private String ATCH_LOC_CD;
    @JsonProperty("rename")
    private String FILE_NM;
    @JsonProperty("name")
    private String FILE_ORGL_NM;
    @JsonProperty("typ")
    private String FILE_TYP_NM;
    @JsonProperty("size")
    private Long FILE_SIZE;
    //common
    @JsonIgnore
    private LocalDateTime FST_REG_DTM;
    @JsonIgnore
    private String FST_REGR_IDNF_NO;
    @JsonIgnore
    private LocalDateTime LAST_UPD_DTM;
    @JsonIgnore
    private String LAST_UPDR_IDNF_NO;

    public static SpaceRescFileDtoBuilder builder(Integer SPACE_NO){
        if (SPACE_NO == null) {
            throw new IllegalArgumentException("필수 파라미터 누락: SPACE_NO");
        }
        return SpaceRescFileDtoBuilder().SPACE_NO(SPACE_NO);
    }
}
