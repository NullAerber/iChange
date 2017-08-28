package com.carporange.ichange.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.AerberBaeeActivity;


/**
 * Created by Aerber on 2017/3/16.
 */
public class IssueActivity extends AerberBaeeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_issue);

        initBar(this.getIntent().getStringExtra("bar_title"));

        findViewById(R.id.tv_issues).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IssueActivity.this, WebActivity.class);
                intent.putExtra("url", "https://github.com/NullAerber/iChange/issues");
                intent.putExtra("bar_title", "IChange问题反馈");
                IssueActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.tv_faq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IssueActivity.this, WebActivity.class);
                intent.putExtra("url", "https://github.com/NullAerber/iChange");
                intent.putExtra("bar_title", "IChange常见问题");
                IssueActivity.this.startActivity(intent);
            }
        });
    }
}