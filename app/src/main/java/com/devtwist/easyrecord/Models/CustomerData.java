package com.devtwist.easyrecord.Models;

public class CustomerData {
    private String customerCnicNo, customerName, cnicFrontUrl, cnicBackUrl;

    public CustomerData() {
    }

    public CustomerData(String customerCnicNo, String customerName, String cnicFrontUrl, String cnicBackUrl) {
        this.customerCnicNo = customerCnicNo;
        this.customerName = customerName;
        this.cnicFrontUrl = cnicFrontUrl;
        this.cnicBackUrl = cnicBackUrl;

    }

    public String getCustomerCnicNo() {
        return customerCnicNo;
    }

    public void setCustomerCnicNo(String customerCnicNo) {
        this.customerCnicNo = customerCnicNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCnicFrontUrl() {
        return cnicFrontUrl;
    }

    public void setCnicFrontUrl(String cnicFrontUrl) {
        this.cnicFrontUrl = cnicFrontUrl;
    }

    public String getCnicBackUrl() {
        return cnicBackUrl;
    }

    public void setCnicBackUrl(String cnicBackUrl) {
        this.cnicBackUrl = cnicBackUrl;
    }

}
