package com.example.musicpost

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PinListAdapter(val itemList: ArrayList<PinDto>, val mediaPlayer: MediaPlayer?): RecyclerView.Adapter<PinListAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val musicTitle: TextView = itemView.findViewById(R.id.musicTitleLabel)
        val selectedLocation: TextView = itemView.findViewById(R.id.selectedLocationLabel)
        val musicPlayButton: ImageButton = itemView.findViewById(R.id.musicPlayButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pin_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PinListAdapter.ViewHolder, position: Int) {

        holder.musicTitle.text = itemList[position].music.songName
        holder.selectedLocation.text = itemList[position].locationName

        holder.musicPlayButton.setOnClickListener{
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(itemList[position].music.musicUrl)
                mediaPlayer?.prepare()
                mediaPlayer?.start()
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