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

public class DesignerDetailActivity extends AerberBaeeActivity implements View.OnClickListener {
    ImageView iv_cloth_one;
    ImageView iv_cloth_two;
    ImageView iv_cloth_three;

    List<Drawable> list_drawables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_detail);
        initView();
        initData();
    }

    private void initView() {
        list_drawables = new ArrayList<>();

        iv_cloth_one = (ImageView) findViewById(R.id.iv_three_one_one);
        iv_cloth_one.setBackgroundColor(getResources().getColor(R.color.colorPageBg));

        iv_cloth_two = (ImageView) findViewById(R.id.iv_three_one_two);
        iv_cloth_two.setBackgroundColor(getResources().getColor(R.color.colorPageBg));

        iv_cloth_three = (ImageView) findViewById(R.id.iv_three_one_three);
        iv_cloth_three.setBackgroundColor(getResources().getColor(R.color.colorPageBg));

        //init contact listener
        findViewById(R.id.btn_contact).setOnClickListener(this);

    }

    private void initData() {
        final String id = this.getIntent().getStringExtra("url");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", id));
                LinkerServer linkerServer = new LinkerServer("designer", params);
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    final String[] str_record = response.split(";");
                    final String[] cloth_id_record = str_record[5].split(",");

                    for (int i = 0; i < cloth_id_record.length + 1; ++i) {
                        byte[] avatar_data = new byte[0];
                        try {
                            if (i != cloth_id_record.length)
                                avatar_data = ImageService.getImage(getString(R.string.LinkUrl) + "designer_link_cloth/" + cloth_id_record[i] + ".jpg");
                            else
                                avatar_data = ImageService.getImage(getString(R.string.LinkUrl) + "designer/" + id + ".jpg");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final byte[] avatar_finalData = avatar_data;
                        list_drawables.add(new BitmapDrawable(BitmapFactory.decodeByteArray(avatar_finalData, 0, avatar_finalData.length)));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initBar(str_record[0]);

                            ((ImageView) findViewById(R.id.account_img_usertitle))
                                    .setImageDrawable(list_drawables.get(cloth_id_record.length));
                            ((TextView) findViewById(R.id.tv_name)).setText(str_record[0]);
                            ((TextView) findViewById(R.id.tv_descri)).setText(str_record[1]);;
                            ((TextView) findViewById(R.id.tv_design_feature)).setText(str_record[2]);
                            ((TextView) findViewById(R.id.tv_designed_cloth)).setText(str_record[3]);
                            ((TextView) findViewById(R.id.tv_honour)).setText(str_record[4]);
                            ((TextView) findViewById(R.id.tv_design_experience)).setText(str_record[6]);

                            ((TextView) findViewById(R.id.tv_url)).setText(getString(R.string.designer_detail_domain) + str_record[0]);


                            for (int i = 0; i < cloth_id_record.length; ++i) {
                                switch (i) {
                                    case 0:
                                        iv_cloth_one.setImageDrawable(list_drawables.get(i));
                                        iv_cloth_one.setOnClickListener(DesignerDetailActivity.this);
                                        iv_cloth_one.setTag(cloth_id_record[i]);
                                        break;
                                    case 1:
                                        iv_cloth_two.setImageDrawable(list_drawables.get(i));
                                        iv_cloth_two.setOnClickListener(DesignerDetailActivity.this);
                                        iv_cloth_two.setTag(cloth_id_record[i]);
                                        break;
                                    case 2:
                                        iv_cloth_three.setImageDrawable(list_drawables.get(i));
                                        iv_cloth_three.setOnClickListener(DesignerDetailActivity.this);
                                        iv_cloth_three.setTag(cloth_id_record[i]);
                                        break;
                                }
                            }
                        }
                    });
                } else toast(getString(R.string.request_fail));
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_contact) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DesignerDetailActivity.this, cn.bmob.imdemo.ui.SearchUserActivity.class);
                    intent.putExtra("go_from_contact", ((TextView) findViewById(R.id.tv_name)).getText().toString());
                    startActivity(intent);
                }
            }).start();
        } else {
            Intent intent = new Intent(this, ClothDetailActivity.class);
            intent.putExtra("url", (String) v.getTag());
            startActivity(intent);
        }
    }
}
