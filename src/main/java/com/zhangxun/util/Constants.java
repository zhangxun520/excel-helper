package com.zhangxun.util;

public class Constants {

	public static final String CONFIG_FILE_NAME = "excel-helper.config";

	/**
	 * 科目配置
	 */
	public static final String SUBJECT_FILE_NAME = "subject.config";

	/**
	 * 科目对应"核算项目"配置
	 */
	public static final String SUBJECT_APPROVEPROJECT = "subject_approveproject.config";

	/**
	 * 机构配置
	 */
	public static final String INSTITUTION_FILE_NAME = "institution.config";

	public static final String T1_MARK = "t1mark";

	/**
	 * 开始行
	 */
	public static final String START_ROW = "startRow";

	/**
	 * 列配置
	 */
	public static final String COLUMN_SETTING = "columnSetting";

	/**
	 * 额外默认生成数据列的科目代码
	 */
	public static final String LFT_SUBJECT_CODE = "1002.05";

	/**
	 * 跨日期替换为固定科目
	 */
	public static final String T1_REPLACE_SUBJECT_CODE = "2202.13";

	/**
	 * 转-借方默认科目代码
	 */
	public static final String TRANSFER_OUT_SUBJECT_CODE = "2202.00";

	/**
	 * 转-贷方默认科目代码
	 */
	public static final String TRANSFER_IN_SUBJECT_CODE = "2202.02";

	/**
	 * 扣-默认借方科目代码
	 */
	public static final String TAKE_OUT_SUBJECT_CODE = "2202.02";

	/**
	 * 扣-默认贷方科目代码
	 */
	public static final String TAKE_IN_SUBJECT_CODE = "2202.03";

	public class T1Mark {
		public static final String MONTH = "M";
		public static final String DAY = "D";
	}

	public class TargetData {
		/**
		 * 操作员
		 */
		public static final String OPERATOR = "opName";

		public static final String SORT_NO = "sortNo";

		public static final String DOCUMENT_STRING = "documentString";

		public static final String EXCHANGE_TYPE = "exchangeType";
	}

}
