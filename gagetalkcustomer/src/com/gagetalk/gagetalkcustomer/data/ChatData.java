package com.gagetalk.gagetalkcustomer.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hyochan on 4/5/15.
 */
public class ChatData implements Parcelable {

    private int num;
    private String marId;
    private String marName;
    private String cusId;
    private String cusName;
    private String message;
    private int type;
    private String path;
    private String sendDate;
    private int readMsg;
    private String sender;

    public ChatData(){
        super();
    }

    public ChatData(String marId, String marName, String cusId, String cusName, String message,
                    int type, String path, String sendDate, int readMsg, String sender) {
        this.marId = marId;
        this.marName = marName;
        this.cusId = cusId;
        this.cusName = cusName;
        this.message = message;
        this.type = type;
        this.path = path;
        this.sendDate = sendDate;
        this.readMsg = readMsg;
        this.sender = sender;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMarId() {
        return marId;
    }

    public void setMarId(String marId) {
        this.marId = marId;
    }

    public String getMarName() {
        return marName;
    }

    public void setMarName(String marketName) {
        this.marName = marketName;
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public int getReadMsg() {
        return readMsg;
    }

    public void setReadMsg(int readMsg) {
        this.readMsg = readMsg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ChatData(Parcel in){
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public ChatData createFromParcel(Parcel source) {
            return new ChatData(source);
        }

        @Override
        public ChatData[] newArray(int size) {
            return new ChatData[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in){
        this.marId = in.readString();
        this.cusId = in.readString();
        this.marName = in.readString();
        this.message = in.readString();
        this.type = in.readInt();
        this.path = in.readString();
        this.sendDate = in.readString();
        this.readMsg = in.readInt();
        this.sender = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(marId);
        dest.writeString(cusId);
        dest.writeString(marName);
        dest.writeString(message);
        dest.writeInt(type);
        dest.writeString(path);
        dest.writeString(sendDate);
        dest.writeInt(readMsg);
        dest.writeString(sender);
    }
}
