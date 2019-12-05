package com.mage.po.vo;

import java.util.List;

/**
 * 每次数据库操作的sql以及对应的参数
 * @author Cushier
 *
 */
public class SqlParams {
	
	private String sql;
	private List<Object> params;

	public SqlParams(String sql, List<Object> params) {
		super();
		this.sql = sql;
		this.params = params;
	}

	public SqlParams() {
	}

	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<Object> getParams() {
		return params;
	}
	public void setParams(List<Object> params) {
		this.params = params;
	}
}
