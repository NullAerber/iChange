package com.carporange.ichange.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class CardPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> data;

    public CardPagerAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }
}