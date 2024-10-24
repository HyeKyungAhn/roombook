package store.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import store.roombook.serializer.SpaceDeserializer;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(using = SpaceDeserializer.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "SpaceDtoBuilder")
public class SpaceDto {
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
    @JsonProperty("maxRsvsTms")
    private Integer spaceMaxRsvdTms;
    @JsonProperty("startTm")
    private LocalTime spaceUsgPosblBgnTm;
    @JsonProperty("finishTm")
    private LocalTime spaceUsgPosblEndTm;
    @JsonProperty("weekend")
    private Character spaceWkendUsgPosblYn = 'N';
    @JsonProperty("hide")
    private Character spaceHideYn = 'N';
    @JsonIgnore
    private LocalDateTime fstRegDtm;
    @JsonIgnore
    private String fstRegrIdnfNo;
    @JsonIgnore
    private LocalDateTime lastUpdDtm;
    @JsonIgnore
    private String lastUpdrIdnfNo;
    @JsonIgnore
    private String emplId;

    @JsonProperty("files")
    private List<FileDto> files;
    @JsonProperty("resources")
    private List<RescDto> resources;

    private SpaceDto(Builder builder) {
        this.spaceNo = builder.spaceNo;
        this.spaceNm = builder.spaceNm;
        this.spaceMaxPsonCnt = builder.spaceMaxPsonCnt;
        this.spaceLocDesc = builder.spaceLocDesc;
        this.spaceAdtnDesc = builder.spaceAdtnDesc;
        this.spaceMaxRsvdTms = builder.spaceMaxRsvdTms;
        this.spaceUsgPosblBgnTm = builder.spaceUsgPosblBgnTm;
        this.spaceUsgPosblEndTm = builder.spaceUsgPosblEndTm;
        this.spaceWkendUsgPosblYn = builder.spaceWkendUsgPosblYn;
        this.spaceHideYn = builder.spaceHideYn;
        this.fstRegDtm = builder.fstRegDtm;
        this.fstRegrIdnfNo = builder.fstRegrIdnfNo;
        this.lastUpdDtm = builder.lastUpdDtm;
        this.lastUpdrIdnfNo = builder.lastUpdrIdnfNo;
        this.emplId = builder.emplId;
        this.files = builder.files;
        this.resources = builder.resources;
    }

    public static class Builder{
        private String spaceNm;
        private Integer spaceMaxPsonCnt;
        private String spaceLocDesc;
        private String spaceAdtnDesc;
        private Integer spaceMaxRsvdTms;
        private LocalTime spaceUsgPosblBgnTm;
        private LocalTime spaceUsgPosblEndTm;
        private Character spaceWkendUsgPosblYn = 'N';
        private Character spaceHideYn = 'N';
        private Integer spaceNo;
        private LocalDateTime fstRegDtm;
        private String fstRegrIdnfNo;
        private LocalDateTime lastUpdDtm;
        private String lastUpdrIdnfNo;
        private String emplId;
        private List<FileDto> files;
        private List<RescDto> resources;

        public Builder spaceNo(Integer spaceNo) {
            this.spaceNo = spaceNo;
            return this;
        }

        public Builder spaceNm(String spaceNm) {
            this.spaceNm = spaceNm;
            return this;
        }

        public Builder spaceMaxPsonCnt(Integer spaceMaxPsonCnt) {
            this.spaceMaxPsonCnt = spaceMaxPsonCnt;
            return this;
        }

        public Builder spaceLocDesc(String spaceLocDesc) {
            this.spaceLocDesc = spaceLocDesc;
            return this;
        }

        public Builder spaceAdtnDesc(String spaceAdtnDesc) {
            this.spaceAdtnDesc = spaceAdtnDesc;
            return this;
        }

        public Builder spaceMaxRsvdTms(Integer spaceMaxRsvdTms) {
            this.spaceMaxRsvdTms = spaceMaxRsvdTms;
            return this;
        }

        public Builder spaceUsgPosblBgnTm(LocalTime spaceUsgPosblBgnTm) {
            this.spaceUsgPosblBgnTm = spaceUsgPosblBgnTm;
            return this;
        }

        public Builder spaceUsgPosblEndTm(LocalTime spaceUsgPosblEndTm) {
            this.spaceUsgPosblEndTm = spaceUsgPosblEndTm;
            return this;
        }

        public Builder spaceWkendUsgPosblYn(Character spaceWkendUsgPosblYn) {
            this.spaceWkendUsgPosblYn = spaceWkendUsgPosblYn;
            return this;
        }

        public Builder spaceHideYn(Character spaceHideYn) {
            this.spaceHideYn = spaceHideYn;
            return this;
        }

        public Builder fstRegDtm(LocalDateTime fstRegDtm) {
            this.fstRegDtm = fstRegDtm;
            return this;
        }

        public Builder fstRegrIdnfNo(String fstRegrIdnfNo) {
            this.fstRegrIdnfNo = fstRegrIdnfNo;
            return this;
        }

        public Builder lastUpdDtm(LocalDateTime lastUpdDtm) {
            this.lastUpdDtm = lastUpdDtm;
            return this;
        }

        public Builder lastUpdrIdnfNo(String lastUpdrIdnfNo) {
            this.lastUpdrIdnfNo = lastUpdrIdnfNo;
            return this;
        }

        public Builder emplId(String emplId) {
            this.emplId = emplId;
            return this;
        }

        public Builder files(List<FileDto> files) {
            this.files = files;
            return this;
        }

        public Builder resources(List<RescDto> resources) {
            this.resources = resources;
            return this;
        }

        public SpaceDto build(){
            return new SpaceDto(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceDto spaceDto = (SpaceDto) o;
        return Objects.equals(spaceNo, spaceDto.spaceNo) && Objects.equals(spaceNm, spaceDto.spaceNm) && Objects.equals(spaceMaxPsonCnt, spaceDto.spaceMaxPsonCnt) && Objects.equals(spaceLocDesc, spaceDto.spaceLocDesc) && Objects.equals(spaceAdtnDesc, spaceDto.spaceAdtnDesc) && Objects.equals(spaceMaxRsvdTms, spaceDto.spaceMaxRsvdTms) && Objects.equals(spaceUsgPosblBgnTm, spaceDto.spaceUsgPosblBgnTm) && Objects.equals(spaceUsgPosblEndTm, spaceDto.spaceUsgPosblEndTm) && Objects.equals(spaceWkendUsgPosblYn, spaceDto.spaceWkendUsgPosblYn) && Objects.equals(spaceHideYn, spaceDto.spaceHideYn) && Objects.equals(fstRegDtm, spaceDto.fstRegDtm) && Objects.equals(fstRegrIdnfNo, spaceDto.fstRegrIdnfNo) && Objects.equals(lastUpdDtm, spaceDto.lastUpdDtm) && Objects.equals(lastUpdrIdnfNo, spaceDto.lastUpdrIdnfNo) && Objects.equals(emplId, spaceDto.emplId) && Objects.equals(files, spaceDto.files) && Objects.equals(resources, spaceDto.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaceNo, spaceNm, spaceMaxPsonCnt, spaceLocDesc, spaceAdtnDesc, spaceMaxRsvdTms, spaceUsgPosblBgnTm, spaceUsgPosblEndTm, spaceWkendUsgPosblYn, spaceHideYn, fstRegDtm, fstRegrIdnfNo, lastUpdDtm, lastUpdrIdnfNo, emplId, files, resources);
    }
}
