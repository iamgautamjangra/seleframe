package selenium.fonantrix.core.parse;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import selenium.fonantrix.core.web.WebUtils;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains utility functions to parse string response.
 *          </p>
 */
public class XmlParser {

	private static WebUtils webUtils = new WebUtils();

	/**
	 * Method to parse XML response from server.
	 * 
	 * @param xmlRecords
	 *            XML response in string format.
	 * @param tagName
	 *            Name of root node.
	 * @return XML data in map format.
	 * @throws Exception
	 */
	public Map<String, Map<String, String>> parseXmlString(String xmlRecords,
			String tagName) throws Exception {

		xmlRecords = xmlRecords.replaceAll("> <", "><")
				.replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll("\\s{2,}", "");
		DocumentBuilder db = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlRecords));
		Map<String, Map<String, String>> xmlData = new HashMap<String, Map<String, String>>();
		// Parsing String according to node.
		Document doc = db.parse(is);
		// starting from given node
		NodeList nodes = doc.getElementsByTagName(tagName);
		for (int row = 0; row < nodes.getLength(); row++) {
			Map<String, String> rowData = new HashMap<String, String>();
			Node node = nodes.item(row);
			NodeList childNodes = node.getChildNodes();
			for (int column = 0; column < childNodes.getLength(); column++) {

				// Putting data on temporary Map
				rowData.put("column" + column + 1, childNodes.item(column)
						.getFirstChild().getNodeValue());
			}
			// Putting data on final Map
			xmlData.put("row" + row + 1, rowData);
		}
		return xmlData;

	}

	/**
	 * Method to parse XML response with expected node values from server.
	 * 
	 * @param xmlRecords
	 *            XML response in string format.
	 * @param tagName
	 *            Name of root node.
	 * @param tagNameArray
	 * 			  Specific list of tags. 
	 * @return Expected node value in map.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Map<String, Map<String, String>> getNodeValue(
			String xmlRecords, String tagName, String[] tagNameArray)
			throws SAXException, IOException, ParserConfigurationException {
		Map<String, Map<String, String>> XmlData = new LinkedHashMap<String, Map<String, String>>();

		String value = "";

		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		InputSource source = new InputSource();
		source.setCharacterStream(new StringReader(xmlRecords));
		Document doc = docBuilder.parse(source);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName(tagName);
		for (int temp = 0; temp < nodeList.getLength(); temp++) {
			Node node = nodeList.item(temp);
			Map<String, String> tagData = new LinkedHashMap<String, String>();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				for (int i = 0; i < tagNameArray.length; i++) {

					value = element.getElementsByTagName(tagNameArray[i])
							.item(0).getTextContent();
					tagData.put("Column :" + (i + 1), value);
				}
			}
			XmlData.put("Row :" + temp, tagData);
		}
		return XmlData;
	}

	/**
	 * Method to assert XML response from server.
	 * 
	 * @param xmlRecords
	 *            XML response in string format.
	 * @param tagName
	 *            Name of root node.
	 * @param ExpValues
	 * 			  Collection of values.
	 * @param testInfo
	 * 			   Test case information.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void assertXMLContent(String xmlRecords, String tagName,
			Map<String, Map<String, String>> expValues, Object testInfo)
			throws ParserConfigurationException, SAXException, IOException {
		xmlRecords = xmlRecords.replaceAll("> <", "><")
				.replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll("\\s{2,}", "");

		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlRecords));
		Map<String, Map<String, String>> xmlData = new LinkedHashMap<String, Map<String, String>>();
		Document doc = docBuilder.parse(is);
		NodeList nodes = doc.getElementsByTagName(tagName);

		for (int row = 0; row < nodes.getLength(); row++) {
			Map<String, String> rowData = new LinkedHashMap<String, String>();
			Node node = nodes.item(row);
			NodeList childNodes = node.getChildNodes();
			for (int column = 0; column < childNodes.getLength(); column++) {

				rowData.put("column" + column + 1, childNodes.item(column)
						.getFirstChild().getNodeValue());

			}
			xmlData.put("row" + row + 1, rowData);
		}

		webUtils.compareTwoMaps(xmlData, expValues, testInfo);

	}

	/**
	 * Method to assert XML response with expected node values from server.
	 * 
	 * @param xmlRecords
	 *            XML response in string format.
	 * @param tagName
	 *            Name of root node.
	 * @param tagNameArray
	 * 			  Specific list of tags. 
	 * @param expNodeValues
	 * 			  Specific collection of values.
	 * @param testInfo
	 * 			   Test case information.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void assertExpNodeValues(String xmlRecords, String tagName,
			String[] tagNameArray,
			Map<String, Map<String, String>> expNodeValues, Object testInfo)
			throws SAXException, IOException, ParserConfigurationException {
		Map<String, Map<String, String>> xmlData = new LinkedHashMap<String, Map<String, String>>();

		String value = "";

		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		InputSource source = new InputSource();
		source.setCharacterStream(new StringReader(xmlRecords));
		Document doc = docBuilder.parse(source);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName(tagName);
		for (int temp = 0; temp < nodeList.getLength(); temp++) {
			Node node = nodeList.item(temp);
			Map<String, String> tagData = new LinkedHashMap<String, String>();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				for (int i = 0; i < tagNameArray.length; i++) {

					value = element.getElementsByTagName(tagNameArray[i])
							.item(0).getTextContent();
					tagData.put("Column :" + (i + 1), value);
				}
			}
			xmlData.put("Row :" + temp, tagData);
		}
		webUtils.compareTwoMaps(xmlData, expNodeValues, testInfo);

	}

}
