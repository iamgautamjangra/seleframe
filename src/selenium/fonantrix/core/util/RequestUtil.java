package selenium.fonantrix.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to print error in log file.
 *          </p>
 */
public class RequestUtil {
	/**
	 * Method to create and return a {@link java.lang.String} from
	 * <code>t</code>'s stacktrace.
	 * 
	 * @param throwable
	 *            Throwable whose stack trace is required.
	 * @return String representing the stack trace of the exception.
	 */
	public static String stackTraceToString(final Throwable throwable) {
		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);

		throwable.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();

		return stringWritter.toString();
	}

}
