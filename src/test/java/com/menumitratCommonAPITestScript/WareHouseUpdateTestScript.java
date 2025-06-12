package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
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

public class WareHouseUpdateTestScript extends APIBase {
    
    private WareHouseRequest wareHouseRequest;
    private JSONObject requestBodyJson;
    private Response response;
    private String baseUri;
    private String accessToken;
    private URL url;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    Logger logger = LogUtils.getLogger(WareHouseUpdateTestScript.class);

    @BeforeClass
    private void wareHouseUpdateSetUp() throws customException {
        try {
            LogUtils.info("Warehouse Update SetUp");
            
            // Initialize ExtentReport before using it
            ExtentReport.getInstance();
            ExtentReport.createTest("Warehouse Update SetUp");
            ExtentReport.getTest().log(Status.INFO, "Warehouse Update SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseUri = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getWareHouseUpdateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Constructed base URI: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseUri);
            } else {
                LogUtils.failure(logger, "No warehouse update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No warehouse update URL found in test data");
                throw new customException("No warehouse update URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
            
        } catch (Exception e) {
            String errorMessage = "Error in warehouse update setup: " + e.getMessage();
            LogUtils.error(errorMessage);
            ExtentReport.getTest().log(Status.FAIL, errorMessage);
            throw new customException(errorMessage);
        }
    }
    
    @DataProvider(name = "getWareHouseUpdateData")
    public Object[][] getWareHouseUpdateData() throws customException {
        try {
            LogUtils.info("Reading warehouse update test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading warehouse update test scenario data");
            
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
                    "warehouseUpdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid warehouse update test data found after filtering";
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
            LogUtils.failure(logger, "Exception while reading warehouse update data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception while reading warehouse update data: " + e.getMessage());
            throw new customException("Exception while reading warehouse update data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getWareHouseUpdateData")
    private void wareHouseUpdateTest(String apiName, String testCaseId, String testType, String description,
            String httpsMethod, String requestBody, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting warehouse update test case: " + testCaseId);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Warehouse Update Test - " + testCaseId);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBody);
            
            // Initialize warehouse request
            wareHouseRequest = new WareHouseRequest();
            
            // Set request data
            wareHouseRequest.setUserId(requestBodyJson.getInt("user_id"));
            wareHouseRequest.setAppSource(requestBodyJson.getString("app_source"));
            wareHouseRequest.setLocation(requestBodyJson.getString("location"));
            wareHouseRequest.setAddress(requestBodyJson.getString("address"));
            wareHouseRequest.setManagerName(requestBodyJson.getString("manager_name"));
            wareHouseRequest.setManagerMobile(requestBodyJson.getString("manager_mobile"));
            wareHouseRequest.setManagerAlternateMobile(requestBodyJson.getString("manager_alternate_mobile"));
            wareHouseRequest.setWarehouseType(requestBodyJson.getString("warehouse_type"));
            wareHouseRequest.setCapacityUnit(requestBodyJson.getString("capacity_unit"));
            wareHouseRequest.setCapacityValue(requestBodyJson.getInt("capacity_value"));
            wareHouseRequest.setIsActive(requestBodyJson.getInt("is_active"));
            wareHouseRequest.setWarehouseId(requestBodyJson.getInt("warehouse_id"));

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseUri);
            LogUtils.info("Making API call to endpoint: " + baseUri);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseUri, wareHouseRequest, httpsMethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());
            
            // Validation
            actualResponseBody = new JSONObject(response.asString());
            expectedResponse = new JSONObject(expectedResponseBody);
            
            // Verify status code
            Assert.assertEquals(response.getStatusCode(), Integer.parseInt(statusCode), 
                "Status code mismatch for test case: " + testCaseId);
            
            // Verify response body
            Assert.assertEquals(actualResponseBody.toString(), expectedResponse.toString(), 
                "Response body mismatch for test case: " + testCaseId);

            LogUtils.success(logger, "Test case " + testCaseId + " executed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test case passed: " + testCaseId, ExtentColor.GREEN));
            
        } catch (Exception e) {
            String errorMessage = "Error in warehouse update test case: " + testCaseId + " - " + e.getMessage();
            LogUtils.error(errorMessage);
            ExtentReport.getTest().log(Status.FAIL, errorMessage);
            throw new customException(errorMessage);
        }
    }
    
    private Object[][] getWareHouseUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading warehouse update URL data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading warehouse update URL data from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "warehouseUpdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No warehouse update URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved warehouse update URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved warehouse update URL data");
            return filteredData;
        } catch (Exception e) {
            String errorMsg = "Error while reading warehouse update URL data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }
}
