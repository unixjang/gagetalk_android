package com.gagetalk.gagetalkcustomer.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hyochan on 3/29/15.
 */
public class MarketData implements Parcelable{
    String marId;
    String marName;
    String tel;
    String phone;
    String img;
    String email;
    String address;
    String category;
    String homepage;
    String description;
    String dateSign;
    String dateLogin;

    public MarketData(){
        super();
    }

    public MarketData(String marId, String marName, String tel, String phone, String img, String email, String address, String category,
                      String homepage, String description, String dateSign, String dateLogin) {
        this.marId = marId;
        this.marName = marName;
        this.tel = tel;
        this.phone = phone;
        this.img = img;
        this.email = email;
        this.address = address;
        this.category = category;
        this.homepage = homepage;
        this.description = description;
        this.dateSign = dateSign;
        this.dateLogin = dateLogin;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(marId);
        dest.writeString(marName);
        dest.writeString(tel);
        dest.writeString(phone);
        dest.writeString(img);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(category);
        dest.writeString(homepage);
        dest.writeString(description);
        dest.writeString(dateSign);
        dest.writeString(dateLogin);
    }

    public MarketData(Parcel in){
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in){
        this.marId = in.readString();
        this.marName = in.readString();
        this.tel = in.readString();
        this.phone = in.readString();
        this.img = in.readString();
        this.email = in.readString();
        this.address = in.readString();
        this.category = in.readString();
        this.homepage = in.readString();
        this.description = in.readString();
        this.dateSign = in.readString();
        this.dateLogin = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public MarketData createFromParcel(Parcel source) {
            return new MarketData(source);
        }

        @Override
        public MarketData[] newArray(int size) {
            return new MarketData[size];
        }
    };

    public String getMarId() {
        return marId;
    }

    public void setMarId(String marId) {
        this.marId = marId;
    }

    public String getMarName() {
        return marName;
    }

    public void setMarName(String marName) {
        this.marName = marName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateSign() {
        return dateSign;
    }

    public void setDateSign(String dateSign) {
        this.dateSign = dateSign;
    }

    public String getDateLogin() {
        return dateLogin;
    }

    public void setDateLogin(String dateLogin) {
        this.dateLogin = dateLogin;
    }
}
