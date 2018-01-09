package com.zhangxun.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.zhangxun.biz.ReloveData;

/**
 * 获取配置文件信息
 * 
 * @author zhangXun
 */
public class PropertiesUtil {
	public static Properties getProperties(String fileName) {
		Properties properties = new Properties();
		InputStream in = null;
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
			in = ReloveData.class.getClassLoader().getResourceAsStream(fileName);
			properties.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
}
