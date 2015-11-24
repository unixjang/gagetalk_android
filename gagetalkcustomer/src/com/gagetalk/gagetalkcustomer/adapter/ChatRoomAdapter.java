package com.gagetalk.gagetalkcustomer.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.gagetalk.gagetalkcommon.util.ImageDownloader;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.data.ChatRoomData;

import java.util.ArrayList;

/**
 * Created by hyochan on 4/5/15.
 */
public class ChatRoomAdapter extends ArrayAdapter<ChatRoomData>{
    private static final String TAG = "ChatRoomAdapter";
    private Context context;
    private ArrayList<ChatRoomData> arrayChatRoom;

    public ChatRoomAdapter(Context context, int resource, ArrayList<ChatRoomData> arrayChatRoom) {
        super(context, resource, arrayChatRoom);
        this.context= context;
        this.arrayChatRoom = arrayChatRoom;
    }

    class ViewHolder{
        RelativeLayout relMsg;
        TextView txtMarketName;
        TextView txtMsg;
        TextView txtTime;
        ImageView imgMarket;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        ChatRoomData chatRoomData = arrayChatRoom.get(position);

        if(convertView == null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.chat_room_adapter, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.relMsg = (RelativeLayout) convertView.findViewById(R.id.rel_msg);
            viewHolder.imgMarket = (ImageView) convertView.findViewById(R.id.img_market);
            viewHolder.txtMarketName = (TextView) convertView.findViewById(R.id.txt_name_peer);
            viewHolder.txtMsg = (TextView) convertView.findViewById(R.id.txt_msg);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.txt_send_date);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtMarketName.setText(arrayChatRoom.get(position).getMarName());
        viewHolder.txtMsg.setText(
                arrayChatRoom.get(position).getSender() + " : " +
                        arrayChatRoom.get(position).getMessage());
        viewHolder.txtTime.setText(arrayChatRoom.get(position).getSendDate());

        if(chatRoomData.getReadMsg() == ConstValue.READ_MSG_UNREAD
                && !chatRoomData.getSender().equals(CustomerFunction.getInstance(context).getCusID())){
            viewHolder.txtMsg.setTypeface(Typeface.DEFAULT_BOLD);
        }else{
            viewHolder.txtMsg.setTypeface(Typeface.DEFAULT);
        }

        if(viewHolder.imgMarket != null){
/*
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                new ImageDownloaderTask(context, viewHolder.imgMarket).
                        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arrayMarket.get(position).getImg());
            }
            else{
                new ImageDownloaderTask(context, viewHolder.imgMarket).
                        execute(arrayMarket.get(position).getImg());
            }
*/
            ImageDownloader.getInstance(context).getImage(
                    NetworkPreference.getInstance(context).getServerUrl()+":"+
                    NetworkPreference.getInstance(context).getServerPort()+"/images/"+
                    arrayChatRoom.get(position).getMarId().replaceAll("\\s","") + ".png",
                    viewHolder.imgMarket);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return arrayChatRoom.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ChatRoomData getItem(int position) {
        return arrayChatRoom.get(position);
    }


}