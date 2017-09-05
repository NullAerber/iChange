package com.carporange.ichange.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.AerberBaeeActivity;

import java.net.URL;


/**
 * Created by Aerber on 2017/3/16.
 */
public class AboutActivity extends AerberBaeeActivity implements View.OnClickListener {
    String bar_title;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_about);

        initBar(this.getIntent().getStringExtra(getString(R.string.BAR_TITLE)));
        InitListener();
    }

    private void InitListener() {
        findViewById(R.id.tv_new_version).setOnClickListener(this);
        findViewById(R.id.tv_update_log).setOnClickListener(this);
        findViewById(R.id.tv_about_star).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String url = "";
        String title = "";

        switch (v.getId()) {
            case R.id.tv_new_version:
                Toast.makeText(this, R.string.au_no_new, Toast.LENGTH_SHORT).show();
                return;
            case R.id.tv_update_log:
                url = getString(R.string.fb_common_issue_url);
                title = getString(R.string.au_update_log);
                break;
            case R.id.tv_about_star:
                url = getString(R.string.fb_common_issue_url);
                title = getString(R.string.au_star);
                break;
        }
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(getString(R.string.URL), url);
        intent.putExtra(getString(R.string.BAR_TITLE), title);
        startActivity(intent);
    }
}