package com.gagetalk.gagetalkcustomer.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.util.HeaderGridView;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities.MarketViewMoreActivity;
import com.gagetalk.gagetalkcustomer.adapter.MarketAdapter;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcustomer.data.MarketData;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 3/28/15.
 */
public class HomeFragment extends Fragment
    implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{

    private static final String TAG = "HomeFragment";
    private Activity activity;
    private Context context;

    private ImageView imgTag;
    private EditText editSearch;
    private ImageView imgErase;
    private GridView gridMarket;
    private MarketAdapter marketAdapter;
    private SwipeRefreshLayout swipeLayout;

    private BroadcastReceiver homeFragReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.i(TAG, "action : " + action);
            if(action.equals(ConstValue.SERVER_ALIVE_RECEIVER)){
                MyLog.d(TAG, "SERVER ALIVE RECEIVER !!!!");
                reqServerMarketList(null);
            }
        }
    };

    @Override
    public void onDestroyView() {
        if(homeFragReceiver != null)
            activity.unregisterReceiver(homeFragReceiver);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        context = activity.getApplicationContext();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstValue.SERVER_ALIVE_RECEIVER);
        activity.registerReceiver(homeFragReceiver, intentFilter);

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        imgTag = (ImageView) view.findViewById(R.id.img_tag);
        editSearch = (EditText) view.findViewById(R.id.edit_search);
        imgErase = (ImageView) view.findViewById(R.id.img_erase);
        gridMarket = (HeaderGridView) view.findViewById(R.id.grid_market);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        imgTag.setOnClickListener(this);
        imgErase.setOnClickListener(this);

        gridMarket.setOnItemClickListener(this);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                MyLog.i(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MyLog.i(TAG, "onTextChanged");
                if (marketAdapter != null)
                    marketAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                MyLog.i(TAG, "afterTextChanged");
            }
        });

        reqServerMarketList(null);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_tag:
                // CustomToast.getInstance(context).createToast("img tag clicked");
                imgTag.setSelected(true);
                final ListPopupWindow listPopupWindow = new ListPopupWindow(context);
                final ArrayAdapter arrayAdapter = new ArrayAdapter<>(
                        context,
                        R.layout.pop_list_category,
                        R.id.txt,
                        getResources().getStringArray(R.array.category)
                );
                listPopupWindow.setAdapter(arrayAdapter);
                listPopupWindow.setAnchorView(imgTag);
                listPopupWindow.setWidth((int) getResources().getDimension(R.dimen.category_popup_width));
                listPopupWindow.setHeight((int) getResources().getDimension(R.dimen.category_popup_height));
                listPopupWindow.setModal(true);
                listPopupWindow.setHorizontalOffset(12);
                listPopupWindow.setVerticalOffset(18);
                listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        imgTag.setSelected(false);
                    }
                });
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // CustomToast.getInstance(context).createToast("item : " + arrayAdapter.getItem(position).toString());
                        reqServerMarketList(arrayAdapter.getItem(position).toString());
                        listPopupWindow.dismiss();
                    }
                });
                listPopupWindow.show();
                break;
            case R.id.img_erase:
                editSearch.setText("");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.grid_market:
                // start MarketViewMoreActivity
                Intent intent = new Intent(context, MarketViewMoreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("marketVal", marketAdapter.getItem(position));
                //intent.putExtra()
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.zoom_in, R.anim.fade_out);
                // CustomToast.getInstance(context).createToast("position : " + position);
                break;
        }
    }

    @Override
    public void onRefresh() {
        reqServerMarketList(null);
        // swipeLayout.setRefreshing(false);
    }

    private void reqServerMarketList(String category) {
        String url = ReqUrl.CustomerMarketTask;

        HashMap<String, String> paramMap = new HashMap<>();
        if (category == null) category = "";
        paramMap.put("category", category);
        RequestParams requestParams = new RequestParams(paramMap);
        Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ArrayList<MarketData> arrayMarket = new ArrayList<>();
                try {
                    JSONArray jsonMarkets = response.getJSONArray("market");
                    for (int i = 0; i < jsonMarkets.length(); i++) {
                        JSONObject json_obj = jsonMarkets.getJSONObject(i);
                        MyLog.i(TAG, "market name : " + json_obj.getString("mar_name"));
                        MyLog.i(TAG, "category name : " + json_obj.getString("category"));
                        MarketData marketData = new MarketData(
                                json_obj.getString("mar_id"),
                                json_obj.getString("mar_name"),
                                json_obj.getString("tel"),
                                json_obj.getString("phone"),
                                NetworkPreference.getInstance(context).getServerUrl()
                                        + ":" + NetworkPreference.getInstance(context).getServerPort()
                                        + "/images/" + json_obj.getString("img"),
                                json_obj.getString("email"),
                                json_obj.getString("address"),
                                json_obj.getString("category"),
                                json_obj.getString("homepage"),
                                json_obj.getString("description"),
                                json_obj.getString("date_sign"),
                                json_obj.getString("date_login")
                        );
                        arrayMarket.add(marketData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MyLog.e(TAG, "Error parsing data " + e.toString());
                }
                swipeLayout.setRefreshing(false);
                if (arrayMarket != null) {
                    marketAdapter = new MarketAdapter(context, R.id.txt_name_peer, arrayMarket);
                    gridMarket.setAdapter(marketAdapter);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Network.getInstance(context).toastErrorMsg(activity);
                swipeLayout.setRefreshing(false);
            }
        });
    }
}