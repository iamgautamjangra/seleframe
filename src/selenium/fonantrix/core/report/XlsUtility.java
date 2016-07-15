package selenium.fonantrix.core.report;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selenium.fonantrix.core.listener.CustomReporter;
import selenium.fonantrix.core.util.ConfigurationMap;
import selenium.fonantrix.core.util.RequestUtil;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to generating excel report after test
 *          execution.
 *          </p>
 */
public class XlsUtility {
	private static XSSFWorkbook workbook;
	private static XSSFCellStyle title_Style, wrap_Cell;
	private static XSSFFont font;
	private static XSSFCellStyle cell_Style;
	private static XSSFFont cell_font;
	public static int autoplusManual = 0;

	public static int moduleCount = 0;
	public static int flowCount = 0;
	public static int testCaseCount = 0;
	public static int validationPointSum = 0;
	public static int passVPSum = 0;
	public static int failVPSum = 0;
	public static long totalExeTime = 0;

	public static long batchPassPercent = 0;
	public static long batchFailPercent = 0;

	public static int totalTCCount = 0;
	public static int automatedTCCount = 0;
	public static int totalPassCount = 0;
	public static long totalTime = 0;

	private static final Logger logger = LoggerFactory
			.getLogger(XlsUtility.class.getName());

	/**
	 * Method to generate excel report.
	 * 
	 * @param detailsTestCases
	 *            Test case Details Map for printing in Excel Sheet.
	 * @param resultOutputDirectory
	 *            Location to generate Excel Report.
	 */
	public static void Excelwritting(
			TreeMap<String, Map<String, Object>> detailsTestCases,
			String resultOutputDirectory) throws Exception {

		logger.info("writeReportToXls: Writing the testcases result to xls file");
		String xlsFileName = ConfigurationMap.getProperty("xlsReportFileName");
		// Blank workbook
		workbook = new XSSFWorkbook();

		moduleCount = 1;
		flowCount = 1;
		testCaseCount = 1;
		validationPointSum = 0;
		passVPSum = 0;
		failVPSum = 0;
		totalExeTime = 0;

		Map<String, Map<String, Object>> GrandTotalModule = new LinkedHashMap<String, Map<String, Object>>();
		// Create a blank sheet
		XSSFSheet summarySheet = workbook.createSheet("Summary");
		summarySheet.setColumnWidth(0, 8000);
		summarySheet.setColumnWidth(1, 6000);
		Map<String, Object[]> data = new TreeMap<String, Object[]>();

		data.put("1", new Object[] { "Module Name", "Total Test cases",
				"Automated Test cases", "Total Passed", "Total Failed",
				"Pass %", "Fail %", "Execution time(ms)" });
		Row titleRow = summarySheet.createRow(0);
		Cell titleCell = titleRow.createCell(1);
		title_Style = workbook.createCellStyle();
		// Create HSSFFont object from the workbook
		font = workbook.createFont();
		// set the weight of the font
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		font.setFontHeight(14);
		// attach the font to the style created earlier
		title_Style.setFont(font);
		titleCell.setCellStyle(title_Style);
		titleCell.setCellValue(String.format("%s Automation Report Summary",
				ConfigurationMap.getProperty("projectName")));
		printHeading(summarySheet, data, "summarySheet");
		Set<String> testCasesName = detailsTestCases.keySet();
		List<String> moduleNameList = new LinkedList<String>();

		for (Object tcName : testCasesName) {
			String[] tcNameArray = tcName.toString().split("\\$");

			if (!moduleNameList.contains(tcNameArray[0]))
				moduleNameList.add(tcNameArray[0]);

		}

		for (int i = 0; i < moduleNameList.size(); i++) {

			XSSFSheet detailSheet = workbook.createSheet(moduleNameList.get(i));

			detailSheet.createFreezePane(0, 6);

			detailSheet.setColumnWidth(1, 3500);
			detailSheet.setColumnWidth(2, 3500);
			detailSheet.setColumnWidth(3, 9000);

			detailSheet.setColumnWidth(4, 3500);
			detailSheet.setColumnWidth(5, 2200);
			detailSheet.setColumnWidth(6, 2200);
			detailSheet.setColumnWidth(7, 2200);
			detailSheet.setColumnWidth(8, 3000);
			detailSheet.setColumnWidth(9, 3000);
			detailSheet.setColumnWidth(10, 3000);
			detailSheet.setColumnWidth(11, 2500);
			detailSheet.setColumnWidth(12, 12000);

			Row detailTitleRow = detailSheet.createRow(0);
			Cell detailTitleCell = detailTitleRow.createCell(1);

			title_Style = workbook.createCellStyle();
			// Create HSSFFont object from the workbook
			font = workbook.createFont();
			// set the weight of the font
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			font.setFontHeight(14);
			// attach the font to the style created earlier
			title_Style.setFont(font);
			detailTitleCell.setCellStyle(title_Style);

			detailTitleCell.setCellValue(String.format(
					"%s Automation Detail Report",
					ConfigurationMap.getProperty("projectName")));
			data.put("1",
					new Object[] { "Module Name", "Flow Name",
							"Test Case Name", "Test Case ID",
							"Number Of Valdation Points",
							"Passed Valdation Points",
							"Failed Valdation Points", "Pass %", "Fail %",
							"Execution Time(ms)", "Test Case Status",
							"Failure Details" });
			printHeading(detailSheet, data, moduleNameList.get(i));

		}
		int rownum = 6;
		Set<String> tcNames = detailsTestCases.keySet();
		Iterator<String> tcNameIterate = tcNames.iterator();
		String firstkey = tcNameIterate.next().toString();
		Map<String, Object> tempMap = detailsTestCases.get(firstkey);
		String testCaseStatus = tempMap.get("TCStatus").toString();

		String[] testCaseNameArray = firstkey.toString().split("\\$");
		String modulename = testCaseNameArray[0];
		String classname = testCaseNameArray[1];
		XSSFSheet detailSheet1 = workbook.getSheet(modulename);
		Row row = detailSheet1.createRow(rownum++);
		Cell cell = row.createCell(1);
		setCellColor(testCaseStatus, cell);
		cell.setCellValue(modulename);
		cell = row.createCell(2);
		setCellColor(testCaseStatus, cell);
		cell.setCellValue(classname);

		int i = 0;

		for (Object tcName : tcNames) {
			i++;
			testCaseNameArray = tcName.toString().split("\\$");
			XSSFSheet detailSheet = workbook.getSheet(testCaseNameArray[0]);
			tempMap = detailsTestCases.get(tcName);

			testCaseStatus = tempMap.get("TCStatus").toString();

			if (i == 1) {
				cell = row.createCell(3);
				cell.setCellStyle(wrap_Cell);
				cell.setCellValue(testCaseNameArray[2]);

				cell = row.createCell(4);
				setCellColor(testCaseStatus, cell);
				cell.setCellValue(tempMap.get("TC_ID").toString());

				cell = row.createCell(5);
				setCellColor(testCaseStatus, cell);
				validationPointSum = validationPointSum
						+ Integer.parseInt(tempMap.get("validationPoints")
								.toString());
				cell.setCellValue(Integer.parseInt(tempMap.get(
						"validationPoints").toString()));

				cell = row.createCell(6);
				setCellColor(testCaseStatus, cell);
				passVPSum = passVPSum
						+ Integer.parseInt(tempMap.get("passedValidations")
								.toString());
				cell.setCellValue(Integer.parseInt(tempMap.get(
						"passedValidations").toString()));

				cell = row.createCell(7);
				setCellColor(testCaseStatus, cell);
				failVPSum = failVPSum
						+ Integer.parseInt(tempMap.get("FailedValidations")
								.toString());
				cell.setCellValue(Integer.parseInt(tempMap.get(
						"FailedValidations").toString()));

				cell = row.createCell(8);
				setCellColor(testCaseStatus, cell);
				cell.setCellValue(tempMap.get("passedPercent").toString());

				cell = row.createCell(9);
				setCellColor(testCaseStatus, cell);
				cell.setCellValue(tempMap.get("FailedPercent").toString());

				cell = row.createCell(10);
				setCellColor(testCaseStatus, cell);
				totalExeTime = totalExeTime
						+ Long.parseLong(tempMap.get("ExecutionTime")
								.toString());
				cell.setCellValue(tempMap.get("ExecutionTime").toString());
				cell.setCellValue(Long.parseLong(tempMap.get("ExecutionTime")
						.toString()));

				cell = row.createCell(11);
				setCellColor(testCaseStatus, cell);
				cell.setCellValue(tempMap.get("TCStatus").toString());

				cell = row.createCell(12);
				setCellColor(testCaseStatus, cell);
				cell.setCellValue(tempMap.get("FailureDtail").toString());

				if (i == tcNames.size()) {
					Map<String, Object> moduleData = new LinkedHashMap<String, Object>();
					moduleData.put("Module Count", moduleCount);
					moduleData.put("Flow Count", flowCount);
					moduleData.put("TC Count", testCaseCount);
					moduleData.put("TC_ID Count", testCaseCount);
					moduleData.put("VP Count", validationPointSum);
					moduleData.put("Pass_VP Count", passVPSum);
					moduleData.put("Fail_VP Count", failVPSum);

					double PassPercent = ((double) passVPSum / (double) validationPointSum) * 100;
					moduleData.put("passedPercent",
							new DecimalFormat("##.##").format(PassPercent)
									+ "%");
					double FailPercent = ((double) failVPSum / (double) validationPointSum) * 100;
					moduleData.put("FailedPercent",
							new DecimalFormat("##.##").format(FailPercent)
									+ "%");
					moduleData.put("Execution Time", totalExeTime);

					GrandTotalModule.put(modulename, moduleData);
				}

				continue;
			}

			rownum = detailSheet.getLastRowNum();
			row = detailSheet.createRow(rownum + 1);

			if (testCaseNameArray[0].equals(modulename)) {
				testCaseCount++;
				cell = row.createCell(1);
				setCellColor(testCaseStatus, cell);
				cell.setCellValue("");

				if (testCaseNameArray[1].equals(classname)) {
					cell = row.createCell(2);
					setCellColor(testCaseStatus, cell);
					cell.setCellValue("");
				} else {
					flowCount++;
					cell = row.createCell(2);
					setCellColor(testCaseStatus, cell);
					cell.setCellValue(testCaseNameArray[1]);
					classname = testCaseNameArray[1];
				}

			} else {
				Map<String, Object> moduleData = new LinkedHashMap<String, Object>();
				moduleData.put("Module Count", moduleCount);
				moduleData.put("Flow Count", flowCount);
				moduleData.put("TC Count", testCaseCount);
				moduleData.put("TC_ID Count", testCaseCount);
				moduleData.put("VP Count", validationPointSum);
				moduleData.put("Pass_VP Count", passVPSum);
				moduleData.put("Fail_VP Count", failVPSum);

				double PassPercent = ((double) passVPSum / (double) validationPointSum) * 100;
				moduleData.put("passedPercent",
						new DecimalFormat("##.##").format(PassPercent) + "%");
				double FailPercent = ((double) failVPSum / (double) validationPointSum) * 100;
				moduleData.put("FailedPercent",
						new DecimalFormat("##.##").format(FailPercent) + "%");
				moduleData.put("Execution Time", totalExeTime);

				GrandTotalModule.put(modulename, moduleData);

				moduleCount = 1;
				flowCount = 1;
				testCaseCount = 1;
				validationPointSum = 0;
				passVPSum = 0;
				failVPSum = 0;
				totalExeTime = 0;

				modulename = testCaseNameArray[0];

				cell = row.createCell(1);
				setCellColor(testCaseStatus, cell);
				cell.setCellValue(testCaseNameArray[0]);

				cell = row.createCell(2);
				setCellColor(testCaseStatus, cell);
				cell.setCellValue(testCaseNameArray[1]);
				classname = testCaseNameArray[1];

			}

			cell = row.createCell(3);
			cell.setCellStyle(wrap_Cell);
			cell.setCellValue(testCaseNameArray[2]);

			cell = row.createCell(4);
			setCellColor(testCaseStatus, cell);
			cell.setCellValue(tempMap.get("TC_ID").toString());

			cell = row.createCell(5);
			setCellColor(testCaseStatus, cell);
			validationPointSum = validationPointSum
					+ Integer.parseInt(tempMap.get("validationPoints")
							.toString());
			cell.setCellValue(Integer.parseInt(tempMap.get("validationPoints")
					.toString()));

			cell = row.createCell(6);
			setCellColor(testCaseStatus, cell);
			passVPSum = passVPSum
					+ Integer.parseInt(tempMap.get("passedValidations")
							.toString());
			cell.setCellValue(Integer.parseInt(tempMap.get("passedValidations")
					.toString()));

			cell = row.createCell(7);
			setCellColor(testCaseStatus, cell);
			failVPSum = failVPSum
					+ Integer.parseInt(tempMap.get("FailedValidations")
							.toString());
			cell.setCellValue(Integer.parseInt(tempMap.get("FailedValidations")
					.toString()));

			cell = row.createCell(8);
			setCellColor(testCaseStatus, cell);
			cell.setCellValue(tempMap.get("passedPercent").toString());

			cell = row.createCell(9);
			setCellColor(testCaseStatus, cell);
			cell.setCellValue(tempMap.get("FailedPercent").toString());

			cell = row.createCell(10);
			setCellColor(testCaseStatus, cell);
			totalExeTime = totalExeTime
					+ Long.parseLong(tempMap.get("ExecutionTime").toString());
			cell.setCellValue(Long.parseLong(tempMap.get("ExecutionTime")
					.toString()));

			cell = row.createCell(11);
			setCellColor(testCaseStatus, cell);
			cell.setCellValue(tempMap.get("TCStatus").toString());

			cell = row.createCell(12);
			setCellColor(testCaseStatus, cell);
			cell.setCellValue(tempMap.get("FailureDtail").toString());

			if (i == tcNames.size()) {
				Map<String, Object> moduleData = new LinkedHashMap<String, Object>();
				moduleData.put("Module Count", moduleCount);
				moduleData.put("Flow Count", flowCount);
				moduleData.put("TC Count", testCaseCount);
				moduleData.put("TC_ID Count", testCaseCount);
				moduleData.put("VP Count", validationPointSum);
				moduleData.put("Pass_VP Count", passVPSum);
				moduleData.put("Fail_VP Count", failVPSum);

				double PassPercent = ((double) passVPSum / (double) validationPointSum) * 100;
				moduleData.put("passedPercent",
						new DecimalFormat("##.##").format(PassPercent) + "%");
				double FailPercent = ((double) failVPSum / (double) validationPointSum) * 100;
				moduleData.put("FailedPercent",
						new DecimalFormat("##.##").format(FailPercent) + "%");
				moduleData.put("Execution Time", totalExeTime);

				GrandTotalModule.put(modulename, moduleData);
			}

		}

		Set<String> allModulesName = GrandTotalModule.keySet();
		for (Object moduleName : allModulesName) {
			XSSFSheet detailSheet2 = workbook.getSheet(moduleName.toString());
			Row row1 = detailSheet2.createRow(detailSheet2.getLastRowNum() + 1);

			Map<String, Object> testCaseData = GrandTotalModule.get(moduleName);

			cell = row1.createCell(0);
			cell.setCellStyle(title_Style);
			cell.setCellValue("Grand Total");

			cell = row1.createCell(1);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("Module Count").toString());

			cell = row1.createCell(2);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("Flow Count").toString());

