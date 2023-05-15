package com.example.musicpost

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MusicListAdapter(val itemList: ArrayList<MusicListLayout>, val mediaPlayer: MediaPlayer?): RecyclerView.Adapter<MusicListAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.listTrackName)
        val artists: TextView = itemView.findViewById(R.id.listArtistName)
        val musicPlayButton: ImageButton = itemView.findViewById(R.id.musicPlayButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MusicListAdapter.ViewHolder, position: Int) {

        holder.name.text = itemList[position].name
        val artistList = itemList[position].artists
        var nameList: MutableList<String> = mutableListOf()
        for (artist in artistList) {
            nameList.add(artist.name)
        }
        val names = nameList.joinToString(", ")
        itemList[position].artistNames = names
        holder.artists.text = names

        holder.musicPlayButton.setOnClickListener{
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer.pause()
                holder.musicPlayButton.setImageResource(R.drawable.play)
            } else {
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(itemList[position].preview_url)
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                holder.musicPlayButton.setImageResource(R.drawable.stop);
            }
        }

// 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }



    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}