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
    @JsonIgnore
    private LocalDateTime fstRegDtm;
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private LocalDateTime lastUpdDtm;
    @JsonIgnore
    private String lastUpdrIdnfNo;

    @JsonIgnore
    private Integer maxFileCnt;

    public static FileDtoBuilder builder(String fileNm){
        if (fileNm == null) {
            throw new IllegalArgumentException("파일명 누락: fileNm");
        }
        return FileDtoBuilder().fileNm(fileNm);
    }
}
