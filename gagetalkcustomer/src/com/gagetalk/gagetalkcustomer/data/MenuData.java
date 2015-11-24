package com.gagetalk.gagetalkcustomer.data;

/**
 * Created by hyochan on 4/5/15.
 */
public class MenuData {
    String txt;
    String txtMore;

    public MenuData(String txt, String txtMore) {
        this.txt = txt;
        this.txtMore = txtMore;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getTxtMore() {
        return txtMore;
    }

    public void setTxtMore(String txtMore) {
        this.txtMore = txtMore;
    }
}
