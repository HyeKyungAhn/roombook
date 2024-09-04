package site.roombook;

public enum ExceptionMsg {
    DEPT_DELETE_SUB_DEPT("0"),
    DEPT_DELETE_MEMBER("1"),

    SIGNUP_EMAIL_ALREADY_EXIST("100", "이미 가입된 이메일입니다."),
    SIGNUP_BLOCK_INVALID_VALUE("101", "올바르지 않은 접근입니다. 10분 뒤 다시 시도해주세요."),
    SIGNUP_BLOCK("102", "마지막 요청으로부터 10분 뒤 다시 시도해주세요."),
    SIGNUP_EXCEED_MAX_AUTH_REQUEST_COUNT("103", "인증 요청 횟수를 3회 초과했습니다. 10분 뒤 다시 시도해주세요."),
    SIGNUP_WRONG_AUTH_CODE("104", "인증 번호가 불일치합니다."),
    SIGNUP_AUTH_CODE_EXPIRE("105", "인증번호가 만료되었습니다. 인증번호를 재요청해주세요."),
    SIGNUP_EXCEED_MAX_AUTH_ATTEMPTS_COUNT("106", "인증 실패 3회를 초과했습니다. 10분 뒤 다시 시도해주세요."),
    SIGNUP_DATA_SAVE_FAIL("107", "예상치 못한 문제가 발생했습니다. 새로고침 후 다시 시도해주세요."),
    SIGNUP_REDIS_CONNECTION_FAIL("108", "죄송합니다. 예상치 못한 문제가 발생했습니다. 관리자에게 문의해주세요."),

    EMAIL_INVALID_EMAIL("201", "인증 코드 전송에 실패했습니다. 유효한 이메일 주소가 아닙니다."),
    EMAIL_SENDING_AUTH_CODE_FAIL("202", "인증 코드 전송에 실패했습니다. 다시 시도해주세요."),

    EMPL_AUTH_UNEXPECTED_PROBLEM("301", "예상치 못한 문제가 발생했습니다.\n새로고침 후 다시 시도해주세요."),
    EMPL_AUTH_INVALID_AUTH_NAME("302", "유효하지 않은 않은 값입니다."),
    EMPL_AUTH_UPDATE_FAIL("303", "업데이트에 실패했습니다. 다시 시도해주세요."),

    DEPT_MEM_DELETE_FAIL("401", "구성원 수정에 실패하였습니다.\n다시 시도해주세요."),
    DEPT_MEM_ADD_FAIL("402", "구성원 수정에 실패하였습니다.\n다시 시도해주세요.");

    private final String code;
    private final String content;

    ExceptionMsg(String code){
        this(code, "");
    }

    ExceptionMsg(String code, String content) {
        this.code = code;
        this.content = content;
    }

    public String getCode(){
        return code;
    }

    public String getContent() { return content; }
}
