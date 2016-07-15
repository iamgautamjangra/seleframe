package selenium.fonantrix.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import selenium.fonantrix.core.util.ConfigurationMap;
import selenium.fonantrix.core.util.RequestUtil;

import com.truemesh.squiggle.SelectQuery;
import com.truemesh.squiggle.Table;
import com.truemesh.squiggle.WildCardColumn;
import com.truemesh.squiggle.criteria.InCriteria;
import com.truemesh.squiggle.criteria.MatchCriteria;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains the MySQL database utility functions.
 *          </p>
 */
public class MySqlUtility {

	/**
	 * Logger Configuration.
	 */

	private static final Logger logger = LoggerFactory
			.getLogger(MySqlUtility.class.getName());
	private static Connection connection;

	/**
	 * Method to create connection to database.
	 * 
	 * @return Connection object.
	 */
	public static Connection createConnection() {
		connection = null;
		ConfigurationMap.loadConfigurations(System.getProperty("user.dir")
				+ "\\config\\configuration.properties");
		try {
			// Getting all required parameter value from property file.
			String drivers = ConfigurationMap.getProperty("dbDriverClass");
			String connectionURL = ConfigurationMap.getProperty("dbUrl");
			String username = ConfigurationMap.getProperty("dbUsername");
			String password = ConfigurationMap.getProperty("dbPassword");
			Class.forName(drivers);

			connection = DriverManager.getConnection(connectionURL, username,
					password);

		} catch (Exception e) {
			logger.error("Exception while connecting to the database and exception text is: "
					+ RequestUtil.stackTraceToString(e));

		}
		return connection;
	}

	/**
	 * Method to get data from database.
	 * 
	 * @param queryString
	 *            SQL query which is executed to retrieve data.
	 * @param skipColumns
	 *           Contains index of columns to be skipped.
	 * @return Map that contains data.
	 */
	public static Map<String, Map<String, String>> getDBTableData(
			String queryString, String[] skipColumns)

	{
		Map<String, Map<String, String>> tableData = new LinkedHashMap<String, Map<String, String>>();
		try {
			Connection con = createConnection();
			Statement statement = con.createStatement();
			ResultSet rset = statement.executeQuery(queryString);
			ResultSetMetaData rsmd = rset.getMetaData();
			int numOfColumns = rsmd.getColumnCount();
			int rowNum = 1;

			while (rset.next()) {
				Map<String, String> rowData = new LinkedHashMap<String, String>();
				for (int columnCount = 0; columnCount < numOfColumns; columnCount++) {
					int col = columnCount + 1;

					if (!(Arrays.asList(skipColumns).contains(rsmd
							.getColumnName(col))))
						rowData.put("column" + (columnCount + 1),
								rset.getString(columnCount + 1));
				}
				tableData.put("row" + rowNum, rowData);
				rowNum = rowNum + 1;
			}

			con.close();

		} catch (Exception e) {
			logger.error("Exception while retriving the datatbase data and the exception text is: "
					+ RequestUtil.stackTraceToString(e));

		}
		return tableData;
	}

	/**
	 * Method to generate query to select all columns from database.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @return Database query string.
	 */
	public static String selectQuery(String tableName) {
		Table table = new Table(tableName);
		SelectQuery select = new SelectQuery();
		select.addToSelection(new WildCardColumn(table));
		logger.info(String.format("select query is %s", select.toString()));
		return select.toString();
	}

	/**
	 * Method to generate query to select particular columns.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param columnsList
	 *            List of columns to select.
	 * @return Database query string with specific columns.
	 */
	public static String selectQuery(String tableName, List<String> columnsList) {
		Table table = new Table(tableName);
		SelectQuery select = new SelectQuery();
		for (int i = 0; i < columnsList.size(); i++) {
			select.addColumn(table, columnsList.get(i));
		}
		logger.info(String.format("select query is %s", select.toString()));
		return select.toString();
	}

	/**
	 * Method to generate query with condition: Single column.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param inCriteria
	 *            Contains the values for the columns to be retrieved.
	 * @param columnName
	 *            Contains column name.
	 * @return Database query string with one column.
	 */
	public static String selectQuery(String tableName, String[] inCriteria,
			String columnName) {
		Table table = new Table(tableName);
		SelectQuery select = new SelectQuery();
		select.addToSelection(new WildCardColumn(table));
		select.addCriteria(new InCriteria(table, columnName, inCriteria));
		logger.info(String.format("select query is %s", select.toString()));
		return select.toString();
	}

