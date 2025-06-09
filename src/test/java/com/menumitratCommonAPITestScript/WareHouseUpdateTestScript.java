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
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.WareHouseCreateRequest;
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
public class WareHouseUpdateTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private WareHouseCreateRequest wareHouseCreateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(WareHouseUpdateTestScript.class);

    @BeforeClass
    private void wareHouseUpdateSetUp() throws customException {
        try {
            LogUtils.info("Warehouse Update SetUp");
            ExtentReport.createTest("Warehouse Update SetUp");
            ExtentReport.getTest().log(Status.INFO, "Warehouse Update SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getWareHouseUpdateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No warehouse update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No warehouse update URL found in test data");
                throw new customException("No warehouse update URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            wareHouseCreateRequest = new WareHouseCreateRequest();

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in warehouse update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in warehouse update setup: " + e.getMessage());
            throw new customException("Error in warehouse update setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getWareHouseUpdateUrl")
    private Object[][] getWareHouseUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Warehouse Update API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Warehouse Update API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Warehouse Update API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "warehouseupdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No warehouse update URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting warehouse update URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting warehouse update URL: " + e.getMessage());
            throw new customException("Error in getting warehouse update URL: " + e.getMessage());
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
                        "warehouseupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            LogUtils.failure(logger, "Error in getting warehouse update test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting warehouse update test data: " + e.getMessage());
            throw new customException("Error in getting warehouse update test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getWareHouseUpdateData")
    public void wareHouseUpdateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting warehouse update test case: " + testCaseid);
            ExtentReport.createTest("Warehouse Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("warehouseupdate")) {
                requestBodyJson = new JSONObject(requestBody);

                  // Set all required fields in proper order
                  wareHouseCreateRequest.setUserId(Long.valueOf(user_id));
                  // Set app_source first as it's required
                  wareHouseCreateRequest.setAppSource(requestBodyJson.optString("app_source", "web"));
                  wareHouseCreateRequest.setLocation(requestBodyJson.optString("location", ""));
                  wareHouseCreateRequest.setAddress(requestBodyJson.optString("address", ""));
                  wareHouseCreateRequest.setManagerName(requestBodyJson.optString("manager_name", ""));
                  wareHouseCreateRequest.setManagerMobile(requestBodyJson.optString("manager_mobile", ""));
                  wareHouseCreateRequest.setManagerAlternateMobile(requestBodyJson.optString("manager_alternate_mobile", ""));
                  wareHouseCreateRequest.setCapacityUnit(requestBodyJson.optString("capacity_unit", ""));
                  wareHouseCreateRequest.setCapacityValue(requestBodyJson.optInt("capacity_value", 0));
                  wareHouseCreateRequest.setIsActive(requestBodyJson.optInt("is_active", 1));
                  wareHouseCreateRequest.setWarehouseType(requestBodyJson.optString("warehouse_type", ""));

               
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, wareHouseCreateRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate status code
                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Only show response without validation
                actualJsonBody = new JSONObject(response.asString());
                LogUtils.info("Warehouse update response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Warehouse update response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                LogUtils.success(logger, "Warehouse update test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Warehouse update test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in warehouse update test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
}