package com.carporange.ichange.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.model.Comment;
import com.carporange.ichange.model.Dynamic;
import com.carporange.ichange.model.User;
import com.carporange.ichange.util.CommentTagHandler;
import com.carporange.ichange.util.DensityUtil;

import java.util.List;

import cn.bmob.imdemo.model.UserModel;

/**
 * Created by 青松 on 2016/9/24.
 */
public class DynamicListAdapter extends RecyclerView.Adapter<DynamicListAdapter.DynamicViewHolder> implements View.OnClickListener {

    private Context context;
    private int imageHeight;
    private View commentView;
    private int commentViewY;
    private TextView send_btn;
    private EditText input_edit;
    private PopupWindow popupWindow;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private List<Dynamic> dynamicList;
    private InputMethodManager inputManager;
    private CommentTagHandler.OnCommentClickListener listener;

    private List<Drawable> mdrawables;
    private List<String> mcontents;

    private int myId = 102;

    public DynamicListAdapter(RecyclerView recyclerView, final Context context, List<Dynamic> dynamicList) {
//        this.mdrawables = drawables;
//        this.mcontents = contents;

        this.context = context;
        this.dynamicList = dynamicList;
        this.recyclerView = recyclerView;
        this.inflater = LayoutInflater.from(context);
        this.imageHeight = (DensityUtil.getScreenWidth() - DensityUtil.dip2px(10) * 3) / 2;
        this.inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.listener = new CommentTagHandler.OnCommentClickListener() {
            @Override
            public void onCommentorClicked(View view, User commentUser) { // 点击评论者
                Toast.makeText(context, commentUser.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReplyerClicked(View view, User replyUser) { //点击回复者
                Toast.makeText(context, replyUser.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCommentContentClicked(View view, String content, User commentUser, User replyUser) { //点击评论内容
                User user = replyUser != null ? replyUser : commentUser;
                List<Comment> commentList = (List<Comment>) view.getTag(CommentTagHandler.KEY_COMMENT_LIST);
                CommentListAdapter commentAdapter = (CommentListAdapter) view.getTag(CommentTagHandler.KEY_COMMENT_ADAPTER);
                int pos = (Integer) view.getTag(CommentTagHandler.KEY_COMMENT_ITEM_POSITION);
                // 如果点击的评论是自己发出的，则删除该评论
                if (user.getId() == myId) {
                    commentList.remove(pos);
                    commentAdapter.notifyDataSetChanged();
                    Toast.makeText(context, R.string.delete_own_comment, Toast.LENGTH_SHORT).show();
                    return;
                }

                String replyName = user.getName();
                input_edit.setHint("回复:" + replyName);
                send_btn.setTag(CommentTagHandler.KEY_REPLYER, user);
                send_btn.setTag(CommentTagHandler.KEY_COMMENT_LIST, commentList);
                send_btn.setTag(CommentTagHandler.KEY_COMMENT_ADAPTER, commentAdapter);
                showCommentPop(view);
            }
        };
        initCommentPop();
    }

    private void initCommentPop() {
        commentView = inflater.inflate(R.layout.layout_comment_pop, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        commentView.setLayoutParams(params);
        input_edit = (EditText) commentView.findViewById(R.id.input_edit);
        send_btn = (TextView) commentView.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(this);

        popupWindow = new PopupWindow(commentView, params.width, params.height);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setOutsideTouchable(true);
        // 该方法很重要，只有设置了该方法为true时，当软键盘弹出时才会将评论框顶起来
        //设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        //设置弹出窗体需要软键盘，
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //再设置模式，和Activity的一样，覆盖，调整大小。
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setFocusable(true);
    }

    private void showCommentPop(final View view) {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
            inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            int[] viewLocation = new int[2];
            view.getLocationOnScreen(viewLocation);
            final int viewY = viewLocation[1];
            // 避免每次都延迟滚动到指定位置，所以记录第一次获取到的评论框的位置
            // 延迟的目的是留足够的时间让评论框弹出来
            if (commentViewY == 0 || commentViewY == (DensityUtil.getScreenHeight() - commentView.getHeight())) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int[] commentLocation = new int[2];
                        commentView.getLocationOnScreen(commentLocation);
                        commentViewY = commentLocation[1];
                        int offsetY = viewY - commentViewY + view.getHeight();
                        recyclerView.smoothScrollBy(0, offsetY);
                    }
                }, 500);
            } else {
                int offsetY = viewY - commentViewY + view.getHeight();
                recyclerView.smoothScrollBy(0, offsetY);
            }
        }
    }