	/**
	 * Method to generate query with condition: Selected multiple columns.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param List
	 *            List of columns to be selected.
	 * @param matchCriteria
	 *            Matches any one of EQUALS, GREATER, GREATEREQUAL, LESS,
	 *            LESSEQUAL, LIKE, NOTEQUAL criteria.
	 * @param criteriaColumns
	 *            Contains column name and its value to be matched.
	 * @return Database query string with multiple columns.
	 */
	public static String selectQuery(String tableName,
			List<String> columnsList, String[] matchCriteria,
			Map<String, String> criteriaColumns) {
		Table table = new Table(tableName);
		SelectQuery select = new SelectQuery();
		for (int i = 0; i < columnsList.size(); i++) {
			select.addColumn(table, columnsList.get(i));
		}
		int i = 0;
		for (Map.Entry<String, String> entry : criteriaColumns.entrySet()) {
			select.addCriteria(new MatchCriteria(table, entry.getKey(),
					matchCriteria[i], entry.getValue()));
			i = i + 1;
		}
		logger.info(String.format("select query is %s", select.toString()));
		return select.toString();
	}

	/**
	 * Method to generate query with condition: All columns.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param matchCriteria
	 *            Matches any one of EQUALS, GREATER, GREATEREQUAL, LESS,
	 *            LESSEQUAL, LIKE, NOTEQUAL criteria.
	 * @param criteriaColumns
	 *            Contains column name and its value to be matched.
	 * @return Database query string.
	 */
	public static String selectQuery(String tableName, String[] matchCriteria,
			Map<String, String> criteriaColumns) {
		Table table = new Table(tableName);
		SelectQuery select = new SelectQuery();
		select.addToSelection(new WildCardColumn(table));
		int i = 0;
		for (Map.Entry<String, String> entry : criteriaColumns.entrySet()) {
			select.addCriteria(new MatchCriteria(table, entry.getKey(),
					matchCriteria[i], entry.getValue()));
			i = i + 1;
		}
		logger.info(String.format("select query is %s", select.toString()));
		return select.toString();
	}

	/**
	 * Method to generate join query.
	 *  
	 * @param joinTableOne
	 *            Source table name.
	 * @param joinTableTwo
	 *            Destination table name.
	 * @param columnsList
	 *            List of columns to select from table one.
	 * @param commonColumn
	 *            Common column present in both the tables.
	 * @return Database join query string.
	 */
	public static String selectQuery(String joinTableOne, String joinTableTwo,
			List<String> columnsList, String commonColumn) {
		Table firstTable = new Table(joinTableOne);
		Table secondTable = new Table(joinTableTwo);
		SelectQuery select = new SelectQuery();
		for (int i = 0; i < columnsList.size(); i++) {
			select.addColumn(firstTable, columnsList.get(i));
		}
		select.addJoin(firstTable, commonColumn, secondTable, "id");
		logger.info(String.format("select query is %s", select.toString()));
		return select.toString();

	}

	/**
	 * Method to assert existence in database: Selected multiple columns.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param columnsList
	 *            List of columns to select from table one.
	 * @param matchCriteria
	 *            Match any one of EQUALS, GREATER, GREATEREQUAL, LESS,
	 *            LESSEQUAL, LIKE, NOTEQUAL criteria.
	 * @param criteriaColumns
	 *            Contains column name and it's value to be matched.
	 * @param expValue
	 *            Value that user expects in database, either true or false.
	 */
	public static void assertAdded(String tableName, List<String> columnsList,
			String[] matchCriteria, Map<String, String> criteriaColumns,
			boolean expValue) {
		boolean actual = false;
		String skipcol[] = new String[1];
		String sql = selectQuery(tableName, columnsList, matchCriteria,
				criteriaColumns);
		Map<String, Map<String, String>> data = new LinkedHashMap<String, Map<String, String>>();
		data = getDBTableData(sql, skipcol);
		if (!data.isEmpty())
			actual = true;
		Assert.assertEquals(actual, expValue, "Data exists in Database");
	}

