package com.menumitra.apiRequest;

public class ChefUpdateRequest {
    private String update_user_id;
    private int user_id;
    private String outlet_id;
    private String name;
    private String mobile;
    private String address;
    private String aadhar_number;
    private String dob;
    private String app_source;
    private String email;
    private int[] functionality_ids;

    public String getUpdate_user_id() {
        return update_user_id;
    }

    public void setUpdate_user_id(String update_user_id) {
        this.update_user_id = update_user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAadhar_number() {
        return aadhar_number;
    }

    public void setAadhar_number(String aadhar_number) {
        this.aadhar_number = aadhar_number;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getApp_source() {
        return app_source;
    }

    public void setApp_source(String app_source) {
        this.app_source = app_source;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int[] getFunctionality_ids() {
        return functionality_ids;
    }

    public void setFunctionality_ids(int[] functionality_ids) {
        this.functionality_ids = functionality_ids;
    }
}
