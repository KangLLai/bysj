/**
 *
 */
package com.bysj_backend.utils;



import java.io.Serializable;

/**
 * 操作返回对象
 * @author <a href="mailto:lihz2@asiainfo.com">FluteD</a>
 * @version 2016-4-11
 */
public class OperationResult<T> implements Serializable {

    private static final long serialVersionUID = 4573613428545769800L;

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILURE = "fail";


    public OperationResult(){
        this.status = STATUS_SUCCESS;
    }

    public OperationResult(String status, String message,T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public OperationResult(String status, String message,T data,long total){
        this.status = status;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    public OperationResult(String status, String message,T data,String errorCode){
        this.status = status;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    public static <T> OperationResult<T> error(String msg) {
        OperationResult operationResult = new OperationResult();
        operationResult.msg = msg;
        operationResult.code = 0;
        return operationResult;
    }
    /**
     * 操作结果状态
     */
    private String status;

    /**
     * 错误代码，各模块自行定义
     * 操作为STATUS_SUCCESS时，为空。
     */
    private String errorCode;

    /**
     * 错误简短信息
     * 操作为STATUS_SUCCESS时，为空。
     */
    private String message;

    /**
     * 操作返回的可序列化对象
     */
    private T data;

    /**
     * 数据总量，用于分页查询时使用
     */
    private long total;
    /**
     * 查询es数据专用，用于分页查询时使用
     * 显示数据总量，es查找默认最大允许返回1万条数据，超过的查询会报错
     * es查询时将示的总条数塞入 total (该字段最大值为1万)
     * 将显查询到的总数据量塞入 esTotal
     * 示例：若查询总条数为  10001，则total=10000  esTotal=10001
     * 		 若查询总条数为9999，则total=9999  esTotal=9999
     */
//	private long esTotal;


    /**
     * 错误
     * @param msg
     * @return
     */
    public OperationResult<T> failure(String msg){
        this.setStatus(OperationResult.STATUS_FAILURE);
        this.setMessage(msg);
        return this;
    }

    /**
     * 成功
     * @param msg
     * @return
     */
    public OperationResult<T> success(String msg){
        this.setStatus(OperationResult.STATUS_SUCCESS);
        this.setMessage(msg);
        return this;
    }



    /**
     * 判断是否成功
     * @return
     */
    public boolean isSuccess(){
        return STATUS_SUCCESS.equals(this.getStatus());
    }


    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     * @return
     */
    public Object setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return null;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * @return the total
     */
    public long getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(long total) {
        this.total = total;
    }

}