	/**
	 * Method to assert existence in database: All columns.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param matchCriteria
	 *            Match any one of EQUALS, GREATER, GREATEREQUAL, LESS,
	 *            LESSEQUAL, LIKE, NOTEQUAL criteria.
	 * @param criteriaColumns
	 *            Contains column name and it's value to be matched.
	 * @param expValue
	 *            Value that user expects in database, either true or false.
	 */
	public static void assertAdded(String tableName, String[] matchCriteria,
			Map<String, String> criteriaColumns, boolean expValue) {
		boolean actValue = false;
		String skipColumn[] = new String[1];
		String sqlQuery = selectQuery(tableName, matchCriteria, criteriaColumns);
		Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		data = getDBTableData(sqlQuery, skipColumn);
		if (!data.isEmpty())
			actValue = true;
		Assert.assertEquals(actValue, expValue, "Data exists in Database");
	}

	/**
	 * Method to assert non-existence in database: Selected multiple columns.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param columnsList
	 *            List of columns to select from table one.
	 * @param matchCriteria
	 *            Match any one of EQUALS, GREATER, GREATEREQUAL, LESS,
	 *            LESSEQUAL, LIKE, NOTEQUAL criteria.
	 * @param criteriaColumns
	 *            Contains column name and it's value to be matched.
	 * @param expValue
	 *            Value that user expects in database, either true or false.
	 */
	public static void assertDelete(String tableName, List<String> columnsList,
			String[] matchCriteria, Map<String, String> criteriaColumns,
			boolean expValue) {
		boolean actValue = true;
		String skipColumn[] = new String[1];
		String sqlQuery = selectQuery(tableName, columnsList, matchCriteria,
				criteriaColumns);
		Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		data = getDBTableData(sqlQuery, skipColumn);
		if (!data.isEmpty())
			actValue = false;
		Assert.assertEquals(actValue, expValue, "Data exists not in Database");
	}

	/**
	 * Method to assert non-existence in database: All columns.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param matchCriteria
	 *            Match any one of EQUALS, GREATER, GREATEREQUAL, LESS,
	 *            LESSEQUAL, LIKE, NOTEQUAL criteria.
	 * @param criteriaColumns
	 *            Contains column name and it's value to be matched.
	 * @param expValue
	 *            Value that user expects in database, either true or false.
	 */
	public static void assertDelete(String tableName, String[] matchCriteria,
			Map<String, String> criteriaColumns, boolean expValue) {
		boolean actValue = true;
		String skipcol[] = new String[1];
		String sql = selectQuery(tableName, matchCriteria, criteriaColumns);
		Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		data = getDBTableData(sql, skipcol);
		if (!data.isEmpty())
			actValue = false;
		Assert.assertEquals(actValue, expValue, "Data exists not in Database");
	}

	/**
	 * Method to retrieve the value of specified column of the table in list.
	 * 
	 * @param tableName
	 *            Source table name.
	 * @param columnName
	 *            Name of column for which value is to be retrieved in list.
	 * @return Column value in list.
	 */
	public static List<String> getColumnList(String tableName, String columnName) {
		List<String> columnValues = new LinkedList<String>();
		try {

			String query = String.format("select %s from %s", columnName,
					tableName);
			// Connection con = createConnection();
			Statement statement = connection.createStatement();
			ResultSet rset = statement.executeQuery(query);
			while (rset.next()) {
				columnValues.add(rset.getString(1));
			}
		} catch (Exception e) {
			logger.error("Exception while fetching column data from table and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}
		return columnValues;
	}

	/**
	 * Method to retrieve the value of specified column of the table in list.
	 * 
	 * @param query
	 *            Query string.
	 * @return Column value in list.
	 */
	public static List<String> getColumnList(String query) {
		List<String> columnValues = new LinkedList<String>();
		try {
			//
			Connection con = createConnection();
			Statement stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(query);
			while (rset.next()) {
				columnValues.add(rset.getString(1));
			}
		} catch (Exception e) {
			logger.error("Exception while fetching column data from table and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}
		return columnValues;
	}

	/**
	 * Method to assert that given column names exist in particular collection.
	 * 
	 * @param tableName
	 *            Table name for which columns to be asserted.
	 * @param CoulmnNames
	 *            String array of column names.
	 */
	public static void asserttblColName(String tableName, String[] CoulmnNames) {
		boolean flag = true;
		String query = "describe " + tableName + ";";
		List<String> columnList = getColumnList(query);
		for (int i = 0; i < CoulmnNames.length; i++) {

			if (!(columnList.contains(CoulmnNames[i]))) {
				flag = false;
			}
		}
		if (flag = true) {
		}
		Assert.assertEquals(flag, true, "All columns are not matched");
	}
}

// ################################# END #####################################