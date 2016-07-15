package selenium.fonantrix.core.util;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions for report summary.
 *          </p>
 */
public class TestSummary {

	private String moduleName;
	private int automatedTestCases;
	private int totalPassed;
	private int totalFailed;
	private int totalTestCases;
	private long executionTime;
	private double passPercent;
	private double failPercent;

	/**
	 * @return Total number of test cases.
	 */
	public int getTotalTestCases() {
		return totalTestCases;
	}

	/**
	 * Method to set test case name to be used in summary report.
	 * 
	 * @param totalTestCases
	 *            Number of test cases.
	 */
	public void setTotalTestCases(int totalTestCases) {
		this.totalTestCases = totalTestCases;
	}

	/**
	 * Method to return module name to be used in summary report.
	 * 
	 * @return Module name.
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * Method to set module name to be used in summary report.
	 * 
	 * @param moduleName
	 *            Module name.
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * Method to return number of automated test cases to be used in summary
	 * report.
	 * 
	 * @return Number of automated test cases.
	 */
	public int getAutomatedTestCases() {
		return automatedTestCases;
	}

	/**
	 * Method to set number of automated test cases to be used in summary
	 * report.
	 * 
	 * @param automatedTestCases
	 *            Number of automated test cases.
	 */
	public void setAutomatedTestCases(int automatedTestCases) {
		this.automatedTestCases = automatedTestCases;
	}

	/**
	 * Method to return number of passed test cases to be used in summary
	 * report.
	 * 
	 * @return Total number of test cases passed.
	 */
	public int getTotalPassed() {
		return totalPassed;
	}

	/**
	 * Method to set number of passed test cases to be used in summary report.
	 * 
	 * @param totalPassed
	 *            Number of test cases passed.
	 */
	public void setTotalPassed(int totalPassed) {
		this.totalPassed = totalPassed;
	}

	/**
	 * Method to return number of failed test cases to be used in summary
	 * report.
	 * 
	 * @return Total number of test cases Failed.
	 */
	public int getTotalFailed() {
		return totalFailed;
	}

	/**
	 * Method to set number of failed test cases to be used in summary report.
	 * 
	 * @param totalFailed
	 *            Number of test cases Failed.
	 */
	public void setTotalFailed(int totalFailed) {
		this.totalFailed = totalFailed;
	}

	/**
	 * Method to return time taken to execute all test cases to be used in
	 * summary report.
	 * 
	 * @return Time to execute all test cases.
	 */
	public long getExecutionTime() {
		return executionTime;
	}

	/**
	 * Method to set time taken to execute all test cases to be used in
	 * summary report.
	 * 
	 * @param executionTime
	 *            Time to execute all test cases.
	 */
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	/**
	 * Method to return percentage of passed test cases to be used in summary
	 * report.
	 * 
	 * @return Percentage of passed test cases.
	 */
	public double getPassPercent() {
		return passPercent;
	}

	/**
	 * Method to set percentage of passed test cases to be used in summary
	 * report.
	 * 
	 * @param passPercent
	 *            Percentage of passed test cases.
	 */
	public void setPassPercent(double passPercent) {
		this.passPercent = passPercent;
	}

	/**
	 * Method to return percentage of failed test cases to be used in summary
	 * report.
	 * 
	 * @return Percentage of failed test cases.
	 */
	public double getFailPercent() {
		return failPercent;
	}

	/**
	 * Method to set percentage of failed test cases to be used in summary
	 * report.
	 * 
	 * @param failPercent
	 *            Percentage of failed test cases.
	 */
	public void setFailPercent(double failPercent) {
		this.failPercent = failPercent;
	}

}
