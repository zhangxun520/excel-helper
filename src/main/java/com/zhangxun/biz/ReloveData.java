package com.zhangxun.biz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.zhangxun.exception.MyException;
import com.zhangxun.model.MyMap;
import com.zhangxun.model.SourceData;
import com.zhangxun.model.TargetData;
import com.zhangxun.util.Constants;
import com.zhangxun.util.DateUtil;
import com.zhangxun.util.Money;
import com.zhangxun.util.PropertiesUtil;
import com.zhangxun.util.StringUtil;

/**
 * EXCEL数据切换
 * 
 * @author zhangxun
 * 
 */
public class ReloveData {

	private static Properties configProperties = PropertiesUtil.getProperties(Constants.CONFIG_FILE_NAME);
	private static Properties subjectProperties = PropertiesUtil.getProperties(Constants.SUBJECT_FILE_NAME);
	private static Properties subjectAppProjectProperties = PropertiesUtil
			.getProperties(Constants.SUBJECT_APPROVEPROJECT);
	private static Properties institutionProperties = PropertiesUtil.getProperties(Constants.INSTITUTION_FILE_NAME);

	public static List<TargetData> relove(File file) throws IOException, MyException {
		List<SourceData> sourceDatas = ReadData.read(file,
				Integer.parseInt(configProperties.getProperty(Constants.START_ROW, "3")));
		List<TargetData> datas = new ArrayList<TargetData>();
		Calendar calendar = Calendar.getInstance();
		Map<String, Object> mve2Map = new HashMap<>();
		int sortNo = 0;
		for (int i = 0; i < sourceDatas.size(); i++) {
			SourceData sourceData = sourceDatas.get(i);
			TargetData targetData = buildBaseTarget(sourceData, calendar);
			mve2Map.put("settleDate", DateUtil.getFormat(DateUtil.YYYYMMDD).format(sourceData.getSettleDate()));
			mve2Map.put("customerName", sourceData.getCustomerName());
			mve2Map.put("capitalProperties", sourceData.getCapitalProperties());

			if (sourceData.getOutNumber() > 0) {// 付款后生成1002.05
				TargetData outTargetData = buildOutTarget(targetData, sourceData, mve2Map);
				outTargetData.setMemuSortNo(sortNo++);
				datas.add(outTargetData);
				if (!Constants.LFT_SUBJECT_CODE.equals(targetData.getSubjectCode())) {
					TargetData _outTargetData = build1002P05Target(outTargetData, sourceData, mve2Map);
					_outTargetData.setMemuSortNo(sortNo++);
					datas.add(_outTargetData);
				}
			}
			if (sourceData.getInNumber() > 0) {// 收款后优先成1002.05
				TargetData inTargetData = buildInTarget(targetData, sourceData, mve2Map);
				if (!Constants.LFT_SUBJECT_CODE.equals(targetData.getSubjectCode())) {
					TargetData _inTargetData = build1002P05Target(inTargetData, sourceData, mve2Map);
					_inTargetData.setMemuSortNo(sortNo++);
					datas.add(_inTargetData);
				}
				inTargetData.setMemuSortNo(sortNo++);
				datas.add(inTargetData);
			}

		}
		return datas;
	}

	/**
	 * 基础生成数据
	 * 
	 * @param sourceData
	 * @param calendar
	 * @return
	 */
	private static TargetData buildBaseTarget(SourceData sourceData, Calendar calendar) {
		TargetData targetData = new TargetData();
		targetData.setDocumentDate(calendar.getTime());
		targetData.setAccountYear(calendar.get(Calendar.YEAR));
		targetData.setAccountMonth(calendar.get(Calendar.MONTH) + 1);
		targetData.setDocumentString(configProperties.getProperty(Constants.TargetData.DOCUMENT_STRING, "记"));
		targetData.setDocumentCode(1);

		targetData.setCurrencyCode("RMB");
		targetData.setCurrencyName("人民币");

		targetData.setOperator(configProperties.getProperty(Constants.TargetData.OPERATOR, "刘毅"));
		targetData.setAuditor("NONE");
		targetData.setApprover("NONE");
		targetData.setCashier("NONE");
		targetData.setSettleWay("*");

		targetData.setNumber("0");
		targetData.setUnit("*");
		targetData.setPrice("0");
		targetData.setBusinessDate(sourceData.getInDate());
		targetData.setAppendCount(0);
		targetData.setSortNo(Integer.parseInt(configProperties.getProperty(Constants.TargetData.SORT_NO, "1001")));
		targetData.setExchangeType(configProperties.getProperty(Constants.TargetData.EXCHANGE_TYPE, "公司汇率"));
		targetData.setExchange("1");
		targetData.setPosting(0);
		return targetData;
	}

