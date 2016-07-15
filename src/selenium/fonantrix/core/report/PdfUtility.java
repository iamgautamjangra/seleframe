package selenium.fonantrix.core.report;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selenium.fonantrix.core.util.ConfigurationMap;
import selenium.fonantrix.core.util.RequestUtil;
import selenium.fonantrix.core.util.TestSummary;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to generating PDF report after test
 *          execution.
 *          </p>
 */
public class PdfUtility {

	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
			Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.BOLD, BaseColor.BLACK);
	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.BOLD, BaseColor.RED);
	private static final Logger logger = LoggerFactory
			.getLogger(PdfUtility.class.getName());

	/**
	 * Method to generate PDF report.
	 * 
	 * @param testSummary
	 *            Test case summary map for printing in Summary Page.
	 * @param totalTestCasesResult
	 *            Test case details map for printing in Details Page.
	 * @param outputDirectory
	 *            Location to generate PDF Report.
	 */
	public static void writeReportToPDF(List<TestSummary> testSummary,
			Map<String, Map<String, Map<String, String>>> totalTestCasesResult,
			String outputDirectory) {
		try {
			logger.info("writeReportToPDF() : Writing the testcases result to pdf file");
			Document document = new Document();
			String pdfReportFileName = ConfigurationMap
					.getProperty("pdfReportFileName");
			PdfWriter.getInstance(document, new FileOutputStream(
					outputDirectory + pdfReportFileName));
			document.open();
			Paragraph paraSummary = new Paragraph();
			paraSummary.add(new Paragraph(ConfigurationMap
					.getProperty("projectName") + " Automation Report Summary",
					catFont));
			addEmptyLine(paraSummary, 1);
			PdfPTable tableSummary = new PdfPTable(8);

			PdfPCell cell1 = new PdfPCell(new Phrase("Flow Name", smallBold));
			tableSummary.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Total Test cases", smallBold));
			tableSummary.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Automated Test cases", smallBold));
			tableSummary.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Total Passed", smallBold));
			tableSummary.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Total Failed", smallBold));
			tableSummary.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Pass %", smallBold));
			tableSummary.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Fail %", smallBold));
			tableSummary.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Execution time(ms)", smallBold));
			tableSummary.addCell(cell1);
			tableSummary.setHeaderRows(1);

			for (int i = 0; i < testSummary.size(); i++) {
				TestSummary summary = (TestSummary) testSummary.get(i);
				String moduleName = (String) summary.getModuleName();
				String totalTestCases = String.valueOf(summary
						.getTotalTestCases());
				String automatedTestcases = String.valueOf(summary
						.getAutomatedTestCases());
				String Passed = String.valueOf((int) summary.getTotalPassed());
				String Failed = String.valueOf((int) summary.getTotalFailed());
				String passPercent = String.valueOf((float) summary
						.getPassPercent()) + "%";
				String failPercent = String.valueOf((float) summary
						.getFailPercent()) + "%";
				String executionTime = String.valueOf((long) summary
						.getExecutionTime());
				tableSummary.addCell(moduleName);
				tableSummary.addCell(totalTestCases);
				tableSummary.addCell(automatedTestcases);
				tableSummary.addCell(Passed);
				tableSummary.addCell(Failed);
				tableSummary.addCell(passPercent);
				tableSummary.addCell(failPercent);
				tableSummary.addCell(executionTime);
			}
			paraSummary.add(tableSummary);
			document.add(paraSummary);

			document.newPage();

			Paragraph paraDetails = new Paragraph();
			paraDetails.add(new Paragraph(ConfigurationMap
					.getProperty("projectName") + " Automation Detail Report",
					catFont));
			addEmptyLine(paraDetails, 1);
			PdfPTable tableDetails = new PdfPTable(11);

			PdfPCell cell2 = new PdfPCell(new Phrase("Flow Name", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Test Case Name", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Test Case ID", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Number Of Valdation Points",
					smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Passed Valdation Points", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Failed Valdation Points", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Pass %", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Fail %", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Execution Time(ms)", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Test Case Status", smallBold));
			tableDetails.addCell(cell2);

			cell2 = new PdfPCell(new Phrase("Failure Details", smallBold));
			tableDetails.addCell(cell2);
			tableDetails.setHeaderRows(1);

			for (Map.Entry<String, Map<String, Map<String, String>>> moduleDetails : totalTestCasesResult
					.entrySet()) {

				Map<String, Map<String, String>> testcaseDetails = (Map<String, Map<String, String>>) moduleDetails
						.getValue();
				for (Map.Entry<String, Map<String, String>> testMethods : testcaseDetails
						.entrySet()) {

					Map<String, String> oneMethod = (Map<String, String>) testMethods
							.getValue();
					String validationPoints = "0";
					String passedValidations = "0";
					String failedValidations = "0";
					String passPercentage = null;
					String failPercentage = null;
					String executionTime = null;
					String testcaseStatus = null;
					String exception = " ";
					for (Map.Entry<String, String> eachOne : oneMethod
							.entrySet()) {

						if ("validationPoints".equals(eachOne.getKey())) {
							validationPoints = eachOne.getValue();
						} else if ("passedValidations".equals(eachOne.getKey())) {
							passedValidations = eachOne.getValue();
						} else if ("failedVPs".equals(eachOne.getKey())) {
							failedValidations = (String) eachOne.getValue();
						} else if ("passPercent".equals(eachOne.getKey())) {
							passPercentage = eachOne.getValue() + "%";
						} else if ("failPercent".equals(eachOne.getKey())) {
							failPercentage = eachOne.getValue() + "%";
						} else if ("executionTime".equals(eachOne.getKey())) {
							executionTime = eachOne.getValue();
						} else if ("testStatus".equals(eachOne.getKey())) {
							testcaseStatus = eachOne.getValue().toString();
						} else if ("exception".equals(eachOne.getKey())) {
							exception = (String) eachOne.getValue();
						}

					}
					String status = oneMethod.get("testStatus");
					PdfPCell cell3 = printColourBasedonStatus(status,
							oneMethod.get("moduleName"));
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, testMethods.getKey());
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status,
							oneMethod.get("testcaseid"));
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, validationPoints);
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, passedValidations);
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, failedValidations);
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, passPercentage);
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, failPercentage);
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, executionTime);
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, testcaseStatus);
					tableDetails.addCell(cell3);

					cell3 = printColourBasedonStatus(status, exception);
					tableDetails.addCell(cell3);
				}
			}
			paraDetails.add(tableDetails);
			document.add(paraDetails);
			document.close();
		} catch (Exception e) {
			 logger.error("Exception while creating PDF report and exception text is: "+RequestUtil.stackTraceToString(e));
		}
	}

	/**
	 * Method to print an empty line.
	 * 
	 * @param paragraph
	 *            Paragraph object name.
	 * @param noOfEmptyLines
	 *            Number of empty lines.
	 */
	private static void addEmptyLine(Paragraph paragraph, int noOfEmptyLines) {
		for (int i = 0; i < noOfEmptyLines; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	/**
	 * Method to print failed test case details in red color.
	 * 
	 * @param status
	 *            Test case status.
	 * @param data
	 *            Test case details.
	 * @return PDF Cell Object.
	 */
	private static PdfPCell printColourBasedonStatus(String status, String data) {
		PdfPCell cell4;
		if (status.equals("FAIL")) {
			cell4 = new PdfPCell(new Phrase(data, redFont));
		} else {
			cell4 = new PdfPCell(new Phrase(data, smallBold));
		}
		return cell4;
	}
}