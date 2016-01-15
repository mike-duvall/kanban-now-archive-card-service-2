package main.exception;


public class RestError {

    private Integer status;
    private Integer code;
    private String message;
    private String developerMessage;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer statusCode) {
        this.status = statusCode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

}
