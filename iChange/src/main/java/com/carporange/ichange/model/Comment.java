package com.carporange.ichange.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2016/9/24.
 */
public class Comment implements Parcelable {

    private User commentUser;
    private User replayUser;
    private String content;
    private int root;
    private int subRoot;

    public Comment() {
    }

    protected Comment(Parcel in) {
        commentUser = in.readParcelable(User.class.getClassLoader());
        replayUser = in.readParcelable(User.class.getClassLoader());
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(commentUser, flags);
        dest.writeParcelable(replayUser, flags);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public User getCommentUser() {
        return commentUser;
    }

    public void setBeenReplayUser(User commentUser) {
        this.commentUser = commentUser;
    }

    public User getReplayUser() {
        return replayUser;
    }

    public void setReplayUser(User replayUser) {
        this.replayUser = replayUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public int getRoot() {
        return root;
    }

    public void setSubRoot(int subRoot) {
        this.subRoot = subRoot;
    }

    public int getSubRoot() {
        return subRoot;
    }
}
