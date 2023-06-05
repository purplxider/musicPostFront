package com.example.musicpost;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedViewHolder> {
    private List<PostDto> itemList;

    public RecommendedAdapter(List<PostDto> itemList) {
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public RecommendedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_list_layout, parent, false);
        return new RecommendedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedViewHolder holder, int position) {
        PostDto postDto = itemList.get(position);
        holder.recommendedPostTitleLabel.setText(postDto.getTitle());
        holder.recommendedPostShortContentLabel.setText(postDto.getDescription());
        holder.recommendedPostMusicTitleLabel.setText(postDto.getMusic().getSongName());
        holder.recommendedPostMusicArtistLabel.setText(postDto.getMusic().getArtist());
        holder.recommendedPostLocationLabel.setText(postDto.getLocationName());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}