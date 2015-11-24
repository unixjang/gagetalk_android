package com.gagetalk.gagetalkcustomer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.gagetalk.gagetalkcommon.util.ImageDownloader;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.data.ChatData;

import java.util.ArrayList;

/**
 * Created by hyochan on 14. 10. 21..
 */
public class ChatAdapter extends ArrayAdapter<ChatData> implements Filterable {

    private final String TAG = "ChatAdapter";
    Context context;
    ArrayList<ChatData> arrayChat;
    ArrayList<ChatData> arrayForSearch;


    public ChatAdapter(Context context, int resource, ArrayList<ChatData> arrayChat) {
        super(context, resource, arrayChat);
        this.context = context;
        this.arrayChat = arrayChat;
        this.arrayForSearch = arrayChat;
    }

    class ViewHolder{
        public LinearLayout linMy;
        public ImageView imgMy;
        public ImageView imgNewMy;
        public TextView txtMsgMy;
        public TextView txtDateMy;
        public TextView txtNameMy;
        public LinearLayout linPeer;
        public ImageView imgNewPeer;
        public ImageView imgPeer;
        public TextView txtMsgPeer;
        public TextView txtDatePeer;
        public TextView txtNamePeer;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.chat_adapter, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.linMy = (LinearLayout) convertView.findViewById(R.id.lin_my);
            viewHolder.imgMy = (ImageView) convertView.findViewById(R.id.img_my);
            viewHolder.imgNewMy = (ImageView) convertView.findViewById(R.id.img_new_my);
            viewHolder.txtMsgMy = (TextView) convertView.findViewById(R.id.txt_msg_my);
            viewHolder.txtNameMy = (TextView) convertView.findViewById(R.id.txt_name_my);
            viewHolder.txtDateMy = (TextView) convertView.findViewById(R.id.txt_date_my);
            viewHolder.linPeer = (LinearLayout) convertView.findViewById(R.id.lin_peer);
            viewHolder.imgPeer = (ImageView) convertView.findViewById(R.id.img_peer);
            viewHolder.imgNewPeer = (ImageView) convertView.findViewById(R.id.img_new_peer);
            viewHolder.txtMsgPeer = (TextView) convertView.findViewById(R.id.txt_msg_peer);
            viewHolder.txtNamePeer = (TextView) convertView.findViewById(R.id.txt_name_peer);
            viewHolder.txtDatePeer = (TextView) convertView.findViewById(R.id.txt_date_peer);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(arrayChat.get(position).getReadMsg() == ConstValue.MSG_READ){
            viewHolder.imgNewMy.setVisibility(View.GONE);
            viewHolder.imgNewPeer.setVisibility(View.GONE);
        }
        else {
            viewHolder.imgNewMy.setVisibility(View.VISIBLE);
            viewHolder.imgNewPeer.setVisibility(View.VISIBLE);
        }

        if(arrayChat.get(position).getSender().equals(CustomerFunction.getInstance(context).getCusID())){
            viewHolder.linMy.setVisibility(View.VISIBLE);
            viewHolder.linPeer.setVisibility(View.GONE);
            viewHolder.txtMsgMy.setText(arrayChat.get(position).getMessage());
            viewHolder.txtDateMy.setText(arrayChat.get(position).getSendDate());
            viewHolder.txtNameMy.setText(arrayChat.get(position).getCusName());
            ImageDownloader.getInstance(context).getImage(
                    NetworkPreference.getInstance(context).getServerUrl() + ":" +
                            NetworkPreference.getInstance(context).getServerPort() + "/images/customer/" +
                            CustomerFunction.getInstance(context).getCusID() + ".png", viewHolder.imgMy
            );
        }else{
            viewHolder.linMy.setVisibility(View.GONE);
            viewHolder.linPeer.setVisibility(View.VISIBLE);
            viewHolder.txtMsgPeer.setText(arrayChat.get(position).getMessage());
            viewHolder.txtDatePeer.setText(arrayChat.get(position).getSendDate());
            viewHolder.txtNamePeer.setText(arrayChat.get(position).getMarName());
            ImageDownloader.getInstance(context).getImage(
                    NetworkPreference.getInstance(context).getServerUrl() + ":" +
                            NetworkPreference.getInstance(context).getServerPort() + "/images/" +
                            arrayChat.get(position).getMarId() + ".png", viewHolder.imgPeer
            );
        }

        return convertView;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ChatData getItem(int position) {
        return arrayChat.get(position);
    }

    @Override
    public int getCount() {
        return arrayChat.size();
    }

    @Override
    public Filter getFilter() {
        return chatFilter;
    }

    Filter chatFilter = new Filter() {
        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            arrayChat = (ArrayList<ChatData>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        public FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<ChatData> arrayTmp=new ArrayList<>();

            if(constraint != null && arrayForSearch !=null) {
                int length = arrayForSearch.size();
                Log.i("Filtering", "glossaries size" + length);
                int i=0;
                while(i<length){
                    ChatData item = arrayForSearch.get(i);
                    // Real filtering:
                    if(item.getMessage() != null &&
                            item.getMessage().toLowerCase().contains(constraint.toString().toLowerCase())){
                        arrayTmp.add(item);
                    }
                    i++;
                }

                filterResults.values = arrayTmp;
                filterResults.count = arrayTmp.size();
                Log.i("Filtering", "Filter result count size"+filterResults.count);
            }
            return filterResults;
        }
    };

}
