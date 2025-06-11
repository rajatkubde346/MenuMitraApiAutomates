package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.UdhariLedgerListRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class UdhariLedgerListTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private UdhariLedgerListRequest udhariLedgerListRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(UdhariLedgerListTestScript.class);

    @BeforeClass
    private void udhariLedgerListSetUp() throws customException {
        try {
            LogUtils.info("Udhari Ledger List SetUp");
            ExtentReport.createTest("Udhari Ledger List SetUp");
            ExtentReport.getTest().log(Status.INFO, "Udhari Ledger List SetUp");

            // First do login and get token
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            // Get access token and user ID right after verification
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            
            if (accessToken == null || accessToken.isEmpty()) {
                throw new customException("Access token is null or empty after verification");
            }
            LogUtils.info("Access Token obtained successfully");
            LogUtils.info("User ID obtained: " + user_id);

            Object[][] getUrl = getUdhariLedgerListUrl();
            if (getUrl.length > 0) {
                // Get the endpoint from column 5 (index 4) which should contain the API endpoint
                String endpoint = Objects.toString(getUrl[0][4], "").trim();
                LogUtils.info("Raw endpoint from Excel: " + endpoint);
                
                if (endpoint.isEmpty()) {
                    throw new customException("Endpoint is empty in test data");
                }
                
                // Ensure endpoint starts with /v2/
                if (!endpoint.startsWith("/")) {
                    endpoint = "/" + endpoint;
                }
                if (!endpoint.startsWith("/v2/")) {
                    endpoint = "/v2" + endpoint;
                }
                
                // Construct the full URL
                String fullUrl = baseURI;
                if (fullUrl.endsWith("/")) {
                    fullUrl = fullUrl.substring(0, fullUrl.length() - 1);
                }
                fullUrl = fullUrl + endpoint;
                
                LogUtils.info("Base URI: " + baseURI);
                LogUtils.info("Cleaned Endpoint: " + endpoint);
                LogUtils.info("Full URL: " + fullUrl);
                
                try {
                    url = new URL(fullUrl);
                    LogUtils.info("Successfully created URL: " + url.toString());
                } catch (Exception e) {
                    throw new customException("Failed to create URL from: " + fullUrl + ". Error: " + e.getMessage());
                }
                
                ExtentReport.getTest().log(Status.INFO, "Using URL: " + url.toString());
            } else {
                LogUtils.failure(logger, "No Udhari Ledger List URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No Udhari Ledger List URL found in test data");
                throw new customException("No Udhari Ledger List URL found in test data");
            }

            udhariLedgerListRequest = new UdhariLedgerListRequest();

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in Udhari Ledger List setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in Udhari Ledger List setup: " + e.getMessage());
            throw new customException("Error in Udhari Ledger List setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUdhariLedgerListData")
    public Object[][] getUdhariLedgerListData() throws customException {
        try {
            LogUtils.info("Reading Udhari Ledger List test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading Udhari Ledger List test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "udhariledgerlist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid Udhari Ledger List test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }

            return result;

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Udhari Ledger List test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Udhari Ledger List test data: " + e.getMessage());
            throw new customException("Error in getting Udhari Ledger List test data: " + e.getMessage());
        }
    }

    private Object[][] getUdhariLedgerListUrl() throws customException {
        try {
            LogUtils.info("Reading Udhari Ledger List URL data");
            ExtentReport.getTest().log(Status.INFO, "Reading Udhari Ledger List URL data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching URL data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            String baseUrl = EnviromentChanges.getBaseUrl();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 5 &&
                        "udhariledgerlist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    // Create a copy of the row to avoid modifying the original data
                    Object[] newRow = row.clone();
                    
                    // Store the correct endpoint path
                    newRow[4] = "common/udhari_ledger_list";
                    
                    filteredData.add(newRow);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid Udhari Ledger List URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }

            return result;

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Udhari Ledger List URL data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Udhari Ledger List URL data: " + e.getMessage());
            throw new customException("Error in getting Udhari Ledger List URL data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUdhariLedgerListData")
    public void udhariLedgerListTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting Udhari Ledger List test case: " + testCaseid);
            ExtentReport.createTest("Udhari Ledger List Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("udhariledgerlist")) {
                // Verify we have valid authentication
                if (accessToken == null || accessToken.isEmpty()) {
                    throw new customException("Access token is not available. Please ensure login and verification are successful.");
                }

                // Clean and format the request body
                requestBody = requestBody.replaceAll("\\r\\n|\\r|\\n", "") // Remove all newlines
                                      .replaceAll("\\s+", " ") // Replace multiple spaces with single space
                                      .trim(); // Remove leading/trailing spaces
                
                LogUtils.info("Cleaned Request Body: " + requestBody);
                requestBodyJson = new JSONObject(requestBody);

                // Set request parameters
                if (requestBodyJson.has("ledger_id")) {
                    udhariLedgerListRequest.setLedger_id(requestBodyJson.getString("ledger_id"));
                }
                if (requestBodyJson.has("outlet_id")) {
                    udhariLedgerListRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                }

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                LogUtils.info("Using Access Token: " + (accessToken != null ? "Yes" : "No"));
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                // Make API call with proper authentication
                LogUtils.info("Making request to URL: " + url.toString());
                LogUtils.info("HTTP Method: " + httpsmethod);
                LogUtils.info("Request Headers - Authorization: Bearer " + accessToken);
                
                // Ensure we're using the correct HTTP method
                httpsmethod = httpsmethod.toUpperCase();
                response = ResponseUtil.getResponseWithAuth(url.toString(), udhariLedgerListRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate response
                int actualStatusCode = response.getStatusCode();
                int expectedStatusCode = Integer.parseInt(statusCode);

                if (actualStatusCode != expectedStatusCode) {
                    String errorMsg = String.format("Status code mismatch. Expected: %d, Actual: %d. Response: %s", 
                        expectedStatusCode, actualStatusCode, response.asString());
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, errorMsg);
                    throw new customException(errorMsg);
                }

                LogUtils.info("Test case " + testCaseid + " passed successfully");
                ExtentReport.getTest().log(Status.PASS, "Test case " + testCaseid + " passed successfully");
            }

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in Udhari Ledger List test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in Udhari Ledger List test: " + e.getMessage());
            throw new customException("Error in Udhari Ledger List test: " + e.getMessage());
        }
    }

    private void validateResponseFields(JSONObject responseJson) throws customException {
        List<String> missingFields = new ArrayList<>();
        
        if (responseJson.has("ledger_id")) {
            missingFields.add("ledger_id");
        }
        if (responseJson.has("outlet_id")) {
            missingFields.add("outlet_id");
        }
        
        
    }
}
