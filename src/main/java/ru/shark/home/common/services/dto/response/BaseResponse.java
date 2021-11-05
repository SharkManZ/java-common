package ru.shark.home.common.services.dto.response;

public class BaseResponse<T> {
    private boolean success;
    private Error error;
    private T body;

    public BaseResponse() {

    }

    public BaseResponse(String code, String message) {
        this.error = new Error(code, message);
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public static BaseResponse buildError(String code, String message) {
        return new BaseResponse(code, message);
    }
}
