package com.gagetalk.gagetalkcustomer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.util.ImageDownloader;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.data.MarketData;
import com.gagetalk.gagetalkcommon.util.SoundSearcher;

import java.util.ArrayList;

/**
 * Created by hyochan on 3/29/15.
 */
public class MarketAdapter extends ArrayAdapter<MarketData>
    implements Filterable{
    private static final String TAG = "MarketAdapter";
    private Context context;
    private ArrayList<MarketData> arrayMarket;
    private ArrayList<MarketData> arraySearch;
    // private ImageLoader imageLoader;

    public MarketAdapter(Context context, int resource, ArrayList<MarketData> arrayMarket) {
        super(context, resource, arrayMarket);

        this.context= context;
        this.arrayMarket = arrayMarket;
        this.arraySearch = arrayMarket;
    }

    class ViewHolder{
        RelativeLayout relMarket;
        ImageView imgMarket;
        TextView txtMarketName;
        TextView txtMarketCategory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.market_adapter, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.relMarket = (RelativeLayout) convertView.findViewById(R.id.rel_market);
            viewHolder.imgMarket = (ImageView) convertView.findViewById(R.id.img_market);
            viewHolder.txtMarketName = (TextView) convertView.findViewById(R.id.txt_name_peer);
            viewHolder.txtMarketCategory = (TextView) convertView.findViewById(R.id.txt_market_category);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtMarketName.setText(arrayMarket.get(position).getMarName());
        viewHolder.txtMarketCategory.setText((arrayMarket.get(position).getCategory()));

        if(viewHolder.imgMarket != null){

            ImageDownloader.getInstance(context).getImage(
                    arrayMarket.get(position).getImg(),
                    viewHolder.imgMarket);

        }
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayMarket.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MarketData getItem(int position) {
        return arrayMarket.get(position);
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            arrayMarket = (ArrayList<MarketData>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        public FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<MarketData> tmpArrayMarket = new ArrayList<MarketData>();

            if(constraint != null && arraySearch !=null) {
                int length = arraySearch.size();
                // Log.i("Filtering", "glossaries size" + length);
                int i=0;
                while(i<length){
                    MarketData item = arraySearch.get(i);
                    // Real filtering:
                    if(item.getMarName() != null &&
                        (
                            SoundSearcher.matchString(item.getMarName().toLowerCase(), constraint.toString().toLowerCase()))
                        ){
                        tmpArrayMarket.add(item);
                    }
                    i++;
                }
                filterResults.values = tmpArrayMarket;
                filterResults.count = tmpArrayMarket.size();
                // Log.i("Filtering", "Filter result count size"+filterResults.count);
            }
            return filterResults;
        }
    };
}
