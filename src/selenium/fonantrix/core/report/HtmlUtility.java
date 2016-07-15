package selenium.fonantrix.core.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selenium.fonantrix.core.util.ConfigurationMap;
import selenium.fonantrix.core.util.RequestUtil;
import selenium.fonantrix.core.util.TestSummary;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to generating HTML report after test
 *          execution.
 *          </p>
 */
public class HtmlUtility {
	private static final Logger logger = LoggerFactory
			.getLogger(HtmlUtility.class.getName());

	/**
	 * Method to generate HTML report.
	 * 
	 * @param testSummary
	 *            Test case summary map for printing in summary table.
	 * @param totalTestCasesResult
	 *            Test case details Map for printing in details table.
	 * @param outputDirectory
	 *            Location to generate HTML report.
	 */
	public static void writeReportToHTML(List<TestSummary> testSummary,
			Map<String, Map<String, Map<String, String>>> totalTestCasesResult,
			String outputDirectory) {
		try {
			String htmlReportFileName = ConfigurationMap
					.getProperty("htmlReportFileName");
			File htmlFile = new File(outputDirectory + htmlReportFileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile));
			writer.write("<html><body>");
			writer.write("<br><br><h3><center>"
					+ ConfigurationMap.getProperty("projectName")
					+ " Automation Report Summary</centers></h3>");
			writer.write("<table align= center cellpadding=0 cellspacing=0 border=1 bordercolor=BLACK>");
			writer.write("<tr> <th> Flow Name </th> <th> Total Test cases </th> <th> Automated Test cases </th> <th> Total Passed </th> <th> Total Failed </th> <th> Pass % </th> <th> Fail % </th> <th> Execution time(ms) </th> </tr>");
			for (int i = 0; i < testSummary.size(); i++) {
				TestSummary summary = (TestSummary) testSummary.get(i);
				String moduleName = (String) summary.getModuleName();
				int totalTestCases = (int) summary.getTotalTestCases();
				int automatedTestCases = (int) summary.getAutomatedTestCases();
				int passed = (int) summary.getTotalPassed();
				int failed = (int) summary.getTotalFailed();
				float passPercent = (float) summary.getPassPercent();
				float failPercent = (float) summary.getFailPercent();
				long executionTime = (long) summary.getExecutionTime();
				writer.write("<tr> <td> " + moduleName + " </td> <td> "
						+ totalTestCases + " </td> <td> " + automatedTestCases
						+ " </td> <td> " + passed + " </td> <td> " + failed
						+ " </td> <td> " + passPercent + "% </td> <td> "
						+ failPercent + "% </td> <td> " + executionTime
						+ " </td> </tr>");
			}
			writer.write("</table><br>");
			writer.write("<br><br><br><h3><center>"
					+ ConfigurationMap.getProperty("projectName")
					+ " Automation Detail Report </centers></h3>");
			writer.write("<table align= center cellpadding=0 cellspacing=0 border=1 bordercolor=BLACK>");
			writer.write("<tr> <th> Flow Name </th> <th> Test Case Name </th> <th> Test Case ID </th> <th> Number Of Valdation Points </th> <th> Passed Valdation Points </th> <th> Failed Valdation Points </th> <th> Pass % </th> <th> Fail % </th> <th> Execution Time(ms) </th> <th> Test Case Status </th> <th> Failure Details </th> </tr>");
			for (Map.Entry<String, Map<String, Map<String, String>>> moduleDetails : totalTestCasesResult
					.entrySet()) {
				Map<String, Map<String, String>> testcaseDetails = (Map<String, Map<String, String>>) moduleDetails
						.getValue();
				for (Map.Entry<String, Map<String, String>> testMethods : testcaseDetails
						.entrySet()) {
					Map<String, String> oneMethod = (Map<String, String>) testMethods
							.getValue();
					int validationPoints = 0;
					int passedValidations = 0;
					int failedValidations = 0;
					String passPercentage = null;
					String failPercentage = null;
					String executionTime = null;
					String testCaseStatus = null;
					String exception = " ";
					for (Map.Entry<String, String> eachOne : oneMethod
							.entrySet()) {

						if ("validationPoints".equals(eachOne.getKey())) {
							validationPoints = Integer
									.parseInt((String) eachOne.getValue());
						} else if ("passedValidations".equals(eachOne.getKey())) {
							passedValidations = Integer
									.parseInt((String) eachOne.getValue());
						} else if ("failedVPs".equals(eachOne.getKey())) {
							failedValidations = Integer
									.parseInt((String) eachOne.getValue());
						} else if ("passPercent".equals(eachOne.getKey())) {
							passPercentage = eachOne.getValue();
						} else if ("failPercent".equals(eachOne.getKey())) {
							failPercentage = eachOne.getValue();
						} else if ("executionTime".equals(eachOne.getKey())) {
							executionTime = eachOne.getValue();
						} else if ("testStatus".equals(eachOne.getKey())) {
							testCaseStatus = eachOne.getValue().toString();
						} else if ("exception".equals(eachOne.getKey())) {
							exception = (String) eachOne.getValue();
						}

					}
					String status = oneMethod.get("testStatus");
					if (status.equals("FAIL")) {
						writer.write("<tr> <td> <font color=red>"
								+ oneMethod.get("moduleName")
								+ "</font> </td> <td> <font color=red> "
								+ testMethods.getKey()
								+ "</font> </td> <td> <font color=red> "
								+ oneMethod.get("testcaseid")
								+ "</font> </td> <td> <font color=red>"
								+ validationPoints
								+ "</font> </td> <td> <font color=red>"
								+ passedValidations
								+ "</font> </td> <td> <font color=red>"
								+ failedValidations
								+ "</font> </td> <td> <font color=red>"
								+ passPercentage
								+ "%</font> </td> <td> <font color=red>"
								+ failPercentage
								+ "%</font> </td> <td> <font color=red>"
								+ executionTime
								+ "</font> </td> <td> <font color=red>"
								+ testCaseStatus
								+ "</font> </td> <td> <font color=red>"
								+ exception + "</font> </td> </tr> ");
					} else {
						writer.write(" <tr> <td> "
								+ oneMethod.get("moduleName") + " </td> <td> "
								+ testMethods.getKey() + " </td> <td> "
								+ oneMethod.get("testcaseid") + " </td> <td> "
								+ validationPoints + " </td> <td> "
								+ passedValidations + " </td> <td> "
								+ failedValidations + " </td> <td> "
								+ passPercentage + "% </td> <td> "
								+ failPercentage + "% </td> <td> "
								+ executionTime + " </td> <td> "
								+ testCaseStatus + " </td> <td> " + exception
								+ " </td> </tr> ");
					}
				}
			}
			writer.write("</table></body></html>");
			writer.close();
		} catch (Exception e) {
			logger.error("Exception while creating HTML report and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}
	}

}
