package com.mage.po;

/**
 * 支出类型bean
 * @author Cushier
 *
 */
public class OutType {

	private Integer id; // 主键id
	private String typeName; // 类型名称
	private Integer pid; // 所属类型,自关联

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

}
