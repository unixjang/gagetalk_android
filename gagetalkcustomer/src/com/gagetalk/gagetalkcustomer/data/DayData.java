package com.gagetalk.gagetalkcustomer.data;

import com.gagetalk.gagetalkcommon.util.MyLog;

import java.util.Calendar;

/**
 * Created by hyochan on 4/5/15.
 */
public class DayData {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int second;

    public DayData(int year, int month, int day, int hour, int min, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.second = second;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    // TODO : SHOW date for chat
    public String getChatDate(){

        String result;

        Calendar cal = Calendar.getInstance();
        int t_year = cal.get(Calendar.YEAR);
        int t_month = cal.get(Calendar.MONTH) + 1;
        int t_day = cal.get(Calendar.DATE);

        String strMin = ":";
        if(min < 10){
            strMin = ":0";
        }

        // parse the hour / min first
        String strAMPM = "AM";
        if(hour > 12){
            strAMPM = "PM";
            hour = hour-12;
        }
        result = hour + strMin + min + " " + strAMPM;

        // 만약 올해가 지난 날짜면 : 년 표시
        if(t_year > year){
            result = year + "년 " + month + "월 " + day + "일, " + result;
        }
        // 만약 이번달이 지났으면 : 월 표시
        else if(t_month > month){
            result = month + "월 " + day + "일, " + result;
        }
        // 만약 오늘이 아니면 : 날짜 표시
        else if(t_day > day){
            result = day + "일, " + result;
        }
        return result;
    }
}
