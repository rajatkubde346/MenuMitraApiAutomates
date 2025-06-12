package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.WareHouseRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import io.restassured.response.Response;

public class WareHouseCreateTestScript extends APIBase {
    
    private WareHouseRequest wareHouseRequest;
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private URL url;
    Logger logger = LogUtils.getLogger(WareHouseCreateTestScript.class);
    
    @BeforeClass
    private void wareHouseCreateSetUp() throws customException {
        try {
            LogUtils.info("Warehouse Create SetUp");
            
            // Initialize ExtentReport before using it
            ExtentReport.getInstance();
            ExtentReport.createTest("Warehouse Create SetUp");
            ExtentReport.getTest().log(Status.INFO, "Warehouse Create SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getWareHouseCreateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No warehouse create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No warehouse create URL found in test data");
                throw new customException("No warehouse create URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
            
        } catch (Exception e) {
            String errorMessage = "Error in warehouse create setup: " + e.getMessage();
            LogUtils.error(errorMessage);
            ExtentReport.getTest().log(Status.FAIL, errorMessage);
            throw new customException(errorMessage);
        }
    }
    
    @DataProvider(name = "getWareHouseCreateData")
    public Object[][] getWareHouseCreateData() throws customException {
        try {
            LogUtils.info("Reading warehouse create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading warehouse create test scenario data");
            
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
                        "warehousecreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid warehouse create test data found after filtering";
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
            LogUtils.failure(logger, "Exception while reading warehouse create data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception while reading warehouse create data: " + e.getMessage());
            throw new customException("Exception while reading warehouse create data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getWareHouseCreateData")
    private void wareHouseCreateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting warehouse create test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Warehouse Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Initialize warehouse request
            wareHouseRequest = new WareHouseRequest();
            
            // Set required fields
            if (!requestBodyJson.has("user_id")) {
                throw new customException("Required field 'user_id' is missing in the request payload");
            }
            wareHouseRequest.setUserId(requestBodyJson.getInt("user_id"));
            
            if (!requestBodyJson.has("location")) {
                throw new customException("Required field 'location' is missing in the request payload");
            }
            wareHouseRequest.setLocation(requestBodyJson.getString("location"));
            
            if (!requestBodyJson.has("address")) {
                throw new customException("Required field 'address' is missing in the request payload");
            }
            wareHouseRequest.setAddress(requestBodyJson.getString("address"));
            
            // Set optional fields
            wareHouseRequest.setAppSource(requestBodyJson.optString("app_source", ""));
            wareHouseRequest.setManagerName(requestBodyJson.optString("manager_name", ""));
            wareHouseRequest.setManagerMobile(requestBodyJson.optString("manager_mobile", ""));
            wareHouseRequest.setManagerAlternateMobile(requestBodyJson.optString("manager_alternate_mobile", ""));
            wareHouseRequest.setWarehouseType(requestBodyJson.optString("warehouse_type", ""));
            wareHouseRequest.setCapacityUnit(requestBodyJson.optString("capacity_unit", ""));
            wareHouseRequest.setCapacityValue(requestBodyJson.optInt("capacity_value", 0));
            wareHouseRequest.setIsActive(requestBodyJson.optInt("is_active", 1));
            
            LogUtils.info("Warehouse request initialized with payload");
            ExtentReport.getTest().log(Status.INFO, "Warehouse request initialized with payload");
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, wareHouseRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());
            
            // Validation
            int expectedStatusCode = Integer.parseInt(statusCode);
            int actualStatusCode = response.getStatusCode();
            
            // For warehouse creation, accept both 201 and 200 as valid status codes
            if (httpsmethod.equalsIgnoreCase("post") && 
                (actualStatusCode == 200 || actualStatusCode == 201) && 
                expectedStatusCode == 200) {
                LogUtils.info("Warehouse creation successful");
                ExtentReport.getTest().log(Status.PASS, "Warehouse creation successful");
            } else if (actualStatusCode != expectedStatusCode) {
                String errorMessage = "Warehouse creation failed expected [" + expectedStatusCode + "] but found [" + actualStatusCode + "]";
                LogUtils.error(errorMessage);
                ExtentReport.getTest().log(Status.FAIL, errorMessage);
                throw new customException(errorMessage);
            }
            
            // Additional response validation if needed
            if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                JSONObject expectedJson = new JSONObject(expectedResponseBody);
                JSONObject actualJson = new JSONObject(response.asString());
                // Add specific response validation logic here
            }
            
        } catch (Exception e) {
            String errorMessage = "Error in warehouse create test case: " + testCaseid + " - " + e.getMessage();
            LogUtils.error(errorMessage);
            ExtentReport.getTest().log(Status.FAIL, errorMessage);
            throw new customException(errorMessage);
        }
    }
    
    private Object[][] getWareHouseCreateUrl() throws customException {
        try {
            LogUtils.info("Reading warehouse create URL data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading warehouse create URL data from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "warehousecreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No warehouse create URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved warehouse create URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved warehouse create URL data");
            return filteredData;
        } catch (Exception e) {
            String errorMsg = "Error while reading warehouse create URL data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }
}
