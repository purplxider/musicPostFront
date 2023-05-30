package com.example.musicpost

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicpost.databinding.ActivityPinnedPostBinding

class PinnedPostActivity: AppCompatActivity() {
    private lateinit var binding : ActivityPinnedPostBinding
    private val listItems = arrayListOf<PinListLayout>()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var pinListAdapter: PinListAdapter
    private var pins = mutableListOf<PinDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinnedPostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mediaPlayer = MediaPlayer()
        pinListAdapter = PinListAdapter(listItems, mediaPlayer)
        binding.pinList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.pinList.adapter = pinListAdapter
        pinListAdapter.setItemClickListener(object: PinListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent().apply {
                    putExtra("source", "pinnedPost")
                    putExtra("location", pins[position].locationName)
                    putExtra("address", pins[position].address)
                    putExtra("longitude", pins[position].coordinate.longitude)
                    putExtra("latitude", pins[position].coordinate.latitude)
                    putExtra("musicArtists", pins[position].music.artist)
                    putExtra("musicTitle", pins[position].music.songName)
                    putExtra("musicURL", pins[position].music.musicUrl)
                }
                setResult(Activity.RESULT_OK, intent)
                onBackPressedDispatcher.onBackPressed()
                overridePendingTransition(R.anim.none, R.anim.horizontal_exit)
            }
        })

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.none, R.anim.horizontal_exit)
        }
    }
}