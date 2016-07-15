package selenium.fonantrix.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to load properties file and get
 *          property value from properties file.
 *          </p>
 */
public class ConfigurationMap {
	static Properties properties = null;

	private static final Logger logger = LoggerFactory
			.getLogger(ConfigurationMap.class.getName());

	/**
	 * Method to get property value.
	 * 
	 * @param propertyName
	 *            Property name.
	 * @return Property value.
	 */
	public static String getProperty(String propertyName) {
		// Read value using the logical name as Key
		String propertyValue = properties.getProperty(propertyName);
		return propertyValue;
	}

	/**
	 * Method to load properties file.
	 * 
	 * @param filePath
	 *            Properties file local path.
	 */
	public static void loadConfigurations(String filePath) {
		properties = new Properties();

		logger.info("loadConfigurations: Loading properties file");
		try {
			FileInputStream inputFile = new FileInputStream(filePath);
			properties.load(inputFile);
			inputFile.close();
		} catch (IOException e) {
			logger.error("Exception while loading the properties file and exception text is "
					+ RequestUtil.stackTraceToString(e));

		}

	}

}