	/**
	 * 贷方(入金)
	 * 
	 * @return
	 */
	private static TargetData buildInTarget(TargetData targetData, SourceData sourceData, Map<String, Object> mve2Map) {
		String subjectName = sourceData.getSettleSubject();
		String subjectCode = null;

		MyMap myData = getPropertiesKeyByContainsValue(subjectProperties, sourceData.getSettleSubject(), false);
		if (myData.getKey() != null) {
			subjectCode = myData.getKey();
			subjectName = myData.getValue();
		} else {
			targetData.setWarn(true);
		}
		targetData.setSubjectCode(subjectCode);
		targetData.setSubjectName(subjectName);
		targetData.setOutAmount(new Money());
		targetData.setInAmount(new Money(sourceData.getInNumber()));
		targetData.setTotalAmount(targetData.getInAmount());
		targetData
				.setDocumentMemo("收" + sourceData.getProjectName() + subjectName + "," + sourceData.getCustomerName());

		targetData.setApproveProject(
				getApproveProject(targetData.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));

		return targetData;
	}

	/**
	 * 借方(出金)
	 * 
	 * @return
	 */
	private static TargetData buildOutTarget(TargetData targetData, SourceData sourceData,
			Map<String, Object> mve2Map) {
		Calendar inDate = Calendar.getInstance();
		Calendar settleDate = Calendar.getInstance();
		inDate.setTime(sourceData.getInDate());
		settleDate.setTime(sourceData.getSettleDate());

		String t1mark = configProperties.getProperty(Constants.T1_MARK, Constants.T1Mark.MONTH);
		if (Constants.T1Mark.MONTH.equals(t1mark)) {
			settleDate.set(Calendar.DATE, 1);
			inDate.set(Calendar.DATE, 1);
		}

		String subjectName = sourceData.getSettleSubject();
		String subjectCode = null;
		if (settleDate.getTimeInMillis() != inDate.getTimeInMillis()) {// 执行跨日期科目替换
			subjectCode = Constants.T1_REPLACE_SUBJECT_CODE;
			subjectName = subjectProperties.getProperty(Constants.T1_REPLACE_SUBJECT_CODE, "暂收款");
		} else {
			MyMap myData = getPropertiesKeyByContainsValue(subjectProperties, sourceData.getSettleSubject(), false);
			if (myData.getKey() != null) {
				subjectCode = myData.getKey();
				subjectName = myData.getValue();
			} else {
				targetData.setWarn(true);
			}
		}

		targetData.setSubjectCode(subjectCode);
		targetData.setSubjectName(subjectName);

		targetData.setOutAmount(new Money(sourceData.getOutNumber()));
		targetData.setInAmount(new Money());
		targetData.setTotalAmount(targetData.getOutAmount());

		targetData.setDocumentMemo(
				"付" + sourceData.getProjectName() + subjectName + "," + sourceData.getCapitalProperties());

		targetData.setApproveProject(
				getApproveProject(targetData.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));
		return targetData;
	}

