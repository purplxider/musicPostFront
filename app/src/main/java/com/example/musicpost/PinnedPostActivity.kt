package com.example.musicpost

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicpost.databinding.ActivityPinnedPostBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class PinnedPostActivity: AppCompatActivity() {

    companion object {
        const val BASE_URL = "http://ec2-52-91-17-50.compute-1.amazonaws.com:8080/"
    }

    private lateinit var binding : ActivityPinnedPostBinding
    private val listItems = arrayListOf<PinListLayout>()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var pinListAdapter: PinListAdapter
    private var pins = mutableListOf<PinDto>()
    private var savedUsername = ""
    private var savedPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinnedPostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getCredentials()
        getPins()

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

    private fun getCredentials() {
        var sharedPref : SharedPreferences
        sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE)
        savedUsername = sharedPref.getString("username", "").toString()
        savedPassword = sharedPref.getString("password", "").toString()
    }

    private fun getPins() {
        val retrofit = Retrofit.Builder()
                .baseUrl(PinnedPostActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(GetPinAPI::class.java)
        val call = api.getPins(savedUsername)

        call.enqueue(object: Callback<List<PinDto>>{
            override fun onResponse(call: Call<List<PinDto>>, response: Response<List<PinDto>>) {
                addPins(response.body())
            }

            override fun onFailure(call: Call<List<PinDto>>, t: Throwable) {
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    private fun addPins(pins: List<PinDto>?) {
        if(!pins.isNullOrEmpty()) {
            listItems.clear()
            for (pin in pins) {
                val item = PinListLayout(pin.locationName,
                pin.music.musicUrl,
                pin.music.songName)
                listItems.add(item)
            }
            pinListAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "임시저장 한 글이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }


}