    @Override
    public DynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_dynamic, parent, false);
        return new DynamicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DynamicViewHolder holder, final int position) {
        Dynamic dynamic = dynamicList.get(position);
        final List<Comment> commentList = dynamic.getCommentList();
        holder.comment_count_tv.setText(context.getString(R.string.comment_num_text)+"(" + commentList.size() + ")");
        holder.tv_content.setText(dynamic.getContent());

        setImages(holder.image_ll, dynamic.getImageCount(), dynamic.getDrawables());

        holder.commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        final CommentListAdapter adapter = new CommentListAdapter(context, commentList, listener);
        holder.commentRecyclerView.setAdapter(adapter);
        holder.comment_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_edit.setHint(R.string.circle_say_something);
                send_btn.setTag(CommentTagHandler.KEY_REPLYER, null);
                send_btn.setTag(CommentTagHandler.KEY_COMMENT_LIST, commentList);
                send_btn.setTag(CommentTagHandler.KEY_COMMENT_ADAPTER, adapter);
                showCommentPop(view);
            }
        });
    }

    private void setImages(LinearLayout image_ll, int count, List<Drawable> drawables) {
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageHeight, imageHeight);
            if (i % 2 == 0) {
                params.setMargins(0, 0, 0, 0);
                imageView.setImageDrawable(drawables.get(i));
            } else {
                params.setMargins(DensityUtil.dip2px(10), 0, 0, 0);
                imageView.setImageDrawable(drawables.get(i));
            }
            imageView.setLayoutParams(params);
            image_ll.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return dynamicList.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_btn:
                startReply(view);
                break;
        }
    }

    private void startReply(View view) {
        if (TextUtils.isEmpty(input_edit.getText().toString())) {
            Toast.makeText(view.getContext(), R.string.input_cannot_null, Toast.LENGTH_SHORT).show();
            return;
        }
        Object tag = view.getTag(CommentTagHandler.KEY_REPLYER);
        User commentUser;
        User replayUser = null;
        if (tag != null) {
            commentUser = (User) view.getTag(CommentTagHandler.KEY_REPLYER);
            replayUser = new User(myId, UserModel.getInstance().getCurrentUser().getUsername());
        } else {
            commentUser = new User(myId, UserModel.getInstance().getCurrentUser().getUsername());
        }
        List<Comment> commentList = (List<Comment>) view.getTag(CommentTagHandler.KEY_COMMENT_LIST);
        CommentListAdapter commentAdapter = (CommentListAdapter) view.getTag(CommentTagHandler.KEY_COMMENT_ADAPTER);

        Comment comment = new Comment();
        comment.setReplayUser(replayUser);
        comment.setCommentUser(commentUser);
        //TODO 进行同步操作  被回复用户ID通过名字来获取
        comment.setContent(input_edit.getText().toString());
        commentList.add(comment);
        // 此处不能使用Adapter的notifyItemInserted方法，因为当点击该item时，
        // 需要使用到该item的position，如果使用notifyItemInserted方法会导致位置错乱
        commentAdapter.notifyDataSetChanged();
        input_edit.setText("");

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static class DynamicViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        RecyclerView commentRecyclerView;
        TextView comment_count_tv;
        LinearLayout image_ll;
        ImageView comment_iv;
        private TextView tv_content;

        public DynamicViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            commentRecyclerView = (RecyclerView) itemView.findViewById(R.id.comment_recyclerview);
            comment_count_tv = (TextView) itemView.findViewById(R.id.comment_count_tv);
            image_ll = (LinearLayout) itemView.findViewById(R.id.image_ll);
            comment_iv = (ImageView) itemView.findViewById(R.id.comment_iv);
            tv_content = (TextView) itemView.findViewById(R.id.dynamic_content);
        }
    }

}
