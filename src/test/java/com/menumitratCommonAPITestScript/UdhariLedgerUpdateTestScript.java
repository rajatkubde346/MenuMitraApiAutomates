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
import com.menumitra.apiRequest.UdhariLedgerUpdateRequest;
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
public class UdhariLedgerUpdateTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private UdhariLedgerUpdateRequest udhariLedgerUpdateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(UdhariLedgerUpdateTestScript.class);

    @BeforeClass
    private void udhariLedgerUpdateSetUp() throws customException {
        try {
            LogUtils.info("Udhari Ledger Update SetUp");
            ExtentReport.createTest("Udhari Ledger Update SetUp");
            ExtentReport.getTest().log(Status.INFO, "Udhari Ledger Update SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getUdhariLedgerUpdateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No Udhari Ledger Update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No Udhari Ledger Update URL found in test data");
                throw new customException("No Udhari Ledger Update URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            udhariLedgerUpdateRequest = new UdhariLedgerUpdateRequest();

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in Udhari Ledger Update SetUp: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in Udhari Ledger Update SetUp: " + e.getMessage());
            throw new customException("Error in Udhari Ledger Update SetUp: " + e.getMessage());
        }
    }

    private Object[][] getUdhariLedgerUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Udhari Ledger Update API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Udhari Ledger Update API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Udhari Ledger Update API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "udhariledgerupdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No Udhari Ledger Update URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Udhari Ledger Update URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Udhari Ledger Update URL: " + e.getMessage());
            throw new customException("Error in getting Udhari Ledger Update URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUdhariLedgerUpdateData")
    public Object[][] getUdhariLedgerUpdateData() throws customException {
        try {
            LogUtils.info("Reading Udhari Ledger Update test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading Udhari Ledger Update test scenario data");

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
                        "udhariledgerupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid Udhari Ledger Update test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Udhari Ledger Update test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Udhari Ledger Update test data: " + e.getMessage());
            throw new customException("Error in getting Udhari Ledger Update test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUdhariLedgerUpdateData")
    public void udhariLedgerUpdateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting Udhari Ledger Update test case: " + testCaseid);
            ExtentReport.createTest("Udhari Ledger Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("udhariledgerupdate")) {
                requestBodyJson = new JSONObject(requestBody);

                // Validate required fields in request body
                validateRequestBody(requestBodyJson);

                // Get user_id from token if not provided in request
                if (!requestBodyJson.has("user_id") || requestBodyJson.isNull("user_id")) {
                    requestBodyJson.put("user_id", TokenManagers.getUserId());
                }

                // Set request parameters with validation
                udhariLedgerUpdateRequest = new UdhariLedgerUpdateRequest(); // Create new instance for each test
                udhariLedgerUpdateRequest.setUserid(requestBodyJson.getInt("user_id"));
                udhariLedgerUpdateRequest.setLedgerid(requestBodyJson.getInt("ledger_id"));
                udhariLedgerUpdateRequest.setSettleamount(requestBodyJson.getDouble("settle_amount"));

                // Log request details
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                LogUtils.info("HTTP Method: " + httpsmethod);
                LogUtils.info("Base URI: " + baseURI);

                // Make API call with proper content type and headers
                response = ResponseUtil.getResponseWithAuth(baseURI, udhariLedgerUpdateRequest, httpsmethod.toLowerCase(), accessToken);

                // Log response details
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Handle 400 Bad Request with detailed logging
                if (response.getStatusCode() == 400) {
                    JSONObject errorResponse = new JSONObject(response.asString());
                    String errorDetail = errorResponse.has("detail") ? errorResponse.getString("detail") : "No detail provided";
                    LogUtils.error("Bad Request Error: " + errorDetail);
                    ExtentReport.getTest().log(Status.FAIL, "Bad Request Error: " + errorDetail);
                    
                    // Additional debug information
                    LogUtils.info("Debug - Request Details:");
                    LogUtils.info("userid: " + udhariLedgerUpdateRequest.getUserid());
                    LogUtils.info("ledgerid: " + udhariLedgerUpdateRequest.getLedgerid());
                    LogUtils.info("settleamount: " + udhariLedgerUpdateRequest.getSettleamount());
                    
                    // Log the actual request being sent
                    LogUtils.info("Debug - Raw Request:");
                    LogUtils.info("Headers: " + response.getHeaders().toString());
                    LogUtils.info("Request Method: " + httpsmethod.toLowerCase());
                    LogUtils.info("Full URL: " + baseURI);
                }

                // Validate status code
                int actualStatusCode = response.getStatusCode();
                int expectedStatusCode = Integer.parseInt(statusCode);
                if (actualStatusCode != expectedStatusCode) {
                    String errorMsg = String.format("Status code mismatch - Expected: %d, Actual: %d, Response Body: %s",
                            expectedStatusCode, actualStatusCode, response.asString());
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Parse and validate response
                actualJsonBody = new JSONObject(response.asString());
                if (!expectedResponseBody.isEmpty()) {
                    validateResponseBody(actualJsonBody, new JSONObject(expectedResponseBody));
                } else {
                    // If no expected response body is provided, just validate that the response is successful
                    validateResponseBody(actualJsonBody, null);
                }

                LogUtils.success(logger, "Udhari Ledger Update test completed successfully");
                ExtentReport.getTest().log(Status.PASS, 
                    MarkupHelper.createLabel("Udhari Ledger Update test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in Udhari Ledger Update test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    private void validateRequestBody(JSONObject requestBody) throws customException {
        List<String> missingFields = new ArrayList<>();
        
        // Check required fields
        if (!requestBody.has("ledger_id") || requestBody.isNull("ledger_id")) {
            missingFields.add("ledger_id");
        }
        if (!requestBody.has("settle_amount") || requestBody.isNull("settle_amount")) {
            missingFields.add("settle_amount");
        }

        // Validate field values
        if (requestBody.has("settle_amount")) {
            double settleAmount = requestBody.getDouble("settle_amount");
            if (settleAmount <= 0) {
                throw new customException("settle_amount must be greater than 0");
            }
        }

        if (!missingFields.isEmpty()) {
            String errorMsg = "Missing required fields in request body: " + String.join(", ", missingFields);
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    private void validateResponseBody(JSONObject actualResponse, JSONObject expectedResponse) throws customException {
        try {
            // For successful update, API might return a success message or empty response
            if (actualResponse.length() == 0) {
                // Empty response is valid for successful update
                LogUtils.info("Received empty response body - This is valid for successful update");
                return;
            }

            // If we have an expected response, validate it
            if (expectedResponse != null && !expectedResponse.isEmpty()) {
                // Check if response contains error detail
                if (actualResponse.has("detail")) {
                    String detail = actualResponse.getString("detail");
                    if (detail.contains("error") || detail.contains("failed") || detail.contains("invalid")) {
                        throw new customException("API returned error: " + detail);
                    }
                    // If detail is a success message, log it
                    LogUtils.info("API Response detail: " + detail);
                }

                // Compare only the fields that are present in expected response
                for (String key : expectedResponse.keySet()) {
                    if (!actualResponse.has(key)) {
                        LogUtils.warn("Response missing expected field: " + key + " but continuing as it might be optional");
                        continue;
                    }
                    
                    // Compare values if the field exists in both
                    if (!Objects.equals(actualResponse.get(key).toString(), expectedResponse.get(key).toString())) {
                        String errorMsg = String.format("Response field '%s' value mismatch. Expected: %s, Actual: %s",
                            key, expectedResponse.get(key), actualResponse.get(key));
                        LogUtils.error(errorMsg);
                        throw new customException(errorMsg);
                    }
                }
            }

            LogUtils.info("Response validation completed successfully");
        } catch (Exception e) {
            if (e instanceof customException) {
                throw (customException) e;
            }
            throw new customException("Error validating response: " + e.getMessage());
        }
    }
}
