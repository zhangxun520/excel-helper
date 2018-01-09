package com.zhangxun.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * EXCEL列序号
 * 
 * @author zhangxun
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSort {

	int sort();

}
