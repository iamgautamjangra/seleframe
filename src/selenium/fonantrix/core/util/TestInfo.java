package selenium.fonantrix.core.util;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains test case informations.
 *          </p>
 */
public class TestInfo {

	private String moduleName, flowName, tcName;

	/**
	 * Method to return module name.
	 * @return Module name.
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * Method to set module name.
	 * 
	 * @param moduleName
	 *            Module name.
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * Method to return flow name.
	 * 
	 * @return Flow name.
	 */
	public String getFlowName() {
		return flowName;
	}

	/**
	 * Method to set flow name.
	 * 
	 * @param flowName
	 *            Flow name.
	 */
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	/**
	 * Method to return test case name.
	 * 
	 * @return Test case name.
	 */
	public String getTCName() {
		return tcName;
	}

	/**
	 * Method to set test case name.
	 * 
	 * @param tcName
	 *            Test case name.
	 */
	public void setTCName(String tcName) {
		this.tcName = tcName;
	}

}
