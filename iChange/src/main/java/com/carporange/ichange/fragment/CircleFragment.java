package com.carporange.ichange.fragment;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.adapter.DynamicListAdapter;
import com.carporange.ichange.model.Comment;
import com.carporange.ichange.model.Dynamic;
import com.carporange.ichange.model.User;
import com.carporange.ichange.ui.base.BaseFragment;
import com.carporange.ichange.util.DensityUtil;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private List<Dynamic> mDynamicList = new ArrayList<>();
    private DynamicListAdapter mAdapter;
    View view_circle;
    String title;
    String comment;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };


    List<String> list_content;
    List<List<Drawable>> list_drawables;
    List<String> list_comment;
    List<Integer> list_root;
    List<Integer> list_father;
    List<String> list_username;
    List<String> list_reply;

    Map<Integer, List> map_comment_root;
    Map<Integer, List> map_comment_father;
    Map<String, List> map_commnet_reply;
    Map<String, List> map_commnet_username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_circle = inflater.inflate(R.layout.fragment_circle, container, false);
        recyclerView = (RecyclerView) view_circle.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initEvents();
        initData();

        return view_circle;
    }

    private void initEvents() {
        mAdapter = new DynamicListAdapter(recyclerView, getActivity(), mDynamicList);
        recyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        list_content = new ArrayList<>();
        list_drawables = new ArrayList<>();
        list_comment = new ArrayList<>();
        list_username = new ArrayList<>();
        list_root = new ArrayList<>();
        list_father = new ArrayList<>();
        list_reply = new ArrayList<>();

        map_commnet_username = new HashMap<>();
        map_comment_root = new HashMap<>();
        map_comment_father = new HashMap<>();
        map_commnet_reply = new HashMap<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LinkerServer linkerServer = new LinkerServer("circle");
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    title = response.substring(0, response.indexOf("*"));
                    comment = response.substring(response.indexOf("*") + 1);

                    String[] str_record = title.split("\\|");
                    for (String str_node : str_record) {
                        String[] record = str_node.split(";");
                        list_content.add(record[0]);

                        String[] pic_urls = record[1].split(",");
                        List<Drawable> drawables = new ArrayList<Drawable>();
                        for (String url : pic_urls) {
                            byte[] data = new byte[0];
                            try {
                                data = ImageService.getImage(getString(R.string.LinkUrl) + "circle/" + url + ".jpg");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final byte[] finalData = data;
                            drawables.add(new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length)));
                        }
                        list_drawables.add(drawables);
                    }

                    String[] str_comment = comment.split("\\|");
                    for (String str_node : str_comment) {
                        String[] record = str_node.split(";");
                        list_username.add(record[0]);
                        list_comment.add(record[1]);
                        list_father.add(Integer.valueOf(record[2]));
                        list_root.add(Integer.valueOf(record[3]));
                        list_reply.add(record[4]);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), R.string.request_fail, Toast.LENGTH_SHORT).show();
                }
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();
    }

    private void initView() {
        for (int i = 0; i < list_content.size(); i++) {
            Dynamic dynamic = new Dynamic();
            dynamic.setImageCount(list_drawables.get(i).size());
            dynamic.setContent(list_content.get(i));
            dynamic.setDrawables(list_drawables.get(i));
            List<Comment> commentList = new ArrayList<>();
            for (int j = 0; j < list_comment.size(); j++) {
                Comment comment = new Comment();
                if (list_root.get(j) == (i+1)) {
                    //TODO
                    if (list_father.get(j) != 0)
                        comment.setReplayUser(getUser(list_reply.get(j), j));
                    comment.setCommentUser(getUser(list_username.get(j), j));
                    comment.setContent(list_comment.get(j));
                } else continue;
                commentList.add(comment);
            }
            dynamic.setCommentList(commentList);
            mDynamicList.add(dynamic);
        }
        mAdapter.notifyDataSetChanged();
    }

    private User getUser(String username, int index) {
        User user = new User();
        user.setId(index);
        user.setName(username);
        return user;
    }
}
