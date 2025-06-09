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
import com.menumitra.apiRequest.sectionRequest;
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
public class sectionUpdateTestScript extends APIBase
{
	private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedJsonBody; 
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    sectionRequest sectionrequest;
    Logger logger=LogUtils.getLogger(sectionCreateTestScript.class);;


    @DataProvider(name="getSectionUpdateURL")
    public Object[][] getSectionUpdateURL() throws customException
    {
        try{
            Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,"commonAPI");
            if(readData==null)
            {
                LogUtils.error("Error: Getting an error while read Section URL Excel File");
                throw new customException("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectionupdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        }
        catch (Exception e) {
            LogUtils.error("Error: Getting an error while read Section URL Excel File");
            throw new customException("Error: Getting an error while read Section URL Excel File");
        }
    }


    @DataProvider(name="getSectionUpdatePositiveInputData")
    private Object[][] getSectionUpdatePositiveInputData() throws customException {
        try {
            LogUtils.info("Reading positive test scenario data for section update API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, property.getProperty("CommonAPITestScenario"));
            
            if (testData == null || testData.length == 0)
            {
                LogUtils.failure(logger, "No Section Update API positive test scenario data found in Excel sheet");
                throw new customException("No Section Update API Positive test scenario data found in Excel sheet");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "sectionupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Section Update API positive test scenario data: " + e.getMessage(), e);
            throw new customException("Error reading Section Update API positive test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void sectionUpdateSetup() throws customException 
    {
        try {
            LogUtils.info("Setting up test environment for section update");
            ExtentReport.createTest("Start Section Update");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set section update URL
            Object[][] sectionUpdateData = getSectionUpdateURL();
            if (sectionUpdateData.length > 0) {
                String endpoint = sectionUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Section Update URL set to: " + baseUri);
            } else {
                LogUtils.failure(logger, "No section update URL found in test data");
                throw new customException("No section update URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            sectionrequest = new sectionRequest();
            LogUtils.info("Section update setup completed successfully");
            ExtentReport.getTest().log(Status.INFO, "Section update setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section update setup: " + e.getMessage(), e);
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

@Test(dataProvider = "getSectionUpdatePositiveInputData", priority = 1)
private void verifySectionUpdateUsingValidInputData(String apiName, String testCaseId, 
    String testType, String description, String httpsMethod, 
    String requestBody, String expectedResponseBody, String statusCode) throws customException
{
    try
    {
        LogUtils.info("start section update api using valid input data");
        ExtentReport.createTest("Verify Section Update APi: "+description);
        ExtentReport.getTest().log(Status.INFO,"====start section update by Using Positive Input Data===");
        ExtentReport.getTest().log(Status.INFO,"Constructed Base URI: "+baseUri);
        if(apiName.contains("sectionupdate") && testType.contains("positive"))
        {
            requestBodyJson=new JSONObject(requestBody);
            sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            sectionrequest.setUser_id(String.valueOf(userId));
            sectionrequest.setSection_name(requestBodyJson.getString("section_name"));
            sectionrequest.setSection_id(String.valueOf(requestBodyJson.getInt("section_id")));
            sectionrequest.setApp_source(requestBodyJson.getString("app_source"));
            
            // Log the HTTP method being used
            LogUtils.info("Using HTTP method: " + httpsMethod);
            ExtentReport.getTest().log(Status.INFO, "HTTP Method: " + httpsMethod);
            
            // Log request body for debugging
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + sectionrequest.toString());
            
            response=ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            
            // Log response status code immediately after request
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("section update api response ");
            ExtentReport.getTest().log(Status.INFO,"section update api response: "+response.getBody().asString());
            
            if (response.getStatusCode() == 200) {
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    expectedJsonBody=new JSONObject(expectedResponseBody);
                    
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    LogUtils.success(logger,"Successfully Validate Section Update Api By using Positive Input data");
                    ExtentReport.getTest().log(Status.PASS,"Successfully Validate Section Update Api By using Positive Input data");
                } else {
                    LogUtils.failure(logger, "Empty response body received");
                    ExtentReport.getTest().log(Status.FAIL, "Empty response body received");
                    throw new customException("Response body is empty");
                }
            } else {
                LogUtils.failure(logger, "Invalid status code to check section update api using positive input data: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Invalid status code to check section update api using positive input data: " + response.getStatusCode());
                throw new customException("In section update api using positive input test case Expected status code 200 but got " + response.getStatusCode());
            }                
            
        }

    }
    catch(Exception e)
    {
        LogUtils.exception(logger, "An error occurred during section update verification: "+e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL,"An error occurred during section update verification: "+e.getMessage());
        throw new customException("An error occurred during section update verification");
    }
}

//@AfterClass
private void tearDown()
{
//    try 
//    {
//        LogUtils.info("===Test environment tear down successfully===");
//       
//        ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
//        
//        ActionsMethods.logout();
//        TokenManagers.clearTokens();
//        
//    } 
//    catch (Exception e) 
//    {
//        LogUtils.exception(logger, "Error during test environment tear down", e);
//        ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
//    }
}

}
