package selenium.fonantrix.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to read data from Excel file.
 *          </p>
 */
public class ReadExcelFile {
	private static final Logger logger = LoggerFactory
			.getLogger(ReadExcelFile.class.getName());

	/**
	 * Method to retrieve test data from excel file.
	 * 
	 * @param fileName
	 *            File name.
	 * @return Excel data.
	 * @throws IOException
	 */
	public static Object[][] getTestCaseData(String fileName)
			throws IOException {
		Object[][] data = null;

		try {
			FileInputStream filepath = new FileInputStream(new File(
					System.getProperty("user.dir")
							+ "\\src\\selenium\\qa\\app\\testcases\\"
							+ fileName + ".xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(filepath);
			Sheet sheet = workbook.getSheet("Sheet1");
			int numberOfRows = sheet.getPhysicalNumberOfRows();
			data = new Object[numberOfRows - 1][1];
			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			int rowNum = 1;
			String[] header = new String[sheet.getRow(0).getLastCellNum() + 1];
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				int colNum = 1;
				if (rowNum == 1) {
					rowNum = rowNum + 1;
					for (org.apache.poi.ss.usermodel.Cell cell : row) {
						header[colNum] = cell.getStringCellValue();
						colNum = colNum + 1;
					}
					continue;
				}
				Map<String, RichTextString> inputMap = new HashMap<String, RichTextString>();
				for (org.apache.poi.ss.usermodel.Cell cell : row) {
					if (!cell.getRichStringCellValue().toString().isEmpty())
						inputMap.put(header[colNum],
								cell.getRichStringCellValue());
					colNum = colNum + 1;

				}
				if (inputMap.size() > 0)
					data[rowNum - 2][0] = inputMap;
				rowNum = rowNum + 1;
			}
		}

		catch (Exception e) {
			logger.error("Exception while getting testcase data and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}
		return data;
	}
}
