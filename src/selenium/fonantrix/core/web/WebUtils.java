package selenium.fonantrix.core.web;

import io.selendroid.SelendroidCapabilities;
import io.selendroid.SelendroidDriver;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selenium.fonantrix.app.constants.Constants;
import selenium.fonantrix.core.database.MySqlUtility;
import selenium.fonantrix.core.util.Assert;
import selenium.fonantrix.core.util.ORUtils;
import selenium.fonantrix.core.util.RequestUtil;

import com.opera.core.systems.OperaDriver;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains utility methods for web elements.
 *          </p>
 */
public class WebUtils {
	private WebDriver driver;
	private List<String> listValues = new LinkedList<String>();
	private final Logger logger = LoggerFactory.getLogger(WebUtils.class
			.getName());
	private DesiredCapabilities capability;

	public WebUtils() {

	}

	/**
	 * @param platforms
	 *            OS name where test cases will be executed.
	 * @param browser
	 *            Browser name on which test cases will be executed.
	 * @param nodeURL
	 *            URL of the machine where test cases will be executed.
	 * @throws Exception
	 */
	public WebUtils(String platforms, String browser, String nodeURL)
			throws Exception {

		if (nodeURL == "") {
			// Set Path for the executable file
			if (browser.equalsIgnoreCase("firefox")) {
				driver = new FirefoxDriver();
			} else if (browser.equalsIgnoreCase("chrome")) {

				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir")
								+ "\\config\\chromedriver.exe");
				driver = new ChromeDriver();
			} else if (browser.equalsIgnoreCase("internetexplorer")) {

				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir")
								+ "\\config\\IEDriverServer.exe");
				driver = new InternetExplorerDriver();
			} else if (browser.equalsIgnoreCase("safari")) {

				driver = new SafariDriver();
			} else if (browser.equalsIgnoreCase("opera")) {
				driver = new OperaDriver();
			} else if (platforms.equalsIgnoreCase("Selendroid")) {
				driver = new SelendroidDriver(SelendroidCapabilities.android());
			}
		} else {

			if (browser.equalsIgnoreCase("firefox")) {
				capability = DesiredCapabilities.firefox();
				capability.setBrowserName(browser);
				capability.setPlatform(Platform.VISTA);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
			} else if (browser.equalsIgnoreCase("chrome")) {
				capability = DesiredCapabilities.chrome();
				capability.setPlatform(Platform.VISTA);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
			} else if (browser.equalsIgnoreCase("internetexplorer")) {
				capability = DesiredCapabilities.internetExplorer();
				capability.setPlatform(Platform.VISTA);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
			} else if (browser.equalsIgnoreCase("safari")) {
				capability = DesiredCapabilities.safari();
				if (platforms.equalsIgnoreCase("MAC"))
					capability.setPlatform(Platform.MAC);
				else
					capability.setPlatform(Platform.VISTA);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
			} else if (browser.equalsIgnoreCase("opera")) {
				capability = DesiredCapabilities.opera();
				capability.setPlatform(Platform.VISTA);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
			} else if (platforms.equalsIgnoreCase("Selendroid")) {
				capability = DesiredCapabilities.android();
				capability.setPlatform(Platform.ANDROID);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
			}

		}
	}

	/**
	 * Method to open a new web browser.
	 * 
	 * @param appURL
	 *            URL address that to be opened.
	 * @param platforms
	 *            OS name where test cases will be executed.
	 */
	public void openBrowsser(String appURL, String platforms) {
		logger.info(String.format("openBrowser() : Opening URL : %s", appURL));
		driver.get(appURL);
		wait(2);
		if (!platforms.equalsIgnoreCase("Selendroid")) {
			logger.info("openBrowser() : Browser is Maximized");
			driver.manage().window().maximize();

			driver.manage()
					.timeouts()
					.implicitlyWait(Constants.ImplicitWaitTime,
							TimeUnit.SECONDS);
		} else
			wait(10);

	}

	/**
	 * Method to create driver object for selendroid.
	 * 
	 * @throws Exception
	 */
	public void openSelendroid() throws Exception {
		SelendroidCapabilities capa = new SelendroidCapabilities(
				"com.flipkart.android:2.2.5");
		driver = new SelendroidDriver(capa);
	}

	/**
	 * Method to verify title of the web page.
	 * 
	 * @param expTitle
	 *            Expected title to be present.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertBrowserTitle(String expTitle, Object testInfo) {
		logger.info(String.format(
				"assertBrowserTitle() : Expected Title is : %s", expTitle));
		String browserTitle = driver.getTitle();
		Assert.assertEquals(
				expTitle.equals(browserTitle),
				true,
				String.format(
						"assertBrowserTitle() : Expected Title is : %s Actual Title is : %s",
						expTitle, browserTitle), testInfo);
	}

	/**
	 * Method to retrieve title of web page.
	 * 
	 * @return Title of page.
	 */
	public String getBrowserTitle() {
		logger.info(String.format("getBrowserTitle() : Browser Title is : %s",
				driver.getTitle()));
		return driver.getTitle();
	}

	/**
	 * Method to verify URL of web page.
	 * 
	 * @param expURL
	 *            Expected URL to be present.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertURL(String expURL, Object testInfo) {
		logger.info(String.format("assertURL() : Expected URL is : %s", expURL));
		String currentURL = driver.getCurrentUrl();
		Assert.assertEquals(expURL.equals(currentURL), true, String.format(
				"assertURL() : Expected URL is : %s Actual URL is : %s",
				expURL, currentURL), testInfo);
	}

	/**
	 * Method to retrieve URL of web page.
	 * 
	 * @return URL of page.
	 */
	public String getURL() {
		logger.info(String.format("getURL() : Current URL is : %s",
				driver.getCurrentUrl()));
		return driver.getCurrentUrl();
	}

	/**
	 * Method to refresh browser.
	 */
	public void refreshBrowser() {
		logger.info("refreshBrowser() : Browser Refreshed");
		driver.navigate().refresh();
	}

	/**
	 * Method to go one step back by clicking back button.
	 */
	public void browserBackClick() {
		logger.info("browserBackClick() : Browser Moved one step Back in history");
		driver.navigate().back();
	}

	/**
	 * Method to go one step forward by clicking forward button.
	 */
	public void browserForwardClick() {
		logger.info("browserForwardClick() : Browser Moved one step Forward in history");
		driver.navigate().forward();
	}

	/**
	 * Method to navigate web browser to given URL.
	 * 
	 * @param URL
	 *            URL address to be navigated.
	 */
	public void navigateTo(String URL) {
		logger.info(String.format("navigateTo() : Navigating Browser to : %s",
				URL));
		driver.navigate().to(URL);
	}

	/**
	 * Method to click on an element.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 */
	public void press(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String.format(
				"press() : Element : %s in Page : %s is Pressed", objectName,
				pageName));
		driver.findElement(element).click();
	}

	/**
	 * Method to enter text in TextBox element.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param value
	 *            Text value to be entered in text box.
	 */
	public void setValue(String pageName, String objectName, String value) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("setValue() : Entering Value : %s for Element : %s in Page : %s ",
						value, objectName, pageName));
		driver.findElement(element).clear();
		driver.findElement(element).sendKeys(value);
	}

	/**
	 * Method to append new text with already presented text in text box element.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param textToAppend
	 *            Text value to be appended.
	 * @param testInfo
	 *            Test case information.
	 */
	public void appendText(String pageName, String objectName,
			String textToAppend, Object testInfo) {
		logger.info(String
				.format("appendText() : Appending Text Value : %s For Element : %s in Page : %s ",
						textToAppend, objectName, pageName));
		By element = ORUtils.ORGenerator(pageName, objectName);
		String presentText = driver.findElement(element).getText();
		logger.info(String
				.format("appendText() : Text that already present is : %s",
						presentText));
		textToAppend = presentText + textToAppend;
		logger.info(String
				.format("appendText() : Newly entered text value is : %s",
						textToAppend));
		driver.findElement(element).sendKeys(textToAppend);
	}

	/**
	 * Method to clear text value in text box.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 */
	public void clearText(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String.format(
				"remove() : Removing value for element : %s in page : %s ",
				objectName, pageName));
		driver.findElement(element).clear();
	}

	/**
	 * Method to verify text present in an element.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param expText
	 *            Expected text to be present in element.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertText(String pageName, String objectName, String expText,
			Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String.format(
				"assertText() : Asserting text for Element : %s in Page : %s",
				objectName, pageName));
		String actualText = driver.findElement(element).getText();
		Assert.assertEquals(
				expText.equals(actualText),
				true,
				String.format(
						"assertText() : Expected Text for Element %s in page %s is :%s Actual Text is :%s",
						objectName, pageName, expText, actualText), testInfo);
	}

	/**
	 * Method to verify text present on element for specified no of character.
	 * 
	 * @param pageName
	 *            Name of sheet used in OR.
	 * @param objectName
	 *            Name of object for which substring to be matched.
	 * @param expText
	 *            Text which is to be matched.
	 * @param noOfChars
	 *            Number of character to be matched.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertNoofChars(String pageName, String objectName,
			String expText, int noOfChars, Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertNoofChars() : Asserting text for Element : %s in Page : %s",
						objectName, pageName));
		String actualText = driver.findElement(element).getText()
				.substring(0, noOfChars);
		expText = expText.substring(0, noOfChars);
		Assert.assertEquals(
				expText.equals(actualText),
				true,
				String.format(
						"assertNoofChars() : Expected Text for Element %s in page %s is :%s Actual Text is :%s",
						objectName, pageName, expText, actualText), testInfo);
	}

	/**
	 * Method to verify text present in element by passing attribute name.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param attributeText
	 *            Value to verify.
	 * @param expValue
	 *            String value to be verified.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertAttributeValue(String pageName, String objectName,
			String attributeText, String expValue, Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertAttributeValue() : Asserting %s Attribute Value  for Element : %s in Page : %s",
						attributeText, objectName, pageName));
		String actualValue = driver.findElement(element).getAttribute(
				attributeText);
		Assert.assertEquals(
				expValue.equals(actualValue),
				true,
				String.format(
						"assertText() : Expected Text for Element %s in page %s is :%s Actual Text is :%s",
						objectName, pageName, expValue, actualValue), testInfo);
	}

	/**
	 * Method to get text present in element by passing attribute name.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls File.
	 * @param objectName
	 *            Element's logical Name.
	 * @param attributeText
	 *            Name of attribute for which value to be extracted.
	 * @return Extracted value as a string.
	 */
	public String getAttributeValue(String pageName, String objectName,
			String attributeText) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("getAttributeValue() : Getting %s Attribute Value  for Element : %s in Page : %s",
						attributeText, objectName, pageName));
		String actualValue = driver.findElement(element).getAttribute(
				attributeText);
		return actualValue;
	}

	/**
	 * Method to verify selected option in DropDown list.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param expText
	 *            Expected value to be selected.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertListText(String pageName, String objectName,
			String expText, Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertListText() : Asserting selected value : %s for list Element : %s in page : %s",
						expText, objectName, pageName));
		Select option = new Select(driver.findElement(element));
		String actualText = option.getFirstSelectedOption().getText();
		Assert.assertEquals(
				expText.equals(actualText),
				true,
				String.format(
						"assertListText() : Expected Value for list Element %s in page %s is : %s Actual Value is : %s",
						objectName, pageName, expText, actualText), testInfo);
	}

	/**
	 * Method to get current date of system.
	 * 
	 * @return System date in string format.
	 */
	public String getCurrentDate() {
		String strDate = null;
		try {
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
			Date now = new Date();
			strDate = sdfDate.format(now);

		} catch (Exception e) {
			logger.error(String
					.format("Exception while formatting the Current Date, Time and exception details are %s",
							RequestUtil.stackTraceToString(e)));
		}
		return strDate;
	}

	/**
	 * Method to increment specified number of days to current date.
	 * 
	 * @param noOfDays
	 *            Integer value to be incremented.
	 * @return Incremented date in string format.
	 */
	@SuppressWarnings("deprecation")
	public String incrementCurrentDate(int noOfDays) {
		Date newDate = new Date();
		newDate.setDate(newDate.getDate() + noOfDays);
		SimpleDateFormat FormattedDATE = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = FormattedDATE.format(newDate);
		return formattedDate;
	}

	/**
	 * Method to create a new <code>HashMap</code> that has all of the elements
	 * of <code>uiFirstMap</code> and <code>uiSecondMap</code> (on key
	 * collision, the latter override the former).
	 * 
	 * @param uiFirstMap
	 *            Fist hashmap to merge.
	 * @param uiSecondMap
	 *            Second hashmap to merge.
	 * @return New hashmap.
	 */
	public Map<String, Map<String, String>> mergeMaps(
			Map<String, Map<String, String>> uiFirstMap,
			Map<String, Map<String, String>> uiSecondMap) {
		Map<String, Map<String, String>> mergedMap = new LinkedHashMap<String, Map<String, String>>();
		mergedMap.putAll(uiFirstMap);
		int uiSecondMapKey = uiFirstMap.size();
		for (Map.Entry<String, Map<String, String>> uiSecondEntryMap : uiSecondMap
				.entrySet()) {
			uiSecondMapKey = uiSecondMapKey + 1;
			mergedMap.put("row" + uiSecondMapKey, uiSecondEntryMap.getValue());
		}
		return mergedMap;
	}

	/**
	 * Method to return text present in an element.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @return Text present in element.
	 */
	public String getText(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		String actualText = driver.findElement(element).getText();
		logger.info(String
				.format("getText() : Returning text present for Element : %s in page : %s is : %s ",
						objectName, pageName, actualText));
		return actualText;
	}

	/**
	 * Method to verify values in the list.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param tableName
	 *            Name of table for which list options is to be extracted.
	 * @param columnName
	 *            Name of column for which list options is to be extracted.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertListOptions(String pageName, String objectName,
			String tableName, String columnName, Object testInfo) {
		logger.info(String
				.format("assertListOptions() : Asserting Options in List Box Object : %s in page : %s ",
						objectName, pageName));
		List<String> listOptions = new LinkedList<String>();

		By element = ORUtils.ORGenerator(pageName, objectName);
		WebElement listBox = driver.findElement(element);
		List<WebElement> listOfOptions = listBox.findElements(By
				.tagName("option"));
		for (WebElement item : listOfOptions) {
			listOptions.add(item.getText());
		}
		logger.info(String
				.format("assertListOptions() : List Options displaying in listbox object is : %s",
						listOptions));
		List<String> dbList = MySqlUtility.getColumnList(tableName, columnName);
		compareList(dbList, listOptions, testInfo);
	}

	/**
	 * Method to verify an element is present or not.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertExist(String pageName, String objectName, Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertExist() : Checking Existance of Element : %s in Page : %s",
						objectName, pageName));
		boolean flag = driver.findElement(element).isDisplayed();
		Assert.assertEquals(flag, true, String.format(
				"assertExist() : Element %s in page %s is not appearing",
				objectName, pageName), testInfo);
	}

	/**
	 * Method to return boolean value based on existence of element.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @return Boolean value based on existence of element.
	 */
	public boolean getElementExist(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("getElementExist() : Returning Existance of Element : %s in Page : %s",
						objectName, pageName));
		boolean flag = driver.findElement(element).isDisplayed();
		return flag;
	}

	/**
	 * Method to switch to a specified frame.
	 * 
	 * @param FrameName
	 *            Frame name to be switched to.
	 */
	public void switchToFrame(String frameName) {
		logger.info(String.format("switchToFrame() : Switching to frame :%s",
				frameName));
		driver.switchTo().frame(frameName);
	}

	/**
	 * Method to switch back to default frame.
	 */
	public void switchToDefault() {
		logger.info("switchToDefault() : Switching to default frame");
		driver.switchTo().defaultContent();
	}

	/**
	 * Method to verify element is enabled or not.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertEnabled(String pageName, String objectName,
			Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertEnabled() : Checking Enabled state of Element %s in Page %s ",
						objectName, pageName));
		boolean flag = driver.findElement(element).isEnabled();
		Assert.assertEquals(flag, true, String.format(
				"assertEnabled() : Element %s in page %s is not Enabled",
				objectName, pageName), testInfo);
	}

	/**
	 * Method to verify element is selected or not.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertSelected(String pageName, String objectName,
			Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertSelected() : Checking Selected state of Element %s in Page %s ",
						objectName, pageName));
		boolean flag = driver.findElement(element).isSelected();
		Assert.assertEquals(flag, true, String.format(
				"assertSelected() : Element %s in page %s is not Selected",
				objectName, pageName), testInfo);
	}

	/**
	 * Method to assert object is not selected.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertNotSelected(String pageName, String objectName,
			Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertNotSelected() : Checking Selected state of Element %s in Page %s ",
						objectName, pageName));
		boolean flag = driver.findElement(element).isSelected();
		Assert.assertEquals(flag, false, String.format(
				"assertNotSelected() : Element %s in page %s is Selected",
				objectName, pageName), testInfo);
	}

	/**
	 * Method to perform mouse over on element.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 */
	public void onMouseOver(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String.format(
				"onMouseOver() : Mouse Over on Element %s in Page %s ",
				objectName, pageName));
		WebElement OnMouseElement = driver.findElement(element);
		Actions builder = new Actions(driver);
		Action mouseover = builder.moveToElement(OnMouseElement).build();
		mouseover.perform();
	}

	/**
	 * Method to select an option from drop down list using text attribute.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param Value
	 *            Option text in text attribute.
	 */
	public void selectDropDownListText(String pageName, String objectName,
			String value) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("selectDropDownListText() : Selecting Value %s from Dropdown Element %s in Page %s",
						value, objectName, pageName));
		Select dropDown = new Select(driver.findElement(element));
		dropDown.selectByVisibleText(value);
	}

	/**
	 * Method to deselect an option from drop down list using text attribute.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param value
	 *            Option value in value attribute.
	 */
	public void deSelectDropDownListText(String pageName, String objectName,
			String value) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("deSelectDropDownListText() : Deselecting Value from Dropdown Element %s in Page %s",
						objectName, pageName));
		Select dropDown = new Select(driver.findElement(element));
		dropDown.deselectByVisibleText(value);
	}

	/**
	 * Method to select an option from drop down list using value attribute.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param value
	 *            Option value in value attribute.
	 */
	public void selectDropDownListValue(String pageName, String objectName,
			String value) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("selectDropDownListValue() : Selecting Value from Dropdown Element %s in Page %s",
						objectName, pageName));
		Select dropDown = new Select(driver.findElement(element));
		dropDown.selectByValue(value);
	}

	/**
	 * Method to deselect an option from drop down list using value attribute.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param value
	 *            Option value in value attribute.
	 */
	public void deSelectDropDownListValue(String pageName, String objectName,
			String value) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("deSelectDropDownListValue() : Deselecting Value from Dropdown Element %s in Page %s",
						objectName, pageName));
		Select dropDown = new Select(driver.findElement(element));
		dropDown.deselectByValue(value);
	}

	/**
	 * Method to verify text displayed on alert.
	 * 
	 * @param expValue
	 *            Expected text to be displayed.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertAlertText(String expValue, Object testInfo) {
		logger.info(String.format(
				"assertAlertText() : Asserting Text Present in Alert %s ",
				expValue));
		String actValue = driver.switchTo().alert().getText();
		Assert.assertEquals(
				expValue.equals(actValue),
				true,
				String.format(
						"assertAlertText() : Expected Text to be displayed on PopUp is : %s Actual Text is : %s ",
						expValue, actValue), testInfo);
	}

	/**
	 * Method to select multiple options from multi-selection box by separating
	 * with semi colon.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param value
	 *            Option Text in text attribute.
	 * @param index
	 *            Index value of the option(s) to be selected.
	 */
	public void selectMultiSelectListBox(String pageName, String objectName,
			String value, String index) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("selectMultiSelectListBox() : Selecting value %s and Value with Index %s from MutltiSelectionBox Element %s in Page %s",
						value, index, objectName, pageName));
		Select dropDown = new Select(driver.findElement(element));
		dropDown.selectByVisibleText(value);
		String[] indexArr = index.split(";");
		for (int i = 0; i < indexArr.length; i++) {
			dropDown.selectByIndex(Integer.parseInt(indexArr[i]));
		}
	}

	/**
	 * Method to explicit wait for visibility of an element.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param numberOfSeconds
	 *            Maximum number of seconds to wait.
	 */
	public void waitElementVisible(String pageName, String objectName,
			int numberOfSeconds) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("waitElementVisible() : Waiting for Visibility of Element %s in Page %s for maximum %s Seconds",
						objectName, pageName, numberOfSeconds));
		WebDriverWait ExplicitWait = new WebDriverWait(driver, numberOfSeconds);
		ExplicitWait.until(ExpectedConditions
				.visibilityOfElementLocated(element));
	}

	/**
	 * Method to wait for given number of seconds.
	 * 
	 * @param numberOfSeconds
	 *            Number of seconds to wait.
	 */
	public void wait(int numberOfSeconds) {
		logger.info(String.format("wait() : Wait for %s Seconds",
				numberOfSeconds));
		try {
			Thread.sleep(numberOfSeconds * 1000);
		} catch (InterruptedException e) {
			logger.error("Exceptions while Waiting are : %s"
					+ RequestUtil.stackTraceToString(e));
		}
	}

	/**
	 * Method to return text search status present in element text.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param searchText
	 *            Search String.
	 * @return Text search status present in element text.
	 */
	public boolean searchText(String pageName, String objectName,
			String searchText) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		String ActText = driver.findElement(element).getText();
		boolean flag = ActText.contains(searchText);
		logger.info(String
				.format("searchText() : Returning Status of searchtext present or not : %s in page : %s is : %s ",
						objectName, pageName, searchText, ActText));
		return flag;
	}

	/**
	 * Method to get status of an element if present or not.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @return True or false based on object existence.
	 */
	public boolean getStatus(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertExist() : Checking Existance of Element : %s in Page : %s",
						objectName, pageName));
		boolean flag = driver.findElement(element).isDisplayed();
		return flag;
	}

	/**
	 * Method to select radio button.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param value
	 *            Radio button value.
	 */
	public void selectRadioButton(String pageName, String objectName, int value) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String.format(
				"selectRadioButton() : Element : %s in Page : %s is Selected",
				objectName, pageName, value));
		List<WebElement> radioGroup = (List<WebElement>) driver
				.findElement(element);

		if (value > 0 && value <= radioGroup.size()) {
			radioGroup.get(value - 1).click();
		} else {
			logger.info(String
					.format("selectRadioButton() : Element : %s in Page : %s is not Selected",
							objectName, pageName, value));
		}
		driver.findElement(element).click();
	}

	/**
	 * Method to select a check box.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 */
	public void SelectCheckBox(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String.format(
				"SelectCheckBox() : Element : %s in Page : %s is Selected",
				objectName, pageName));
		WebElement checkBox = driver.findElement(element);
		if (!checkBox.isSelected())
			checkBox.click();
	}

	/**
	 * Method to verify an element status if present or not.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param expStatus
	 *            Expected status.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertExistStatus(String pageName, String objectName,
			boolean expStatus, Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertExist() : Checking Existance of Element : %s in Page : %s",
						objectName, pageName));
		boolean flag = driver.findElement(element).isDisplayed();
		Assert.assertEquals(flag, expStatus, String.format(
				"assertExist() : Element %s in page %s is not appearing",
				objectName, pageName), testInfo);
	}

	/**
	 * Method to verify an element status if present or not.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertNotExist(String pageName, String objectName,
			Object testInfo) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("assertNotExist() : Checking Element : %s is not Displaying in Page : %s",
						objectName, pageName));
		boolean flag = driver.findElement(element).isDisplayed();
		Assert.assertEquals(flag, false, String.format(
				"assertNotExist() : Element %s in page %s is appearing",
				objectName, pageName), testInfo);
	}

	/**
	 * Method to close active WebDriver opened browsers.
	 */
	public void quitBrowser() {
		logger.info("quitBrowser()");
		driver.quit();
	}

	/**
	 * Method to verify logout entry in audit logs.
	 * 
	 * @param list
	 *            Number of logged users.
	 * @param expValue
	 *            Value to be compared.
	 * @param testInfo
	 *            Test case information.
	 */
	public void assertLogoutListData(List<String> list, String expValue,
			Object testInfo) {
		String actVal = list.get(0);
		boolean flag;
		if (expValue.equals(actVal))
			flag = true;
		else
			flag = false;

		Assert.assertEquals(flag, true,
				"No entry for Logout operation in Audit Logs", testInfo);
	}

	/**
	 * Method to close active WebDriver opened browsers.
	 */
	public void closeBrowser() {
		logger.info("closeBrowser()");
		driver.quit();
	}

	/**
	 * Method to return UI table data in Map.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @return UI table data in Map.
	 */
	public Map<String, Map<String, String>> getTableData(String pageName,
			String objectName) {

		By element = ORUtils.ORGenerator(pageName, objectName);
		logger.info(String
				.format("getTableData() : Returning data for Table Element %s in Page %s",
						objectName, pageName));
		WebElement table = driver.findElement(element);
		List<WebElement> rowCollection = table.findElements(By.tagName("tr"));
		Map<String, Map<String, String>> tableMap = new LinkedHashMap<String, Map<String, String>>();
		int rowNum = 1;
		for (int i = 1; i < rowCollection.size(); i++) {
			List<WebElement> colCollection = rowCollection.get(i).findElements(
					By.xpath("td"));
			int colNum = 1;
			Map<String, String> rowData = new LinkedHashMap<String, String>();
			for (int j = 0; j < colCollection.size(); j++) {
				if (!colCollection.get(j).getText().contains("Actions")) {
					if (!(colCollection.get(j).getText().equals("") || colCollection
							.get(j).getText().contains("Actions"))) {
						rowData.put("column" + colNum, colCollection.get(j)
								.getText());
						colNum++;
					} else {
					}
					tableMap.put("row" + rowNum, rowData);
				}

			}

			rowNum++;
		}
		logger.info(String.format("getTableData() : Data from Table is :",
				tableMap));
		return tableMap;
	}

	/**
	 * Method to compare two maps and verify data in those two maps are same or
	 * not.
	 * 
	 * @param dbTableData
	 *            DataBase table map.
	 * @param uiTableData
	 *            UI table map.
	 * @param testInfo
	 *            Test case information.
	 */

	public void compareTwoMaps(Map<String, Map<String, String>> dbTableData,
			Map<String, Map<String, String>> uiTableData, Object testInfo) {
		logger.info(String
				.format("compareDBAndTblData() : comparing data from two maps, Data in Map1 is %s and Data in Map2 is %s",
						dbTableData, uiTableData));
		boolean flag = true;
		logger.info(String.format(
				"compareDBAndTblData() : Map1 size %s and Map2 size is %s",
				dbTableData.size(), uiTableData.size()));
		if (dbTableData.size() != uiTableData.size())
			flag = false;
		else
			for (Map.Entry<String, Map<String, String>> dbData : dbTableData
					.entrySet()) {
				Map<String, String> dbRow = dbData.getValue();
				Map<String, String> uiRow = uiTableData.get(dbData.getKey());
				List<String> dbList = new ArrayList<String>(dbRow.values());
				List<String> uiList = new ArrayList<String>(uiRow.values());
				Collections.sort(dbList);
				Collections.sort(uiList);

				if (!dbList.equals(uiList)) {
					flag = false;
					break;
				}
			}
		Assert.assertEquals(flag, true, String.format(
				"Data is Different in Two Maps Map1 : %s Map2 : %s ",
				dbTableData, uiTableData), testInfo);
	}

	/**
	 * Method to compare two maps and verify data in those two maps are same or
	 * not.
	 * 
	 * @param uiTableData
	 *            UI table map.
	 * @param dbTableData
	 *            DataBase table map.
	 * @param testInfo
	 *            Test case information.
	 */

	public void compareTwoMapsForEquality(
			Map<String, Map<String, String>> uiTableData,
			Map<String, Map<String, String>> dbTableData, Object TestInfo) {
		logger.info(String
				.format("compareDBAndTblData() : comparing data from two maps, Data in Map1 is %s and Data in Map2 is %s",
						uiTableData, dbTableData));
		boolean flag = true;
		logger.info(String.format(
				"compareDBAndTblData() : Map1 size %s and Map2 size is %s",
				uiTableData.size(), dbTableData.size()));

		for (Map.Entry<String, Map<String, String>> dbData : uiTableData
				.entrySet()) {
			Map<String, String> dbRow = dbData.getValue();
			if (!dbTableData.get(dbData.getKey()).equals("")) {
				if (dbTableData.containsKey(dbData.getKey())) {

					Map<String, String> uiRow = dbTableData
							.get(dbData.getKey());
					List<String> dbList = new ArrayList<String>(dbRow.values());
					List<String> uiList = new ArrayList<String>(uiRow.values());
					Collections.sort(dbList);
					Collections.sort(uiList);

					if (!dbList.equals(uiList)) {

						flag = false;
						break;
					}
				}
			}
		}
		Assert.assertEquals(flag, true, String.format(
				"Data is Different in Two Maps Map1 : %s Map2 : %s ",
				uiTableData, dbTableData), TestInfo);
	}

	/**
	 * Method to get text present on a hidden object.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @return Text for hidden object.
	 * 
	 */
	public String getHiddenText(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		WebElement webElement = driver.findElement(element);
		return (String) ((JavascriptExecutor) driver).executeScript(
				"return jQuery(arguments[0]).text();", webElement);
	}

	/**
	 * Method to press hidden object.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @return Clicks and returns text for hidden object.
	 */
	public String hiddenPress(String pageName, String objectName) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		WebElement webelement = driver.findElement(element);
		return (String) ((JavascriptExecutor) driver).executeScript(
				"return arguments[0].click();", webelement);
	}

	/**
	 * Method to get value of a hidden object of a list.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param attribute
	 *            Text present on list object.
	 * @return Value for hidden object.
	 */
	public String getHiddenAttributeValue(String pageName, String objectName,
			String attribute) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		WebElement webElement = driver.findElement(element);
		return (String) ((JavascriptExecutor) driver).executeScript(
				"return arguments[0].getAttribute('" + attribute + "')",
				webElement);
	}

	/**
	 * Method to assert attribute value of pair objects.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param testInfo
	 *            Test case information.
	 */
	public void asssertAttributeValuePair(String pageName, String objectName,
			Object testInfo) {
		boolean flag;
		int count = 0;
		By element = ORUtils.ORGenerator(pageName, objectName);
		String attributeValuePairs = ORUtils.getAttributeValuePair(pageName,
				objectName);
		String[] attributeValuePair = attributeValuePairs.split(";");
		for (int i = 0; i < attributeValuePair.length; i++) {
			String[] attribute = attributeValuePair[i].split("=");
			String attributeName = attribute[0];
			String actualText = driver.findElement(element).getAttribute(
					attributeName);
			String expectedText = attribute[1].replaceAll("\"", "");
			if (!expectedText.equals(actualText)) {
				count = count + 1;
			}
		}
		if (count > 0) {
			flag = false;
		} else {
			flag = true;
		}
		Assert.assertEquals(
				flag,
				true,
				String.format(
						"asssertAttributeValuePair() : Difference found in object Properties of Object %s in page %s: ",
						objectName, pageName), testInfo);
	}

	/**
	 * Method to select hidden value in DropDown list.
	 * 
	 * @param pageName
	 *            Sheet name of ObjectRepository.xls file.
	 * @param objectName
	 *            Element's logical name.
	 * @param value
	 *            Value to be selected.
	 */
	public void selectHiddenDropDownListText(String pageName,
			String objectName, String value) {
		By element = ORUtils.ORGenerator(pageName, objectName);
		WebElement webelement = driver.findElement(element);
		logger.info(String
				.format("selectHiddenDropDownListText() : Selecting Value %s from Dropdown Element %s in Page %s",
						value, objectName, pageName));
		((JavascriptExecutor) driver).executeScript(
				"return arguments[0].style.display='block';", webelement);
		Select dropDown = new Select(driver.findElement(element));
		dropDown.selectByVisibleText(value);
		((JavascriptExecutor) driver).executeScript(
				"return arguments[0].style.display='none';", webelement);
	}

	/**
	 * Method to compare two lists.
	 * 
	 * @param uiList
	 *            List one to be compared.
	 * @param dbList
	 *            List two to be compared.
	 * @param TestInfo
	 *            Test case information.
	 */
	public void compareList(List<String> uiList, List<String> dbList,
			Object TestInfo) {
		logger.info(String
				.format("compareList() : Comparing Data in two lists List1 : %s List2 : %s",
						uiList, uiList));
		boolean flag = true;
		Collections.sort(uiList);
		Collections.sort(dbList);
		Iterator<String> itr1 = uiList.iterator();
		Iterator<String> itr2 = dbList.iterator();
		while (itr1.hasNext() || itr2.hasNext()) {
			String uiValue = (String) itr1.next();
			String dbValue = (String) itr2.next();
			logger.info(String.format(
					"Value in list1 : %s and value in list2 : %s", uiValue,
					dbValue));
			if (uiValue.equals(dbValue)) {
				logger.info("Both Values are Same");
				flag = true;
			} else {
				logger.info("Both Values are Different");
				flag = false;
			}
		}
		Assert.assertEquals(
				flag,
				true,
				String.format(
						"compareList() : Data is different in two lists List1 : %s List2 : %s",
						uiList, dbList), TestInfo);
	}

}