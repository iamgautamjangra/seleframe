package selenium.fonantrix.app.testcases;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import selenium.fonantrix.core.database.MySqlUtility;
import selenium.fonantrix.core.parse.JsonParser;
import selenium.fonantrix.core.rest.RestUtils;
import selenium.fonantrix.core.util.Assert;
import selenium.fonantrix.core.util.ConfigurationMap;
import selenium.fonantrix.core.util.TestInfo;
import selenium.fonantrix.core.web.WebUtils;

public class CampaignManager {
	private WebUtils WebUtils;

	@Parameters({ "platform", "browser", "version", "Appurl", "Nodeurl" })
	@BeforeClass
	public void SetUp(String platform, String browser, String version,
			String Appurl, String Nodeurl) throws Exception {
		ConfigurationMap.loadConfigurations(System.getProperty("user.dir")
				+ "\\config\\configuration.properties");
		PropertyConfigurator.configure(System.getProperty("user.dir")
				+ "\\config\\log4j.properties");

		WebUtils = new WebUtils(platform, browser, Nodeurl);
		WebUtils.openBrowsser(Appurl, platform);
		WebUtils.wait(5);

	}

	@AfterClass
	public void Close() throws IOException {

		WebUtils.closeBrowser();

	}

	// Testcase to validate the UI controls on Login Page and validating login
	// functionality with valid credentials

	@Parameters({ "ModuleName" })
	@Test(description = "CM_003", priority = 1)
	public void Login_ValidateUI(String ModuleName) {
		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("Login_ValidateUI");

		// Validating the existence of UserName textbox
		WebUtils.assertExist("Login", "txt_UserName", TestInfo);
		// Fetching the UserName from Configuration file
		WebUtils.setValue("Login", "txt_UserName",
				ConfigurationMap.getProperty("appLoginUserName"));
		WebUtils.wait(2);
		// Validating the existence of Password textbox
		WebUtils.assertExist("Login", "txt_Password", TestInfo);
		// Fetching the Password from Configuration file
		WebUtils.setValue("Login", "txt_Password",
				ConfigurationMap.getProperty("appLoginPassword"));
		WebUtils.wait(2);
		// Validating the existence of Login button
		WebUtils.assertExist("Login", "btn_Login", TestInfo);
		// Click on Login button
		WebUtils.press("Login", "btn_Login");

	}

