package com.gagetalk.gagetalkcommon.constant;

/**
 * Created by hyochan on 3/28/15.
 */
public class ConstValue {

    // RESPONSE code
    public static final int RESPONSE_WRONG_PARAMETER = -3;
    public static final int RESPONSE_ALREADY_INSERTED  = -2;
    public static final int RESPONSE_NOT_LOGGED_IN = -1;
    public static final int RESPONSE_NO_DATA = 0;
    public static final int RESPONSE_SUCCESS = 1;
    public static final int RESPONSE_NO_REQ_PARAM = 2;


    // fragment number inside MainFragment
    public static final int HOME_FRAGMENT = 0;
    public static final int MSG_FRAGMENT = 1;

    // fragment number inside MainActivity
    public static final int MAIN_FRAGMENT = 0;
    public static final int ACCOUNT_FRAGMENT = 1;
    public static final int HELP_FRAGMENT = 2;
    public static final int SETTING_FRAGMENT = 3;

    // activity request
    public static final int LOGOUT_ACTIVITY_REQUEST = 0;
    public static final int REQUEST_PHOTO_ACTIVITY = 2;
    public static final int SIGNUP_ID_ALREADY_EXISTS = 0;

    // account list
    public static final int ACCOUNT_MAIN_POS = 0;
    public static final int ACCOUNT_PASSWORD_POS = 1;
    public static final int ACCOUNT_NAME_POS = 2;
    public static final int ACCOUNT_PHONE_POS = 3;
    public static final int ACCOUNT_PROFILE_EMAIL_POS = 4;
    public static final int ACCOUNT_PROFILE_IMG_POS = 5;
    public static final int ACCOUNT_PROFILE_DESCRIPTION_POS = 6;
    public static final int ACCOUNT_SIGNUP_DATE_POS = 7;
    public static final int ACCOUNT_LOGIN_DATE_POS = 8;

    // help list
    public static final int HELP_GUIDE_POS = 0;
    public static final int HELP_MOST_QUESTION_POS = 1;
    public static final int HELP_CONTACT_POS = 2;
    public static final int HELP_ABOUT_POS = 3;

    // setting list
    public static final int SETTING_MANAGE_MSG_POS = 0;
    public static final int SETTING_MANAGE_ALERT_POS = 1;
    public static final int SETTING_VERSION_INFO_POS = 2;

    // request url sql
    public static final int REQUEST_SELECT = 0;
    public static final int REQUEST_UPDATE = 1;
    public static final int REQUEST_INSERT = 2;
    public static final int REQUEST_DELETE = 3;


    // result failed // result ok
    public static final int RESULT_FAILED = 0;
    public static final int RESULT_OK = 1;

    // CustomerAccountTask for update
    public static final int UPDATE_NAME = 0;
    public static final int UPDATE_PASSWORD = 1;
    public static final int UPDATE_PHONE = 2;
    public static final int UPDATE_IMG = 3;
    public static final int UPDATE_DESCRIPTION = 4;
    public static final int UPDATE_EMAIL = 5;

    // message type
    public static final int MSG_TEXT = 0;
    public static final int MSG_IMG = 1;
    public static final int MSG_FILE = 2;
    public static final int MSG_MOV = 3;

    // readmessage type
    public static final int MSG_NOT_READ = 0;
    public static final int MSG_READ = 1;

    // chatroom & chat db field
    public static final int DB_CHAT_MAR_ID = 1;
    public static final int DB_CHAT_MAR_NAME = 2;
    public static final int DB_CHAT_CUS_ID = 3;
    public static final int DB_CHAT_CUS_NAME = 4;
    public static final int DB_CHAT_MESSAGE = 5;
    public static final int DB_CHAT_TYPE = 6;
    public static final int DB_CHAT_PATH = 7;
    public static final int DB_CHAT_SEND_DATE = 8;
    public static final int DB_CHAT_READ_MSG = 9;
    public static final int DB_CHAT_SENDER = 10;

    public static final int IMG_SIZE = 128;

    // receiver string
    public static final String LOGIN_FILTER = "com.gagetalk.customer.login";
    public static final String MOVE_TO_MSG_FRAG_FILTER = "com.gagetalk.customer.moveToMsgFrag";
    public static final String UPDATE_MSG_ADAPTER_FILTER = "com.gagetalk.customer.updateMsgAdapter";

    // string for chat receiver
    public static final String SERVER_ALIVE_RECEIVER = "com.gagetalk.gagetalkcustomer.server_alive";
    public static final String CHAT_MY_RECEIVER = "com.gagetalk.gagetalkcustomer.message_my";
    public static final String CHAT_PEERS_RECEIVER = "com.gagetalk.gagetalkcustomer.message_peers";
    public static final String CHAT_UNREAD_RECEIVER = "com.gagetalk.gagetalkcustomer.message_unread_request";
    public static final String CHAT_READ_RECEIVER = "com.gagetalk.gagetalkcustomer.message_read";

    // wifi status
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;

    // value for read_msg
    /*
        0 : 메시지 읽지 않음
        1 : 메시지 읽음¡¡
     */
    public static final int READ_MSG_UNREAD = 0;
    public static final int READ_MSG_READ = 1;

    // MainActivity started from chat notification
    public static final String ACTIVITY_STARTED_FROM_CHAT_ACTIVITY = "activity_started_from_chat_activity";
    public static final String ACTIVITY_STARTED_FROM_CHAT_NOTI = "activity_started_from_chat_noti";

    // Notification ID
    public static final int CHAT_NOTIFICATION_ID = 100;

}
