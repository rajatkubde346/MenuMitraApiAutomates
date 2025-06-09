package com.menumitra.apiRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WareHouseCreateRequest {
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("app_source")
    private String appSource;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("manager_name")
    private String managerName;
    
    @JsonProperty("manager_mobile")
    private String managerMobile;
    
    @JsonProperty("manager_alternate_mobile")
    private String managerAlternateMobile;
    
    @JsonProperty("warehouse_type")
    private String warehouseType;
    
    @JsonProperty("capacity_unit")
    private String capacityUnit;
    
    @JsonProperty("capacity_value")
    private Integer capacityValue;
    
    @JsonProperty("is_active")
    private Integer isActive;
    
    @JsonProperty("warehouse_id")
    private Integer warehouseId;

    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAppSource() {
        return appSource;
    }

    public void setAppSource(String appSource) {
        this.appSource = appSource;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerMobile() {
        return managerMobile;
    }

    public void setManagerMobile(String managerMobile) {
        this.managerMobile = managerMobile;
    }

    public String getManagerAlternateMobile() {
        return managerAlternateMobile;
    }

    public void setManagerAlternateMobile(String managerAlternateMobile) {
        this.managerAlternateMobile = managerAlternateMobile;
    }

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }

    public String getCapacityUnit() {
        return capacityUnit;
    }

    public void setCapacityUnit(String capacityUnit) {
        this.capacityUnit = capacityUnit;
    }

    public Integer getCapacityValue() {
        return capacityValue;
    }

    public void setCapacityValue(Integer capacityValue) {
        this.capacityValue = capacityValue;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    


}
    