package com.zhangxun.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static final String YYYYMMDD = "yyyyMMdd";

	public static final String YYYYMMDD2 = "yyyy/MM/dd";

	public static final String YYYYMMDD3 = "yyyy-MM-dd";

	public static DateFormat format = new SimpleDateFormat(YYYYMMDD3);

	public static DateFormat getFormat(String pattern) {
		return new SimpleDateFormat(pattern);
	}

	public static Date getDateByStr(String value) {
		if (value == null || "".equals(value)) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		String[] v = value.split("[^\\d]");
		int i = 0;
		i = getValueIndex(v, i);
		int year = Integer.parseInt(v[i]);

		i = getValueIndex(v, i + 1);
		int month = Integer.parseInt(v[i]) - 1;

		i = getValueIndex(v, i + 1);
		int day = Integer.parseInt(v[i]);
		System.out.println(year + " " + month + " " + day);
		calendar.set(year, month, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private static int getValueIndex(String[] v, int i) {
		for (; i < v.length; i++) {
			if (StringUtil.isNotEmpty(v[i])) {
				return i;
			}
		}

		throw new RuntimeException("日期格式错误:" + v);
	}

}
