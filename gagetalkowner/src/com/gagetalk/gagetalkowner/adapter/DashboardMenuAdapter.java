package com.gagetalk.gagetalkowner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkowner.R;
import com.gagetalk.gagetalkowner.data.DashboardMenuData;

import java.util.ArrayList;

/**
 * Created by hyochan on 3/29/15.
 */
public class DashboardMenuAdapter extends BaseAdapter {
    private static final String TAG = "DashboardMenuAdapter";

    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList<DashboardMenuData> arrayList;

    public DashboardMenuAdapter(Context context, ArrayList<DashboardMenuData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    private static class ViewHolder{
        public RelativeLayout relMenu;
        public TextView txtMenu;
        public ImageView imgMenu;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
            1. title layout
            2. sub layout
         */

        ViewHolder viewHolder = null;

        int type = this.getItemViewType(position);
        if(convertView == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.dashboard_menu_list, null);
            viewHolder.relMenu = (RelativeLayout) convertView.findViewById(R.id.rel_menu);
            viewHolder.txtMenu = (TextView) convertView.findViewById(R.id.txt_menu);
            viewHolder.imgMenu = (ImageView) convertView.findViewById(R.id.img_menu);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtMenu.setText(arrayList.get(position).getName());
        viewHolder.imgMenu.setBackground(context.getResources().getDrawable(arrayList.get(position).getImg()));


        return convertView;
    }
}
