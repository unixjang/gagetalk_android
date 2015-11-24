package com.gagetalk.gagetalkcommon.api;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gagetalk.gagetalkcommon.R;
import com.gagetalk.gagetalkcommon.constant.ConstPrefValue;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hyochan on 3/28/15.
 */
public class Function {

    private static final String TAG = "Function";

    private static Function function;
    private SharedPreferences sharedPreferences;
    private Context context;

    private Function(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static Function getInstance(Context context) {
        if (function == null) function = new Function(context);
        return function;
    }

    public void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void logErrorParsingJson(Exception e){
        MyLog.e(TAG, "Error parsing json data " + e.toString());
        e.printStackTrace();
    }

    public Bitmap getBitmapFromURL(String strURL) {


        try {
            URL url = new URL(strURL);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encodeURLIfNeed(String input) {
        Pattern HANGLE_PATTERN = Pattern.compile("[가-힣]");
        // Pattern HANGLE_PATTERN = Pattern.compile("[\\x{ac00}-\\x{d7af}]");
        if (input == null || input.equals("")) {
            return input;
        }

        Matcher matcher = HANGLE_PATTERN.matcher(input);
        while(matcher.find()) {
            String group = matcher.group();

            try {
                input = input.replace(group, URLEncoder.encode(group, "UTF-8"));
            } catch (UnsupportedEncodingException ignore) {
            }
        }

        return input;
    }

    public LinearLayout getEmptyViewForListView(){
        LinearLayout emptyView = new LinearLayout(context);
        emptyView.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Function.getInstance(context).dpToPx(30));
        emptyView.setLayoutParams(lp);
        return  emptyView;
    }

    public boolean checkEditTextEmpty(List<EditText> editTextList){
        boolean result = false;
        Iterator<EditText> itr = editTextList.iterator();
        while (itr.hasNext()) { // 값이 나올때까지 while문을 돈다
            EditText editText = itr.next();
            if(editText.getText().toString().equals("")){
                return true;
            }
        }
        return result;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String strPath,int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(strPath, options);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)); //px
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)); //dp
    }

    // => from here, used when checking editform when signup
    public Boolean checkId(String id) {
        // check if id is alphanumeric
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a)
                return false;
        }
        return true;
    }

    public Boolean checkPw(String password, String password_ok) {
        return password.equals(password_ok); // return true or false
    }

    public Boolean checkName(String name) {
        // return false if it is not fully hangul
        final int HANGUL_UNICODE_START = 0xAC00;
        final int HANGUL_UNICODE_END = 0xD7AF;

        int text_count = name.length();
        int is_hangul_count = 0;

        for (int i = 0; i < text_count; i++) {
            char syllable = name.charAt(i);
            if ((HANGUL_UNICODE_START <= syllable)
                    && (syllable <= HANGUL_UNICODE_END)) {
                is_hangul_count++;
            }
        }

        return (is_hangul_count == text_count); // return true or false
    }

    public Boolean checkPhone(String phone) {
        String regex = "^\\+?[0-9. ()-]{10,25}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);

        return matcher.matches(); // return true or false
    }

    public Boolean checkEmail(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}