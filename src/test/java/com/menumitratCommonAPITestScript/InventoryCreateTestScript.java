package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.InventoryRequest;
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
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class InventoryCreateTestScript extends APIBase
{
    private InventoryRequest inventoryCreateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(InventoryCreateTestScript.class);

    /**
     * Data provider for inventory create API endpoint URLs
     */
    @DataProvider(name = "getInventoryCreateUrl")
    public static Object[][] getInventoryCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Inventory Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

             return Arrays.stream(readExcelData)
                    .filter(row -> "inventorycreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Inventory Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Inventory Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Inventory Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for inventory create test scenarios
     * Only provides positive test cases for inventory creation
     */
    @DataProvider(name = "getInventoryCreateData")
    public static Object[][] getInventoryCreateData() throws customException {
        try {
            LogUtils.info("Reading inventory create positive test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory create test scenario data found in Excel sheet");
                throw new customException("No inventory create test scenario data found in Excel sheet");
            }

            // Filter to only include positive test cases for inventory creation
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && 
                    "inventorycreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            Object[][] filteredData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + filteredData.length + " positive test scenarios for inventory create");
            return filteredData;
            
        } catch (Exception e) {
            LogUtils.error("Error while reading inventory create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading inventory create test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading inventory create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

  
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for inventory create test====");
            ExtentReport.createTest("Inventory Create Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set inventory create URL
            Object[][] inventoryCreateData = getInventoryCreateUrl();
            if (inventoryCreateData.length > 0) {
                String endpoint = inventoryCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for inventory create: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No inventory create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No inventory create URL found in test data");
                throw new customException("No inventory create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            inventoryCreateRequest = new InventoryRequest();
            LogUtils.success(logger, "Inventory Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Inventory Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during inventory create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during inventory create setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create inventory
     */
    @Test(dataProvider = "getInventoryCreateData")
    private void createInventory(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting inventory create test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Inventory Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            // Initialize inventory request with payload from Excel sheet
            inventoryCreateRequest.setUserId(user_id);
            
            // Required fields
            if (!requestBodyJson.has("name")) {
                throw new customException("Required field 'name' is missing in the request payload");
            }
            inventoryCreateRequest.setName(requestBodyJson.getString("name"));
            
            if (!requestBodyJson.has("sub_category_id")) {
                throw new customException("Required field 'sub_category_id' is missing in the request payload");
            }
            inventoryCreateRequest.setSubCategoryId(requestBodyJson.getInt("sub_category_id"));
            
            if (!requestBodyJson.has("unit")) {
                throw new customException("Required field 'unit' is missing in the request payload");
            }
            inventoryCreateRequest.setUnit(requestBodyJson.getString("unit"));
            
            if (!requestBodyJson.has("purchase_price")) {
                throw new customException("Required field 'purchase_price' is missing in the request payload");
            }
            inventoryCreateRequest.setPurchasePrice(requestBodyJson.getDouble("purchase_price"));
            
            if (!requestBodyJson.has("quantity")) {
                throw new customException("Required field 'quantity' is missing in the request payload");
            }
            inventoryCreateRequest.setQuantity(requestBodyJson.getInt("quantity"));
            
            // Optional fields with default values
            inventoryCreateRequest.setMinThresholdQuantity(requestBodyJson.has("min_threshold_quantity") ? 
                requestBodyJson.getInt("min_threshold_quantity") : 0);
            
            inventoryCreateRequest.setRepeat(requestBodyJson.has("repeat") ? 
                requestBodyJson.getBoolean("repeat") : false);
            
            inventoryCreateRequest.setRepeatFrequency(requestBodyJson.has("repeat_frequency") ? 
                requestBodyJson.getString("repeat_frequency") : "");
            
            inventoryCreateRequest.setRepeatFrequencyValue(requestBodyJson.has("repeat_frequency_value") ? 
                requestBodyJson.getInt("repeat_frequency_value") : 0);
            
            inventoryCreateRequest.setDescription(requestBodyJson.has("description") ? 
                requestBodyJson.getString("description") : "");
            
            inventoryCreateRequest.setTaxType(requestBodyJson.has("tax_type") ? 
                requestBodyJson.getString("tax_type") : "");
            
            inventoryCreateRequest.setTaxRate(requestBodyJson.has("tax_rate") ? 
                requestBodyJson.getDouble("tax_rate") : 0.0);
            
            inventoryCreateRequest.setExpiryDate(requestBodyJson.has("expiration_date") ? 
                requestBodyJson.getString("expiration_date") : "");
            
            LogUtils.info("Inventory request initialized with payload from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Inventory request initialized with payload from Excel sheet");
            
            LogUtils.info("Final Request Body prepared for inventory create");

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, inventoryCreateRequest, httpsmethod, accessToken); 
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            int expectedStatusCode = Integer.parseInt(statusCode);
            int actualStatusCode = response.getStatusCode();
            boolean isCreateRequest = httpsmethod.equalsIgnoreCase("post") && url.getPath().endsWith("/create_inventory_item");
            
            // For inventory creation, accept both 201 and 200 as valid
            if (isCreateRequest) {
                if (actualStatusCode == 201 || actualStatusCode == 200) {
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + actualStatusCode);
                    LogUtils.success(logger, "Status code validation passed: " + actualStatusCode);
                    
                    actualResponseBody = new JSONObject(response.asString());
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                    LogUtils.info("Starting response body validation");
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                    LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                    LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                    
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Inventory created successfully with status code " + actualStatusCode, ExtentColor.GREEN));
                    return;
                }
            }
            
            // For non-create requests
            if (actualStatusCode == expectedStatusCode) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + actualStatusCode);
                LogUtils.success(logger, "Status code validation passed: " + actualStatusCode);
                actualResponseBody = new JSONObject(response.asString());
                
                if (!actualResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                    LogUtils.info("Starting response body validation");
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                    LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                    LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                    
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Operation completed successfully", ExtentColor.GREEN));
                } else {
                    ExtentReport.getTest().log(Status.INFO, "Response body is empty");
                    LogUtils.info("Response body is empty");
                }
            } else {
                String errorMsg = "Status code validation failed - Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode;
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                LogUtils.failure(logger, errorMsg);
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            LogUtils.error(errorMsg);
            LogUtils.error("Stack trace: " + Arrays.toString(e.getStackTrace()));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
                LogUtils.error("Failed Response Status Code: " + response.getStatusCode());
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
    
   // @AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down started===");
            ExtentReport.createTest("Inventory Create Test Teardown");
            
            LogUtils.info("Logging out user");
            //ActionsMethods.logout();
            
            LogUtils.info("Clearing tokens");
            TokenManagers.clearTokens();
            
            LogUtils.info("===Test environment tear down completed successfully===");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }
}