package com.gagetalk.gagetalkowner.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkowner.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 3/29/15.
 */
public class SignupActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "SignupActivity";
    private Activity activity;
    private Context context;
    // ui
    private ImageView imgClose;
    private EditText editId;
    private EditText editEmail;
    private EditText editPw;
    private EditText editPwOk;
    private EditText editMarketName;
    private EditText editTel;
    private EditText editPhone;
    private ImageView imgProfile;
    private EditText editAddress;
    private Spinner spinCategory;
    private EditText editHomepage;
    private EditText editDescription;
    private Button btnProfile;
    private Button btnSignup;

    private String[] arrCategory;
    private int[] arrDrawable = {
            R.drawable.sel_ic_wear,
            R.drawable.sel_ic_food,
            R.drawable.sel_ic_salon,
            R.drawable.sel_ic_hospital,
            R.drawable.sel_ic_cosmetic,
            R.drawable.sel_ic_sing,
            R.drawable.sel_ic_computer,
            R.drawable.sel_ic_coffee,
            R.drawable.sel_ic_beer,
            R.drawable.sel_ic_electronic
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        activity = this;
        context = this;

        arrCategory = getResources().getStringArray(R.array.category);

        imgClose = (ImageView) findViewById(R.id.img_close);
        editId = (EditText) findViewById(R.id.edit_id);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPw = (EditText) findViewById(R.id.edit_pw);
        editPwOk = (EditText) findViewById(R.id.edit_pw_ok);
        editMarketName = (EditText) findViewById(R.id.edit_market_name);
        editTel = (EditText) findViewById(R.id.edit_tel);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        imgProfile = (ImageView) findViewById(R.id.img_profile);
        editAddress = (EditText) findViewById(R.id.edit_address);
        spinCategory = (Spinner) findViewById(R.id.spin_category);
        editHomepage = (EditText) findViewById(R.id.edit_homepage);
        editDescription = (EditText) findViewById(R.id.edit_description);
        btnProfile  = (Button) findViewById(R.id.btn_profile);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        spinCategory.setAdapter(new SpinnerAdapter(this, R.layout.spinner_layout, arrCategory));

        // oncreate hide the imgProfile
        imgProfile.setEnabled(false);

        imgClose.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
    }

    // Adapter class for spinner control
    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();
            View spinnerLayout = inflater.inflate(R.layout.spinner_layout, parent, false);
            TextView txt=(TextView)spinnerLayout.findViewById(R.id.txt);
            txt.setText(arrCategory[position]);

            ImageView img = (ImageView)spinnerLayout.findViewById(R.id.img);
            img.setImageResource(arrDrawable[position]);
            return spinnerLayout;
        }
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
            case R.id.btn_profile:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, ConstValue.REQUEST_PHOTO_ACTIVITY);
                break;
            case R.id.btn_signup:
                // check if the correct data is set to edit text
                List<EditText> editTextList = new ArrayList<>();
                editTextList.add(editEmail);
                editTextList.add(editMarketName);
                editTextList.add(editPw);
                editTextList.add(editPwOk);
                editTextList.add(editTel);
                editTextList.add(editPhone);
                editTextList.add(editAddress);
                editTextList.add(editHomepage);
                editTextList.add(editDescription);
                // check empty editText
                if(Function.getInstance(context).checkEditTextEmpty(editTextList)){
                    CustomToast.getInstance(context).createToast(
                            getResources().getString(R.string.plz_fill_edit_text)
                    );
                    return;
                }
                // check if img is empty
                if(!imgProfile.isEnabled()){
                    CustomToast.getInstance(context).createToast(
                            getResources().getString(R.string.plz_pick_image)
                    );
                    return;
                }
                reqServerOwnerSignup();
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imgReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imgReturnedIntent);
        switch(requestCode) {
            case ConstValue.REQUEST_PHOTO_ACTIVITY:
                if(resultCode == RESULT_OK){
                    btnProfile.setVisibility(View.INVISIBLE);
                    final Uri imageUri = imgReturnedIntent.getData();
                    final String imgPath = Function.getInstance(context).getRealPathFromURI(imageUri);
                    Bitmap selectedImage = Function.getInstance(context).decodeSampledBitmapFromResource(
                            imgPath,
                            imgProfile.getWidth(),
                            imgProfile.getHeight()
                    );
                    imgProfile.setImageBitmap(selectedImage);
                    imgProfile.setEnabled(true);
                }
        }
    }

    /*
        'market_name VARCHAR(32),' +
        'password VARCHAR(32), ' +
        'tel VARCHAR(16), ' +
        'phone VARCHAR(16), ' +
        'img VARCHAR(32), ' +
        'email VARCHAR(32), ' +
        'address VARCHAR(32), ' +
        'category VARCHAR(32), ' +
        'homepage VARCHAR(32), ' +
        'description TEXT, ' +
        'date_sign datetime, ' +
        'date_login datetime
     */

    // TODO : should implement below
    private void reqServerOwnerSignup(){
        String url = ReqUrl.OwnerSignupTask;
        String imgName = editMarketName.getText().toString() + ".png";
        HashMap<String, String> paramMap = new HashMap<>();
        // put edittext params
        paramMap.put("mar_id", editId.getText().toString());
        paramMap.put("password", editPw.getText().toString());
        paramMap.put("mar_name", editMarketName.getText().toString());
        paramMap.put("email", editEmail.getText().toString());
        paramMap.put("tel", editTel.getText().toString());
        paramMap.put("phone", editPhone.getText().toString());
        paramMap.put("image_name", imgName);
        paramMap.put("address", editAddress.getText().toString());
        paramMap.put("category", spinCategory.getSelectedItem().toString());
        paramMap.put("homepage", editHomepage.getText().toString());
        paramMap.put("description", editDescription.getText().toString());
        RequestParams requestParams = new RequestParams(paramMap);
        // put image params
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable)imgProfile.getDrawable()).getBitmap();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        /*
        int height=bitmap.getHeight();
        int width=bitmap.getWidth();
        int divideSize = 1;
        if(height > 1000 || width > 1000) {
            divideSize = 2;
        }
        MyLog.i(TAG, "height : " + height + ", width : " + width);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,
                width / divideSize,
                height / divideSize,
                true);
        */
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,
                ConstValue.IMG_SIZE,
                ConstValue.IMG_SIZE,
                true);
        resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        requestParams.put("image", new ByteArrayInputStream(byteArray), imgName);

        Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                int resultCode = 0;
                try {
                    resultCode = response.getInt("resultCode");
                } catch (Exception e) {
                    Function.getInstance(context).logErrorParsingJson(e);
                }
                if (resultCode == ConstValue.RESULT_FAILED) {
                    String strDuplicate = null;
                    try {
                        strDuplicate = response.getString("duplicate");
                    } catch (Exception e) {
                        Function.getInstance(context).logErrorParsingJson(e);
                    }
                    if(strDuplicate.equals("mar_id")) {
                        CustomToast.getInstance(context).
                                createToast(getResources().getString(R.string.signup_id_exist));
                    }else if(strDuplicate.equals("market_name")){
                        CustomToast.getInstance(context).
                                createToast(getResources().getString(R.string.signup_owner_exist));
                    }
                } else {
                    CustomToast.getInstance(context).createToast(
                            getResources().getString(R.string.signup_success_plz_login)
                    );
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Network.getInstance(context).toastErrorMsg(activity);
            }
        });
    }


}