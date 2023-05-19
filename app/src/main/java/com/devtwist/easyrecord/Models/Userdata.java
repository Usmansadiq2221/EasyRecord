package com.devtwist.easyrecord.Models;

public class Userdata {


    private String uId, cnicNo, username, shopName, phoneNo, province, city, address, profilePic, cnicFront, cnicBack;
    private boolean isActive;
    private String token;

    public Userdata() {
    }

    public Userdata(String uId, String cnicNo, String username, String shopName, String phoneNo, String province, String city, String address, String profilePic, String cnicFront, String cnicBack, boolean isActive, String token) {
        this.uId = uId;
        this.cnicNo = cnicNo;
        this.username = username;
        this.shopName = shopName;
        this.phoneNo = phoneNo;
        this.province = province;
        this.city = city;
        this.address = address;
        this.profilePic = profilePic;
        this.cnicFront = cnicFront;
        this.cnicBack = cnicBack;
        this.isActive = isActive;
        this.token = token;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getCnicNo() {
        return cnicNo;
    }

    public void setCnicNo(String cnicNo) {
        this.cnicNo = cnicNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getCnicFront() {
        return cnicFront;
    }

    public void setCnicFront(String cnicFront) {
        this.cnicFront = cnicFront;
    }

    public String getCnicBack() {
        return cnicBack;
    }

    public void setCnicBack(String cnicBack) {
        this.cnicBack = cnicBack;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
