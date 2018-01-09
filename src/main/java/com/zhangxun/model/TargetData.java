package com.zhangxun.model;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import com.zhangxun.util.DateUtil;
import com.zhangxun.util.Money;

/**
 * 目标类型
 * 
 * @author zhangxun
 */
public class TargetData implements Cloneable {

	/**
	 * 凭证日期
	 */
	@ExcelSort(sort = 0)
	private Date documentDate;

	/**
	 * 会计年度
	 */
	@ExcelSort(sort = 1)
	private int accountYear;

	/**
	 * 会计期间
	 */
	@ExcelSort(sort = 2)
	private int accountMonth;

	/**
	 * 凭证字
	 */
	@ExcelSort(sort = 3)
	private String documentString;

	/**
	 * 凭证号
	 */
	@ExcelSort(sort = 4)
	private int documentCode;

	/**
	 * 科目代码
	 */
	@ExcelSort(sort = 5)
	private String subjectCode;

	/**
	 * 科目名称
	 */
	@ExcelSort(sort = 6)
	private String subjectName;

	/**
	 * 币种代码
	 */
	@ExcelSort(sort = 7)
	private String currencyCode;

	/**
	 * 币种名称
	 */
	@ExcelSort(sort = 8)
	private String currencyName;

	/**
	 * 原币金额
	 */
	@ExcelSort(sort = 9)
	private Money totalAmount;

	/**
	 * 借方
	 */
	@ExcelSort(sort = 10)
	private Money outAmount;

	/**
	 * 贷方
	 */
	@ExcelSort(sort = 11)
	private Money inAmount;

	/**
	 * 制单
	 */
	@ExcelSort(sort = 12)
	private String operator;

	/**
	 * 审核员
	 */
	@ExcelSort(sort = 13)
	private String auditor;

	/**
	 * 核准员
	 */
	@ExcelSort(sort = 14)
	private String approver;

	/**
	 * 出纳员
	 */
	@ExcelSort(sort = 15)
	private String cashier;

	/**
	 * 经办员
	 */
	@ExcelSort(sort = 16)
	private String handler;

	/**
	 * 结算方式
	 */
	@ExcelSort(sort = 17)
	private String settleWay;

	/**
	 * 结算号
	 */
	@ExcelSort(sort = 18)
	private String settleNo;

	/**
	 * 凭证摘要
	 */
	@ExcelSort(sort = 19)
	private String documentMemo;

	/**
	 * 数量
	 */
	@ExcelSort(sort = 20)
	private String number;

	/**
	 * 单位
	 */
	@ExcelSort(sort = 21)
	private String unit;

	/**
	 * 单价
	 */
	@ExcelSort(sort = 22)
	private String price;

	/**
	 * 参考信息
	 */
	@ExcelSort(sort = 23)
	private String referenceInformation;

	/**
	 * 业务日期
	 */
	@ExcelSort(sort = 24)
	private Date businessDate;

	/**
	 * 业务编号
	 */
	@ExcelSort(sort = 25)
	private String bussnessCode;

	/**
	 * 附件数
	 */
	@ExcelSort(sort = 26)
	private int appendCount;

	/**
	 * 序号
	 */
	@ExcelSort(sort = 27)
	private int sortNo;

	/**
	 * 系统模块
	 */
	@ExcelSort(sort = 28)
	private String systemModule;

	/**
	 * 业务描述
	 */
	@ExcelSort(sort = 29)
	private String bussnessMemo;

	/**
	 * 汇率类型
	 */
	@ExcelSort(sort = 30)
	private String exchangeType;

	/**
	 * 汇率
	 */
	@ExcelSort(sort = 31)
	private String exchange;

	/**
	 * 分录序号
	 */
	@ExcelSort(sort = 32)
	private int memuSortNo;

	/**
	 * 核算项目
	 */
	@ExcelSort(sort = 33)
	private String approveProject;

	/**
	 * 过账
	 */
	@ExcelSort(sort = 34)
	private int posting;

	/**
	 * 机制凭证
	 */
	@ExcelSort(sort = 35)
	private String mechanismDocument;

	/**
	 * 现金流量
	 */
	@ExcelSort(sort = 36)
	private String cashFlow;

	/**
	 * 需要额外警告标识
	 */
	private boolean warn = false;

