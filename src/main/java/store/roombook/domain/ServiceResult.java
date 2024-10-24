package store.roombook.domain;

import store.roombook.ExceptionMsg;

public class ServiceResult {
    private boolean isSuccessful;
    private ExceptionMsg msg;

    public ServiceResult(){}

    public ServiceResult(boolean isSuccessful){
        this(isSuccessful, null);
    }

    public ServiceResult(boolean isSuccessful, ExceptionMsg msg) {
        this.isSuccessful = isSuccessful;
        this.msg = msg;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public ExceptionMsg getMsg() {
        return msg;
    }

    public void setMsg(ExceptionMsg msg) {
        this.msg = msg;
    }


}
