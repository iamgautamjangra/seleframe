package selenium.fonantrix.core.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.seleniumhq.jetty7.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selenium.fonantrix.core.rest.FileIO;
import selenium.fonantrix.core.util.Assert;
import selenium.fonantrix.core.util.ConfigurationMap;
import selenium.fonantrix.core.util.RequestUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains the mongoDB utility functions.
 *          </p>
 */
public class MongoDbUtility {

	// Static variables for MongoDbUtility

	private static final Logger logger = LoggerFactory
			.getLogger(MongoDbUtility.class.getName());
	public static Map<String, Object> queryParameterMap = new HashMap<String, Object>();
	public static Map<String, Object> columnsMap = new HashMap<String, Object>();
	public static DB dbConnection;

	/**
	 * Method to insert condition into database query.
	 * 
	 * @param key
	 *            Name of column to which condition is to be applied.
	 * @param value
	 *            Value of column.
	 */
	public static void insertParameter(String key, Object value) {
		queryParameterMap.put(key, value);

	}

	/**
	 * Method to insert columns, which are required in result data.
	 * 
	 * @param key
	 *            Name of column.
	 * @param value
	 *            Will be true if specified column is required otherwise it will
	 *            be false.
	 */
	public static void insertColumn(String key, Object value) {
		columnsMap.put(key, value);
	}

	/**
	 * Method to connect with MongoDB.
	 * 
	 * @return Database object.
	 */

	public static DB createMongoDBConnection() {
		logger.info("createMongoDBConnection: creating mongo db connection");
		dbConnection = null;
		// Getting required parameter values for mongo DB connection from
		// configuration property.

		String username = ConfigurationMap.getProperty("mongoUserName");
		String password = ConfigurationMap.getProperty("mongoPwd");
		String hostName = ConfigurationMap.getProperty("mongoHostIp");
		String dbName = ConfigurationMap.getProperty("mongoDbName");
		int dbPort = Integer.parseInt(ConfigurationMap
				.getProperty("mongoDbPort"));

		try {
			boolean authentication = false;
			int i = 0;

			// trying to connect with mongo server at most 5 times.

			while (i < 5) {
				MongoClient mongoClient = new MongoClient(hostName, dbPort);
				dbConnection = mongoClient.getDB("admin");

				// Authenticating user on server.

				authentication = dbConnection.authenticate(username,
						password.toCharArray());
				dbConnection = mongoClient.getDB(dbName);
				i++;
				if (authentication) {
					// connection created successfully.
					// Terminating loop.
					break;
				}

			}
		} catch (Exception e) {
			logger.error("Error while connecting to the mongoDB database and the exception details are "
					+ RequestUtil.stackTraceToString(e));
		}
		return dbConnection;
	}

	/**
	 * Method to retrieve data from collection.
	 * 
	 * @param collectionName
	 *            Collection name from where to retrieve data.
	 * @param query
	 *            Query string with condition.
	 * @return All records from the map.
	 */
	public static Map<String, Map<String, String>> assertCollectionData(
			String collectionName, DBObject query) {
		DBCursor cursor = null;

		DBCollection collection = dbConnection.getCollection(collectionName);
		if (columnsMap != null) {
			BasicDBObject fields = null;
			// providing names of required columns to select from collection.
			fields = new BasicDBObject(columnsMap);
			// fetching data from DB based on input query and fields.
			cursor = collection.find(query, fields);
		} else {
			cursor = collection.find(query);

		}

		// Extracting result data in Map<String,Map<String,String>> format.
		Map<String, Map<String, String>> collectionData = extractCollectionData(cursor);
		columnsMap.clear();
		// To return result in map form.
		return collectionData;
	}

