package selenium.fonantrix.core.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selenium.fonantrix.core.util.Assert;
import selenium.fonantrix.core.util.ConfigurationMap;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains web services functions.
 *          </p>
 */
@SuppressWarnings("deprecation")
public class RestUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(RestUtils.class.getName());
	private static HttpClient httpClient = null;
	private static HttpPost post = null;
	private static HttpResponse httpResponse = null;
	private static HttpEntity resEntity = null;
	private static HttpGet httget = null;

	/**
	 * Method to create Rest API.
	 * 
	 * @param queryString
	 *            Map of request parameters.
	 * @param appendUrl
	 *            Module name which appends base URL.
	 * @return Rest API Http Response.
	 * @throws Exception
	 */
	public static HttpResponse getResponseXml(Map<String, String> queryString,
			String appendUrl) throws Exception {
		ConfigurationMap.loadConfigurations(System.getProperty("user.dir")
				+ "\\config\\configuration.properties");
		String baseURL = ConfigurationMap.getProperty("baseDataUrl");
		String queryParameter = "?";
		for (Map.Entry<String, String> entry : queryString.entrySet()) {
			queryParameter = queryParameter + entry.getKey() + "=" + entry.getValue()
					+ "&";
		}
		@SuppressWarnings("resource")
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httget = new HttpGet(baseURL + appendUrl + queryParameter);

		HttpResponse httpResponse = httpClient.execute(httget);
		logger.info(String.format(
				"getDataFromWebservice - Web Service base URL is: %s", baseURL
						+ appendUrl + queryParameter));
		return httpResponse;
	}

	/**
	 * Method to get XML response in string format.
	 * 
	 * @param queryString
	 *            Map of request parameters.
	 * @param appendUrl
	 *            Module name which appends base URL.
	 * @return Response in string format.
	 * @throws Exception
	 */
	public static String getDataFromWebservice(Map<String, String> queryString,
			String appendUrl) throws Exception {
		HttpResponse response = getResponseXml(queryString, appendUrl);
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		String responseXML = convertStreamToString(stream);
		return responseXML;
	}

	private static final String convertStreamToString(final InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * Method to get XML response in string format.
	 * 
	 * @param response
	 *            String format.
	 * @return Response in string format.
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static String getResponseXml(final HttpResponse response)
			throws IOException, JAXBException {
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		String responseXML = convertStreamToString(stream);
		return responseXML;
	}

	/**
	 * Method to get entity from http response.
	 * 
	 * @param response
	 *            Response as string.
	 * @return Entity value.
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static HttpEntity getEntity(final HttpResponse response)
			throws IOException, JAXBException {
		return response.getEntity();

	}

	/**
	 * Method to get all headers of Rest API.
	 * 
	 * @param queryString
	 *            Map of request parameters.
	 * @param appendUrl
	 *            Module name which appends base URL.
	 * @return Header array.
	 * @throws Exception
	 */
	public static Header[] wsGetAllResponseHeaders(
			Map<String, String> queryString, String appendUrl) throws Exception {

		HttpResponse response = getResponseXml(queryString, appendUrl);
		Header[] Headers = response.getAllHeaders();
		return Headers;
	}

	/**
	 * Method to get expected header value of Rest API.
	 * 
	 * @param queryString
	 *            Map of request parameters.
	 * @param appendUrl
	 *            Module name which appends base URL.
	 * @param headerType
	 *            Expected header name.
	 * @return Header array.
	 * @throws Exception
	 */
	public static Header[] wsGetResponseHeader(Map<String, String> queryString,
			String appendUrl, String headerType) throws Exception {
		HttpResponse response = getResponseXml(queryString, appendUrl);
		Header[] Header = response.getHeaders(headerType);
		return Header;
	}

	/**
	 * Method to get expected header status of Rest API.
	 * 
	 * @param queryString
	 *            Map of request parameters.
	 * @param appendUrl
	 *            Module name which appends base URL.
	 * @param headerType
	 *            Expected header name.
	 * @return True if Exist otherwise false.
	 * @throws Exception
	 */
	public static boolean assertHeaderExistence(
			Map<String, String> queryString, String appendUrl, String headerType)
			throws Exception {
		HttpResponse response = getResponseXml(queryString, appendUrl);
		boolean Headerflag = response.containsHeader(headerType);
		return Headerflag;
	}

	/**
	 * Method to get expected header status of Rest API.
	 * 
	 * @param queryString
	 *            Map of request parameters.
	 * @param appendUrl
	 *            Module name which appends base URL.
	 * @param expVal
	 *            Boolean value.
	 * @throws Exception
	 */
	public static void assertwsResponseStatus(Map<String, String> queryString,
			String appendUrl, boolean expValue, Object testInfo) throws Exception {
		boolean flag = false;
		HttpResponse response = getResponseXml(queryString, appendUrl);
		StatusLine HeadersStatus = response.getStatusLine();

		if (HeadersStatus.toString().contains("200"))
			flag = true;
		else
			flag = false;

		Assert.assertEquals(flag, expValue,
				String.format("Header Status is Matched"), testInfo);
	}

	/**
	 * Method to get Data from Web API.
	 * 
	 * @param queryString
	 *            Map of request parameters.
	 * @param strUrl
	 *            Base URL of API.
	 * @return Response in string format.
	 * @throws Exception
	 */
	public static String getDataFromWebAPI(Map<String, String> queryString,
			String strUrl) throws Exception {

		if (queryString.isEmpty()) {
		} else {
			String queryStng = "?";
			for (Map.Entry<String, String> entry : queryString.entrySet()) {
				queryStng = queryStng + entry.getKey() + "=" + entry.getValue()
						+ "&";
			}
		}

		httpClient = new DefaultHttpClient();
		httget = new HttpGet(strUrl);
		httpResponse = httpClient.execute(httget);

		logger.info(String.format(
				"getDataFromWebservice - Web Service base URL is: %s", strUrl));
		String responseMessage = getResponseXml(httpResponse);
		return responseMessage;
	}

	/**
	 * Method to get Data from Web API.
	 * 
	 * @param strUrl
	 *            Base URL of API.
	 * @return Response in string format.
	 * @throws Exception
	 */
	public static String getDataFromWebAPI(String strUrl) throws Exception {

		httpClient = new DefaultHttpClient();
		httget = new HttpGet(strUrl);
		httpResponse = httpClient.execute(httget);

		logger.info(String.format(
				"getDataFromWebservice - Web Service base URL is: %s", strUrl));
		String responseMessage = getResponseXml(httpResponse);
		logger.info(String
				.format("*******************************************************************************"));
		logger.info(responseMessage);
		logger.info(String
				.format("*******************************************************************************"));

		return responseMessage;
	}

	/**
	 * Method to get status of HTTP response.
	 * 
	 * @return Response status.
	 */
	public static int getStatus() {
		return httpResponse.getStatusLine().getStatusCode();
	}

	/**
	 * Method to get StatusLine of HTTP response.
	 * 
	 * @return Response statusLine.
	 */
	public static StatusLine getStatusLine() {
		return httpResponse.getStatusLine();
	}

	/**
	 * Method to post XML file on Server.
	 * 
	 * @param strURL
	 *            HTTP post URL.
	 * @param strUserName
	 *            User name.
	 * @param strPass
	 *            Password.
	 * @param postFile
	 *            XML file.
	 * @return Response in string format.
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static String sendRequest(String strURL, String strUserName,
			String strPass, String postFile) throws ClientProtocolException,
			IOException, JAXBException {
		httpClient = new DefaultHttpClient();
		post = new HttpPost(strURL);

		String authStr = strUserName + ":" + strPass;
		String authEncoded = Base64.encodeBytes(authStr.getBytes());

		logger.info("----------------------------------------");
		logger.info(authEncoded);

		if (postFile.equalsIgnoreCase("")) {

		} else {
			File file = new File(postFile);
			InputStreamEntity reqEntity = new InputStreamEntity(
					new FileInputStream(file), -1);
			reqEntity.setContentType("application/xml");
			reqEntity.setChunked(true);

			post.setEntity(reqEntity);
		}

		logger.info("----------------------------------------");
		logger.info("Executing request " + post.getRequestLine());

		httpResponse = httpClient.execute(post);
		logger.info(String.format(
				"getDataFromWebservice - Web Service base URL is: %s", strURL));
		resEntity = getEntity(httpResponse);

		if (resEntity != null) {
			logger.info(String.format("Response content length: ",
					resEntity.getContentLength()));
			logger.info(String.format("Chunked?: ", resEntity.isChunked()));
		}
		EntityUtils.consume(resEntity);
		String responseMessage = getResponseXml(httpResponse);
		return responseMessage;
	}

	/**
	 * Method to get entity attributes from HTTP response.
	 * 
	 * @param strOption
	 * 			  Attribute name.
	 * @return Attribute value in string.
	 */
	public static String getEntityAttribute(String strOption) {
		String strValue = "";

		if (resEntity != null) {
			if (strOption.equalsIgnoreCase("Content Length")) {
				strValue = String.valueOf(resEntity.getContentLength());
			} else if (strOption.equalsIgnoreCase("Chuncked")) {
				strValue = String.valueOf(resEntity.isChunked());
			}
		}
		return strValue;
	}

	/**
	 * Method to retrieve HTTP parameters as map from string.
	 * 
	 * @param queryMapValues
	 * 			  HTTP parameters as string.
	 * @return HTTP parameters as map.
	 */
	public static Map<String, String> returnQueryStringMap(String queryMapValues) {
		Map<String, String> queryStringMap = new LinkedHashMap<String, String>();
		if (!queryMapValues.equals("")) {
			String[] queryMapValuesArray = queryMapValues.split(";");
			for (int i = 0; i < queryMapValuesArray.length; i++) {
				String[] fieldName = queryMapValuesArray[i].split("=");

				queryStringMap.put(fieldName[0], fieldName[1]);
			}
		}
		return queryStringMap;
	}
}
