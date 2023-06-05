package com.example.musicpost;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecommendedViewHolder extends RecyclerView.ViewHolder {
    TextView recommendedPostTitleLabel;
    TextView recommendedPostShortContentLabel;
    ImageButton recommendedPostMusicPlayButton;
    TextView recommendedPostMusicTitleLabel;
    TextView recommendedPostMusicArtistLabel;
    TextView recommendedPostLocationLabel;

    public RecommendedViewHolder(View itemView) {
        super(itemView);
        recommendedPostTitleLabel = itemView.findViewById(R.id.recommendedPostTitleLabel);
        recommendedPostShortContentLabel = itemView.findViewById(R.id.recommendedPostShortContentLabel);
        recommendedPostMusicPlayButton = itemView.findViewById(R.id.recommendedPostMusicPlayButton);
        recommendedPostMusicTitleLabel = itemView.findViewById(R.id.recommendedPostMusicTitleLabel);
        recommendedPostMusicArtistLabel = itemView.findViewById(R.id.recommendedPostMusicArtistLabel);
        recommendedPostLocationLabel = itemView.findViewById(R.id.recommendedPostLocationLabel);
    }
}