	public Date getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
	}

	public int getAccountYear() {
		return accountYear;
	}

	public void setAccountYear(int accountYear) {
		this.accountYear = accountYear;
	}

	public int getAccountMonth() {
		return accountMonth;
	}

	public void setAccountMonth(int accountMonth) {
		this.accountMonth = accountMonth;
	}

	public String getDocumentString() {
		return documentString;
	}

	public void setDocumentString(String documentString) {
		this.documentString = documentString;
	}

	public int getDocumentCode() {
		return documentCode;
	}

	public void setDocumentCode(int documentCode) {
		this.documentCode = documentCode;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Money getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(Money outAmount) {
		this.outAmount = outAmount;
	}

	public Money getInAmount() {
		return inAmount;
	}

	public void setInAmount(Money inAmount) {
		this.inAmount = inAmount;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getCashier() {
		return cashier;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getSettleWay() {
		return settleWay;
	}

	public void setSettleWay(String settleWay) {
		this.settleWay = settleWay;
	}

	public String getSettleNo() {
		return settleNo;
	}

	public void setSettleNo(String settleNo) {
		this.settleNo = settleNo;
	}

	public String getDocumentMemo() {
		return documentMemo;
	}

	public void setDocumentMemo(String documentMemo) {
		this.documentMemo = documentMemo;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getReferenceInformation() {
		return referenceInformation;
	}

	public void setReferenceInformation(String referenceInformation) {
		this.referenceInformation = referenceInformation;
	}

	public Date getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}

	public String getBussnessCode() {
		return bussnessCode;
	}

	public void setBussnessCode(String bussnessCode) {
		this.bussnessCode = bussnessCode;
	}

	public int getAppendCount() {
		return appendCount;
	}

	public void setAppendCount(int appendCount) {
		this.appendCount = appendCount;
	}

	public int getSortNo() {
		return sortNo;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public String getSystemModule() {
		return systemModule;
	}

	public void setSystemModule(String systemModule) {
		this.systemModule = systemModule;
	}

	public String getBussnessMemo() {
		return bussnessMemo;
	}

	public void setBussnessMemo(String bussnessMemo) {
		this.bussnessMemo = bussnessMemo;
	}

	public String getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(String exchangeType) {
		this.exchangeType = exchangeType;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public int getMemuSortNo() {
		return memuSortNo;
	}

	public void setMemuSortNo(int memuSortNo) {
		this.memuSortNo = memuSortNo;
	}

	public String getApproveProject() {
		return approveProject;
	}

	public void setApproveProject(String approveProject) {
		this.approveProject = approveProject;
	}

	public int getPosting() {
		return posting;
	}

	public void setPosting(int posting) {
		this.posting = posting;
	}

	public String getMechanismDocument() {
		return mechanismDocument;
	}

	public void setMechanismDocument(String mechanismDocument) {
		this.mechanismDocument = mechanismDocument;
	}

	public String getCashFlow() {
		return cashFlow;
	}

	public void setCashFlow(String cashFlow) {
		this.cashFlow = cashFlow;
	}

	public boolean isWarn() {
		return warn;
	}

	public void setWarn(boolean warn) {
		this.warn = warn;
	}

	public Object[] getRow(int length) {
		Object[] row = new Object[length];
		Class _class = this.getClass();
		Field[] fields = _class.getDeclaredFields();
		for (Field f : fields) {
			// 获取字段中包含ExcelSort的注解
			ExcelSort meta = f.getAnnotation(ExcelSort.class);
			if (meta != null) {
				try {
					Object data = new PropertyDescriptor(f.getName(), _class).getReadMethod().invoke(this);
					if (data != null && data.getClass() == Date.class) {
						data = DateUtil.getFormat(DateUtil.YYYYMMDD2).format(data);
					}
					row[meta.sort()] = data;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (IntrospectionException e) {
					e.printStackTrace();
				}
			}
		}

		return row;
	}

	@Override
	public Object clone() {
		TargetData targetData = null;
		try {
			targetData = (TargetData) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return targetData;
	}

	@Override
	public String toString() {
		return "TargetData [totalAmount=" + totalAmount + ", outAmount=" + outAmount + ", inAmount=" + inAmount + "]";
	}

}
