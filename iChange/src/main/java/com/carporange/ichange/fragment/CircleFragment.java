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

import cn.bmob.imdemo.model.UserModel;

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


    List<String> list_content;
    List<List<Drawable>> list_drawables;
    //    List<String> list_comment_content;
//    List<Integer> list_root;
//    List<String> list_reply;
//    List<String> list_benn_reply;
//    List<Integer> list_subroot;
//    List<List<List<Comment>>> list_comment;
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
        list_content = new ArrayList<>();
        list_drawables = new ArrayList<>();
//        list_comment_content = new ArrayList<>();
//        list_reply = new ArrayList<>();
//        list_root = new ArrayList<>();
//        list_benn_reply = new ArrayList<>();
//        list_subroot = new ArrayList<>();
//        list_comment = new ArrayList<>();

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

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                LinkerServer linkerServer = new LinkerServer("circle");
//                if (linkerServer.Linker()) {
//                    String response = linkerServer.getResponse();
//                    String str_content = response.substring(0, response.indexOf("*"));
//                    String str_comment = response.substring(response.indexOf("*") + 1);
//
//                    //说说内容本身
//                    String[] str_record = str_content.split("\\|");
//                    for (String str_node : str_record) {
//                        String[] record = str_node.split(";");
//                        list_content.add(record[0]);
//
//                        String[] pic_urls = record[1].split(",");
//                        List<Drawable> drawables = new ArrayList<Drawable>();
//                        for (String url : pic_urls) {
//                            byte[] data = new byte[0];
//                            try {
//                                data = ImageService.getImage(getString(R.string.LinkUrl) + "circle/" + url + ".jpg");
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            final byte[] finalData = data;
//                            drawables.add(new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length)));
//                        }
//                        list_drawables.add(drawables);
//                    }
//
//                    //评论
//                    String[] str_record_comment = str_comment.split("\\|");
//                    for (int i = 0; i < str_record_comment.length; ++i) {
//                        String[] record = str_record_comment[i].split(";");
//                        Comment comment = new Comment();
//
//                        if (!record[3].equals("-1")) {
//                            comment.setReplayUser(getUser("\t\t" + record[0], i));
//                            comment.setBeenReplayUser(getUser(record[3], i));
//                        } else comment.setBeenReplayUser(getUser(record[0], i));
//                        comment.setContent(record[1]);
//                        comment.setRoot(Integer.valueOf(record[2]));
//
//                        if (list_comment.size() <= Integer.valueOf(record[2]))
//                            list_comment.add(new ArrayList<List<Comment>>());
//                        if (list_comment.get(comment.getRoot()).size() <= Integer.valueOf(record[4]))
//                            list_comment.get(comment.getRoot()).add(new ArrayList<Comment>());
//                        list_comment.get(comment.getRoot()).get(Integer.valueOf(record[4])).add(comment);
//
////                        list_reply.add(record[0]);
////                        list_comment_content.add(record[1]);
////                        list_root.add(Integer.valueOf(record[2]));
////                        list_benn_reply.add(record[3]);
////                        list_subroot.add(Integer.valueOf(record[4]));
//                    }
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            initView();
//                        }
//                    });
//                } else {
//                    Toast.makeText(getActivity(), R.string.request_fail, Toast.LENGTH_SHORT).show();
//                }
//                handler.sendEmptyMessage(0);
//                Looper.loop();
//            }
//        }).start();
    }

    private void initView() {
        for (int content_index = 0; content_index < List_content_root.size(); ++content_index) {
            Dynamic dynamic = new Dynamic();
            dynamic.setImageCount(list_drawables.get(content_index).size());
            dynamic.setContent(list_content.get(content_index));
            dynamic.setDrawables(list_drawables.get(content_index));
            mDynamicList.add(dynamic);

            netwo(content_index);


//
//        //当所有的评论都已经加载后终止循环
//        for (int content_index = 0; content_index < mDynamicList.size(); ++content_index) {
//            List<Comment> commentList = new ArrayList<>();
//            if (content_index >= list_comment.size()) continue;
//            for (int subroot = 0; subroot < list_comment.get(content_index).size(); ++subroot)
//                commentList.addAll(list_comment.get(content_index).get(subroot));
//
//            mDynamicList.get(content_index).setCommentList(commentList);
////
//
//            for (int i = 0; i < list_comment_content.size(); i++) {
//                int j;
//                for (j = 0; j < list_comment_content.size(); j++) {
//                    Comment comment = new Comment();
//                    //对于在第content_index条说说下进行评论的加入到commentlist中
//                    if (list_root.get(i) == content_index && list_subroot.get(i) == j) {
//                        //如果该评论不是根评论的，即有被回复者，设置相应的被回复者
//                        if (!list_benn_reply.get(i).equals("-1")) {
//                            comment.setReplayUser(getUser("\t\t" + list_reply.get(i), i));
//                            comment.setBeenReplayUser(getUser(list_benn_reply.get(i), i));
//                        } else comment.setBeenReplayUser(getUser(list_reply.get(i), i));
//                        comment.setContent(list_comment_content.get(i));
//                        comment.setRoot(content_index);
//
//                        list_reply.remove(i);
//                        list_comment_content.remove(i);
//                        list_root.remove(i);
//                        list_benn_reply.remove(i);
//                        list_subroot.remove(i);
//                        --j;
//                    } else continue;
//                    commentList.add(comment);
//                }
//                i = j;
//            }
//            mDynamicList.get(content_index).setCommentList(commentList);
//    }
        }
    }

    private void netwo(int content_index) {
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

                    do {
                        for (int i = 0; i < list_comment.size(); ++i) {
                            String[] record = list_comment.get(i).split(";");
                            Comment comment = new Comment();

                            if (Integer.valueOf(record[2]) == i) {
                                if (!record[3].equals("-1")) {
                                    comment.setReplayUser(getUser("\t\t" + record[0], i));
                                    comment.setBeenReplayUser(getUser(record[3], i));
                                } else comment.setBeenReplayUser(getUser(record[0], i));
                                comment.setContent(record[1]);
                                comment.setRoot(root);

                                commentList.add(comment);
                                list_comment.remove(i);
                                --i;
                            } else continue;
                        }
                    } while (list_comment.isEmpty());
//                        do {
//                            String[] record = list_comment.get(content_index).split(";");
//                            Comment comment = new Comment();
//
//                            if (!record[3].equals("-1")) {
//                                comment.setReplayUser(getUser("\t\t" + record[0], content_index));
//                                comment.setBeenReplayUser(getUser(record[3], content_index));
//                            } else comment.setBeenReplayUser(getUser(record[0], content_index));
//                            comment.setContent(record[1]);
//                            comment.setRoot(Integer.valueOf(root));
//
//                            if (list_comment.size() <= Integer.valueOf(record[4]))
//                                list_comment.add(new ArrayList<Comment>());
//                            list_comment.get(Integer.valueOf(record[4])).add(comment);
//                        }while (list_comment.isEmpty());
//                        for (int i = 0; i < list_comment.size(); ++i) {
//
//                        }
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
