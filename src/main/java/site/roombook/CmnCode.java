package site.roombook;

public enum CmnCode {
    ATCH_LOC_CD_SPACE("100001"),
    ATCH_LOC_CD_NOTICE("100002");

    private final String code;

    CmnCode(String code) { this.code = code; }

    public String getCode() {
        return code;
    }
}
