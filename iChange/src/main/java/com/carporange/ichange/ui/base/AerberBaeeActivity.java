package com.carporange.ichange.ui.base;

import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.carporange.ichange.R;

/**
 * Created by aerber on 17-8-27.
 */

public class AerberBaeeActivity extends AppCompatActivity {

    protected void toast(String str) {
        Looper.prepare();
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    protected void initBar(String str_toolbar_title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (str_toolbar_title != null)
                toolbar.setTitle(str_toolbar_title);
        }
    }
}