	/**
	 * Method to save data in map.
	 * 
	 * @param cursor
	 *            DBCursor pointing to the first row of the retrieved data.
	 * @return All records in the map.
	 */
	public static Map<String, Map<String, String>> extractCollectionData(
			DBCursor cursor) {
		Map<String, Map<String, String>> collectionData = new LinkedHashMap<String, Map<String, String>>();
		int rowNumber = 1;
		while (cursor.hasNext()) {
			Map<String, String> documentData = new LinkedHashMap<String, String>();

			// Getting data from cusor object

			DBObject collectionObject = cursor.next();
			Set<String> columnNames = collectionObject.keySet();

			for (String columnName : columnNames) {
				try {
					// Putting data of cursor into temporary map with column
					// name as keys.
					documentData.put(columnName,
							collectionObject.get(columnName).toString());
				}

				catch (Exception e) {
					logger.error("Exception while sending email and exception text is: "
							+ RequestUtil.stackTraceToString(e));
					documentData.put(columnName, null);
				}
			}

			// Putting data on result map
			collectionData.put("row" + rowNumber++, documentData);
		}

		return collectionData;

	}

	/**
	 * Method to create database query with AND / OR condition.
	 * 
	 * @param condition
	 *            Can be AND / OR.
	 * @return Conditional database query.
	 */
	public static DBObject createMongoDBQuery(String condition) {
		DBObject query = null;
		String parameter = "";
		Object value = "";
		BasicDBList dbObject = new BasicDBList();
		// Getting condition required condition for query
		for (Map.Entry<String, Object> queryMap : queryParameterMap.entrySet()) {
			parameter = queryMap.getKey();
			value = queryMap.getValue();
			DBObject clause = new BasicDBObject(parameter, value);
			dbObject.add(clause);
		}
		// Adding "OR" condition according to specification of user
		if (condition.equals("OR")) {
			query = new BasicDBObject("$or", dbObject);
		}

		// Adding "AND" condition according to specification of user
		else if (condition.equals("AND")) {
			query = new BasicDBObject("$and", dbObject);
		}

		logger.info(String
				.format("createMongoDBQuery:  created mondb query with %s condition and the query is %s ",
						condition, query.toString()));
		queryParameterMap.clear();
		return query;

	}

	/**
	 * Method to create database query with AND / OR / AND_IN condition.
	 * 
	 * @param colName
	 *            Field name that is using for in condition.
	 * @param condition
	 *            OR / AND / AND_IN.
	 * @param columnValues
	 *            String array of values used in IN condition.
	 * @return Conditional database query.
	 */
	public static DBObject createMongoDBQuery(String colName, String condition,
			String[] columnValues) {
		DBObject query = null;
		String parameter = "";
		Object value = "";
		BasicDBList dbObject = new BasicDBList();
		for (Map.Entry<String, Object> queryMap : queryParameterMap.entrySet()) {
			parameter = queryMap.getKey();
			value = queryMap.getValue();

			DBObject clause = new BasicDBObject(parameter, value);
			dbObject.add(clause);
		}
		// Adding "OR" condition according to specification of user
		if (condition.equals("OR")) {
			query = new BasicDBObject("$or", dbObject);
		} else if (condition.equals("AND")) {
			query = new BasicDBObject("$and", dbObject);
		}

		// Adding AND with IN condition according to specification of user
		else if (condition.equals("AND_IN")) {
			DBObject dbObjectIN = createMongoDBQuery(colName, columnValues);
			dbObject.add(dbObjectIN);
			query = new BasicDBObject("$and", dbObject);
		}

		queryParameterMap.clear();
		return query;

	}

	/**
	 * Method to retrieve the data for specific rows: Pagination.
	 * 
	 * @param collectionName
	 *            Collection name from where we retrieve the data.
	 * @param start
	 *            Start of the row.
	 * @param end
	 *            End of the row.
	 * @return Specific collection of data.
	 */
	public static Map<String, Map<String, String>> assertPaginationData(
			String collectionName, int start, int end) {

		DBCollection collection = dbConnection.getCollection(collectionName);
		DBCursor cursor = collection.find().skip(start).limit(end);
		Map<String, Map<String, String>> collectionData = extractCollectionData(cursor);
		return collectionData;

	}

