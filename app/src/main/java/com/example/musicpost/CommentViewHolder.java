package com.example.musicpost;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView commentWriter;
    TextView commentContent;

    public CommentViewHolder(View itemView) {
        super(itemView);
        commentWriter = itemView.findViewById(R.id.commentWriter);
        commentContent = itemView.findViewById(R.id.commentContent);
    }
}
