package com.gagetalk.gagetalkcustomer.activities_dialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.gagetalk.gagetalkcommon.util.ImageDownloader;

/**
 * Created by hyochan on 7/22/15.
 */
public class ChatDialogActivity extends Activity {

    private String TAG = "ChatDialogActivity";
    private ImageView imgSender;
    private TextView txtName;
    private TextView txtChat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_dialog_activity);

        imgSender = (ImageView) findViewById(R.id.img_sender);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtChat = (TextView) findViewById(R.id.txt_chat);

        String marId = getIntent().getStringExtra("mar_id");

        ImageDownloader.getInstance(this).getImage(NetworkPreference.getInstance(this).getServerUrl() + ":" +
                NetworkPreference.getInstance(this).getServerPort() + "/images/" + marId.replaceAll("\\s", "") + ".png", imgSender);
        txtName.setText(getIntent().getStringExtra("mar_name"));
        txtChat.setText(getIntent().getStringExtra("message"));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, R.anim.fade_out);
            }
        }, 3000);
    }
}