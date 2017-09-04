package com.carporange.ichange.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.AerberBaeeActivity;


/**
 * Created by Aerber on 2017/3/16.
 */
public class AboutActivity extends AerberBaeeActivity {
    String bar_title;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_about);

        initBar(this.getIntent().getStringExtra(getString(R.string.BAR_TITLE)));
    }
}