			cell = row1.createCell(3);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("TC Count").toString());

			cell = row1.createCell(4);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("TC_ID Count").toString());

			cell = row1.createCell(5);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("VP Count").toString());

			cell = row1.createCell(6);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("Pass_VP Count").toString());

			cell = row1.createCell(7);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("Fail_VP Count").toString());

			cell = row1.createCell(8);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("passedPercent").toString());

			cell = row1.createCell(9);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("FailedPercent").toString());

			cell = row1.createCell(10);
			cell.setCellStyle(title_Style);
			cell.setCellValue(testCaseData.get("Execution Time").toString());

		}

		XSSFSheet detailSheet3 = workbook.getSheet("Summary");
		Set<String> modulesName = GrandTotalModule.keySet();
		for (Object moduleName : modulesName) {

			Row row1 = detailSheet3.createRow(detailSheet3.getLastRowNum() + 1);

			Map<String, Object> testCaseData = GrandTotalModule.get(moduleName);

			cell = row1.createCell(1);
			cell.setCellValue(moduleName.toString());

			cell = row1.createCell(2);
			int totalTC = Integer.parseInt(testCaseData.get("TC Count")
					.toString());

			totalTCCount = totalTCCount + totalTC;
			cell.setCellValue(totalTC);

			cell = row1.createCell(3);
			automatedTCCount = automatedTCCount
					+ Integer.parseInt(testCaseData.get("TC Count").toString());
			cell.setCellValue(Integer.parseInt(testCaseData.get("TC Count")
					.toString()));

			cell = row1.createCell(4);
			int passTC = Integer.parseInt(testCaseData.get("TC Count")
					.toString())
					- Integer.parseInt(testCaseData.get("Fail_VP Count")
							.toString());
			totalPassCount = totalPassCount + passTC;
			cell.setCellValue(passTC);

			cell = row1.createCell(5);
			cell.setCellValue(Integer.parseInt(testCaseData
					.get("Fail_VP Count").toString()));

			cell = row1.createCell(6);
			double passPercent = ((double) passTC / Double
					.parseDouble(testCaseData.get("TC Count").toString())) * 100;
			cell.setCellValue(new DecimalFormat("##.##").format(passPercent)
					+ "%");

			cell = row1.createCell(7);
			double failPercent = (Double.parseDouble(testCaseData.get(
					"Fail_VP Count").toString()) / Double
					.parseDouble(testCaseData.get("TC Count").toString())) * 100;
			cell.setCellValue(new DecimalFormat("##.##").format(failPercent)
					+ "%");

			cell = row1.createCell(8);
			totalTime = totalTime
					+ Long.parseLong(testCaseData.get("Execution Time")
							.toString());
			cell.setCellValue(Long.parseLong(testCaseData.get("Execution Time")
					.toString()));

		}

		Row row1 = detailSheet3.createRow(detailSheet3.getLastRowNum() + 1);

		cell = row1.createCell(0);
		cell.setCellStyle(title_Style);
		cell.setCellValue("Grand Total");

		cell = row1.createCell(1);
		cell.setCellStyle(title_Style);
		cell.setCellValue(modulesName.size());

		cell = row1.createCell(2);
		cell.setCellStyle(title_Style);
		cell.setCellValue(totalTCCount);

		cell = row1.createCell(3);
		cell.setCellStyle(title_Style);
		cell.setCellValue(automatedTCCount);

		cell = row1.createCell(4);
		cell.setCellStyle(title_Style);
		cell.setCellValue(totalPassCount);

		cell = row1.createCell(5);
		cell.setCellStyle(title_Style);
		cell.setCellValue(automatedTCCount - totalPassCount);

		cell = row1.createCell(6);
		cell.setCellStyle(title_Style);
		double passPercent = ((double) totalPassCount / (double) automatedTCCount) * 100;
		cell.setCellValue(new DecimalFormat("##.##").format(passPercent) + "%");

		cell = row1.createCell(7);
		cell.setCellStyle(title_Style);
		double failPercent = ((double) (automatedTCCount - totalPassCount) / (double) automatedTCCount) * 100;
		cell.setCellValue(new DecimalFormat("##.##").format(failPercent) + "%");

		Row row2 = detailSheet3.createRow(detailSheet3.getLastRowNum() + 2);
		cell = row2.createCell(0);
		cell.setCellStyle(title_Style);
		cell.setCellValue("Execution Start Time");

		cell = row2.createCell(1);
		cell.setCellValue(CustomReporter.startTime);

		Row row3 = detailSheet3.createRow(detailSheet3.getLastRowNum() + 1);
		cell = row3.createCell(0);
		cell.setCellStyle(title_Style);
		cell.setCellValue("Execution End Time");

		cell = row3.createCell(1);
		cell.setCellValue(CustomReporter.endTime);

		Row row4 = detailSheet3.createRow(detailSheet3.getLastRowNum() + 1);
		cell = row4.createCell(0);
		cell.setCellStyle(title_Style);
		cell.setCellValue("Total Parallel Execution Time");

		cell = row4.createCell(1);
		cell.setCellValue(CustomReporter.parallelExeTime);

		Row row5 = detailSheet3.createRow(detailSheet3.getLastRowNum() + 1);
		cell = row5.createCell(0);
		cell.setCellStyle(title_Style);
		cell.setCellValue("Total Sequential Execution Time");

		cell = row5.createCell(1);

		int exeTimeSec = (int) (totalTime / 1000);
		int timeInHours = (exeTimeSec / 3600);
		int timeInMin = (exeTimeSec / 60);
		long timeInSec = Math.round(exeTimeSec % 60);
		cell.setCellValue(String.format("%02d", timeInHours) + ":"
				+ String.format("%02d", timeInMin) + ":"
				+ String.format("%02d", timeInSec));

		try {
			if (new File(resultOutputDirectory + xlsFileName).exists())
				new File(resultOutputDirectory + xlsFileName).delete();
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File(
					resultOutputDirectory + xlsFileName));
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("Exception while writing the test cases result to xls file and exception text is "
					+ RequestUtil.stackTraceToString(e));

		}
	}

	/**
	 * Method to print failed test case details in red color.
	 * 
	 * @param status
	 *            Test case status.
	 * @param cell
	 *            Cell object name.
	 */
	private static void setCellColor(String status, Cell cell) {
		wrap_Cell = workbook.createCellStyle();
		wrap_Cell.setWrapText(true);

		cell_font = workbook.createFont();
		cell_Style = workbook.createCellStyle();
		if (status.equals("FAIL")) {
			cell_font.setColor(XSSFFont.COLOR_RED);
			wrap_Cell.setFont(cell_font);
		} else {
			cell_font.setColor(new XSSFColor(Color.BLACK));
			wrap_Cell.setFont(cell_font);
		}
		cell_Style.setFont(cell_font);
		cell.setCellStyle(cell_Style);
	}

	/**
	 * Method to print Headings.
	 * 
	 * @param sheet
	 *            Sheet object name.
	 * @param data
	 *            Heading name map.
	 * @param sheetName
	 *            Sheet name.
	 */
	private static void printHeading(XSSFSheet sheet,
			Map<String, Object[]> data, String sheetName) {
		/* Iterate over data and write to sheet */
		Set<String> keyset = data.keySet();
		Row row;
		for (String key : keyset) {
			if (sheetName.equals("summarySheet"))
				row = sheet.createRow(3);
			else
				row = sheet.createRow(5);
			Object[] objArr = data.get(key);
			int cellnum = 1;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String)
					title_Style = workbook.createCellStyle();
				/* Create HSSFFont object from the workbook */
				XSSFFont my_font = workbook.createFont();
				/* set the weight of the font */
				my_font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				my_font.setFontHeight(11);
				/* attach the font to the style created earlier */
				title_Style.setFont(my_font);
				cell.setCellStyle(title_Style);
				title_Style.setWrapText(true);
				cell.setCellValue((String) obj);
			}
		}
	}

}
