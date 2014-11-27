package com.aaron.library.frame.pojo;

import java.io.Serializable;

/**
 * @author linjinfa 331710168@qq.com
 * @date 2014-4-15
 */
public class MSG implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Boolean isSuccess;
	/**
	 * 是否网络错误
	 */
	private Boolean isNetworkError;
	private String msg;
	private Object obj;
	public MSG() {
		super();
	}
	public MSG(Boolean isSuccess, String msg) {
		super();
		this.isSuccess = isSuccess;
		this.msg = msg;
	}
	public MSG(Boolean isSuccess, String msg, Object obj) {
		super();
		this.isSuccess = isSuccess;
		this.msg = msg;
		this.obj = obj;
	}
	public MSG(Boolean isSuccess, Object obj) {
		super();
		this.isSuccess = isSuccess;
		this.obj = obj;
	}
	public MSG(Boolean isSuccess, Boolean isNetworkError, String msg) {
		super();
		this.isSuccess = isSuccess;
		this.isNetworkError = isNetworkError;
		this.msg = msg;
	}
	public Boolean getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public Boolean getIsNetworkError() {
		return isNetworkError;
	}
	public void setIsNetworkError(Boolean isNetworkError) {
		this.isNetworkError = isNetworkError;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	@Override
	public String toString() {
		return "MSG [isSuccess=" + isSuccess + ", msg=" + msg + ", obj=" + obj
				+ "]";
	}
}
