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
    String content;
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
                    content = response.substring(0, response.indexOf("*"));
                    comment = response.substring(response.indexOf("*") + 1);

                    //说说内容本身
                    String[] str_record = content.split("\\|");
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

                    //评论
                    String[] str_comment = comment.split("\\|");
                    for (String str_node : str_comment) {
                        String[] record = str_node.split(";");
                        list_username.add(record[0]);
                        list_comment.add(record[1]);
                        list_root.add(Integer.valueOf(record[2]));
                        list_reply.add(record[3]);
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
            mDynamicList.add(dynamic);
        }

        //当所有的评论都已经加载后终止循环
        for (int content_index = 0; content_index < mDynamicList.size(); ++content_index) {
            List<Comment> commentList = new ArrayList<>();
            for (int i = 0; i < list_comment.size(); i++) {
                Comment comment = new Comment();
                //对于在第content_index条说说下进行评论的加入到commentlist中
                if (list_root.get(i) == content_index) {
                    //如果该评论不是根评论的，即有被回复者，设置相应的被回复者
                    if (!list_reply.get(i).equals("-1"))
                        comment.setReplayUser(getUser("\t\t" + list_reply.get(i), i));
                    comment.setBeenReplayUser(getUser(list_username.get(i), i));
                    comment.setContent(list_comment.get(i));
                    comment.setRoot(content_index);

                    list_username.remove(i);
                    list_comment.remove(i);
                    list_root.remove(i);
                    list_reply.remove(i);
                    --i;
                } else continue;
                commentList.add(comment);
            }
            mDynamicList.get(content_index).setCommentList(commentList);
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
