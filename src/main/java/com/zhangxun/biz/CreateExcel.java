package com.zhangxun.biz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;

import com.zhangxun.model.ExcelSort;
import com.zhangxun.model.TargetData;
import com.zhangxun.util.DateUtil;
import com.zhangxun.util.Money;

public class CreateExcel {

	public static boolean saveData(String fileName, String[] titles, Object[][] datas) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Page1");
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell;
		for (int i = 0; i < titles.length; i++) {
			cell = row.createCell((short) i);
			cell.setCellValue(titles[i]);
		}

		CellStyle cellStyle = wb.createCellStyle();
		CreationHelper creationHelper = wb.getCreationHelper();
		cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(DateUtil.YYYYMMDD2));

		Class<TargetData> clazz = TargetData.class;

		for (int i = 1; i <= datas.length; i++) {
			row = sheet.createRow(i);
			Field[] fields = clazz.getDeclaredFields();
			for (Field f : fields) {
				// 获取字段中包含ExcelSort的注解
				ExcelSort meta = f.getAnnotation(ExcelSort.class);
				if (meta != null) {
					try {
						Object data = datas[i - 1][meta.sort()];
						if (data == null) {
							continue;
						}
						Class type = f.getType();
						cell = row.createCell((short) meta.sort());
						if (type == Date.class) {
							data = DateUtil.getFormat(DateUtil.YYYYMMDD2).parse((String) data);
						} else if (type == Integer.class) {
							if (data.getClass() != Integer.class) {
								data = (Integer) data;
							}
						}

						setValue(cell, data, cellStyle);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// 加载t_schema
		String currentClassPath = System.getProperty("user.dir");
		HSSFWorkbook shcemawb;
		try {
			shcemawb = new HSSFWorkbook(new FileInputStream(new File(currentClassPath + "/config/t_schema.xls")));
			HSSFSheet shcemaSheet = shcemawb.getSheetAt(0);
			int rowNum = shcemaSheet.getLastRowNum();
			sheet = wb.createSheet("t_Schema");

			Row fromRow;
			for (int i = 0; i <= rowNum; i++) {
				fromRow = shcemaSheet.getRow(i);
				row = sheet.createRow(i);
				int columnCount = fromRow.getLastCellNum();
				for (int j = 0; j < columnCount; j++) {
					setValue(row.createCell(j), getRowData(fromRow, j), null);
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			wb.write(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] content = os.toByteArray();
		File file = new File(fileName);// Excel文件生成后存储的位置。
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream fos = null;
			fos = new FileOutputStream(file);

			fos.write(content);
			os.close();
			fos.close();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static Object getRowData(Row row, int i) {
		if (row.getCell(i) == null) {
			return null;
		} else if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
			return row.getCell(i).getNumericCellValue();
		} else if (row.getCell(i).getCellTypeEnum() == CellType.STRING) {
			return row.getCell(i).getStringCellValue();
		}
		return null;
	}

	private static void setValue(Cell cell, Object o, CellStyle cellStyle) {
		if (o == null) {
			return;
		}
		if (o instanceof Double) {
			cell.setCellValue((Double) o);
		}
		if (o instanceof String) {
			cell.setCellValue((String) o);
		}
		if (o instanceof Integer) {
			cell.setCellValue((Integer) o);
		}
		if (o instanceof Date) {
			cell.setCellValue((Date) o);
			cell.setCellStyle(cellStyle);
		}
		if (o instanceof Money) {
			cell.setCellValue(((Money) o).getAmount().doubleValue());
		}
	}

}
