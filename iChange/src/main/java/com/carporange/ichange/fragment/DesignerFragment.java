package com.carporange.ichange.fragment;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.activity.DesignerDetailActivity;
import com.carporange.ichange.ui.base.BaseFragment;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DesignerFragment extends BaseFragment implements View.OnClickListener {
    View view_designer;

    private ImageView iv_one_one;
    private ImageView iv_one_two;
    private ImageView iv_one_three;
    private ImageView iv_two_one;
    private ImageView iv_two_two;

    List<String> list_url;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    public DesignerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_designer = inflater.inflate(R.layout.fragment_designer, container, false);
        InitStaticData();
        initView();
        return view_designer;
    }

    private void InitStaticData() {
        list_url = new ArrayList<>();

        View title_four = view_designer.findViewById(R.id.index_title_four);
        ((TextView) title_four.findViewById(R.id.tv_title_type)).setText(R.string.df_other);
        ((TextView) title_four.findViewById(R.id.tv_more)).setText(R.string.df_click_for_more);
        //返回顶部按钮
        (view_designer.findViewById(R.id.ll_back2top_item)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView trans_scrollview = (ScrollView) view_designer.findViewById(R.id.scro_designer);
                trans_scrollview.smoothScrollTo(0, 0);
            }
        });
    }

    private void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LinkerServer linkerServer = new LinkerServer("designerlist");
                if (linkerServer.Linker()) {
                    final String response = linkerServer.getResponse();
                    String[] str_record = response.split("\\|");
                    for (int i = 0; i < str_record.length; ++i) {
                        final String[] record = str_record[i].split(";");
                        list_url.add(record[2]);

                        byte[] data = new byte[0];
                        try {
                            data = ImageService.getImage(getString(R.string.LINKUSRL) + "designerlist/" + (i+1) +".jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final byte[] finalData = data;
                        final Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length));

                        //update the UI in main thread
                        switch (i) {
                            case 0:
                                //第一个设计师
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        View title_one = view_designer.findViewById(R.id.index_title_one);
                                        title_one.setOnClickListener(DesignerFragment.this);
                                        title_one.setTag(0);
                                        ((TextView) title_one.findViewById(R.id.tv_title_type)).setText(record[0]);

                                        View one_one = view_designer.findViewById(R.id.index_one_one);
                                        ((TextView) one_one.findViewById(R.id.tv_one_photo_title)).setText(record[1]);
                                        iv_one_one = (ImageView) one_one.findViewById(R.id.iv_image);
                                        iv_one_one.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 1:
                                //第二个设计师
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        View title_two = view_designer.findViewById(R.id.index_title_two);
                                        title_two.setOnClickListener(DesignerFragment.this);
                                        title_two.setTag(1);
                                        ((TextView) title_two.findViewById(R.id.tv_title_type)).setText(record[0]);

                                        View one_two = view_designer.findViewById(R.id.index_one_two);
                                        ((TextView) one_two.findViewById(R.id.tv_one_photo_title)).setText(record[1]);
                                        iv_one_two = (ImageView) one_two.findViewById(R.id.iv_image);
                                        iv_one_two.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 2:
                                //第三个设计师
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        View title_three = view_designer.findViewById(R.id.index_title_three);
                                        title_three.setOnClickListener(DesignerFragment.this);
                                        title_three.setTag(2);
                                        ((TextView) title_three.findViewById(R.id.tv_title_type)).setText(record[0]);

                                        View one_three = view_designer.findViewById(R.id.index_one_three);
                                        ((TextView) one_three.findViewById(R.id.tv_one_photo_title)).setText(record[1]);
                                        iv_one_three = (ImageView) one_three.findViewById(R.id.iv_image);
                                        iv_one_three.setImageDrawable(drawable);

                                    }
                                });
                                break;
                            case 3:
                                //"其他“第一个内容初始化
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView) view_designer.findViewById(R.id.tv_two_one_one_title))
                                                .setText(record[0] + "，" + record[1]);
                                        iv_two_one = (ImageView) view_designer.findViewById(R.id.iv_two_one_one);
                                        iv_two_one.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 4:
                                //"其他“第二个内容初始化
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView) view_designer.findViewById(R.id.tv_two_one_two_title))
                                                .setText(record[0] + "，" + record[1]);
                                        iv_two_two = (ImageView) view_designer.findViewById(R.id.iv_two_one_two);
                                        iv_two_two.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            InitListener();
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), R.string.REQUEST_FAIL, Toast.LENGTH_SHORT).show();
                }
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();
    }

    private void InitListener() {
        iv_one_one.setOnClickListener(this);
        iv_one_one.setTag(0);

        iv_one_two.setOnClickListener(this);
        iv_one_two.setTag(1);

        iv_one_three.setOnClickListener(this);
        iv_one_three.setTag(2);

        iv_two_one.setOnClickListener(this);
        iv_two_one.setTag(3);

        iv_two_two.setOnClickListener(this);
        iv_two_two.setTag(4);
    }

    @Override
    public void onClick(View v) {
        final int id = (int) v.getTag();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Intent intent = new Intent(getActivity(), DesignerDetailActivity.class);
                intent.putExtra(getString(R.string.URL), list_url.get(id));
                getActivity().startActivity(intent);
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();
    }
}
