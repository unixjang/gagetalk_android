package com.gagetalk.gagetalkcustomer.activities_dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.gagetalk.gagetalkcommon.util.ImageDownloader;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 4/5/15.
 */
public class ProfileImgDialogActivity extends Activity
    implements View.OnClickListener{

    private static final String TAG = "ProfileImgDialogActivity";

    private Activity activity;
    private Context context;
    private ImageView imgClose;
    private ImageView imgProfile;
    private Button btnUpdate;
    private byte[] byteArray;
    private String imgPath;
    private Bitmap selectedImage;

    // usage of intent onclick listener
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profile_img_dialog_activity);

        activity = this;
        context = this;
        imgClose = (ImageView) findViewById(R.id.img_close);
        imgProfile = (ImageView) findViewById(R.id.img_profile);
        btnUpdate = (Button) findViewById(R.id.btn_update);

        Bundle extras = getIntent().getExtras();
        Bitmap bmp = extras.getParcelable("imagebitmap");
        imgProfile.setImageBitmap(bmp);

        imgClose.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.fade_out);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_close:
                onBackPressed();
                break;
            case R.id.img_profile:
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, ConstValue.REQUEST_PHOTO_ACTIVITY);
                break;
            case R.id.btn_update:
                if(imgPath != null)
                    reqServerUploadImg();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imgReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imgReturnedIntent);

        switch(requestCode) {
            case ConstValue.REQUEST_PHOTO_ACTIVITY:
                if(resultCode == RESULT_OK){
                    final Uri imageUri = imgReturnedIntent.getData();
                    imgPath = Function.getInstance(context).getRealPathFromURI(imageUri);
                    selectedImage = Function.getInstance(context).decodeSampledBitmapFromResource(
                            imgPath,
                            imgProfile.getWidth(),
                            imgProfile.getHeight()
                    );
                    imgProfile.setImageBitmap(selectedImage);
                }
        }
    }

    private void reqServerUploadImg(){
        if(Network.getInstance(context).isNetworkAvailable()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
/*
            // Bitmap src = BitmapFactory.decodeFile("/sdcard/image.jpg", options);
            int height=selectedImage.getHeight();
            int width=selectedImage.getWidth();
            int divideSize = 1;
            if(height > 1000 || width > 1000){
                divideSize = 2;
            }
            MyLog.i(TAG, "height : " + height + ", width : " + width);

*/
            Bitmap resized = Bitmap.createScaledBitmap(selectedImage,
                    ConstValue.IMG_SIZE,
                    ConstValue.IMG_SIZE,
                    true);
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            String url = ReqUrl.CustomerAccountTask + ReqUrl.UPDATE;
            String imgName = CustomerFunction.getInstance(context).getCusID() + ".png";
            RequestParams requestParams = new RequestParams();
            requestParams.put("request_param", ConstValue.UPDATE_IMG);
            requestParams.put("request_value", imgName);
            requestParams.put("image", new ByteArrayInputStream(byteArray), imgName);
            Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Network.getInstance(context).toastErrorMsg(activity);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    int resultCode = 0;
                    try {
                        resultCode = response.getInt("resultCode");
                    }catch (Exception e){
                        Function.getInstance(context).logErrorParsingJson(e);
                    }
                    int checkResponse = CustomerNetwork.getInstance(context).checkResponse(resultCode, true);
                    if(checkResponse == ConstValue.RESPONSE_NOT_LOGGED_IN){
                        onBackPressed();
                    }
                    else if (checkResponse == ConstValue.RESPONSE_SUCCESS) {
                        CustomToast.getInstance(context).createToast(
                                getResources().getString(R.string.upload_image_success)
                        );
                        Intent intent = new Intent();
                        intent.putExtra("imgArray", byteArray);
                        setResult(RESULT_OK, intent);
                        ImageDownloader.getInstance(context).updateCache(
                                NetworkPreference.getInstance(context).getServerUrl() + ":" + NetworkPreference.getInstance(context).getServerPort() +
                                        "/images/customer/" + CustomerFunction.getInstance(context).getCusID() + ".png"
                        );
                        onBackPressed();
                    }
                }
            });
        } else{
            CustomToast.getInstance(context).
                    createToast(context.getResources().getString(R.string.network_error));
        }
    }
}