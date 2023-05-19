package com.devtwist.easyrecord.Models;

public class RecordData {

    private String customerCnicNo, tId,tPhone,tDate,company, shopUser,tAmount, tType,tTime;
    private double timestamp;

    public RecordData() {
    }

    public RecordData(String customerCnicNo, String tId, String tPhone, String tDate, String company, String shopUser, String tAmount, String tType, String tTime, double timestamp) {
        this.customerCnicNo = customerCnicNo;
        this.tId = tId;
        this.tPhone = tPhone;
        this.tDate = tDate;
        this.company = company;
        this.shopUser = shopUser;
        this.tAmount = tAmount;
        this.tType = tType;
        this.tTime = tTime;
        this.timestamp = timestamp;
    }

    public String getCustomerCnicNo() {
        return customerCnicNo;
    }

    public void setCustomerCnicNo(String customerCnicNo) {
        this.customerCnicNo = customerCnicNo;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String gettPhone() {
        return tPhone;
    }

    public void settPhone(String tPhone) {
        this.tPhone = tPhone;
    }

    public String gettDate() {
        return tDate;
    }

    public void settDate(String tDate) {
        this.tDate = tDate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getShopUser() {
        return shopUser;
    }

    public void setShopUser(String shopUser) {
        this.shopUser = shopUser;
    }

    public String gettAmount() {
        return tAmount;
    }

    public void settAmount(String tAmount) {
        this.tAmount = tAmount;
    }

    public String gettType() {
        return tType;
    }

    public void settType(String tType) {
        this.tType = tType;
    }

    public String gettTime() {
        return tTime;
    }

    public void settTime(String tTime) {
        this.tTime = tTime;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

}
