package com.zhangxun.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 获取配置文件信息
 * 
 * @author zhangXun
 */
public class PropertiesUtil {
	public static Properties configProperties = PropertiesUtil.getProperties(Constants.CONFIG_FILE_NAME);

	public static Properties getProperties(String fileName) {
		Properties properties = new Properties();
		try {
			String currentClassPath = System.getProperty("user.dir");
			File file = new File(currentClassPath + "/config");
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(currentClassPath + "/config/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			properties.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}
}
