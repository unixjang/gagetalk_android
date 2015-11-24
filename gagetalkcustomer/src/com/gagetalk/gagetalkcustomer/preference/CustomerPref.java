package com.gagetalk.gagetalkcustomer.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gagetalk.gagetalkcustomer.data.DayData;

/**
 * Created by hyochan on 3/29/15.
 */
public class CustomerPref {

    private String cusId;
    private String password;
    private String email;
    private String name;
    private String phone;
    private String img;
    private String description;
    private String dateSign;
    private DayData dateLogin;

    private static CustomerPref customerPref;
    private SharedPreferences sharedPreferences;
    private Context context;

    private CustomerPref(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static CustomerPref getInstance(Context context) {
        if (customerPref == null) customerPref = new CustomerPref(context);
        return customerPref;
    }


    public CustomerPref(String cusId, String email,
                        String password, String name,
                        String phone, String img,
                        String description, String dateSign,
                        DayData dateLogin) {
        this.cusId = cusId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.img = img;
        this.description = description;
        this.dateSign = dateSign;
        this.dateLogin = dateLogin;
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public DayData getDateLogin() {
        return dateLogin;
    }

    public void setDateLogin(DayData dateLogin) {
        this.dateLogin = dateLogin;
    }
}
