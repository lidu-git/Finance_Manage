package com.mage.po;

/**
 * 用户bean
 * @author Cushier
 *
 */
public class User {

	private Integer id; // 主键id
	private String name; // 用户名
	private String pwd; // 密码
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
