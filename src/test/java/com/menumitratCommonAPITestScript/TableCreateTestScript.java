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
import com.menumitra.apiRequest.TableRequest;
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
public class TableCreateTestScript extends APIBase
{
    private static final String API_NAME = "tablecreate";
    private TableRequest tableRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private final Logger logger = LogUtils.getLogger(TableCreateTestScript.class);

    @DataProvider(name = "getTableCreateUrl")
    public static Object[][] getTableCreateUrl() throws customException {
        try {
            LogUtils.info("Starting to read Table Create API endpoint data from Excel");
            
            if(excelSheetPathForGetApis == null || excelSheetPathForGetApis.isEmpty()) {
                String errorMsg = "Excel sheet path is null or empty";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Table Create API endpoint data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length > 0 && API_NAME.equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if(filteredData.length == 0) {
                String errorMsg = "No matching Table Create API endpoints found after filtering";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved " + filteredData.length + " Table Create API endpoints");
            return filteredData;

        } catch (Exception e) {
            String errorMsg = "Error while reading Table Create API endpoint data from Excel sheet: " + 
                            (e.getMessage() != null ? e.getMessage() : "Unknown error");
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getTableCreateData")
    public static Object[][] getTableCreateData() throws customException {
        try {
            LogUtils.info("Starting to read table create test scenario data from Excel");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No table create test scenario data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : testData) {
                if (row != null && row.length >= 3 &&
                        API_NAME.equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No positive test scenarios found for table create";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] obj = filteredData.toArray(new Object[0][]);
            LogUtils.info("Successfully filtered " + obj.length + " test scenarios for table create");
            return obj;

        } catch (Exception e) {
            String errorMsg = "Error while reading table create test scenario data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            ExtentReport.createTest("Table Create Test Script");
            LogUtils.info("=====Starting Table Create Test Script Setup=====");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            LogUtils.info("Getting base URL from environment");
            baseURI = EnviromentChanges.getBaseUrl();
            if (baseURI == null || baseURI.isEmpty()) {
                throw new customException("Base URL is null or empty");
            }
            
            LogUtils.info("Retrieving table create URL configuration");
            Object[][] tableCreateData = getTableCreateUrl();
            
            if (tableCreateData.length > 0 && tableCreateData[0].length > 2 && tableCreateData[0][2] != null) {
                String endpoint = tableCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
               
                LogUtils.success(logger, "Successfully constructed Table Create Base URI: " + baseURI);
            } else {
                String errorMsg = "Failed to construct Table Create Base URI - Invalid endpoint data";
                LogUtils.failure(logger, errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Retrieving authentication tokens");
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken == null || accessToken.isEmpty()) {
                String errorMsg = "Authentication failed - Required tokens not found. Please verify login and OTP verification";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            tableRequest = new TableRequest();
            LogUtils.success(logger, "Table create test script Setup completed successfully");

        } catch (Exception e) {
            String errorMsg = "Setup failed: " + e.getMessage();
            LogUtils.exception(logger, "Error during table create test script setup", e);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider="getTableCreateData")
    private void verifyTableCreate(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode) throws customException {
        try {
            if (testCaseid == null || description == null || httpsmethod == null || 
                requestBodyPayload == null || expectedResponseBody == null || statusCode == null) {
                throw new customException("One or more required test parameters are null");
            }

            ExtentReport.createTest("Table Create Test - " + testCaseid);
            LogUtils.info("=====Starting Table Create Test Execution=====");
            LogUtils.info("Test Case ID: " + testCaseid);
            LogUtils.info("Description: " + description);
            
            ExtentReport.getTest().log(Status.INFO, "API URL: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "HTTP Method: " + httpsmethod);
            LogUtils.info("API URL: " + baseURI);
            LogUtils.info("HTTP Method: " + httpsmethod);

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            if (!requestBodyJson.has("outlet_id") || !requestBodyJson.has("user_id") || 
                !requestBodyJson.has("app_source") || !requestBodyJson.has("section_id")) {
                throw new customException("Required fields missing in request payload");
            }

            tableRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            tableRequest.setUser_id(user_id); // Using the user_id from setup
            tableRequest.setApp_source(requestBodyJson.getString("app_source"));
            tableRequest.setSection_id(requestBodyJson.getInt("section_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Final Request Body: " + requestBodyJson.toString(2));

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, Math.min(15, accessToken.length())) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, Math.min(15, accessToken.length())) + "...");
            response = ResponseUtil.getResponseWithAuth(baseURI, tableRequest, httpsmethod, accessToken);

            if (response == null) {
                throw new customException("API response is null");
            }

            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            if(response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                String responseBody = response.asString();
                if (responseBody == null || responseBody.isEmpty()) {
                    throw new customException("Response body is empty");
                }

                actualResponseBody = new JSONObject(responseBody);
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
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table created successfully", ExtentColor.GREEN));
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
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
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
                LogUtils.error("Failed Response Status Code: " + response.getStatusCode());
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
}
