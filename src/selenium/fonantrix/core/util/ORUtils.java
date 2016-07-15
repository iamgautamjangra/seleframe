package selenium.fonantrix.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to retrieve Object/Element property
 *          type and value.
 *          </p>
 */
public class ORUtils {
	// This function returns Reference of WebElement
	private static final Logger logger = LoggerFactory.getLogger(ORUtils.class
			.getName());

	/**
	 * Method to retrieve element property type and value.
	 * 
	 * @param sheetName
	 *            Excel sheet name on which sheet it is to be stored.
	 * @param objectLogicalName
	 *            Element's logical name.
	 * @return Property type included with property value.
	 */
	public static By ORGenerator(String sheetName, String objectLogicalName) {
		String ORFileName = "ObjectRepository.xls";
		By by = null;
		try {
			FileInputStream file = new FileInputStream(new File(
					System.getProperty("user.dir") + "\\config\\" + ORFileName));
			String objectPropertyType = null, objectPropertyValue = null;

			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheet(sheetName);
			;
			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			List<Row> rowContents = new ArrayList<Row>();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// For each row, iterate through each columns
				if (objectLogicalName.equals(row.getCell(0)
						.getStringCellValue())) {
					rowContents.add(row);
					break;
				}
			}
			Row row = null;
			if (rowContents.size() == 1) {
				row = rowContents.get(0);
				objectPropertyType = (String) row.getCell(1)
						.getStringCellValue();
				objectPropertyValue = (String) row.getCell(2)
						.getStringCellValue();
				if (objectPropertyType.equalsIgnoreCase("css")) {
					by = By.cssSelector(objectPropertyValue);
				} else if (objectPropertyType.trim().equalsIgnoreCase("id")) {
					by = By.id(objectPropertyValue);
				} else if (objectPropertyType.equalsIgnoreCase("linkText")) {
					by = By.linkText(objectPropertyValue);
				} else if (objectPropertyType.equalsIgnoreCase("name")) {
					by = By.name(objectPropertyValue);
				} else if (objectPropertyType
						.equalsIgnoreCase("partialLinkText")) {
					by = By.partialLinkText(objectPropertyValue);
				}

				else if (objectPropertyType.equalsIgnoreCase("tagName")) {
					by = By.tagName(objectPropertyValue);
				}

				else if (objectPropertyType.equalsIgnoreCase("xpath")) {

					by = By.xpath(objectPropertyValue);
				} else if (objectPropertyType.equalsIgnoreCase("link")) {

					by = By.linkText(objectPropertyValue);
				}
			} else {
				throw new AssertionError(
						String.format(
								"%s Object in page %s is Not Found in Object Repository",
								objectLogicalName, sheetName));
			}

		} catch (Exception e) {
			logger.error("Exception while fetching object in Object Repository and exception text is: "
					+ RequestUtil.stackTraceToString(e));

		}
		return by;
	}

	/**
	 * Method to retrieve multiple elements property type and value in pair.
	 * 
	 * @param sheetName
	 *            Excel sheet name on which sheet it is to be stored.
	 * @param objectName
	 *            Element's logical name.
	 * @return Property types included with property values in pair.
	 */
	public static String getAttributeValuePair(String sheetName,
			String objectLogicalName) {
		String ORFileName = ConfigurationMap.getProperty("ORFileName");
		String attributeValuePair = null;
		try {
			FileInputStream file = new FileInputStream(new File(
					System.getProperty("user.dir") + "\\config\\" + ORFileName));

			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheet(sheetName);
			;
			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			List<Row> rowContents = new ArrayList<Row>();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// For each row, iterate through each columns
				if (objectLogicalName.equals(row.getCell(0)
						.getStringCellValue())) {
					rowContents.add(row);
					break;
				}
			}
			Row row = null;
			if (rowContents.size() == 1) {
				row = rowContents.get(0);
				attributeValuePair = (String) row.getCell(3)
						.getStringCellValue();
			} else {
				throw new AssertionError(
						String.format(
								"Attribute Value Pairs for Object %s in page %s is Not Found in Object Repository",
								objectLogicalName, sheetName));
			}
		} catch (Exception e) {
			logger.error("Exception while fetching Attribute Value pairs for object in Object Repository and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}
		return attributeValuePair;
	}
}