	/**
	 * 借方(出金)
	 * 
	 * @return
	 */
	private static TargetData[] buildTransferTarget(TargetData targetData, SourceData sourceData,
			Map<String, Object> mve2Map) {
		Calendar inDate = Calendar.getInstance();
		Calendar settleDate = Calendar.getInstance();
		inDate.setTime(sourceData.getInDate());
		settleDate.setTime(sourceData.getSettleDate());

		String t1mark = configProperties.getProperty(Constants.T1_MARK, Constants.T1Mark.MONTH);
		if (Constants.T1Mark.MONTH.equals(t1mark)) {
			settleDate.set(Calendar.DATE, 1);
			inDate.set(Calendar.DATE, 1);
		}

		String subjectName = sourceData.getSettleSubject();
		String subjectCode = null;
		if (settleDate.getTimeInMillis() != inDate.getTimeInMillis()) {// 执行跨日期科目替换
			subjectCode = Constants.T1_REPLACE_SUBJECT_CODE;
			subjectName = subjectProperties.getProperty(Constants.T1_REPLACE_SUBJECT_CODE, "暂收款");
		} else {
			MyMap myData = getPropertiesKeyByContainsValue(subjectProperties, sourceData.getSettleSubject(), false);
			if (myData.getKey() != null) {
				subjectCode = myData.getKey();
				subjectName = myData.getValue();
			} else {
				targetData.setWarn(true);
			}
		}

		targetData.setSubjectCode(subjectCode);
		targetData.setSubjectName(subjectName);

		targetData.setOutAmount(new Money(sourceData.getOutNumber()));
		targetData.setInAmount(new Money());
		targetData.setTotalAmount(targetData.getOutAmount());

		targetData.setDocumentMemo(
				"付" + sourceData.getProjectName() + subjectName + "," + sourceData.getCapitalProperties());

		targetData.setApproveProject(
				getApproveProject(targetData.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));
		return new TargetData[]{};
	}

	private static TargetData build1002P05Target(TargetData targetData, SourceData sourceData,
			Map<String, Object> mve2Map) {
		TargetData _targetData = (TargetData) targetData.clone();
		_targetData.setSubjectCode(Constants.LFT_SUBJECT_CODE);
		_targetData.setSubjectName(subjectProperties.getProperty(Constants.LFT_SUBJECT_CODE, "联付通数据"));
		_targetData.setWarn(false);
		_targetData.setApproveProject(
				getApproveProject(_targetData.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));

		_targetData.setInAmount(targetData.getOutAmount());
		_targetData.setOutAmount(targetData.getInAmount());
		return _targetData;
	}

	/**
	 * 生成核算项目
	 * 
	 * @param template
	 * @param datas
	 * @return
	 */
	private static String getApproveProject(String subjectCode, Map<String, Object> mve2Map, String institutionName) {
		MyMap myData = getPropertiesKeyByContainsValue(institutionProperties, institutionName, true);
		mve2Map.put("institutionCode", myData.getKey());
		mve2Map.put("institutionName", myData.getValue() == null ? institutionName : myData.getValue());
		if (StringUtil.isEmpty(subjectCode)) {
			return null;
		}
		String template = (String) subjectAppProjectProperties.getProperty(subjectCode);
		if (StringUtil.isEmpty(template)) {
			return null;
		}
		CompiledTemplate compiledTemplate = TemplateCompiler.compileTemplate(template, new ParserContext());
		return (String) TemplateRuntime.execute(compiledTemplate, mve2Map);
	}

	private static MyMap getPropertiesKeyByContainsValue(Properties properties, String value, boolean ignoreBlank) {
		for (Object key : properties.keySet()) {
			String _value = (String) properties.get(key);
			if (StringUtil.isEmpty(_value)) {
				continue;
			}
			if (ignoreBlank) {
				String[] strings = _value.split("\\|");
				if (strings.length == 1) {
					if (_value.contains(value)) {
						return new MyMap((String) key, _value);
					}
				} else if (strings.length >= 2) {
					if (value.contains(strings[1])) {
						return new MyMap((String) key, strings[0]);
					}
				}
			} else {
				String[] ss = _value.split("\\s+");
				for (String s : ss) {
					String[] _ss = s.split("-");
					if (_ss.length == 1) {
						if (value.equals(_ss[0])) {
							return new MyMap((String) key, _ss[0]);
						}
					} else if (_ss.length == 2) {
						if (value.contains(_ss[0])) {
							String[] sss = _ss[1].split("\\|");
							boolean f = true;
							for (String _sss : sss) {
								if (value.equals(_sss))
									f = false;
							}
							if (f) {
								return new MyMap((String) key, _ss[0]);
							}
						}
					}
				}

			}

		}
		return new MyMap(null, null);
	}

}
