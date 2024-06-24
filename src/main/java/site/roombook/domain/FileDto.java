package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "FileDtoBuilder")
public class FileDto {
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
    @JsonIgnore
    private LocalDateTime FST_REG_DTM;
    @JsonIgnore
    private String FST_REGR_IDNF_NO;
    @JsonIgnore
    private LocalDateTime LAST_UPD_DTM;
    @JsonIgnore
    private String LAST_UPDR_IDNF_NO;

    @JsonIgnore
    private Integer maxFileCnt;

    public static FileDtoBuilder builder(String FILE_NM){
        if (FILE_NM == null) {
            throw new IllegalArgumentException("파일명 누락: FILE_NM");
        }
        return FileDtoBuilder().FILE_NM(FILE_NM);
    }
}
