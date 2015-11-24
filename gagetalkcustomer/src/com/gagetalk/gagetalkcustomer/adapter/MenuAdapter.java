package com.gagetalk.gagetalkcustomer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.util.ImageDownloader;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.data.MenuData;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;

import java.util.ArrayList;


/**
 * Created by hyochan on 4/5/15.
 */
public class MenuAdapter extends ArrayAdapter<MenuData> {

    private static final String TAG = "CustomAdapter";
    private Context context;
    private ArrayList<MenuData> arrayCustom;


    public MenuAdapter(Context context, int resource, ArrayList<MenuData> arrayCustom) {
        super(context, resource, arrayCustom);
        this.context= context;
        this.arrayCustom = arrayCustom;
    }

    class ViewHolder{
        TextView txt;
        TextView txtMore;
        ImageView imgProfile;
        ImageView imgArrow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.custom_adapter, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txt = (TextView) convertView.findViewById(R.id.txt);
            viewHolder.txtMore = (TextView) convertView.findViewById(R.id.txt_more);
            viewHolder.imgProfile = (ImageView) convertView.findViewById(R.id.img_profile);
            viewHolder.imgArrow = (ImageView) convertView.findViewById(R.id.img_arrow);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        switch (parent.getId()){
            case R.id.list_account:
                switch (position){
                    case ConstValue.ACCOUNT_MAIN_POS:
                        viewHolder.imgProfile.setVisibility(View.GONE);
                        viewHolder.imgArrow.setVisibility(View.GONE);
                        viewHolder.txtMore.setVisibility(View.VISIBLE);
                        viewHolder.txt.setEnabled(false);
                        break;
                    case ConstValue.ACCOUNT_SIGNUP_DATE_POS:
                        viewHolder.imgProfile.setVisibility(View.GONE);
                        viewHolder.imgArrow.setVisibility(View.GONE);
                        viewHolder.txtMore.setVisibility(View.VISIBLE);
                        viewHolder.txt.setEnabled(false);
                        break;
                    case ConstValue.ACCOUNT_LOGIN_DATE_POS:
                        viewHolder.imgProfile.setVisibility(View.GONE);
                        viewHolder.imgArrow.setVisibility(View.GONE);
                        viewHolder.txtMore.setVisibility(View.VISIBLE);
                        viewHolder.txt.setEnabled(false);
                        break;
                    case ConstValue.ACCOUNT_PROFILE_IMG_POS:
                        viewHolder.txtMore.setVisibility(View.GONE);
                        viewHolder.imgArrow.setVisibility(View.GONE);
                        viewHolder.imgProfile.setVisibility(View.VISIBLE);
                        ImageDownloader.getInstance(context).getImage(
                                NetworkPreference.getInstance(context).getServerUrl() + ":" +
                                NetworkPreference.getInstance(context).getServerPort() + "/images/customer/" +
                                CustomerFunction.getInstance(context).getCusID() + ".png", viewHolder.imgProfile
                        );
                        viewHolder.txt.setEnabled(false);
                        break;
                    default:
                        viewHolder.imgProfile.setVisibility(View.GONE);
                        viewHolder.txtMore.setVisibility(View.VISIBLE);
                        viewHolder.imgArrow.setVisibility(View.VISIBLE);
                        viewHolder.txt.setEnabled(true);
                        break;
                }
                break;
            case R.id.list_help:
                // list help will show imgs to all list
                break;
            case R.id.list_setting:
                switch (position){
                    // hide array imgs in version info list
                    case ConstValue.SETTING_VERSION_INFO_POS:
                        viewHolder.imgArrow.setVisibility(View.GONE);
                        viewHolder.txt.setEnabled(false);
                        break;
                    default:
                        viewHolder.imgArrow.setVisibility(View.VISIBLE);
                        viewHolder.txt.setEnabled(true);
                        break;
                }
        }
        viewHolder.txt.setText(arrayCustom.get(position).getTxt());
        viewHolder.txtMore.setText(arrayCustom.get(position).getTxtMore());

        return convertView;
    }

    @Override
    public int getCount() {
        return arrayCustom.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MenuData getItem(int position) {
        return arrayCustom.get(position);
    }

}
