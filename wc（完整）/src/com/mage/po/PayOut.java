package com.mage.po;

import java.util.Date;

/**
 * 支出bean
 * @author Cushier
 *
 */
public class PayOut {

	private Integer id; // 主键id
	private String outName; // 支出名称
	private Integer outTypeId; // 外键，支出类型表的主键id
	private Double money; // 支出金额
	private Integer accountId; // 外键，所属账户
	private Date createTime; // 创建时间
	private Date updateTime; // 更新时间
	private String remark; // 支出备注
	
	private String accountName; // 账户名称
	private String typeName; // 支出类型名称
	private Integer parentId; // 父类型ID

	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOutName() {
		return outName;
	}
	public void setOutName(String outName) {
		this.outName = outName;
	}
	public Integer getOutTypeId() {
		return outTypeId;
	}
	public void setOutTypeId(Integer outTypeId) {
		this.outTypeId = outTypeId;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
