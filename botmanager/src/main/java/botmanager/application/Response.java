package botmanager.application;

public class Response<T>{

    public enum ResponseStatus {
        SUCCESS,
        FAIL;
    }

    final public String message;
    final public ResponseStatus status;
    public T object;
    Response(ResponseStatus status, String message){ this.status = status; this.message = message;}

}
