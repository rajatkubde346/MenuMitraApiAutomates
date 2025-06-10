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
import com.menumitra.apiRequest.UbacGetListRequest;
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
public class UbacFunctionalityGetListTesScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private UbacGetListRequest ubacGetListRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(UbacFunctionalityGetListTesScript.class);

    @BeforeClass
    private void ubacGetListSetUp() throws customException {
        try {
            LogUtils.info("UBAC GetList SetUp");
            ExtentReport.createTest("UBAC GetList SetUp");
            ExtentReport.getTest().log(Status.INFO, "UBAC GetList SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getUbacGetListUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No UBAC GetList URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No UBAC GetList URL found in test data");
                throw new customException("No UBAC GetList URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            ubacGetListRequest = new UbacGetListRequest();

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in UBAC GetList setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in UBAC GetList setup: " + e.getMessage());
            throw new customException("Error in UBAC GetList setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUbacGetListUrl")
    private Object[][] getUbacGetListUrl() throws customException {
        try {
            LogUtils.info("Reading UBAC GetList API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading UBAC GetList API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No UBAC GetList API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "ubacgetlist".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No UBAC GetList URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting UBAC GetList URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting UBAC GetList URL: " + e.getMessage());
            throw new customException("Error in getting UBAC GetList URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUbacGetListData")
    public Object[][] getUbacGetListData() throws customException {
        try {
            LogUtils.info("Reading UBAC GetList test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading UBAC GetList test scenario data");

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
                        "ubacgetlist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid UBAC GetList test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting UBAC GetList test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting UBAC GetList test data: " + e.getMessage());
            throw new customException("Error in getting UBAC GetList test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUbacGetListData")
    public void ubacGetListTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting UBAC GetList test case: " + testCaseid);
            ExtentReport.createTest("UBAC GetList Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("ubacgetlist")) {
                // Validate request body
                if (requestBody == null || requestBody.trim().isEmpty()) {
                    LogUtils.info("Request body is empty, proceeding with default values");
                    ubacGetListRequest.setRole_name("owner"); // Set default role name
                } else {
                    try {
                        requestBodyJson = new JSONObject(requestBody);
                        ubacGetListRequest.setRole_name(requestBodyJson.optString("role_name", "owner"));
                    } catch (Exception e) {
                        LogUtils.info("Invalid JSON request body, using default values: " + e.getMessage());
                        ubacGetListRequest.setRole_name("owner"); // Set default role name
                    }
                }

                LogUtils.info("Request Body: " + (requestBodyJson != null ? requestBodyJson.toString() : "Using default values"));
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + (requestBodyJson != null ? requestBodyJson.toString() : "Using default values"));

                // Make the API call
                response = ResponseUtil.getResponseWithAuth(baseURI, ubacGetListRequest, httpsmethod, accessToken);

                // Log raw response first
                String rawResponse = response.asString();
                LogUtils.info("Raw Response: " + rawResponse);
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Raw Response: " + rawResponse);

                // Validate status code
                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // For successful responses, try to parse JSON
                if (response.getStatusCode() == 200) {
                    try {
                        if (rawResponse != null && !rawResponse.trim().isEmpty()) {
                            // Try to parse as JSON array first
                            if (rawResponse.trim().startsWith("[")) {
                                org.json.JSONArray jsonArray = new org.json.JSONArray(rawResponse);
                                LogUtils.info("Successfully parsed JSON array response with " + jsonArray.length() + " items");
                                ExtentReport.getTest().log(Status.PASS, "Successfully parsed JSON array response");
                                ExtentReport.getTest().log(Status.PASS, "Response: " + jsonArray.toString(2));
                            } else if (rawResponse.trim().startsWith("{")) {
                                // Try to parse as JSON object
                                actualJsonBody = new JSONObject(rawResponse);
                                LogUtils.info("Successfully parsed JSON object response");
                                ExtentReport.getTest().log(Status.PASS, "Successfully parsed JSON object response");
                                ExtentReport.getTest().log(Status.PASS, "Response: " + actualJsonBody.toString(2));
                            } else {
                                // Handle non-JSON response
                                LogUtils.info("Non-JSON response received: " + rawResponse);
                                ExtentReport.getTest().log(Status.PASS, "Non-JSON response received");
                                ExtentReport.getTest().log(Status.PASS, "Response: " + rawResponse);
                            }
                        } else {
                            LogUtils.info("Empty response body received");
                            ExtentReport.getTest().log(Status.PASS, "Empty response body received");
                        }
                    } catch (Exception e) {
                        LogUtils.info("Response parsing info: " + e.getMessage() + "\nRaw response: " + rawResponse);
                        ExtentReport.getTest().log(Status.INFO, "Response parsing info: " + e.getMessage());
                        ExtentReport.getTest().log(Status.INFO, "Raw response: " + rawResponse);
                    }
                }

                LogUtils.success(logger, "UBAC GetList test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("UBAC GetList test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in UBAC GetList test: " + e.getMessage();
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
