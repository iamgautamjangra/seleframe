package selenium.fonantrix.core.listener;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.xml.XmlSuite;

import selenium.fonantrix.core.report.XlsUtility;
import selenium.fonantrix.core.util.Assert;
import selenium.fonantrix.core.util.ConfigurationMap;
import selenium.fonantrix.core.util.MailSender;
import selenium.fonantrix.core.util.RequestUtil;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains the report generation functions using listener.
 *          </p>
 */
public class CustomReporter extends TestListenerAdapter implements IReporter {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomReporter.class.getName());

	public static String resultOutputDirectory;
	public static boolean failTCFlag;
	public static String startTime;
	public static String endTime;
	public static String parallelExeTime;

	public static Map<String, Map<String, Object>> testCasesDetails = new LinkedHashMap<String, Map<String, Object>>();
	public static Map<String, String> testCaseStatus = new LinkedHashMap<String, String>();
	public static Map<String, Object> executionTime = new LinkedHashMap<String, Object>();
	public static Map<String, Object> failureDetail = new LinkedHashMap<String, Object>();
	public static Map<String, Object> testCaseIdDetail = new LinkedHashMap<String, Object>();

	public CustomReporter() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.IReporter#generateReport(java.util.List, java.util.List,
	 * java.lang.String)
	 */
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {
		logger.info("generateReport(): Test cases execution is completed");
		testCasesDetails.clear();
		testCasesDetails = Assert.validationPoints;
		Set<String> methodNames = testCasesDetails.keySet();
		for (Object methodName : methodNames) {

			Map<String, Object> testCaseDataInfo = new LinkedHashMap<String, Object>();
			Map<String, Object> testCaseData = testCasesDetails.get(methodName);
			int validationPoint = (Integer) testCaseData
					.get("validationPoints");

			if (testCaseStatus.get(methodName).contains("Pass")) {
				testCaseDataInfo.put("TC_ID", testCaseIdDetail.get(methodName));
				testCaseDataInfo.put("validationPoints", validationPoint);
				testCaseDataInfo.put("passedValidations", validationPoint);
				testCaseDataInfo.put("FailedValidations", 0);
				testCaseDataInfo.put("passedPercent", "100%");
				testCaseDataInfo.put("FailedPercent", "0%");
				testCaseDataInfo.put("ExecutionTime",
						executionTime.get(methodName));
				testCaseDataInfo.put("TCStatus", "PASS");
				testCaseDataInfo.put("FailureDtail", "");

				testCasesDetails.put(methodName.toString(), testCaseDataInfo);

			} else if (testCaseStatus.get(methodName).contains("Fail")) {
				testCaseDataInfo.put("TC_ID", testCaseIdDetail.get(methodName));
				testCaseDataInfo.put("validationPoints", validationPoint);
				testCaseDataInfo.put("passedValidations", validationPoint - 1);
				testCaseDataInfo.put("FailedValidations", 1);

				double passPercent = ((double) (validationPoint - 1) / (double) validationPoint) * 100;
				testCaseDataInfo.put("passedPercent",
						new DecimalFormat("##.##").format(passPercent) + "%");

				double failPercent = ((double) 1 / (double) validationPoint) * 100;
				testCaseDataInfo.put("FailedPercent",
						new DecimalFormat("##.##").format(failPercent) + "%");

				testCaseDataInfo.put("ExecutionTime",
						executionTime.get(methodName));
				testCaseDataInfo.put("TCStatus", "FAIL");
				testCaseDataInfo.put("FailureDtail",
						failureDetail.get(methodName));

				testCasesDetails.put(methodName.toString(), testCaseDataInfo);

			} else if (testCaseStatus.get(methodName).contains("Skip")) {
				testCaseDataInfo.put("TC_ID", testCaseIdDetail.get(methodName));
				testCaseDataInfo.put("validationPoints", 0);
				testCaseDataInfo.put("passedValidations", 0);
				testCaseDataInfo.put("FailedValidations", 0);
				testCaseDataInfo.put("passedPercent", "0%");
				testCaseDataInfo.put("FailedPercent", "0%");
				testCaseDataInfo.put("ExecutionTime",
						executionTime.get(methodName));
				testCaseDataInfo.put("TCStatus", "SKIP");
				testCaseDataInfo.put("FailureDtail",
						failureDetail.get(methodName));

				testCasesDetails.put(methodName.toString(), testCaseDataInfo);
			}

		}

		Set<String> TCStatusSet = testCaseStatus.keySet();

		for (String string : TCStatusSet) {
			if (!(methodNames.contains(string))) {
				Map<String, Object> TCDataInfo = new LinkedHashMap<String, Object>();
				if (testCaseStatus.get(string).contains("Pass")) {
					TCDataInfo.put("TC_ID", testCaseIdDetail.get(string));
					TCDataInfo.put("validationPoints", 1);
					TCDataInfo.put("passedValidations", 1);
					TCDataInfo.put("FailedValidations", 0);
					TCDataInfo.put("passedPercent", "100%");
					TCDataInfo.put("FailedPercent", "0%");
					TCDataInfo.put("ExecutionTime", executionTime.get(string));
					TCDataInfo.put("TCStatus", "PASS");
					TCDataInfo.put("FailureDtail", "");

					testCasesDetails.put(string.toString(), TCDataInfo);

				}

				else if (testCaseStatus.get(string).contains("Fail")) {
					TCDataInfo.put("TC_ID", testCaseIdDetail.get(string));
					TCDataInfo.put("validationPoints", 1);
					TCDataInfo.put("passedValidations", 0);
					TCDataInfo.put("FailedValidations", 1);
					TCDataInfo.put("passedPercent", "0%");
					TCDataInfo.put("FailedPercent", "100%");
					TCDataInfo.put("ExecutionTime", executionTime.get(string));
					TCDataInfo.put("TCStatus", "FAIL");
					TCDataInfo.put("FailureDtail", failureDetail.get(string));

					testCasesDetails.put(string.toString(), TCDataInfo);

				}

				else if (testCaseStatus.get(string).contains("Skip")) {

					TCDataInfo.put("TC_ID", testCaseIdDetail.get(string));
					TCDataInfo.put("validationPoints", 1);
					TCDataInfo.put("passedValidations", 0);
					TCDataInfo.put("FailedValidations", 1);
					TCDataInfo.put("passedPercent", "0%");
					TCDataInfo.put("FailedPercent", "100%");
					TCDataInfo.put("ExecutionTime", executionTime.get(string));
					TCDataInfo.put("TCStatus", "FAIL");
					TCDataInfo.put("FailureDtail", "");

					testCasesDetails.put(string.toString(), TCDataInfo);
				}
			}

		}

		TreeMap<String, Map<String, Object>> sortedDetailTestCases = new TreeMap<String, Map<String, Object>>();
		sortedDetailTestCases.putAll(testCasesDetails);

		if (!failTCFlag) {
			outputDirectory = ConfigurationMap
					.getProperty("outputResultsDirectory");
			resultOutputDirectory = outputDirectory + getDateTime() + "//";
			(new File(resultOutputDirectory)).mkdirs();
		}

		try {
			XlsUtility.Excelwritting(sortedDetailTestCases,
					resultOutputDirectory);
		} catch (Exception e) {
			logger.error("Exception while seraching XLS file containing testcase details and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}

		MailSender.sendMail(resultOutputDirectory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onTestSuccess(ITestResult testResult) {
		logger.info(String.format("End Test Case : %s - Passed",
				testResult.getName()));
		ITestNGMethod testMethod = (ITestNGMethod) testResult.getMethod();
		testCaseStatus.put(
				testResult.getTestContext().getName()
						+ "$"
						+ testMethod.getTestClass().getName().toString()
								.split("\\.")[4] + "$"
						+ testMethod.getMethodName(), "Pass");
		onTestComplete(testResult);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onTestFailure(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailure(ITestResult testResult) {
		logger.info(String.format("End Test Case : %s - Failed",
				testResult.getName()));
		logger.error(String.format("Error : %s ", testResult.getThrowable()
				.toString()));
		// call the superclass

		ITestNGMethod testMethod = (ITestNGMethod) testResult.getMethod();
		testCaseStatus.put(
				testResult.getTestContext().getName()
						+ "$"
						+ testMethod.getTestClass().getName().toString()
								.split("\\.")[4] + "$"
						+ testMethod.getMethodName(), "Fail");

		super.onTestFailure(testResult);
		getDateTime();
		if (!failTCFlag) {
			resultOutputDirectory = ConfigurationMap
					.getProperty("outputResultsDirectory");
			resultOutputDirectory = resultOutputDirectory + getDateTime()
					+ "//";
			(new File(resultOutputDirectory)).mkdirs();
		}

		failTCFlag = true;
		onTestComplete(testResult);
		failureDetail.put(
				testResult.getTestContext().getName()
						+ "$"
						+ testMethod.getTestClass().getName().toString()
								.split("\\.")[4] + "$"
						+ testMethod.getMethodName(), testResult.getThrowable()
						.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public void onTestSkipped(ITestResult testResult) {
		logger.info(String.format("End Test Case : %s - Skipped",
				testResult.getName()));
		ITestNGMethod testMethod = (ITestNGMethod) testResult.getMethod();
		testCaseStatus.put(
				testResult.getTestContext().getName()
						+ "$"
						+ testMethod.getTestClass().getName().toString()
								.split("\\.")[4] + "$"
						+ testMethod.getMethodName(), "Skip");

		onTestComplete(testResult);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onFinish(ITestContext testContext) {

		long suiteTime = testContext.getEndDate().getTime()
				- testContext.getStartDate().getTime();
		int suiteTimeInSec = (int) (suiteTime / 1000);

		int timeInHours = (suiteTimeInSec / 3600);
		int timeInMin = (suiteTimeInSec / 60);
		long timeInSec = Math.round(suiteTimeInSec % 60);

		SimpleDateFormat dateFormate = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");

		startTime = dateFormate.format(testContext.getStartDate()) + " "
				+ timeFormate.format(testContext.getStartDate());
		endTime = dateFormate.format(testContext.getEndDate()) + " "
				+ timeFormate.format(testContext.getEndDate());
		parallelExeTime = String.format("%02d", timeInHours) + ":"
				+ String.format("%02d", timeInMin) + ":"
				+ String.format("%02d", timeInSec);
	}

	/**
	 * @return Formatted system date & time as string.
	 */
	private static String getDateTime() {
		String sDateTime = null;
		try {
			SimpleDateFormat dateFormate = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
			Date currentDateTime = new Date();
			String currentDate = dateFormate.format(currentDateTime);
			String currentTime = timeFormate.format(currentDateTime);
			currentTime = currentTime.replace(":", "-");
			sDateTime = currentDate + "_" + currentTime;
		} catch (Exception e) {
			logger.error("Exception while formatting the Current Date, Time and exception details are : "
					+ RequestUtil.stackTraceToString(e));
		}
		return sDateTime;
	}

	/**
	 * @param testResult
	 *            Test case information.
	 * 
	 */
	public void onTestComplete(ITestResult testResult) {
		ITestNGMethod testMethod = (ITestNGMethod) testResult.getMethod();

		executionTime
				.put(testResult.getTestContext().getName()
						+ "$"
						+ testMethod.getTestClass().getName().toString()
								.split("\\.")[4] + "$"
						+ testMethod.getMethodName(),
						Long.valueOf(
								testResult.getEndMillis()
										- testResult.getStartMillis())
								.toString());
		testCaseIdDetail.put(
				testResult.getTestContext().getName()
						+ "$"
						+ testMethod.getTestClass().getName().toString()
								.split("\\.")[4] + "$"
						+ testMethod.getMethodName(), testResult.getMethod()
						.getDescription());

	}
}
