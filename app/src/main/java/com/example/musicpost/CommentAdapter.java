package com.example.musicpost;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private List<CommentDto> itemList;

    public CommentAdapter(List<CommentDto> itemList) {
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
        CommentDto comment = itemList.get(position);
        holder.commentWriter.setText(comment.getCommenter().getUsername());
        holder.commentContent.setText(comment.getCommentText());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
