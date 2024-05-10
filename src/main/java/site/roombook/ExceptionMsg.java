package site.roombook;

public enum ExceptionMsg {
    DEPT_DELETE_SUB_DEPT("0"),
    DEPT_DELETE_MEMBER("1");

    private final String code;

    ExceptionMsg(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
