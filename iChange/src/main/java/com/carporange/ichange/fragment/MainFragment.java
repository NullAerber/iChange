package com.carporange.ichange.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.carporange.ichange.R;
import com.carporange.ichange.adapter.CarpFragmentPagerAdapter;
import com.carporange.ichange.ui.activity.SearchActivity;
import com.carporange.ichange.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener{
    View view_index;
    private ImageView mHomeImage;
    private OnHomeClickListener mOnHomeClickListener;
    private Activity mActivity;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;

    private ImageView mIvMenuSearch;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    public MainFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mOnHomeClickListener = (OnHomeClickListener) mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_main, container, false);
            initViews();
            initSearchView();
            setListeners();
        }
        return mContentView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnHomeClickListener = null;
    }

    private void initViews() {
        view_index = findView(R.layout.fragment_index);
        mHomeImage = findView(R.id.actionbar_home);
        mHomeImage.setOnClickListener(this);
        mViewPager = findView(R.id.viewPager_main);

        List<Fragment> list = new ArrayList<>();
        list.add(new IndexFragment());
        list.add(new CircleFragment());
        list.add(new MurmurFragment());

        FragmentPagerAdapter fpa = new CarpFragmentPagerAdapter(list, getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(fpa);
        mRadioGroup = findView(R.id.radioGroup);
        mRadioGroup.check(R.id.rb_index);
    }

    private void initSearchView() {
        mIvMenuSearch = (ImageView) mContentView.findViewById(R.id.ivMenuSearch);
        mIvMenuSearch.setOnClickListener(this);
    }

    private void setListeners() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRadioGroup.check(R.id.rb_index);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.rb_circle);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.rb_murmur);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rb_index:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_circle:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_murmur:
                        mViewPager.setCurrentItem(2);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_home:
                if(mOnHomeClickListener != null) {
                    mOnHomeClickListener.onHomeClick();
                }
                break;
            case R.id.ivMenuSearch:// 搜索按钮
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        intent.putExtra("bar_title", "查找");
                        getActivity().startActivity(intent);
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
                break;
        }
    }

    public interface OnHomeClickListener{
        void onHomeClick();
    }
}
