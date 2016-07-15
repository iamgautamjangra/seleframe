package selenium.fonantrix.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to calculate validation point.
 *          </p>
 */
public class ValidationPoints {

	public static String currentTestMethod;
	public static Map<String, Map<String, Integer>> validationPoints = new HashMap<String, Map<String, Integer>>();

	/**
	 * Method to return test case name under execution.
	 * 
	 * @return Test case name currently in execution.
	 */
	public static String getCurrentTestMethod() {
		return currentTestMethod;
	}

	/**
	 * Method to return number of validation points for test case name under
	 * execution.
	 * 
	 * @return Number of validation points for test case in execution.
	 */
	public static Map<String, Map<String, Integer>> getValidationPoints() {
		return validationPoints;
	}

	/**
	 * Method to set number of validation points for test case name under
	 * execution.
	 * 
	 * @param validationPoints
	 *            Number of validation points for test case in execution.
	 */
	public static void setValidationPoints(
			Map<String, Map<String, Integer>> validationPoints) {
		ValidationPoints.validationPoints = validationPoints;
	}

	/**
	 * Method to set test case name under execution.
	 * 
	 * @param currentTestMethod
	 *            Test case name currently in execution.
	 */
	public static void setCurrentTestMethod(String currentTestMethod) {
		ValidationPoints.currentTestMethod = currentTestMethod;
	}

}
