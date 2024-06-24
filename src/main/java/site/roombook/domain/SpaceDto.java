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
    private Integer SPACE_NO;
    @JsonProperty("spaceNm")
    private String SPACE_NM;
    @JsonProperty("maxCapacity")
    private Integer SPACE_MAX_PSON_CNT;
    @JsonProperty("spaceLoc")
    private String SPACE_LOC_DESC;
    @JsonProperty("spaceDesc")
    private String SPACE_ADTN_DESC;
    @JsonProperty("maxRsvsTms")
    private Integer SPACE_MAX_RSVD_TMS;
    @JsonProperty("startTm")
    private LocalTime SPACE_USG_POSBL_BGN_TM;
    @JsonProperty("finishTm")
    private LocalTime SPACE_USG_POSBL_END_TM;
    @JsonProperty("weekend")
    private Character SPACE_WKEND_USG_POSBL_YN = 'N';
    @JsonProperty("hide")
    private Character SPACE_HIDE_YN = 'N';
    @JsonIgnore
    private LocalDateTime FST_REG_DTM;
    @JsonIgnore
    private String FST_REGR_IDNF_NO;
    @JsonIgnore
    private LocalDateTime LAST_UPD_DTM;
    @JsonIgnore
    private String LAST_UPDR_IDNF_NO;

    @JsonProperty("files")
    private MultipartFile[] FILES;
    @JsonProperty("rescs")
    private List<RescDto> RESCS;

    private SpaceDto(Builder builder) {
        this.SPACE_NO = builder.SPACE_NO;
        this.SPACE_NM = builder.SPACE_NM;
        this.SPACE_MAX_PSON_CNT = builder.SPACE_MAX_PSON_CNT;
        this.SPACE_LOC_DESC = builder.SPACE_LOC_DESC;
        this.SPACE_ADTN_DESC = builder.SPACE_ADTN_DESC;
        this.SPACE_MAX_RSVD_TMS = builder.SPACE_MAX_RSVD_TMS;
        this.SPACE_USG_POSBL_BGN_TM = builder.SPACE_USG_POSBL_BGN_TM;
        this.SPACE_USG_POSBL_END_TM = builder.SPACE_USG_POSBL_END_TM;
        this.SPACE_WKEND_USG_POSBL_YN = builder.SPACE_WKEND_USG_POSBL_YN;
        this.SPACE_HIDE_YN = builder.SPACE_HIDE_YN;
        this.FST_REG_DTM = builder.FST_REG_DTM;
        this.FST_REGR_IDNF_NO = builder.FST_REGR_IDNF_NO;
        this.LAST_UPD_DTM = builder.LAST_UPD_DTM;
        this.LAST_UPDR_IDNF_NO = builder.LAST_UPDR_IDNF_NO;
        this.FILES = builder.FILES;
        this.RESCS = builder.RESCS;
    }

    public static class Builder{
        //필수
        private String SPACE_NM;
        private Integer SPACE_MAX_PSON_CNT;
        private String SPACE_LOC_DESC;
        private String SPACE_ADTN_DESC;
        private Integer SPACE_MAX_RSVD_TMS;
        private LocalTime SPACE_USG_POSBL_BGN_TM;
        private LocalTime SPACE_USG_POSBL_END_TM;
        private Character SPACE_WKEND_USG_POSBL_YN = 'N';
        private Character SPACE_HIDE_YN = 'N';

        //비필수
        private Integer SPACE_NO;
        private LocalDateTime FST_REG_DTM;
        private String FST_REGR_IDNF_NO;
        private LocalDateTime LAST_UPD_DTM;
        private String LAST_UPDR_IDNF_NO;
        private MultipartFile[] FILES;
        private List<RescDto> RESCS;

        public Builder spaceNo(Integer SPACE_NO) {
            this.SPACE_NO = SPACE_NO;
            return this;
        }

        public Builder spaceNm(String SPACE_NM) {
            this.SPACE_NM = SPACE_NM;
            return this;
        }

        public Builder spaceMaxPsonCnt(Integer SPACE_MAX_PSON_CNT) {
            this.SPACE_MAX_PSON_CNT = SPACE_MAX_PSON_CNT;
            return this;
        }

        public Builder spaceLocDesc(String SPACE_LOC_DESC) {
            this.SPACE_LOC_DESC = SPACE_LOC_DESC;
            return this;
        }

        public Builder spaceAdtnDesc(String SPACE_ADTN_DESC) {
            this.SPACE_ADTN_DESC = SPACE_ADTN_DESC;
            return this;
        }

        public Builder spaceMaxRsvdTms(Integer SPACE_MAX_RSVD_TMS) {
            this.SPACE_MAX_RSVD_TMS = SPACE_MAX_RSVD_TMS;
            return this;
        }

        public Builder spaceUsgPosblBgnTm(LocalTime SPACE_USG_POSBL_BGN_TM) {
            this.SPACE_USG_POSBL_BGN_TM = SPACE_USG_POSBL_BGN_TM;
            return this;
        }

        public Builder spaceUsgPosblEndTm(LocalTime SPACE_USG_POSBL_END_TM) {
            this.SPACE_USG_POSBL_END_TM = SPACE_USG_POSBL_END_TM;
            return this;
        }

        public Builder spaceWkendUsgPosblYn(Character SPACE_WKEND_USG_POSBL_YN) {
            this.SPACE_WKEND_USG_POSBL_YN = SPACE_WKEND_USG_POSBL_YN;
            return this;
        }

        public Builder spaceHideYn(Character SPACE_HIDE_YN) {
            this.SPACE_HIDE_YN = SPACE_HIDE_YN;
            return this;
        }

        public Builder fstRegDtm(LocalDateTime FST_REG_DTM) {
            this.FST_REG_DTM = FST_REG_DTM;
            return this;
        }

        public Builder fstRegrIdnfNo(String FST_REGR_IDNF_NO) {
            this.FST_REGR_IDNF_NO = FST_REGR_IDNF_NO;
            return this;
        }

        public Builder lastUpdDtm(LocalDateTime LAST_UPD_DTM) {
            this.LAST_UPD_DTM = LAST_UPD_DTM;
            return this;
        }

        public Builder lastUpdrIdnfNo(String LAST_UPDR_IDNF_NO) {
            this.LAST_UPDR_IDNF_NO = LAST_UPDR_IDNF_NO;
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
            if (this.SPACE_NM == null || this.SPACE_NM.isEmpty()) {
                throw new IllegalArgumentException("spaceNm is a required field and cannot be null or empty");
            }

            if (this.SPACE_MAX_PSON_CNT == null || this.SPACE_MAX_PSON_CNT==0) {
                throw new IllegalArgumentException("spaceMaxPsonCnt is a required field and cannot be null");
            }

            if (this.SPACE_LOC_DESC == null || this.SPACE_LOC_DESC.isEmpty()) {
                throw new IllegalArgumentException("spaceLocDesc is a required field and cannot be null or empty");
            }

            if (this.SPACE_ADTN_DESC == null || this.SPACE_ADTN_DESC.isEmpty()) {
                throw new IllegalArgumentException("spaceAdtnDesc is a required field and cannot be null or empty");
            }

            if (this.SPACE_MAX_RSVD_TMS == null || this.SPACE_MAX_RSVD_TMS == 0) {
                throw new IllegalArgumentException("spaceMaxRsvdTms is a required field and cannot be null or zero");
            }

            if (this.SPACE_USG_POSBL_BGN_TM == null) {
                throw new IllegalArgumentException("spaceUsgPosblBgnTm is a required field and cannot be null or empty");
            }

            if (this.SPACE_USG_POSBL_END_TM == null) {
                throw new IllegalArgumentException("spaceUsgPosblEndTm is a required field and cannot be null or empty");
            }

            if (this.SPACE_WKEND_USG_POSBL_YN == null) {
                throw new IllegalArgumentException("spaceWkendUsgPosblYn is a required field and cannot be null or empty");
            }

            if (this.SPACE_HIDE_YN == null) {
                throw new IllegalArgumentException("spaceHideYn is a required field and cannot be null or empty");
            }

            return new SpaceDto(this);
        }
    }
}
