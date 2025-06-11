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
import com.menumitra.apiRequest.UdhariLedgerRequest;
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
public class UdhariLedgerTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private UdhariLedgerRequest udhariLedgerRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(UdhariLedgerTestScript.class);

    @BeforeClass
    private void udhariLedgerSetUp() throws customException {
        try {
            LogUtils.info("Udhari Ledger SetUp");
            ExtentReport.createTest("Udhari Ledger SetUp");
            ExtentReport.getTest().log(Status.INFO, "Udhari Ledger SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getUdhariLedgerUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No Udhari Ledger URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No Udhari Ledger URL found in test data");
                throw new customException("No Udhari Ledger URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            udhariLedgerRequest = new UdhariLedgerRequest();

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in Udhari Ledger setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in Udhari Ledger setup: " + e.getMessage());
            throw new customException("Error in Udhari Ledger setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUdhariLedgerUrl")
    private Object[][] getUdhariLedgerUrl() throws customException {
        try {
            LogUtils.info("Reading Udhari Ledger API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Udhari Ledger API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Udhari Ledger API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "udhariledger".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No Udhari Ledger URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Udhari Ledger URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Udhari Ledger URL: " + e.getMessage());
            throw new customException("Error in getting Udhari Ledger URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUdhariLedgerData")
    public Object[][] getUdhariLedgerData() throws customException {
        try {
            LogUtils.info("Reading Udhari Ledger test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading Udhari Ledger test scenario data");

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
                        "udhariledger".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid Udhari Ledger test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting Udhari Ledger test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Udhari Ledger test data: " + e.getMessage());
            throw new customException("Error in getting Udhari Ledger test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUdhariLedgerData")
    public void udhariLedgerTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting Udhari Ledger test case: " + testCaseid);
            ExtentReport.createTest("Udhari Ledger Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("udhariledger")) {
                requestBodyJson = new JSONObject(requestBody);

                // Set request parameters based on your UdhariLedgerRequest class requirements
                udhariLedgerRequest.setUserId(requestBodyJson.getLong("user_id"));
                udhariLedgerRequest.setCustomerName(requestBodyJson.getString("customer_name"));
                udhariLedgerRequest.setCustomerMobile(requestBodyJson.getString("customer_mobile"));
                udhariLedgerRequest.setCustomerAddress(requestBodyJson.getString("customer_address"));
                udhariLedgerRequest.setOrderId(requestBodyJson.getLong("order_id"));
                udhariLedgerRequest.setBillAmount(requestBodyJson.getDouble("bill_amount"));
                udhariLedgerRequest.setEstimatedSettlementPeriod(requestBodyJson.getString("estimated_settlement_period"));

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, udhariLedgerRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate status code - accept both 200 and 201 for successful creation
                int actualStatusCode = response.getStatusCode();
                int expectedStatusCode = Integer.parseInt(statusCode);
                if (actualStatusCode != expectedStatusCode && !(expectedStatusCode == 200 && actualStatusCode == 201)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + actualStatusCode;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Parse and log response
                actualJsonBody = new JSONObject(response.asString());
                LogUtils.info("Udhari Ledger response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Udhari Ledger response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                LogUtils.success(logger, "Udhari Ledger test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Udhari Ledger test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in Udhari Ledger test: " + e.getMessage();
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