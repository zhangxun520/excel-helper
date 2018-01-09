package com.zhangxun.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateUtil {

	public static final String YYYYMMDD = "yyyyMMdd";

	public static final String YYYYMMDD2 = "yyyy/MM/dd";

	public static final String YYYYMMDD3 = "yyyy-MM-dd";

	public static DateFormat format = new SimpleDateFormat(YYYYMMDD3);

	public static DateFormat getFormat(String pattern) {
		return new SimpleDateFormat(pattern);
	}

}
