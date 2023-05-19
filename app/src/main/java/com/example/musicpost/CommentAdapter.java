package com.example.musicpost;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private List<Comment> itemList;

    public CommentAdapter(List<Comment> itemList) {
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = itemList.get(position);
        holder.commentWriter.setText(comment.getCommentWriter());
        holder.commentContent.setText(comment.getCommentContent());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
