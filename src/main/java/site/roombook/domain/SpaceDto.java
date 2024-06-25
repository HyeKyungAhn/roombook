package site.roombook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import site.roombook.serializer.SpaceDeserializer;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@JsonDeserialize(using = SpaceDeserializer.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder(builderMethodName = "SpaceDtoBuilder")
public class SpaceDto {
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

    @JsonProperty("files")
    private MultipartFile[] FILES;
    @JsonProperty("rescs")
    private List<RescDto> RESCS;

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
        this.FILES = builder.FILES;
        this.RESCS = builder.RESCS;
    }

    public static class Builder{
        //필수
        private String spaceNm;
        private Integer spaceMaxPsonCnt;
        private String spaceLocDesc;
        private String spaceAdtnDesc;
        private Integer spaceMaxRsvdTms;
        private LocalTime spaceUsgPosblBgnTm;
        private LocalTime spaceUsgPosblEndTm;
        private Character spaceWkendUsgPosblYn = 'N';
        private Character spaceHideYn = 'N';

        //비필수
        private Integer spaceNo;
        private LocalDateTime fstRegDtm;
        private String fstRegrIdnfNo;
        private LocalDateTime lastUpdDtm;
        private String lastUpdrIdnfNo;
        private MultipartFile[] FILES;
        private List<RescDto> RESCS;

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

        public Builder files(MultipartFile[] FILES) {
            this.FILES = FILES;
            return this;
        }

        public Builder rescs(List<RescDto> RESCS) {
            this.RESCS = RESCS;
            return this;
        }

        public SpaceDto build(){
            if (this.spaceNm == null || this.spaceNm.isEmpty()) {
                throw new IllegalArgumentException("spaceNm is a required field and cannot be null or empty");
            }

            if (this.spaceMaxPsonCnt == null || this.spaceMaxPsonCnt==0) {
                throw new IllegalArgumentException("spaceMaxPsonCnt is a required field and cannot be null");
            }

            if (this.spaceLocDesc == null || this.spaceLocDesc.isEmpty()) {
                throw new IllegalArgumentException("spaceLocDesc is a required field and cannot be null or empty");
            }

            if (this.spaceAdtnDesc == null || this.spaceAdtnDesc.isEmpty()) {
                throw new IllegalArgumentException("spaceAdtnDesc is a required field and cannot be null or empty");
            }

            if (this.spaceMaxRsvdTms == null || this.spaceMaxRsvdTms == 0) {
                throw new IllegalArgumentException("spaceMaxRsvdTms is a required field and cannot be null or zero");
            }

            if (this.spaceUsgPosblBgnTm == null) {
                throw new IllegalArgumentException("spaceUsgPosblBgnTm is a required field and cannot be null or empty");
            }

            if (this.spaceUsgPosblEndTm == null) {
                throw new IllegalArgumentException("spaceUsgPosblEndTm is a required field and cannot be null or empty");
            }

            if (this.spaceWkendUsgPosblYn == null) {
                throw new IllegalArgumentException("spaceWkendUsgPosblYn is a required field and cannot be null or empty");
            }

            if (this.spaceHideYn == null) {
                throw new IllegalArgumentException("spaceHideYn is a required field and cannot be null or empty");
            }

            return new SpaceDto(this);
        }
    }
}
