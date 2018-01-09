package com.zhangxun.util;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() < 1;
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	public static double parseDouble(String s) {
		if (isEmpty(s)) {
			return 0.0;
		}
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return 0.0;
		}
	}
}
