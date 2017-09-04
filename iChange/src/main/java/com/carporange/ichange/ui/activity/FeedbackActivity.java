package com.carporange.ichange.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.AerberBaeeActivity;


/**
 * Created by Aerber on 2017/3/16.
 */
public class FeedbackActivity extends AerberBaeeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_issue);

        initBar(this.getIntent().getStringExtra(getString(R.string.BAR_TITLE)));

        findViewById(R.id.tv_issues).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, WebActivity.class);
                intent.putExtra(getString(R.string.URL), getString(R.string.fb_feedback_url));
                intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.fb_feedback));
                FeedbackActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.tv_faq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, WebActivity.class);
                intent.putExtra(getString(R.string.URL), getString(R.string.fb_common_issue_url));
                intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.fb_common_issue));
                FeedbackActivity.this.startActivity(intent);
            }
        });
    }
}