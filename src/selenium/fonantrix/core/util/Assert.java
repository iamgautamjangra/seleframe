package selenium.fonantrix.core.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions for checking expected value and actual
 *          value is same or not.
 *          </p>
 */
public class Assert {

	private static final Logger logger = LoggerFactory.getLogger(Assert.class
			.getName());

	static Map<String, Integer> invokationMap = new HashMap<String, Integer>();
	public static Map<String, Map<String, Object>> validationPoints = new HashMap<String, Map<String, Object>>();

	/**
	 * Method to compare expected and actual values.
	 * 
	 * @param actual
	 *            Actual value.
	 * @param expected
	 *            Expected value.
	 * @param message
	 *            Error message to be printed on failure.
	 * @param testInfo
	 *            Test case information.
	 */
	static public void assertEquals(boolean actual, boolean expected,
			Object message, Object testInfo) {

		TestInfo testInfoObj = (TestInfo) testInfo;

		String currentInvokationMethod = testInfoObj.getModuleName() + "$"
				+ testInfoObj.getFlowName() + "$" + testInfoObj.getTCName();

		validationPoint(validationPoints, true, currentInvokationMethod);
		assertEquals(Boolean.valueOf(actual), Boolean.valueOf(expected),
				message, testInfo);

	}

	/**
	 * Method to compare expected and actual values.
	 * 
	 * @param actual
	 *            Actual value.
	 * @param expected
	 *            Expected value.
	 * @param message
	 *            Error message to be printed on failure.
	 * @param testInfo
	 *            Test case information.
	 */
	static public void assertEquals(Object actual, Object expected,
			Object message, Object testInfo) {
		TestInfo testInfoObj = (TestInfo) testInfo;
		String currentInvokationMethod = testInfoObj.getModuleName() + "$"
				+ testInfoObj.getFlowName() + "$" + testInfoObj.getTCName();

		if ((expected == null) && (actual == null)) {
			return;
		}
		if ((expected != null) && expected.equals(actual)) {
			validationPoint(validationPoints, false, currentInvokationMethod);
			logger.info(String.format("Assertion passed - %s",
					message.toString()));
			return;
		}
		failNotEquals(actual, expected, message);
	}

	/**
	 * Method to print and throw error message.
	 * 
	 * @param actual
	 *            Actual value.
	 * @param expected
	 *            Expected value.
	 * @param message
	 *            Error message to be printed on failure.
	 */
	static private void failNotEquals(Object actual, Object expected,
			Object message) {
		logger.info(String.format("Assertion failed - %s", message.toString()));
		fail(message.toString());
	}

	/**
	 * Method to throw error message.
	 * 
	 * @param message
	 *            Error Message.
	 */
	static public void fail(String message) {

		throw new AssertionError(message);
	}

	/**
	 * Method to count validation points.
	 * 
	 * @param validationPoints
	 *            Validation points map.
	 * @param flag
	 *            Validation status.
	 * @param invokedMethod
	 *            Test case name.
	 */
	private static void validationPoint(
			Map<String, Map<String, Object>> validationPoints, boolean flag,
			String invokedMethod) {

		if (!validationPoints.containsKey(invokedMethod)) {
			Map<String, Object> onePoint = new HashMap<String, Object>();
			onePoint.put("validationPoints", 1);
			if (flag == true)
				onePoint.put("passedValidations", 0);
			validationPoints.put(invokedMethod, onePoint);

		} else {
			Map<String, Object> onePoint = (Map<String, Object>) validationPoints
					.get(invokedMethod);
			for (Map.Entry<String, Object> eachOne : onePoint.entrySet()) {
				if ("validationPoints".equals(eachOne.getKey())) {
					int incValidationPoint = (Integer) eachOne.getValue();
					if (flag == true)
						incValidationPoint = incValidationPoint + 1;
					onePoint.put("validationPoints", incValidationPoint);
				} else if ("passedValidations".equals(eachOne.getKey())) {
					int incPassedValidationPoint = (Integer) eachOne.getValue();
					if (!flag == true)
						incPassedValidationPoint = (Integer) eachOne.getValue() + 1;
					onePoint.put("passedValidations", incPassedValidationPoint);
				}

			}
			validationPoints.put(invokedMethod, onePoint);
		}

	}

}