	// Testcase to validate the UI controls on Edit Client Page
	@Parameters({ "platform", "ModuleName" })
	@Test(description = "Client_Page_01", priority = 2)
	public void HoustonClient_Page_ValidateUI(String platform, String ModuleName) {
		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("HoustonClient_Page_ValidateUI");

		// Navigating to ClientPage by fetching clientURL from Configuration
		// file
		WebUtils.navigateTo(ConfigurationMap.getProperty("ClientUrl"));
		WebUtils.wait(2);
		// Click on clientId in Client table and navigating to Edit Client Page
		WebUtils.press("CampaignManager", "lnk_ClientID");
		WebUtils.wait(5);

		// Click on expand/collapse link on Client Page if selected platform is
		// Selendroid.
		if (platform.equalsIgnoreCase("Selendroid")) {

			WebUtils.press("CampaignManager", "lnk_Collapse");
			WebUtils.wait(2);
		}

		// Validating the existence of link on Edit Client Header
		WebUtils.assertExist("CampaignManager", "lnk_Campaign", TestInfo);
		WebUtils.assertExist("CampaignManager", "lnk_Reports", TestInfo);
		WebUtils.assertExist("CampaignManager", "lnk_Analytics", TestInfo);
		WebUtils.assertExist("CampaignManager", "lnk_UserGuide", TestInfo);
		WebUtils.wait(2);

		// Validating the existence of UI controls in Edit Client section
		WebUtils.assertExist("CampaignManager", "lbl_EditClient", TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_ClientId", TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_ClientName", TestInfo);
		WebUtils.assertExist("CampaignManager", "txt_ClientName", TestInfo);
		WebUtils.wait(2);
		WebUtils.assertExist("CampaignManager", "lbl_DateCreated", TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_LastModified", TestInfo);
		WebUtils.assertExist("CampaignManager", "lnk_ViewLog", TestInfo);

		// Validating the existence of UI controls in Advertiser Table
		WebUtils.assertExist("CampaignManager", "lnk_Advertisers", TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_Adv_AdvertiserId",
				TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_Adv_AdvertiserName",
				TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_Adv_Campaigns", TestInfo);
		WebUtils.assertExist("CampaignManager", "cbx_Adv_AdvId_1", TestInfo);
		WebUtils.assertExist("CampaignManager", "cbx_Adv_AdvId_2", TestInfo);
		WebUtils.assertExist("CampaignManager", "cbx_Adv_AdvId_3", TestInfo);
		WebUtils.assertExist("CampaignManager", "lst_Adv_Actions", TestInfo);
		WebUtils.assertExist("CampaignManager", "btn_Adv_Go", TestInfo);

	}

	// Testcase to validate the UI controls and actions of Advertisers table on
	// Edit Client Page
	@Parameters({ "ModuleName" })
	@Test(description = "Client_Page_09", priority = 3)
	public void HoustonClient_Page_AdvertisersTable_ValidateUI(String ModuleName) {

		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("HoustonClient_Page_AdvertisersTable_ValidateUI");

		// Navigating to ClientPage by fetching clientURL from Configuration
		// file
		WebUtils.navigateTo(ConfigurationMap.getProperty("ClientUrl"));
		WebUtils.wait(2);
		// Click on clientId in Client table and navigating to Edit Client Page
		WebUtils.press("CampaignManager", "lnk_ClientID");
		WebUtils.wait(2);
		WebUtils.assertExist("CampaignManager", "lnk_Advertisers", TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_Adv_AdvertiserId",
				TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_Adv_AdvertiserName",
				TestInfo);

		// Selecting first 3 advertisers and deactivate them using Deactivate
		// action in Action List
		WebUtils.press("CampaignManager", "cbx_Adv_AdvId_1");
		WebUtils.press("CampaignManager", "cbx_Adv_AdvId_2");
		WebUtils.press("CampaignManager", "cbx_Adv_AdvId_3");
		WebUtils.assertExist("CampaignManager", "lst_Adv_Actions", TestInfo);
		WebUtils.wait(2);
		WebUtils.press("CampaignManager", "lst_Adv_Actions");
		WebUtils.wait(2);
		WebUtils.press("CampaignManager", "lst_Adv_Actions_Deactivate");
		WebUtils.wait(2);
		WebUtils.press("CampaignManager", "btn_Adv_Go");

	}

	// Testcase to validate functionality of Collapse/Expand icon above the
	// Advertisers table on Edit Client Page
	@Parameters({ "ModuleName" })
	@Test(description = "Client_Page_19", priority = 5)
	public void HoustonClientEdit_Page_Validate_Advertisers_Link(
			String ModuleName) {

		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("HoustonClientEdit_Page_Validate_Advertisers_Link");

		// Navigating to ClientPage by fetching clientURL from Configuration
		// file
		WebUtils.navigateTo(ConfigurationMap.getProperty("ClientUrl"));
		WebUtils.wait(2);
		// Click on clientId in Client table and navigating to Edit Client Page
		WebUtils.press("CampaignManager", "lnk_ClientID");
		WebUtils.wait(2);
		// Click on Collapse/Expand icon to hide Advertisers table
		WebUtils.press("CampaignManager", "lnk_Advertisers");
		WebUtils.wait(3);
		// Validating the existence of UI controls in Advertisers table
		WebUtils.assertNotExist("CampaignManager", "lbl_Adv_AdvertiserId",
				TestInfo);
		WebUtils.assertNotExist("CampaignManager", "lbl_Adv_AdvertiserName",
				TestInfo);
		WebUtils.assertNotExist("CampaignManager", "lbl_Adv_Campaigns",
				TestInfo);
		WebUtils.assertNotExist("CampaignManager", "lst_Adv_Actions", TestInfo);
		WebUtils.assertNotExist("CampaignManager", "btn_Adv_Go", TestInfo);
		// Click on Collapse/Expand icon to show Advertisers table
		WebUtils.press("CampaignManager", "lnk_Advertisers");
		WebUtils.wait(3);
		// Validating the existence of UI controls in Advertisers table
		WebUtils.assertExist("CampaignManager", "lbl_Adv_AdvertiserId",
				TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_Adv_AdvertiserName",
				TestInfo);
		WebUtils.assertExist("CampaignManager", "lbl_Adv_Campaigns", TestInfo);
		WebUtils.assertExist("CampaignManager", "lst_Adv_Actions", TestInfo);
		WebUtils.assertExist("CampaignManager", "btn_Adv_Go", TestInfo);

	}

	// Testcase to validate Clients table data with Rest api
	@Parameters({ "ModuleName" })
	@Test(description = "Client_Page_20", priority = 6)
	public void HoustonClient_Validate_TableDataWithAPI(String ModuleName)
			throws Exception {

		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("HoustonClient_Validate_TableDataWithAPI");

		// Navigating to ClientPage by fetching clientURL from Configuration
		// file
		WebUtils.navigateTo(ConfigurationMap.getProperty("ClientUrl"));
		WebUtils.wait(5);
		// Fetching the Client table data in a Map
		Map<String, Map<String, String>> uiClientsMap = WebUtils.getTableData(
				"CampaignManager", "tbl_Clients");
		// Adding parameters to a QueryMap
		Map<String, String> queryString = RestUtils.returnQueryStringMap("");
		// Fetching the server response using rest api
		String jsonString = RestUtils.getDataFromWebservice(queryString,
				"clients");
		String[] skipColumns = { "" };
		String[] clientsArrayKeys = { "clients" };
		// Parse the server response(JSON) in a Map
		Map<String, Map<String, String>> apiClientsMap = JsonParser.parseJson(
				clientsArrayKeys, jsonString, skipColumns);
		// Comparing maps containing Client table data and server response
		WebUtils.compareTwoMaps(apiClientsMap, uiClientsMap, TestInfo);

	}

	// Testcase to validate Advertisers table data with Rest api
	@Parameters({ "ModuleName" })
	@Test(description = "Client_Page_21", priority = 7)
	public void EditClient_Advertisers_Validate_TableDataWithAPI(
			String ModuleName) throws Exception {

		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("EditClient_Advertisers_Validate_TableDataWithAPI");

		// Navigating to ClientPage by fetching clientURL from Configuration
		// file
		WebUtils.navigateTo(ConfigurationMap.getProperty("ClientUrl"));
		WebUtils.wait(2);
		// Getting the clientId of first client in Client table
		String clientId = WebUtils.getText("CampaignManager", "lnk_ClientID");
		WebUtils.wait(2);
		// Click on clientId in Client table and navigating to Edit Client Page
		WebUtils.press("CampaignManager", "lnk_ClientID");
		WebUtils.wait(2);
		// Fetching the Advertisers table data in a Map
		Map<String, Map<String, String>> uiAdvertisersMap = WebUtils
				.getTableData("CampaignManager", "tbl_Advertisers");
		// Adding parameters to a QueryMap
		Map<String, String> queryString = RestUtils.returnQueryStringMap("");
		// Fetching the server response using rest api
		String jsonString = RestUtils.getDataFromWebservice(queryString,
				"advertisers/" + clientId);
		String[] skipColumns = { "clientId" };
		String[] advertisersArrayKeys = { "advertises" };
		// Parse the server response(JSON) in a Map
		Map<String, Map<String, String>> apiAdvertisersMap = JsonParser
				.parseJson(advertisersArrayKeys, jsonString, skipColumns);
		// Comparing maps containing Advertisers table data and server response
		WebUtils.compareTwoMaps(apiAdvertisersMap, uiAdvertisersMap, TestInfo);
	}

	// Testcase to validate Clients data from Rest api and database
	@Parameters({ "ModuleName" })
	@Test(description = "Client_Page_22", priority = 8)
	public void HoustonClient_Validate_APIWithDatabase(String ModuleName)
			throws Exception {

		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("HoustonClient_Validate_APIWithDatabase");

		// Adding parameters to a QueryMap
		Map<String, String> queryString = RestUtils.returnQueryStringMap("");
		// Fetching the server response using rest api
		String jsonString = RestUtils.getDataFromWebservice(queryString,
				"clients");
		String[] skipColumns = { "" };
		String[] advertisersArrayKeys = { "clients" };
		// Parse the server response(JSON) in a Map
		Map<String, Map<String, String>> apiClientMap = JsonParser.parseJson(
				advertisersArrayKeys, jsonString, skipColumns);
		String[] skipDBColumns = { "" };
		// Fetching the Clients data from database in a Map
		Map<String, Map<String, String>> dbClientMap = MySqlUtility
				.getDBTableData("SELECT * FROM fonantrix_demo.clients c;",
						skipDBColumns);
		// Comparing maps containing Clients data from Database and server
		// response
		WebUtils.compareTwoMaps(apiClientMap, dbClientMap, TestInfo);
	}

	// Testcase to validate Advertisers data from Rest api and database
	@Parameters({ "ModuleName" })
	@Test(description = "Client_Page_23", priority = 9)
	public void EditClient_Advertisers_Validate_APIWithDatabase(
			String ModuleName) throws Exception {

		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("EditClient_Advertisers_Validate_APIWithDatabase");

		// Adding parameters to a QueryMap
		Map<String, String> queryString = RestUtils.returnQueryStringMap("");
		// Fetching the server response using rest api
		String jsonString = RestUtils.getDataFromWebservice(queryString,
				"advertisers/cl1");
		String[] skipColumns = { "clientId" };
		String[] advertisersArrayKeys = { "advertises" };
		// Parse the server response(JSON) in a Map
		Map<String, Map<String, String>> apiAdvertiserMap = JsonParser
				.parseJson(advertisersArrayKeys, jsonString, skipColumns);
		String[] skipDBColumns = { "clientId" };
		// Fetching the Clients data from database in a Map
		Map<String, Map<String, String>> dbAdvertiserMap = MySqlUtility
				.getDBTableData(
						"SELECT * FROM fonantrix_demo.advertisers a where clientId='cl1';",
						skipDBColumns);
		// Comparing maps containing Advertisers data from Database and server
		// response
		WebUtils.compareTwoMaps(apiAdvertiserMap, dbAdvertiserMap, TestInfo);
	}

	// Testcase to validate the changes on Edit Client Page with Rest api
	@Parameters({ "ModuleName" })
	@Test(description = "Client_Page_24", priority = 10)
	public void HoustonClient_Page_ValidateEditClientWithAPI(String ModuleName)
			throws Exception {
		TestInfo TestInfo = new TestInfo();
		TestInfo.setModuleName(ModuleName);
		TestInfo.setFlowName("CampaignManager");
		TestInfo.setTCName("HoustonClient_Page_ValidateEditClientWithAPI");

		String clientNewName = "CampaignManager";

		// Navigating to ClientPage by fetching clientURL from Configuration
		// file
		WebUtils.navigateTo(ConfigurationMap.getProperty("ClientUrl"));
		WebUtils.wait(2);
		// Getting the clientId of first client in Client table
		String clientId = WebUtils.getText("CampaignManager", "lnk_ClientID");
		// Click on clientId in Client table and navigating to Edit Client Page
		WebUtils.press("CampaignManager", "lnk_ClientID");
		WebUtils.wait(5);

		// Validating the existence of UI controls in Edit Client page
		WebUtils.assertExist("CampaignManager", "lbl_ClientName", TestInfo);
		WebUtils.assertExist("CampaignManager", "txt_ClientName", TestInfo);
		WebUtils.wait(2);

		// Renaming a existing Client
		WebUtils.setValue("CampaignManager", "txt_ClientName", clientNewName);
		WebUtils.wait(5);

		// Click on Save changes after changing the Client Name
		WebUtils.press("CampaignManager", "btn_SaveChanges");
		WebUtils.wait(2);

		// Adding parameters to a QueryMap
		Map<String, String> queryString = RestUtils.returnQueryStringMap(String
				.format("name=%s", clientNewName));
		// Fetching the server response using rest api
		String jsonString = RestUtils.getDataFromWebservice(queryString,
				"clients/edit/" + clientId);
		String columnName = "name";
		String[] editClientsArrayKeys = { "client" };
		// Parse the server response(JSON) in a String
		String apiEditClient = JsonParser.parseJsonString(editClientsArrayKeys,
				jsonString, columnName);
		// Extracting the Client Name from Map containing server response

		// Matching given client name with name fetched from rest api
		if (apiEditClient.equals(clientNewName))

			Assert.assertEquals(
					true,
					true,
					"Client name returned from rest api matched with client name in Client table.",
					TestInfo);

		else
			Assert.assertEquals(
					false,
					true,
					"Client name returned from rest api not matched with client name in Client table.",
					TestInfo);
	}
}
