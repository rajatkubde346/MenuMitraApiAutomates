package com.menumitra.apiRequest;

public class WareHouseCreateRequest {
    private Long userId;
    private String appSource;
    private String location;
    private String address;
    private String managerName;
    private String managerMobile;
    private String managerAlternateMobile;
    private String warehouseType;
    private String capacityUnit;
    private Integer capacityValue;
    private Integer isActive;

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
}
    