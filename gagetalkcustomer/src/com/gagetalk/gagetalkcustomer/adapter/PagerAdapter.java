package com.gagetalk.gagetalkcustomer.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by hyochan on 3/28/15.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Fragment> arrFragments;

    public PagerAdapter(FragmentManager fm, Context context, List fragments) {
        super(fm);
        this.context = context;
        this.arrFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return arrFragments.get(position);
    }

    @Override
    public int getCount() {
        if(arrFragments == null) return 0;
        return arrFragments.size();
    }
}
