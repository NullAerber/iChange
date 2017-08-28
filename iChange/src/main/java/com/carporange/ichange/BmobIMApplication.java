package com.carporange.ichange;

import android.app.Application;

import com.carporange.ichange.Hander.CrashHandler;
import com.carporange.ichange.util.ToolImage;

/**
 * Created by Liyuchen on 2016/6/14.
 * email:987424501@qq.com
 * phone:18298376275
 */
public class BmobIMApplication extends Application{
    private static BmobIMApplication INSTANCE;
    public static BmobIMApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        ToolImage.initImageLoader(this);

        CrashHandler handler = CrashHandler.getInstance();
        //在Appliction里面设置我们的异常处理器为UncaughtExceptionHandler处理器
        handler.init(getApplicationContext());
    }
}
