package com.carporange.ichange.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.AerberBaeeActivity;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.imdemo.model.UserModel;


/**
 * Created by Aerber on 2017/3/16.
 */
public class HomepageActivity extends AerberBaeeActivity {
    double bmi;
    TextView tvConfirm;
    TextView tv_edit;
    TextView tv_bmi;
    TextView tv_bmi_tag;

    EditText ed_hight;
    EditText ed_weight;
    EditText ed_hipline;
    EditText ed_waist;
    EditText ed_bust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_homepage);
        initBar(this.getIntent().getStringExtra(getString(R.string.BAR_TITLE)));

        final LinearLayout ll_info = (LinearLayout) findViewById(R.id.ll_info);
        ll_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCenterPopupWindow(ll_info);
            }
        });
        findViewById(R.id.ll_contact_Customer_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, cn.bmob.imdemo.ui.SearchUserActivity.class);
                intent.putExtra(getString(R.string.GO_FROM_CONTACT), getString(R.string.hp_customer_service));
                HomepageActivity.this.startActivity(intent);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<>();
                if (UserModel.getInstance().getCurrentUser() == null) {
                    System.exit(0);
                }

                params.add(new BasicNameValuePair("username",
                        UserModel.getInstance().getCurrentUser().getUsername()));
                LinkerServer linkerServer = new LinkerServer("user", params);
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    byte[] data = new byte[0];
                    try {
                        data = ImageService.getImage(getString(R.string.LINKUSRL) + "user/" + response + ".png");
                    } catch (IOException e) {
                        try {
                            data = ImageService.getImage(getString(R.string.LINKUSRL) + "user/default.png");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    final byte[] finalData = data;
                    final Drawable avatar = new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) findViewById(R.id.account_img_usertitle)).setImageDrawable(avatar);
                            ((TextView) findViewById(R.id.tv_name)).setText(UserModel.getInstance().getCurrentUser().getUsername());
                        }
                    });
                } else {
                    toast(getString(R.string.REQUEST_FAIL));
                }
            }
        }).start();
    }

    /**
     * 中间弹出PopupWindow
     * <p>
     * 设置PopupWindow以外部分有一中变暗的效果
     *
     * @param view parent view
     */
    public void showCenterPopupWindow(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, 600, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvConfirm = (TextView) contentView.findViewById(R.id.tv_confirm);
        tv_edit = (TextView) contentView.findViewById(R.id.tv_edit);
        tv_bmi = (TextView) contentView.findViewById(R.id.tv_bmi);
        tv_bmi_tag = (TextView) contentView.findViewById(R.id.tv_bmi_tag);

        ed_hight = (EditText) contentView.findViewById(R.id.ed_hight);
        ed_weight = (EditText) contentView.findViewById(R.id.ed_weight);
        ed_hipline = (EditText) contentView.findViewById(R.id.ed_hipline);
        ed_waist = (EditText) contentView.findViewById(R.id.ed_waist);
        ed_bust = (EditText) contentView.findViewById(R.id.ed_bust);

        Calculate();

        ed_hight.setEnabled(false);
        ed_weight.setEnabled(false);
        ed_hipline.setEnabled(false);
        ed_waist.setEnabled(false);
        ed_bust.setEnabled(false);

        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_hight.setEnabled(true);
                ed_weight.setEnabled(true);
                ed_hipline.setEnabled(true);
                ed_waist.setEnabled(true);
                ed_bust.setEnabled(true);
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_hight.setEnabled(false);
                ed_weight.setEnabled(false);
                ed_hipline.setEnabled(false);
                ed_waist.setEnabled(false);
                ed_bust.setEnabled(false);
                Calculate();
            }
        });


        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置PopupWindow以外部分的背景颜色  有一种变暗的效果
        final WindowManager.LayoutParams wlBackground = getWindow().getAttributes();
        wlBackground.alpha = 0.5f;      // 0.0 完全不透明,1.0完全透明
        getWindow().setAttributes(wlBackground);
        // 当PopupWindow消失时,恢复其为原来的颜色
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wlBackground.alpha = 1.0f;
                getWindow().setAttributes(wlBackground);
            }
        });
        //设置PopupWindow进入和退出动画
        popupWindow.setAnimationStyle(R.style.anim_popup_centerbar);
        // 设置PopupWindow显示在中间
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    private void Calculate() {
        java.text.DecimalFormat df = new java.text.DecimalFormat("######0.00");
        bmi = Double.valueOf(ed_weight.getText().toString()) / Math.pow(Double.valueOf(ed_hight.getText().toString()) / 100, 2);
        String tag = getString(R.string.hp_normal);
        if (bmi < 18.5) {
            tv_bmi.setTextColor(getResources().getColor(R.color.green));
            tv_bmi_tag.setTextColor(getResources().getColor(R.color.green));
            tag = getString(R.string.hp_light);
        } else if (bmi > 24 && bmi < 27) {
            tv_bmi.setTextColor(getResources().getColor(R.color.colorTheme));
            tv_bmi_tag.setTextColor(getResources().getColor(R.color.colorTheme));
            tag = getString(R.string.hp_little_fat);
        } else if (bmi > 27 && bmi < 32) {
            tv_bmi.setTextColor(getResources().getColor(R.color.red));
            tv_bmi_tag.setTextColor(getResources().getColor(R.color.red));
            tag = getString(R.string.hp_fat);
        } else if (bmi > 32) {
            tv_bmi.setTextColor(getResources().getColor(R.color.Bigred));
            tv_bmi_tag.setTextColor(getResources().getColor(R.color.Bigred));
            tag = getString(R.string.hp_reduce_weight);
        } else {
            tv_bmi.setTextColor(getResources().getColor(R.color.black));
            tv_bmi_tag.setTextColor(getResources().getColor(R.color.black));
        }

        tv_bmi.setText(df.format(bmi));
        tv_bmi_tag.setText(tag);
    }
}