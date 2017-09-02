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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CircleFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private List<Dynamic> mDynamicList = new ArrayList<>();
    private DynamicListAdapter mAdapter;
    View view_circle;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };


    List<String> list_content = new ArrayList<>();
    List<List<Drawable>> list_drawables = new ArrayList<>();
    List<Integer> List_content_root = new ArrayList<>();

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LinkerServer linkerServer = new LinkerServer("circle");
                if (linkerServer.Linker()) {
                    final String response = linkerServer.getResponse();

                    //说说内容本身
                    String[] str_record = response.split("\\|");
                    for (String str_node : str_record) {
                        String[] record = str_node.split(";");
                        list_content.add(record[0]);
                        List_content_root.add(Integer.valueOf(record[2]) - 1);

                        String[] pic_urls = record[1].split(",");
                        List<Drawable> drawables = new ArrayList<>();
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
        for (int content_index = 0; content_index < List_content_root.size(); ++content_index) {
            Dynamic dynamic = new Dynamic();
            dynamic.setImageCount(list_drawables.get(content_index).size());
            dynamic.setContent(list_content.get(content_index));
            dynamic.setDrawables(list_drawables.get(content_index));
            mDynamicList.add(dynamic);

            getComment(content_index);
        }
    }

    private void getComment(int content_index) {
        final int root = List_content_root.get(content_index);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("root", String.valueOf(root)));

                LinkerServer linkerServer = new LinkerServer("comment", params);
                List<Comment> commentList = new ArrayList<>();

                if (linkerServer.Linker()) {
                    String[] temp = linkerServer.getResponse().split("\\|");
                    List<String> list_abstract_comment = java.util.Arrays.asList(temp);
                    List<String> list_comment = new ArrayList<>(list_abstract_comment);

                    for (int sub_index = 0;!list_comment.isEmpty();++sub_index){
                        for (int i = 0; i < list_comment.size(); ++i) {
                            String[] record = list_comment.get(i).split(";");
                            Comment comment = new Comment();

                            if (Integer.valueOf(record[2]) == sub_index) {
                                if (!record[3].equals("-1")) {
                                    comment.setReplayUser(getUser("\t\t" + record[0], i));
                                    comment.setBeenReplayUser(getUser(record[3], i));
                                } else comment.setBeenReplayUser(getUser(record[0], i));
                                comment.setContent(record[1]);
                                comment.setRoot(root);
                                comment.setSubRoot(sub_index);

                                commentList.add(comment);
                                list_comment.remove(i);
                                --i;
                            } else continue;
                        }
                    }
                }

                mDynamicList.get(root).setCommentList(commentList);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();

    }

    private User getUser(String username, int index) {
        User user = new User();
        user.setId(index);
        user.setName(username);
        return user;
    }
}
