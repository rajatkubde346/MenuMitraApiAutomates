package com.menumitra.utilityclass;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.menumitra.apiRequest.UbacFuctionalityRequest;
import com.menumitra.apiRequest.UdhariLedgerRequest;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class ResponseUtil 
{
	// Static request specification for API calls
    static RequestSpecification request;
    
    // Static response object to store API responses
    static Response response;
    
    // Static ObjectMapper for JSON serialization
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    /**
     * Makes an HTTP request based on the specified method and returns the response
     * 
     * @param url The endpoint URL to send the request to
     * @param requestBody The request body object
     * @param method The HTTP method to use (post, put, etc.)
     * @return Response object containing the API response
     * @throws customException If there is an error during the API request
     */
    public static Response getResponse(String url, Object requestBody, String method) throws customException {
        try {
            LogUtils.info("Making " + method.toUpperCase() + " request to: " + url);
            LogUtils.info("Request body: " + objectMapper.writeValueAsString(requestBody));
            
            switch (method.toLowerCase()) {
                case "post":
                    try {
                        LogUtils.info("Executing POST request");
                        String jsonBody = objectMapper.writeValueAsString(requestBody);
                        LogUtils.info("Request body object type: " + requestBody.getClass().getName());
                        LogUtils.info("Request body content: " + jsonBody);
                        
                        response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .body(jsonBody)
                            .when()
                            .post(url)
                            .then()
                            .log().all()
                            .extract()
                            .response();
                            
                        LogUtils.info("POST request completed. Status code: " + response.getStatusCode());
                        LogUtils.info("Response body: " + response.asString());
                        return response;
                    } catch (Exception e) {
                        LogUtils.error("Error executing POST request: " + e.getMessage());
                        throw new customException("Error executing POST request: " + e.getMessage());
                    }
                    
                case "put":
                    try {
                        LogUtils.info("Executing PUT request");
                        String jsonBody = objectMapper.writeValueAsString(requestBody);
                        
                        response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .body(jsonBody)
                            .when()
                            .put(url)
                            .then()
                            .log().all()
                            .extract()
                            .response();
                            
                        LogUtils.info("PUT request completed. Status code: " + response.getStatusCode());
                        LogUtils.info("Response body: " + response.asString());
                        return response;
                    } catch (Exception e) {
                        LogUtils.error("Error executing PUT request: " + e.getMessage());
                        throw new customException("Error executing PUT request: " + e.getMessage());
                    }

                case "get":
                    try {
                        LogUtils.info("Executing GET request");
                        
                        response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .when()
                            .get(url)
                            .then()
                            .log().all()
                            .extract()
                            .response();
                            
                        LogUtils.info("GET request completed. Status code: " + response.getStatusCode());
                        LogUtils.info("Response body: " + response.asString());
                        return response;
                    } catch (Exception e) {
                        LogUtils.error("Error executing GET request: " + e.getMessage());
                        throw new customException("Error executing GET request: " + e.getMessage());
                    }
                    
                case "delete":
                    try {
                        LogUtils.info("Executing DELETE request");
                        String jsonBody = objectMapper.writeValueAsString(requestBody);
                        
                        response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .body(jsonBody)
                            .when()
                            .delete(url)
                            .then()
                            .log().all()
                            .extract()
                            .response();
                            
                        LogUtils.info("DELETE request completed. Status code: " + response.getStatusCode());
                        LogUtils.info("Response body: " + response.asString());
                        return response;
                    } catch (Exception e) {
                        LogUtils.error("Error executing DELETE request: " + e.getMessage());
                        throw new customException("Error executing DELETE request: " + e.getMessage());
                    }
                
                default:
                    String errorMsg = "Unsupported HTTP method: " + method;
                    LogUtils.error(errorMsg);
                    throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error during " + method.toUpperCase() + " request: " + e.getMessage();
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    /**
     * Makes an HTTP request based on the specified method and returns the response
     * 
     * @param url The endpoint URL to send the request to
     * @param requestBody The request body object
     * @param method The HTTP method to use (post, put, etc.)
     * @return Response object containing the API response
     * @throws customException If there is an error during the API request
     */
    public static Response getResponseWithAuth(String url, Object requestBody, String method, String jwttoken) throws customException 
    {
        try {
            LogUtils.info("Making " + method.toUpperCase() + " request to: " + url);
            
            switch (method.toLowerCase()) {
                case "post":
                    try {
                        LogUtils.info("Executing POST request");
                        String jsonBody = objectMapper.writeValueAsString(requestBody);
                        LogUtils.info("Request body object type: " + requestBody.getClass().getName());
                        LogUtils.info("Request body content: " + jsonBody);
                        
                        response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .header("Authorization", "Bearer " + jwttoken)
                            .body(jsonBody)
                            .when()
                            .post(url)
                            .then()
                            .log().all()
                            .extract()
                            .response();
                            
                        LogUtils.info("POST request completed. Status code: " + response.getStatusCode());
                        LogUtils.info("Response body: " + response.asString());
                        return response;
                    } catch (Exception e) {
                        LogUtils.error("Error executing POST request: " + e.getMessage());
                        throw new customException("Error executing POST request: " + e.getMessage());
                    }
                    
                case "get":
                    try {
                        response = RestAssured.given()
                            .header("Authorization","Bearer "+jwttoken)
                            .when()
                            .get(url);
                        
                        LogUtils.info("successfully get response..");
                        return response;
                    } catch(Exception e) {
                        LogUtils.error("Error: Get response..");
                        throw new customException("Error: Get response");
                    }
                    
                case "put":
                    try
                    {
                        LogUtils.info("Executing PUT request");
                        response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .header("Authorization","Bearer "+jwttoken)
                            .body(requestBody)
                            .when()
                            .put(url)
                            .then()
                            .log().all()
                            .extract()
                            .response();
                        LogUtils.info("PUT request completed successfully");
                        return response;
                    }
                    catch (Exception e)
                    {
                        LogUtils.error("Error: Get response..");
                        throw new customException("Error: Get response");
                    }

                case "delete":
                                try {
                                    
                                    LogUtils.info("start delete data..");
                                    response =RestAssured.given()
                                    .contentType(ContentType.JSON)
                                    .header("Authorization", "Bearer " + jwttoken)
                                    .body(requestBody)
                                    .when()
                                    .delete(url)
                                    .then()
                                    .log()
                                    .all()
                                    .extract()
                                    .response();
                                    
                                    LogUtils.info("Delete Data Successfully");
                                    return response;
                                    
                                } catch (Exception e) {
                                  
                                	LogUtils.error("Error:Unable to delete data check request body");
                                	throw new customException("Error:Unble to delete data check request body");
                                }
                case "patch":
                                try {
                                    LogUtils.info("Executing PATCH request");
                                    String jsonBody = objectMapper.writeValueAsString(requestBody);
                                    LogUtils.info("Request body object type: " + requestBody.getClass().getName());
                                    LogUtils.info("Request body content: " + jsonBody);
                                    
                                    response = RestAssured.given()
                                        .contentType(ContentType.JSON)
                                        .header("Authorization", "Bearer " + jwttoken)
                                        .body(jsonBody)
                                        .when()
                                        .patch(url)
                                        .then()
                                        .log().all()
                                        .extract()
                                        .response();
                                    LogUtils.info("PATCH request completed successfully");
                                    return response;
                                } catch (Exception e) {
                                    LogUtils.error("Error: PATCH request failed");
                                    throw new customException("Error: Failed to execute PATCH request");
                                }
                default:
                    LogUtils.warn("Unsupported HTTP method: " + method);
                    return null;
            }
        } catch (Exception e) {
            LogUtils.error("Error during " + method.toUpperCase() + " request: " + e.getMessage());
            throw new customException("Error during API request: " + e.getMessage());
        }
    }

    /**
     * Validates the response schema against a JSON schema definition file
     * @param response The REST Assured Response object to validate
     * @param jsonPath Path to the JSON schema file to validate against
     * @throws customException If schema validation fails or other errors occur
     */
    public static void validateResponseSchema(Response response, String jsonPath) throws customException {
        try {
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonPath));
            LogUtils.info("Response schema validation successful");
        } catch (Exception e) {
            LogUtils.error("Response schema validation failed: " + e.getMessage());
            throw new customException("Response schema validation failed: " + e.getMessage());
        }
    }
}
