package cenco.xz.com.commonlib.bean;

/**
 * Created by Administrator on 2018/3/13.
 */

public class Result<T> {

    /**
     * code : 1
     * message : 操作成功
     * data : {"name":"xz","age":"20","address":"山东","phone":"15588591960"}
     */

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}