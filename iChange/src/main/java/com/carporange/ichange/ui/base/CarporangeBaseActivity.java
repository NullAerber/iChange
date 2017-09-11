package com.carporange.ichange.ui.base;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.util.SpUtil;

public abstract class CarporangeBaseActivity extends AppCompatActivity {
    private MyBaseActiviy_Broad oBaseActiviy_Broad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        boolean isNightMode = SpUtil.getBoolean("nightMode", false);
//        setTheme(isNightMode ? R.style.NightTheme : R.style.DayTheme);
        initActionBar();
        initViews();
        initWindow();

        //动态注册广播
        oBaseActiviy_Broad = new MyBaseActiviy_Broad();
        IntentFilter intentFilter = new IntentFilter("com.carporange.ichange.ui.base.CarporangeBaseActivity");
        registerReceiver(oBaseActiviy_Broad, intentFilter);
    }

    public abstract void initActionBar();

    public abstract void initViews();

    public void getData() {

    }

    public void setListeners() {

    }

    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    //在销毁的方法里面注销广播
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(oBaseActiviy_Broad);//注销广播
    }

    //定义一个广播
    public class MyBaseActiviy_Broad extends BroadcastReceiver {

        public void onReceive(Context arg0, Intent intent) {
            //接收发送过来的广播内容
            int closeAll = intent.getIntExtra("closeAll", 0);
            if (closeAll == 1) {
                finish();//销毁BaseActivity
            }
        }

    }

    /**
     * 显示Toast信息
     */
    public void showToast(String text) {
        Toast.makeText(this, text, 2000).show();
    }

}
