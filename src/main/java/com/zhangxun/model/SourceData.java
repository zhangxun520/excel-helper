package com.zhangxun.model;

import java.util.Date;

/**
 * 源类型
 * 
 * @author zhangxun
 * 
 */
public class SourceData {
	/**
	 * 序号
	 */
	@ExcelSort(sort = 0)
	private String sortNo;

	/**
	 * 机构名称
	 */
	@ExcelSort(sort = 1)
	private String institutionName;

	/**
	 * 项目名称
	 */
	@ExcelSort(sort = 2)
	private String projectName;

	/**
	 * 客户名称
	 */
	@ExcelSort(sort = 3)
	private String customerName;

	/**
	 * 资金属性
	 */
	@ExcelSort(sort = 4)
	private String capitalProperties;

	/**
	 * 结算科目
	 */
	@ExcelSort(sort = 5)
	private String settleSubject;

	/**
	 * 收
	 */
	@ExcelSort(sort = 6)
	private double inNumber;

	/**
	 * 转
	 */
	private double transferNumber;

	/**
	 * 扣
	 */
	private double takeNumber;

	/**
	 * 付
	 */
	@ExcelSort(sort = 7)
	private double outNumber;

	/**
	 * 银行收
	 */
	@ExcelSort(sort = 8)
	private String bankInNumber;

	/**
	 * 银行付
	 */
	@ExcelSort(sort = 9)
	private String bankOutNumber;

	/**
	 * 到账日期
	 */
	@ExcelSort(sort = 10)
	private Date inDate;

	/**
	 * 结算日期
	 */
	@ExcelSort(sort = 11)
	private Date settleDate;

	/**
	 * 是否默认结算日期
	 */
	private boolean defaultSettleDate = true;

	public String getSortNo() {
		return sortNo;
	}

	public void setSortNo(String sortNo) {
		this.sortNo = sortNo;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCapitalProperties() {
		return capitalProperties;
	}

	public void setCapitalProperties(String capitalProperties) {
		this.capitalProperties = capitalProperties;
	}

	public String getSettleSubject() {
		return settleSubject;
	}

	public void setSettleSubject(String settleSubject) {
		this.settleSubject = settleSubject;
	}

	public double getInNumber() {
		return inNumber;
	}

	public void setInNumber(double inNumber) {
		this.inNumber = inNumber;
	}

	public double getTransferNumber() {
		return transferNumber;
	}

	public void setTransferNumber(double transferNumber) {
		this.transferNumber = transferNumber;
	}

	public double getTakeNumber() {
		return takeNumber;
	}

	public void setTakeNumber(double takeNumber) {
		this.takeNumber = takeNumber;
	}

	public double getOutNumber() {
		return outNumber;
	}

	public void setOutNumber(double outNumber) {
		this.outNumber = outNumber;
	}

	public String getBankInNumber() {
		return bankInNumber;
	}

	public void setBankInNumber(String bankInNumber) {
		this.bankInNumber = bankInNumber;
	}

	public String getBankOutNumber() {
		return bankOutNumber;
	}

	public void setBankOutNumber(String bankOutNumber) {
		this.bankOutNumber = bankOutNumber;
	}

	public Date getInDate() {
		return inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	public boolean isDefaultSettleDate() {
		return defaultSettleDate;
	}

	public void setDefaultSettleDate(boolean defaultSettleDate) {
		this.defaultSettleDate = defaultSettleDate;
	}

}
