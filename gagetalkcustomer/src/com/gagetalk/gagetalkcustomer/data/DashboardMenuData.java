package com.gagetalk.gagetalkcustomer.data;

/**
 * Created by hyochan on 3/28/15.
 */
public class DashboardMenuData {
    private int img;
    private String name;

    public DashboardMenuData(int img, String name) {
        this.img = img;
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


