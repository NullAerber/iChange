package com.carporange.ichange.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ClothDetailActivity extends AerberBaeeActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_detail);
        initData();
        initListen();
    }

    private void initData() {
        final String id = this.getIntent().getStringExtra("url");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", id));
                LinkerServer linkerServer = new LinkerServer("detail", params);
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    final String[] str_record = response.split("\\|");

                    byte[] data = new byte[0];
                    try {
                        data = ImageService.getImage(getString(R.string.LINKUSRL) + "cloth/" + id + ".jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final byte[] finalData = data;
                    final Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            InitView(str_record, drawable);
                        }
                    });
                } else toast(getString(R.string.REQUEST_FAIL));
            }
        }).start();
    }

    private void InitView(String[] str_record, Drawable drawable) {
        ((ImageView) findViewById(R.id.iv_image)).setImageDrawable(drawable);
        ((TextView) findViewById(R.id.tv_name)).setText(str_record[0]);
        ((TextView) findViewById(R.id.tv_designer)).setText(str_record[1]);
        ((TextView) findViewById(R.id.tv_tailor)).setText(str_record[2]);
        ((TextView) findViewById(R.id.tv_up_sale)).setText(str_record[3]);
        ((TextView) findViewById(R.id.tv_sale)).setText(str_record[4]);


        ((TextView) findViewById(R.id.tv_suitable_crowd)).setText(str_record[5]);
        ((TextView) findViewById(R.id.tv_characteristic)).setText(str_record[6]);
        ((TextView) findViewById(R.id.tv_suitable_postion)).setText(str_record[7]);
        ((TextView) findViewById(R.id.tv_design_feature)).setText(str_record[8]);
        ((TextView) findViewById(R.id.tv_suitable_accessories)).setText(str_record[9]);
        ((TextView) findViewById(R.id.tv_taboo_accessories)).setText(str_record[10]);
        ((TextView) findViewById(R.id.tv_advantage)).setText(str_record[11]);
        ((TextView) findViewById(R.id.tv_disadvantage)).setText(str_record[12]);

        initBar(str_record[0]);
    }

    private void initListen() {
        findViewById(R.id.contact).setOnClickListener(this);
        findViewById(R.id.add_to_try).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ClothDetailActivity.this, cn.bmob.imdemo.ui.SearchUserActivity.class);
                        intent.putExtra("go_from_contact", ((TextView) findViewById(R.id.tv_designer)).getText().toString());

                        ClothDetailActivity.this.startActivity(intent);
                    }
                }).start();
                break;
            case R.id.add_to_try:
                final String id = this.getIntent().getStringExtra("url");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair("item", id));
                        params.add(new BasicNameValuePair("username", UserModel.getInstance().getCurrentUser().getUsername()));
                        LinkerServer linkerServer = new LinkerServer("try_add", params);
                        if (linkerServer.Linker())
                            toast(linkerServer.getResponse());
                        else
                            toast(getString(R.string.REQUEST_FAIL));
                    }
                }).start();
                break;
            default:
                break;
        }
    }
}
