package com.carporange.ichange.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.activity.AboutActivity;
import com.carporange.ichange.ui.activity.HomepageActivity;
import com.carporange.ichange.ui.activity.FeedbackActivity;
import com.carporange.ichange.ui.activity.SignActivity;
import com.carporange.ichange.ui.activity.TryListActivity;
import com.carporange.ichange.ui.activity.WebActivity;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.ui.MainActivity;

public class MenuLeftFragment extends Fragment implements View.OnClickListener {
    View view_menu_left;
    ImageView iv_avatar;
    TextView tv_username;
    LinearLayout ll_try;
    LinearLayout ll_homepage;
    LinearLayout ll_recent;
    LinearLayout ll_third;
    LinearLayout ll_issue;
    LinearLayout ll_setting;
    LinearLayout ll_about;
    LinearLayout ll_sign;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_menu_left = inflater.inflate(R.layout.menu_left, container, true);
        initView();
        initListener();
        return view_menu_left;
    }

    private void initView() {
        ll_try = (LinearLayout) view_menu_left.findViewById(R.id.ll_nav_try);
        ll_homepage = (LinearLayout) view_menu_left.findViewById(R.id.ll_nav_homepage);
        ll_recent = (LinearLayout) view_menu_left.findViewById(R.id.ll_nav_recent);
        ll_third = (LinearLayout) view_menu_left.findViewById(R.id.ll_nav_third);
        ll_issue = (LinearLayout) view_menu_left.findViewById(R.id.ll_nav_issue);
//        ll_setting = (LinearLayout) view_menu_left.findViewById(R.id.ll_nav_setting);
        ll_about = (LinearLayout) view_menu_left.findViewById(R.id.ll_nav_about);
        ll_sign = (LinearLayout) view_menu_left.findViewById(R.id.ll_nav_sign);

        iv_avatar = (ImageView) view_menu_left.findViewById(R.id.iv_avatar);
        tv_username = (TextView) view_menu_left.findViewById(R.id.tv_username);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                List<NameValuePair> params = new ArrayList<>();
                if (UserModel.getInstance().getCurrentUser() == null){
                    System.exit(0);
                }

                params.add(new BasicNameValuePair("username",
                        UserModel.getInstance().getCurrentUser().getUsername()));
                LinkerServer linkerServer = new LinkerServer("user",params);
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    byte[] data = new byte[0];
                    try {
                        data = ImageService.getImage(getString(R.string.LINKUSRL) + "user/" + response + ".png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final byte[] finalData = data;
                    final Drawable avatar = new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            iv_avatar.setImageDrawable(avatar);
                            tv_username.setText(UserModel.getInstance().getCurrentUser().getUsername());
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), R.string.REQUEST_FAIL, Toast.LENGTH_SHORT).show();
                }
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_nav_homepage:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Intent intent = new Intent(getActivity(), HomepageActivity.class);
                        intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.hp));
                        getActivity().startActivity(intent);
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
                break;
            case R.id.ll_nav_recent:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.rc));
                        getActivity().startActivity(intent);
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
                break;
            case R.id.ll_nav_try:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Intent intent = new Intent(getActivity(), TryListActivity.class);
                        intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.fr));
                        getActivity().startActivity(intent);
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
                break;
            case R.id.ll_nav_third:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra(getString(R.string.URL), getString(R.string.tp_github_login));
                        intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.tp));
                        getActivity().startActivity(intent);
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
                break;
            case R.id.ll_nav_issue:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                        intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.fb));
                        getActivity().startActivity(intent);
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
                break;
//            case R.id.ll_nav_setting:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(getActivity(), SettingActivity.class);
//                        getActivity().startActivity(intent);
//                    }
//                }).start();
//                break;
            case R.id.ll_nav_about:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Intent intent = new Intent(getActivity(), AboutActivity.class);
                        intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.au));
                        getActivity().startActivity(intent);
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
                break;
            case R.id.ll_nav_sign:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        String response = "";
                        List<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair("username",
                                ((TextView) view_menu_left.findViewById(R.id.tv_username)).getText().toString()));

                        LinkerServer linkerServer = new LinkerServer("sign", params);
                        if (linkerServer.Linker()) {
                            response = linkerServer.getResponse();
                        } else {
                            Toast.makeText(getActivity(), R.string.REQUEST_FAIL, Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(getActivity(), SignActivity.class);
                        intent.putExtra(getString(R.string.URL), response);
                        intent.putExtra(getString(R.string.USERNAME),
                                ((TextView) view_menu_left.findViewById(R.id.tv_username)).getText().toString());
                        intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.sign));
                        getActivity().startActivity(intent);

                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    private void initListener() {
        ll_homepage.setOnClickListener(this);
        ll_recent.setOnClickListener(this);
        ll_third.setOnClickListener(this);
        ll_issue.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_try.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        ll_sign.setOnClickListener(this);
    }
}