	/**
	 * Method to retrieve the data for specific rows: Pagination with condition.
	 * 
	 * @param collectionName
	 *            Collection name from where we retrieve the data.
	 * @param query
	 *            Query to be executed.
	 * @param start
	 *            Start of the row.
	 * @param end
	 *            End of the row.
	 * @return Conditional collection data.
	 */
	public static Map<String, Map<String, String>> assertPaginationData(
			String collectionName, DBObject query, int start, int end) {

		DBCollection collection = dbConnection.getCollection(collectionName);
		BasicDBObject fields = null;
		if (columnsMap != null)
			fields = new BasicDBObject(columnsMap);
		DBCursor cursor = collection.find(query, fields).skip(start).limit(end);
		Map<String, Map<String, String>> collectionData = extractCollectionData(cursor);
		dbConnection.cleanCursors(true);
		columnsMap.clear();
		return collectionData;

	}

	/**
	 * Method to retrieve all data from table.
	 * 
	 * @param collectionName
	 *            Collection name from where we retrieve the data.
	 * @return All collection data.
	 */
	public static Map<String, Map<String, String>> assertAllCollectionData(
			String collectionName) {
		DBCollection collection = dbConnection.getCollection(collectionName);
		DBCursor cursor = collection.find();
		Map<String, Map<String, String>> collectionData = extractCollectionData(cursor);
		return collectionData;

	}

	/**
	 * Method to count the number of records in specified collection.
	 * 
	 * @param collectionName
	 *            Name of collection.
	 * @param query
	 *            Condition on columns.
	 * @return Conditional collection data.
	 */
	public static int assertCountData(String collectionName, DBObject query) {
		DBCollection collection = dbConnection.getCollection(collectionName);
		int Count = collection.find(query).count();
		return Count;

	}

	/**
	 * Method to retrieve single row data.
	 * 
	 * @param collectionName
	 *            Collection name from where we retrieve the data.
	 * @return Collection of data for one row.
	 */
	public static Map<String, Map<String, String>> getOneRowCollectionData(
			String collectionName) {
		logger.info("Connection succesful");
		DBCollection table = dbConnection.getCollection(collectionName);
		logger.info("table Name" + table);
		DBCursor cursor = table.find().skip(0).limit(1);
		logger.info("cursor Name" + cursor);
		Map<String, Map<String, String>> collectionData = extractCollectionData(cursor);
		return collectionData;

	}

	/**
	 * Method to retrieve 'like' query data.
	 * 
	 * @param columnName
	 *            Column name on which we apply like query.
	 * @param value
	 *            Column specific value.
	 * @return Database query on 'like' condition.
	 */
	public static DBObject createMongoDBQuery(String columnName, String value) {
		DBObject likeQuery = new BasicDBObject();
		Pattern columnValue = Pattern.compile(value);
		likeQuery.put(columnName, columnValue);
		return likeQuery;
	}

	/**
	 * Method to retrieve 'in' query data.
	 * 
	 * @param columnName
	 *            Column name on which to apply query.
	 * @param values
	 *            Column specific multiple values.
	 * @return Database query on 'in' condition.
	 */
	public static DBObject createMongoDBQuery(String columnName, String[] values) {

		DBObject inQuery = new BasicDBObject(columnName, new BasicDBObject(
				"$in", values));
		return inQuery;
	}

	/**
	 * Method to retrieve 'greater than' and 'less than' query data.
	 * 
	 * @param columnName
	 *            Column name on which we apply query.
	 * @param greaterThanVar
	 *            Column specific value which is greater than.
	 * @param lessThanVar
	 *            Column specific value which is less than.
	 * @return Database query on 'greater than' and 'less than' condition.
	 */
	public static DBObject createMongoDBQuery(String columnName,
			String greaterThanVar, String lessThanVar) {
		DBObject betweenQuery;

		if (greaterThanVar.isEmpty()) {
			betweenQuery = new BasicDBObject(columnName, new BasicDBObject(
					"$lt", lessThanVar));
		} else if (lessThanVar.isEmpty()) {
			betweenQuery = new BasicDBObject(columnName, new BasicDBObject(
					"$gt", greaterThanVar));
		} else {
			betweenQuery = new BasicDBObject(columnName, new BasicDBObject(
					"$gt", greaterThanVar).append("$lt", lessThanVar));
		}
		// DBCursor cursor = coll.find(query, fields);
		return betweenQuery;
	}

