package com.carporange.ichange.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carporange.ichange.R;
import com.carporange.ichange.ui.activity.WebActivity;
import com.carporange.ichange.ui.base.BaseFragment;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SubIndexFragment extends BaseFragment implements View.OnClickListener {
    private View view_index;
    private ViewPager mainViewPager;
    private ViewPager SubIndexViewPager;

    Banner banner;
    List<String> list_banner_pic_url;
    List<String> list_banner_title;
    List<String> list_banner_url;
    List<String> list_banner_next_bar_title;

    List<String> list_next_url_or_id;

    RelativeLayout rl_title_one;
    RelativeLayout rl_title_two;
    RelativeLayout rl_title_three;
    RelativeLayout rl_title_four;

    //图片组件
    private ImageView iv_three_one;
    private ImageView iv_three_two;
    private ImageView iv_three_three;
    private ImageView iv_one_one;
    private ImageView iv_two_one;
    private ImageView iv_two_two;
    private ImageView iv_one_two;
    private ImageView iv_one_three;

    String murur_title;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_index = inflater.inflate(R.layout.fragment_index, container, false);

        GetParentViewPager(container);
        //加载布局
        InitStaticView();
        InitDynamicsView();
        //建立监听
        InitStaticListener();

        return view_index;
    }

    private void InitStaticListener() {
        //三个圆圈按钮的监听跳转
        (view_index.findViewById(R.id.ib_circle)).setOnClickListener(this);
        (view_index.findViewById(R.id.ib_murmur)).setOnClickListener(this);
        (view_index.findViewById(R.id.ib_rank)).setOnClickListener(this);

        //返回顶部按钮
        (view_index.findViewById(R.id.ll_back2top_item)).setOnClickListener(this);

        //每个栏目中的“更多”按钮监听跳转+
        rl_title_one.setOnClickListener(this);
        rl_title_one.setTag(8);

        rl_title_two.setOnClickListener(this);
        rl_title_two.setTag(9);

        rl_title_three.setOnClickListener(this);
        rl_title_three.setTag(10);

        rl_title_four.setOnClickListener(this);
        rl_title_four.setTag(11);
    }

    private void InitStaticView() {
        //部件声明
        View title_one = view_index.findViewById(R.id.index_title_one);
        View title_two = view_index.findViewById(R.id.index_title_two);
        View title_three = view_index.findViewById(R.id.index_title_three);
        View title_four = view_index.findViewById(R.id.index_title_four);
        //日期tv初始化
        TextView tv_day = (TextView) view_index.findViewById(R.id.tv_daily_text);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String str_day = sdf.format(new Date());
        tv_day.setText(str_day);

        //title内容初始化
        rl_title_one = (RelativeLayout) title_one.findViewById(R.id.rl_title);
        TextView tv_title_one = (TextView) title_one.findViewById(R.id.tv_title_type);
        tv_title_one.setText(R.string.si_ranking);
        ImageView iv_title_one = (ImageView) title_one.findViewById(R.id.iv_title_type);
        iv_title_one.setImageDrawable(getResources().getDrawable(R.drawable.tailor_red));

        rl_title_two = (RelativeLayout) title_two.findViewById(R.id.rl_title);
        TextView tv_title_two = (TextView) title_two.findViewById(R.id.tv_title_type);
        tv_title_two.setText(R.string.si_designer);
        ImageView iv_title_two = (ImageView) title_two.findViewById(R.id.iv_title_type);
        iv_title_two.setImageDrawable(getResources().getDrawable(R.drawable.designer_logo));

        rl_title_three = (RelativeLayout) title_three.findViewById(R.id.rl_title);
        TextView tv_title_three = (TextView) title_three.findViewById(R.id.tv_title_type);
        tv_title_three.setText(R.string.si_circle);
        ImageView iv_title_three = (ImageView) title_three.findViewById(R.id.iv_title_type);
        iv_title_three.setImageDrawable(getResources().getDrawable(R.drawable.home_title_movie));

        rl_title_four = (RelativeLayout) title_four.findViewById(R.id.rl_title);
        TextView tv_title_four = (TextView) title_four.findViewById(R.id.tv_title_type);
        tv_title_four.setText(R.string.si_murmur);
        ImageView iv_title_four = (ImageView) title_four.findViewById(R.id.iv_title_type);
        iv_title_four.setImageDrawable(getResources().getDrawable(R.drawable.home_title_movie));
    }

    private void initBanner() {
        list_banner_pic_url = new ArrayList<>();
        list_banner_title = new ArrayList<>();
        list_banner_url = new ArrayList<>();
        list_banner_next_bar_title = new ArrayList<>();

        banner = (Banner) view_index.findViewById(R.id.banner);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LinkerServer linkerServer = new LinkerServer("banner");
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    String[] str_record = response.split("\\|");
                    for (int i = 0; i < str_record.length; ++i) {
                        String[] record = str_record[i].split(";");
                        list_banner_title.add(record[0]);
                        list_banner_pic_url.add(getString(R.string.LINKUSRL) + "banner/" + record[1] + ".jpg");
                        list_banner_next_bar_title.add(record[2]);
                        list_banner_url.add(record[3]);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //设置banner样式
                            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
                            //设置图片加载器
                            banner.setImageLoader(new GlideImageLoader());
                            //设置图片集合
                            banner.setImages(list_banner_pic_url);
                            //设置banner动画效果
                            banner.setBannerAnimation(Transformer.Default);
                            //设置标题集合（当banner样式有显示title时）
                            banner.setBannerTitles(list_banner_title);
                            //设置自动轮播，默认为true
                            banner.isAutoPlay(true);
                            //设置轮播时间
                            banner.setDelayTime(4500);
                            //设置指示器位置（当banner模式中有指示器时）
                            banner.setIndicatorGravity(BannerConfig.CENTER);
                            //banner设置方法全部调用完毕时最后调用
                            banner.start();
                            //设置banner的点击事件
                            banner.setOnBannerListener(new OnBannerListener() {
                                @Override
                                public void OnBannerClick(final int position) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Looper.prepare();
                                            Intent intent = new Intent(getActivity(), WebActivity.class);
                                            intent.putExtra(getString(R.string.URL), list_banner_url.get(position));
                                            intent.putExtra(getString(R.string.BAR_TITLE), list_banner_next_bar_title.get(position));
                                            getActivity().startActivity(intent);
                                            handler.sendEmptyMessage(0);
                                            Looper.loop();
                                        }
                                    }).start();
                                }
                            });
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

    private void InitDynamicsView() {
        //banner的动态加载
        initBanner();

        list_next_url_or_id = new ArrayList<>();
        //动态获取subindex的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LinkerServer linkerServer = new LinkerServer("subindex");
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    String[] str_record = response.split("\\|");
                    for (int i = 0; i < str_record.length; ++i) {
                        final String[] record = str_record[i].split(";");
                        list_next_url_or_id.add(record[2]);

                        //生成图片
                        byte[] data = new byte[0];
                        try {
                            data = ImageService.getImage(getString(R.string.LINKUSRL) + "subindex/" + record[1] + ".jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final byte[] finalData = data;
                        final Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length));

                        //对于相对应的id的组件进行数据填充
                        switch (i) {
                            case 0:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView tv_three_one = (TextView) view_index.findViewById(R.id.tv_three_one_one_title);
                                        tv_three_one.setText(record[0]);
                                        iv_three_one = (ImageView) view_index.findViewById(R.id.iv_three_one_one);
                                        iv_three_one.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 1:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView tv_three_two = (TextView) view_index.findViewById(R.id.tv_three_one_two_title);
                                        tv_three_two.setText(record[0]);
                                        iv_three_two = (ImageView) view_index.findViewById(R.id.iv_three_one_two);
                                        iv_three_two.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 2:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView tv_three_three = (TextView) view_index.findViewById(R.id.tv_three_one_three_title);
                                        tv_three_three.setText(record[0]);
                                        iv_three_three = (ImageView) view_index.findViewById(R.id.iv_three_one_three);
                                        iv_three_three.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 3:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        View one_one = view_index.findViewById(R.id.index_one_one);
                                        TextView tv_one_one = (TextView) one_one.findViewById(R.id.tv_one_photo_title);
                                        tv_one_one.setText(record[0]);
                                        iv_one_one = (ImageView) one_one.findViewById(R.id.iv_image);
                                        iv_one_one.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 4:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //设计师
                                        TextView tv_two_one = (TextView) view_index.findViewById(R.id.tv_two_one_one_title);
                                        tv_two_one.setText(record[0]);
                                        iv_two_one = (ImageView) view_index.findViewById(R.id.iv_two_one_one);
                                        iv_two_one.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 5:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView tv_two_two = (TextView) view_index.findViewById(R.id.tv_two_one_two_title);
                                        tv_two_two.setText(record[0]);
                                        iv_two_two = (ImageView) view_index.findViewById(R.id.iv_two_one_two);
                                        iv_two_two.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 6:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //千金圈
                                        View one_two = view_index.findViewById(R.id.index_one_two);
                                        TextView tv_one_two = (TextView) one_two.findViewById(R.id.tv_one_photo_title);
                                        tv_one_two.setText(record[0]);
                                        iv_one_two = (ImageView) one_two.findViewById(R.id.iv_image);
                                        iv_one_two.setImageDrawable(drawable);
                                    }
                                });
                                break;
                            case 7:
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //碎碎念
                                        View one_three = view_index.findViewById(R.id.index_one_three);
                                        TextView tv_one_three = (TextView) one_three.findViewById(R.id.tv_one_photo_title);
                                        tv_one_three.setText(record[0]);
                                        murur_title = record[0];
                                        iv_one_three = (ImageView) one_three.findViewById(R.id.iv_image);
                                        iv_one_three.setImageDrawable(drawable);
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
                            InitDynamicsListener();
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

    private void InitDynamicsListener() {
        iv_three_one.setOnClickListener(this);
        iv_three_one.setTag(0);

        iv_three_two.setOnClickListener(this);
        iv_three_two.setTag(1);

        iv_three_three.setOnClickListener(this);
        iv_three_three.setTag(2);

        iv_one_one.setOnClickListener(this);
        iv_one_one.setTag(3);

        iv_two_one.setOnClickListener(this);
        iv_two_one.setTag(4);

        iv_two_two.setOnClickListener(this);
        iv_two_two.setTag(5);

        iv_one_three.setOnClickListener(this);
        iv_one_three.setTag(7);

        //下方千金圈图片点击跳转
        iv_one_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewPager.setCurrentItem(1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        //用于判断是getid来跳转还是getTag来跳转
        boolean flag = true;
        switch (v.getId()) {
            case R.id.ib_murmur:
                mainViewPager.setCurrentItem(2);
                flag = false;
                break;
            case R.id.ib_rank:
                SubIndexViewPager.setCurrentItem(1);
                flag = false;
                break;
            case R.id.ib_circle:
                mainViewPager.setCurrentItem(1);
                flag = false;
                break;
            case R.id.ll_back2top_item:
                ScrollView trans_scrollview = (ScrollView) view_index.findViewById(R.id.subindex_scro);
                trans_scrollview.smoothScrollTo(0, 0);
                flag = false;
                break;
            default:
                break;
        }

        if (flag) {
            final int id = (int) v.getTag();
            if (id > 7) {
                switch (id) {
                    case 8:
                        SubIndexViewPager.setCurrentItem(1);
                        break;
                    case 9:
                        SubIndexViewPager.setCurrentItem(2);
                        break;
                    case 10:
                        mainViewPager.setCurrentItem(1);
                        break;
                    case 11:
                        mainViewPager.setCurrentItem(2);
                        break;
                    default:
                        break;
                }

            } else {
                String NextClassName = "";
                if (id < 4) {
                    NextClassName = getString(R.string.si_clothDetail_path);
                } else if (id == 7) {
                    NextClassName = getString(R.string.si_webAct_path);
                } else {
                    NextClassName = getString(R.string.si_designeDetail_path);
                }

                try {
                    final Class NextClass = Class.forName(NextClassName);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Intent intent = new Intent(getActivity(), NextClass);
                            intent.putExtra(getString(R.string.URL), list_next_url_or_id.get(id));
                            if (NextClass == WebActivity.class)
                                intent.putExtra(getString(R.string.BAR_TITLE), murur_title);
                            getActivity().startActivity(intent);
                            handler.sendEmptyMessage(0);
                            Looper.loop();
                        }
                    }).start();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void GetParentViewPager(ViewGroup container) {
        //父viewpager获取
        mainViewPager = (ViewPager) container.getParent().getParent();
        //父index页面的viewpager获取
        SubIndexViewPager = (ViewPager) mainViewPager.findViewById(R.id.viewPager_subindex);
    }

    //banner的图片加载器
    private class GlideImageLoader extends ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }
}
