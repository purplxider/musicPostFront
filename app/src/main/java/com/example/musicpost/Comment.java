package com.example.musicpost;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Comment implements Parcelable {
    private String commentWriter;
    private String commentContent;

    public Comment(String commentWriter, String commentContent) {
        this.commentWriter = commentWriter;
        this.commentContent = commentContent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(commentWriter);
        parcel.writeString(commentContent);
    }

    protected Comment(Parcel in) {
        commentWriter = in.readString();
        commentContent = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel parcel) {
            return new Comment(parcel);
        }

        @Override
        public Comment[] newArray(int i) {
            return new Comment[i];
        }
    };

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentWriter() {
        return commentWriter;
    }

    public void setCommentWriter(String commentWriter) {
        this.commentWriter = commentWriter;
    }
}