	/**
	 * Method to retrieve data for specific rows.
	 * 
	 * @return Database conditional query.
	 */
	public static DBObject createMongoDBQuery() {

		BasicDBObject dbObject = null;
		// providing condition which are put in queryParameterMap
		dbObject = new BasicDBObject(queryParameterMap);

		return dbObject;
	}

	/**
	 * Method to insert data.
	 * 
	 * @param localPath
	 *            Path of file from which data is to be extracted.
	 * @param fileName
	 *            Name of file name.
	 * @throws IOException
	 */
	public static void insertDataInMongoDB(String localPath, String fileName)
			throws IOException {
		DBObject dbObject = null;
		// Getting data to be inserted from a file.
		String readlocationFeed = FileIO.readExistingLocalFile(localPath,
				fileName);

		DB dbObject2 = createMongoDBConnection();

		DBCollection collection = dbObject2.getCollection("demo");

		// convert JSON to DBObject directly

		String collectionObjects = readlocationFeed.replace("},", "}");
		StringTokenizer collectionobject = new StringTokenizer(
				collectionObjects, "}");
		String[] collectionObjectArray = new String[collectionobject
				.countTokens()];
		int i = 0;
		collectionObjectArray[i] = collectionobject.nextToken() + "}";
		while (collectionobject.hasMoreTokens()) {

			dbObject = (DBObject) JSON.parse(collectionObjectArray[i]);
			collection.insert(dbObject);

			collectionObjectArray[i] = collectionobject.nextToken() + "}";
		}
	}

	/**
	 * Method to retrieve data from a file and delete records.
	 * 
	 * @param localPath
	 *            Path of file.
	 * @param fileName
	 *            FileName from which existing data to be deleted.
	 * @throws IOException
	 */
	public static void DeleteDataFromMongoDB(String localPath, String fileName)
			throws IOException {
		DBObject dbObject = null;
		String readlocationFeed = FileIO.readExistingLocalFile(localPath,
				fileName);

		DB dbObject2 = createMongoDBConnection();

		DBCollection collection = dbObject2.getCollection("demo");

		// convert JSON to DBObject directly

		String collectionobjects = readlocationFeed.replace("},", "}");
		StringTokenizer collectionObject = new StringTokenizer(
				collectionobjects, "}");
		String[] collectionObjectArray = new String[collectionObject
				.countTokens()];
		int i = 0;
		collectionObjectArray[i] = collectionObject.nextToken() + "}";
		while (collectionObject.hasMoreTokens()) {

			dbObject = (DBObject) JSON.parse(collectionObjectArray[i]);

			collection.remove(dbObject);
			collectionObjectArray[i] = collectionObject.nextToken() + "}";
		}

	}

	/**
	 * Method to assert that given columns names exists in particular
	 * collection.
	 * 
	 * @param collectionName
	 *            Collection name for which columns to be assert.
	 * @param matchColumnsName
	 *            String array of column names.
	 * @param testInfo
	 *            Test case information.
	 */
	public static void assertColumnNames(String collectionName,
			String[] matchColumnsName, Object testInfo) {
		boolean flag = true;
		// Getting one row of collection.
		Map<String, Map<String, String>> collectionData = MongoDbUtility
				.getOneRowCollectionData(collectionName);
		Map<String, String> dbData = new LinkedHashMap<String, String>();
		Set<String> collectionNameSet = collectionData.keySet();
		for (Object object : collectionNameSet) {
			// Extracting columns name of collection.
			dbData = collectionData.get(object);
		}
		for (int i = 0; i < matchColumnsName.length - 1; i++) {

			// Checking specified column name in all collection columns name.
			if (!(dbData.containsKey(matchColumnsName[i]))) {
				flag = false;
			}
		}
		Assert.assertEquals(flag, true, "Both values are Equals", testInfo);
	}

	/**
	 * Method to retrieve count of records.
	 * 
	 * @param collectionName
	 *            Count the records in the collection.
	 * @return Number of records in the collection.
	 */
	public static int assertdataCount(String collectionName) {
		DBCollection collection = dbConnection.getCollection(collectionName);
		return collection.find().count();

	}

}

// ************************************************ END
// *******************************************