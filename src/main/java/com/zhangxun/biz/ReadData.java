package com.zhangxun.biz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.zhangxun.exception.MyException;
import com.zhangxun.model.SourceData;
import com.zhangxun.util.DateUtil;
import com.zhangxun.util.StringUtil;

/**
 * 获取源数据
 * 
 * @author zhangXun
 */
public class ReadData {
	public static List<SourceData> read(File file, int startRow) throws IOException, MyException {
		Workbook wb = null;
		String fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());

		if (fileType.equals("xls")) {
			wb = new HSSFWorkbook(new FileInputStream(file));
		} else if (fileType.equals("xlsx")) {
			wb = new XSSFWorkbook(new FileInputStream(file));
		} else {
			throw new MyException("您的文档格式不正确");
		}
		Sheet sheet = wb.getSheetAt(0);
		int rowNum = sheet.getLastRowNum();
		if (rowNum < startRow + 1) {
			wb.close();
			throw new MyException("源EXCEL无可生成数据");
		}
		List<SourceData> datas = new ArrayList<SourceData>();
		Row row;
		for (int i = startRow; i <= rowNum; i++) {
			row = sheet.getRow(i);
			if (row == null || row.getCell(0) == null) {
				continue;
			}
			SourceData sourceData = new SourceData();
			sourceData.setSortNo(getRowData(row, 0));
			sourceData.setInstitutionName(getRowData(row, 1));
			sourceData.setProjectName(getRowData(row, 2));
			sourceData.setCustomerName(getRowData(row, 3));
			sourceData.setCapitalProperties(getRowData(row, 4));
			sourceData.setSettleSubject(getRowData(row, 5));
			sourceData.setInNumber(StringUtil.parseDouble(getRowData(row, 6)));
			sourceData.setOutNumber(StringUtil.parseDouble(getRowData(row, 7)));
			sourceData.setTransferNumber(StringUtil.parseDouble(getRowData(row, 8)));
			sourceData.setTakeNumber(StringUtil.parseDouble(getRowData(row, 9)));
			sourceData.setInDate(getRowDateData(row, 10));
			Date settleDate = getRowDateData(row, 11);
			sourceData.setSettleDate(settleDate == null ? sourceData.getInDate() : settleDate);
			if (sourceData.getInNumber() != 0.0 || sourceData.getOutNumber() != 0.0
					|| sourceData.getTransferNumber() != 0.0 || sourceData.getTakeNumber() != 0.0) {
				datas.add(sourceData);
			}
		}
		wb.close();
		return datas;
	}

	private static String getRowData(Row row, int i) {
		if (row.getCell(i) == null) {
			return null;
		}
		if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(row.getCell(i))) {
				SimpleDateFormat sdf = null;
				if (row.getCell(i).getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("yyyy/MM/dd")) {
					sdf = new SimpleDateFormat("yyyy/MM/dd");
				} else {// 日期
					sdf = new SimpleDateFormat("yyyy/MM/dd");
				}
				return sdf.format(row.getCell(i).getDateCellValue());
			}

			return String.valueOf(row.getCell(i).getNumericCellValue());
		} else if (row.getCell(i).getCellTypeEnum() == CellType.STRING) {
			return String.valueOf(row.getCell(i).getStringCellValue());
		}
		return null;
	}

	private static Date getRowDateData(Row row, int i) {
		if (row.getCell(i) == null) {
			return null;
		}
		if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(row.getCell(i))) {
				return row.getCell(i).getDateCellValue();
			}
		} else if (row.getCell(i).getCellTypeEnum() == CellType.STRING) {
			try {
				return DateUtil.format.parse(row.getCell(i).getStringCellValue());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
