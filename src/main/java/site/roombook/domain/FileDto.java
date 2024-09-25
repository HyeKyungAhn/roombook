package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDto fileDto = (FileDto) o;
        return Objects.equals(fileNo, fileDto.fileNo) && Objects.equals(atchLocNo, fileDto.atchLocNo) && Objects.equals(atchLocCd, fileDto.atchLocCd) && Objects.equals(fileNm, fileDto.fileNm) && Objects.equals(fileOrglNm, fileDto.fileOrglNm) && Objects.equals(fileTypNm, fileDto.fileTypNm) && Objects.equals(fileSize, fileDto.fileSize) && Objects.equals(fstRegDtm, fileDto.fstRegDtm) && Objects.equals(fstRegrIdnfNo, fileDto.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, fileDto.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, fileDto.lastUpdrIdnfNo) && Objects.equals(maxFileCnt, fileDto.maxFileCnt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileNo, atchLocNo, atchLocCd, fileNm, fileOrglNm, fileTypNm, fileSize, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo, maxFileCnt);
    }
}
