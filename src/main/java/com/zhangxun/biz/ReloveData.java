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

	private static Properties subjectProperties = PropertiesUtil.getProperties(Constants.SUBJECT_FILE_NAME);
	private static Properties subjectAppProjectProperties = PropertiesUtil
			.getProperties(Constants.SUBJECT_APPROVEPROJECT);
	private static Properties institutionProperties = PropertiesUtil.getProperties(Constants.INSTITUTION_FILE_NAME);

	public static List<TargetData> relove(File file) throws IOException, MyException {
		List<SourceData> sourceDatas = ReadData.read(file,
				Integer.parseInt(PropertiesUtil.configProperties.getProperty(Constants.START_ROW, "3")));
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

			// 用数组接收处理结果，用于排序收-付-转-扣 借-贷
			TargetData[] targetDatas = new TargetData[8];

			TargetData inTargetData = null;
			if (sourceData.getInNumber() != 0) {// 收款后优先成1002.05
				inTargetData = buildInTarget(targetData, sourceData, mve2Map);
				if (!Constants.LFT_SUBJECT_CODE.equals(targetData.getSubjectCode())) {
					TargetData _inTargetData = build1002P05Target(inTargetData, sourceData, mve2Map);
					targetDatas[0] = _inTargetData;
				}
				targetDatas[1] = inTargetData;
			}
			if (sourceData.getOutNumber() != 0) {// 付款后生成1002.05
				TargetData outTargetData = buildOutTarget(targetData, sourceData, mve2Map);
				targetDatas[2] = outTargetData;
				if (!Constants.LFT_SUBJECT_CODE.equals(targetData.getSubjectCode())) {
					TargetData _outTargetData = build1002P05Target(outTargetData, sourceData, mve2Map);
					targetDatas[3] = _outTargetData;
				}
			}
			if (sourceData.getTransferNumber() != 0) {
				buildTransferTarget(targetData, sourceData, mve2Map, targetDatas);
			}
			if (sourceData.getTakeNumber() != 0 && sourceData.getOutNumber() == 0) {
				TargetData[] takeTargetDatas = buildTakeTarget(targetData, sourceData, mve2Map);
				targetDatas[6] = takeTargetDatas[0];
				targetDatas[7] = takeTargetDatas[1];

			}
			for (TargetData _targetData : targetDatas) {
				if (_targetData == null) {
					continue;
				}
				_targetData.setMemuSortNo(sortNo++);
				datas.add(_targetData);
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
		targetData.setDocumentString(
				PropertiesUtil.configProperties.getProperty(Constants.TargetData.DOCUMENT_STRING, "记"));
		targetData.setDocumentCode(1);

		targetData.setCurrencyCode("RMB");
		targetData.setCurrencyName("人民币");

		targetData.setOperator(PropertiesUtil.configProperties.getProperty(Constants.TargetData.OPERATOR, "刘毅"));
		targetData.setAuditor("NONE");
		targetData.setApprover("NONE");
		targetData.setCashier("NONE");
		targetData.setSettleWay("*");

		targetData.setNumber("0");
		targetData.setUnit("*");
		targetData.setPrice("0");
		targetData.setBusinessDate(sourceData.getInDate());
		targetData.setAppendCount(0);
		targetData.setSortNo(
				Integer.parseInt(PropertiesUtil.configProperties.getProperty(Constants.TargetData.SORT_NO, "1001")));
		targetData.setExchangeType(
				PropertiesUtil.configProperties.getProperty(Constants.TargetData.EXCHANGE_TYPE, "公司汇率"));
		targetData.setExchange("1");
		targetData.setPosting(0);
		return targetData;
	}

	/**
	 * 贷方(入金)
	 * 
	 * @return
	 */
	private static TargetData buildInTarget(TargetData _targetData, SourceData sourceData,
			Map<String, Object> mve2Map) {
		TargetData targetData = (TargetData) _targetData.clone();
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
		if (sourceData.getInNumber() < 0) {
			targetData.setInAmount(new Money());
			targetData.setTotalAmount(new Money());
			targetData.setWarn(true);
		} else {
			targetData.setInAmount(new Money(sourceData.getInNumber()));
			targetData.setTotalAmount(new Money(sourceData.getInNumber()));
		}
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
	private static TargetData buildOutTarget(TargetData _targetData, SourceData sourceData,
			Map<String, Object> mve2Map) {
		TargetData targetData = (TargetData) _targetData.clone();

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

		targetData.setInAmount(new Money());
		if (sourceData.getOutNumber() > 0) {
			targetData.setOutAmount(new Money(sourceData.getOutNumber()));
			targetData.setTotalAmount(new Money(sourceData.getOutNumber()));
		} else {
			targetData.setOutAmount(new Money());
			targetData.setTotalAmount(new Money());
			targetData.setWarn(true);
		}
		targetData.setDocumentMemo(
				"付" + sourceData.getProjectName() + subjectName + "," + sourceData.getCapitalProperties());

		targetData.setApproveProject(
				getApproveProject(targetData.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));
		return targetData;
	}

	/**
	 * 转
	 * 
	 * @return
	 */
	private static void buildTransferTarget(TargetData _targetData, SourceData sourceData, Map<String, Object> mve2Map,
			TargetData[] targetDatas) {
		TargetData targetData = (TargetData) _targetData.clone();

		// 借方
		String subjectCode = Constants.TRANSFER_OUT_SUBJECT_CODE;
		String subjectName = StringUtil.defaultValue(getPropertiesValueBykey(subjectProperties, subjectCode, false),
				sourceData.getSettleSubject());
		targetData.setSubjectCode(subjectCode);
		targetData.setSubjectName(subjectName);

		if (sourceData.getTransferNumber() < 0) {
			targetData.setOutAmount(new Money(0));
			targetData.setWarn(true);
		} else {
			targetData.setOutAmount(new Money(sourceData.getTransferNumber()));
		}
		targetData.setInAmount(new Money());
		targetData.setTotalAmount(targetData.getOutAmount());

		targetData.setDocumentMemo("收" + sourceData.getProjectName() + "保证金转价款," + sourceData.getCustomerName());

		targetData.setApproveProject(
				getApproveProject(targetData.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));
		targetDatas[4] = targetData;

		// 贷方
		// 转-收和转同时有数据,且收科目为价款,合并记录
		TargetData inTargetData = targetDatas[1];

		if (sourceData.getInNumber() > 0 && inTargetData != null
				&& inTargetData.getSubjectCode().equals(Constants.TRANSFER_IN_SUBJECT_CODE)) {
			inTargetData.setInAmount(inTargetData.getInAmount().add(new Money(sourceData.getTransferNumber())));
			inTargetData.setTotalAmount(inTargetData.getTotalAmount().add(new Money(sourceData.getTransferNumber())));
			targetDatas[5] = inTargetData;
			targetDatas[1] = null;
		} else {
			TargetData targetData2 = (TargetData) targetData.clone();
			subjectCode = Constants.TRANSFER_IN_SUBJECT_CODE;
			subjectName = StringUtil.defaultValue(getPropertiesValueBykey(subjectProperties, subjectCode, false),
					sourceData.getSettleSubject());
			targetData2.setSubjectCode(subjectCode);
			targetData2.setSubjectName(subjectName);
			targetData2.setOutAmount(new Money());
			targetData2.setInAmount(new Money(targetData.getOutAmount().getCent() / 100,
					(int) (targetData.getOutAmount().getCent() % 100)));
			targetData2.setApproveProject(
					getApproveProject(targetData2.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));
			targetDatas[5] = targetData2;
		}
	}

	/**
	 * 扣
	 * 
	 * @return
	 */
	private static TargetData[] buildTakeTarget(TargetData _targetData, SourceData sourceData,
			Map<String, Object> mve2Map) {
		TargetData[] targetDatas = new TargetData[2];
		TargetData targetData = (TargetData) _targetData.clone();
		// 借方
		String subjectCode = Constants.TAKE_OUT_SUBJECT_CODE;
		String subjectName = StringUtil.defaultValue(getPropertiesValueBykey(subjectProperties, subjectCode, false),
				sourceData.getSettleSubject());
		targetData.setSubjectCode(subjectCode);
		targetData.setSubjectName(subjectName);

		if (sourceData.getTakeNumber() < 0) {
			targetData.setOutAmount(new Money(0));
			targetData.setWarn(true);
		} else {
			targetData.setOutAmount(new Money(sourceData.getTakeNumber()));
		}
		targetData.setInAmount(new Money());
		targetData.setTotalAmount(targetData.getOutAmount());

		targetData.setDocumentMemo("收" + sourceData.getProjectName() + "价款扣服务费," + sourceData.getCapitalProperties());

		targetData.setApproveProject(
				getApproveProject(targetData.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));
		targetDatas[0] = targetData;

		// 贷方
		TargetData targetData2 = (TargetData) targetData.clone();
		subjectCode = Constants.TAKE_IN_SUBJECT_CODE;
		subjectName = StringUtil.defaultValue(getPropertiesValueBykey(subjectProperties, subjectCode, false),
				sourceData.getSettleSubject());
		targetData2.setSubjectCode(subjectCode);
		targetData2.setSubjectName(subjectName);
		targetData2.setApproveProject(
				getApproveProject(targetData2.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));
		targetData2.setOutAmount(new Money());
		targetData2.setInAmount(new Money(targetData.getOutAmount().getCent() / 100,
				(int) (targetData.getOutAmount().getCent() % 100)));
		targetDatas[1] = targetData2;

		return targetDatas;
	}

	private static TargetData build1002P05Target(TargetData targetData, SourceData sourceData,
			Map<String, Object> mve2Map) {
		TargetData _targetData = (TargetData) targetData.clone();

		Calendar inDate = Calendar.getInstance();
		Calendar settleDate = Calendar.getInstance();
		inDate.setTime(sourceData.getInDate());
		settleDate.setTime(sourceData.getSettleDate());

		String t1mark = PropertiesUtil.configProperties.getProperty(Constants.T1_MARK, Constants.T1Mark.MONTH);
		if (Constants.T1Mark.MONTH.equals(t1mark)) {
			settleDate.set(Calendar.DATE, 1);
			inDate.set(Calendar.DATE, 1);
		}
		// 执行跨日期科目替换,
		if (settleDate.getTimeInMillis() != inDate.getTimeInMillis() && targetData.getInAmount().getCent() > 0) {
			_targetData.setSubjectCode(Constants.T1_REPLACE_SUBJECT_CODE);
			_targetData.setSubjectName(StringUtil.defaultValue(
					getPropertiesValueBykey(subjectProperties, Constants.T1_REPLACE_SUBJECT_CODE, false), "暂收款"));
		} else {
			_targetData.setSubjectCode(Constants.LFT_SUBJECT_CODE);
			_targetData.setSubjectName(StringUtil.defaultValue(
					getPropertiesValueBykey(subjectProperties, Constants.LFT_SUBJECT_CODE, false), "联付通数据"));
		}
		_targetData.setWarn(targetData.isWarn());
		_targetData.setApproveProject(
				getApproveProject(_targetData.getSubjectCode(), mve2Map, sourceData.getInstitutionName()));

		_targetData.setInAmount(targetData.getOutAmount());
		_targetData.setOutAmount(targetData.getInAmount());
		_targetData.setTotalAmount(targetData.getOutAmount().add(targetData.getInAmount()));
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

	private static String getPropertiesValueBykey(Properties properties, String key, boolean ignoreBlank) {
		String _value = (String) properties.get(key);
		if (StringUtil.isEmpty(_value)) {
			return null;
		}
		if (ignoreBlank) {
			String[] strings = _value.split("\\|");
			if (strings.length == 1) {
				return _value;
			} else if (strings.length >= 2) {
				return strings[0];
			}
		} else {
			String[] ss = _value.split("\\s+");
			for (String s : ss) {
				String[] _ss = s.split("-");
				if (_ss.length == 1) {
					return _ss[0];
				} else if (_ss.length == 2) {
					return _ss[0];
				}
			}

		}
		return _value;
	}

}
