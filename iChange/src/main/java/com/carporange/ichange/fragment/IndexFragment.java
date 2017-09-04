package com.carporange.ichange.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class IndexFragment extends BaseFragment {
    View view_index;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public IndexFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_index = inflater.inflate(R.layout.fragment_index, container, false);
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_subindex, container, false);
            initViews();
            initLister();
        }
        return mContentView;
    }


    private void initLister() {
    }


    private void initViews() {
        mTabLayout = findView(R.id.tabLayout);
        mViewPager = findView(R.id.viewPager_subindex);
        CarpFragmentPagerAdapter fpa = new CarpFragmentPagerAdapter(getChildFragmentManager());
        fpa.addFragment(new SubIndexFragment(), getString(R.string.mfg_index));
        fpa.addFragment(new RankListFragment(), getString(R.string.mfg_ranking));
        fpa.addFragment(new DesignerFragment(), getString(R.string.mfg_designer));
        mViewPager.setAdapter(fpa);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    public class CarpFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList = new ArrayList<>();
        private List<String> mTitleList = new ArrayList<>();

        public CarpFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mList.add(fragment);
            mTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }

}
