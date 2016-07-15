package selenium.fonantrix.core.parse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selenium.fonantrix.core.util.RequestUtil;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains utility functions to parse JSON response.
 *          </p>
 */
public class JsonParser {

	private static final Logger logger = LoggerFactory
			.getLogger(JsonParser.class.getName());

	/**
	 * Method to parse the response, to string, received from server which is in
	 * JSON Format.
	 * 
	 * @param arrayKey
	 *            Key in JSON string which is name of column.
	 * @param jsonString
	 *            JSON response in string format.
	 * @param columnName
	 *            Name of column whose value is to fetched from JSON response.
	 * @return Value of column as a String.
	 */
	public static String parseJsonString(String[] arrayKey, String jsonString,
			String columnName) {
		String JsonValue = null;
		logger.info(String.format(
				"parseJson: parsing json string with array key %s", arrayKey));
		try {
			final JSONObject obj = new JSONObject(jsonString);
			JSONObject object = null;

			if (arrayKey.length == 1) {
				object = (JSONObject) obj.get(arrayKey[0]);
				JsonValue = (String) object.get(columnName);
			}
		} catch (Exception e) {
			logger.error("Exception while pasing JSON string and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}
		return JsonValue;
	}

	/**
	 * Method to parse the response, to JSON string, received from server which is in JSON
	 * Format.
	 * 
	 * @param arrayKey
	 *            Key in JSON string which is name of column.
	 * @param jsonString
	 *            JSON response in string format.
	 * @param skipColums
	 *            Name of column which is to be skip in result data.
	 * @return Map related to JSON string.
	 */
	public static Map<String, Map<String, String>> parseJson(String[] arrayKey,
			String jsonString, String[] skipColums) {
		logger.info(String.format(
				"parseJson: parsing json string with array key %s", arrayKey));
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			@SuppressWarnings("rawtypes")
			public List<?> creatArrayContainer() {
				return new LinkedList();
			}

			@SuppressWarnings("rawtypes")
			public Map<?, ?> createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		Map<String, Map<String, String>> jsonData = new HashMap<String, Map<String, String>>();
		try {
			final JSONObject obj = new JSONObject(jsonString);
			JSONArray jsonArray = null;
			JSONObject object = null;

			if (arrayKey.length == 1) {
				jsonArray = (JSONArray) obj.get(arrayKey[0]);
			} else {
				object = obj.getJSONObject(arrayKey[0]);
				jsonArray = object.getJSONArray(arrayKey[arrayKey.length - 1]);
			}
			final int noOfRows = jsonArray.length();
			for (int row = 0; row < noOfRows; ++row) {
				int colNum = 1;
				final JSONObject jsonObject = jsonArray.getJSONObject(row);
				Map<?, ?> json = (Map<?, ?>) parser.parse(
						jsonObject.toString(), containerFactory);
				Iterator<?> iter = json.entrySet().iterator();
				Map<String, String> rowData = new LinkedHashMap<String, String>();
				while (iter.hasNext()) {
					Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
					if (!(Arrays.asList(skipColums).contains(entry.getKey()
							.toString()))) {
						rowData.put("column" + colNum, entry.getValue()
								.toString());
						colNum = colNum + 1;
					}
				}
				jsonData.put("row" + (row + 1), rowData);
			}
		} catch (Exception e) {
			logger.error("Exception while parsing the json string and exception details are "
					+ RequestUtil.stackTraceToString(e));

		}
		return jsonData;
	}

}
