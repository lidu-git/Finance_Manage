package com.mage.po.vo;

public class Result {

	private Integer code; // 响应状态码  1成功  0失败
	private String msg; // 提示信息

	public Result(Integer code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public Result() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
