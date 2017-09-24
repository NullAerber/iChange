package com.carporange.ichange.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carporange.ichange.R;
import com.carporange.ichange.sign_calender.DPCManager;
import com.carporange.ichange.sign_calender.DPDecor;
import com.carporange.ichange.sign_calender.DatePicker2;
import com.carporange.ichange.ui.base.AerberBaeeActivity;
import com.carporange.ichange.util.LinkerServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * Created by Aerber on 2017/3/16.
 */
public class SignActivity extends AerberBaeeActivity {
    LinearLayout ll_sign_bottom;
    TextView tv_sign;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        initBar(this.getIntent().getStringExtra(getString(R.string.BAR_TITLE)));

        initView();
        initListener();
        GetInfo();
        Sign();
    }

    private void initView() {
        ll_sign_bottom = (LinearLayout) findViewById(R.id.ll_sign);
        ll_sign_bottom.setClickable(true);
        tv_sign = (TextView) findViewById(R.id.tv_sign);
        username = this.getIntent().getStringExtra(getString(R.string.USERNAME));
    }

    private void GetInfo() {
        List<String> signed_data = new ArrayList<>();
        String response = this.getIntent().getStringExtra(getString(R.string.URL));

        if (response != null && !Objects.equals(response, " ")) {
            String[] str_record = response.split("\\|");
            for (String aStr_record : str_record) {
                signed_data.add(aStr_record);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str_day = sdf.format(new Date());
            if (signed_data.remove(str_day)) {
                tv_sign.setText(R.string.have_been_signed);
                tv_sign.setTextColor(Color.GRAY);
                ll_sign_bottom.setClickable(false);
                tv_sign.setClickable(false);
            }
            DPCManager.getInstance().setDecorBG(signed_data);
        }
    }

    private void initListener() {
        ll_sign_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<NameValuePair> params = new ArrayList<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String str_day = sdf.format(new Date());

                        params.add(new BasicNameValuePair("username", username));
                        params.add(new BasicNameValuePair("date", str_day));

                        LinkerServer linkerServer = new LinkerServer("sign_today", params);
                        if (linkerServer.Linker()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_sign.setText(R.string.have_been_signed);
                                    tv_sign.setTextColor(Color.GRAY);
                                    ll_sign_bottom.setClickable(false);
                                    tv_sign.setClickable(false);
                                }
                            });
                            toast(getString(R.string.sign_success));
                        } else toast(getString(R.string.REQUEST_FAIL));
                    }
                }).start();
            }
        });
    }

    private void Sign() {
        DatePicker2 picker = (DatePicker2) findViewById(R.id.main_dp);

        picker.setFestivalDisplay(true); //是否显示节日
        picker.setHolidayDisplay(true); //是否显示假期
        picker.setDeferredDisplay(true); //是否显示补休

        Calendar c = Calendar.getInstance();//首先要获取日历对象
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        final int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        picker.setDate(mYear, mMonth);
        picker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(Color.RED);
                paint.setAntiAlias(true);
                InputStream is = getResources().openRawResource(+R.drawable.banner_grey);
                Bitmap mBitmap = BitmapFactory.decodeStream(is);
                canvas.drawBitmap(mBitmap, rect.centerX() - mBitmap.getWidth(), rect.centerY() - mBitmap.getHeight(), paint);
            }
        });
//        picker.setOnDateSelectedListener(new DatePicker.OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(List<String> date) {
//                String result = "asdwd";
//                Iterator iterator = date.iterator();
//                while (iterator.hasNext()) {
//                    result += iterator.next();
//                    if (iterator.hasNext()) {
//                        result += "\n";
//                    }
//                }
//                Log.i("zmy","sign");
//                toast(result);
////                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}