package com.example.musicpost

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicpost.databinding.ActivityMusicSearchBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MusicSearchActivity: AppCompatActivity() {

    companion object {
        const val TOKEN_BASE_URL = "https://accounts.spotify.com"
        const val SEARCH_BASE_URL = "https://api.spotify.com/v1/"
        const val CLIENT_ID = "48ec963edf6147b49c54370210e3b278"
        const val CLIENT_SECRET = "76b0c72805b341f080de8ca045516265"
    }

    private lateinit var binding : ActivityMusicSearchBinding
    private val listItems = arrayListOf<MusicListLayout>()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var musicListAdapter: MusicListAdapter
    private var keyword = "" // 검색 키워드
    private lateinit var accessToken : String
    private var musicURL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicSearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        lifecycleScope.launch {getToken()}

        // 음악 검색 리사이클 리스트 바인딩
        mediaPlayer = MediaPlayer()
        musicListAdapter = MusicListAdapter(listItems, mediaPlayer)
        binding.recycleList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycleList.adapter = musicListAdapter
        musicListAdapter.setItemClickListener(object: MusicListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                musicURL = listItems[position].preview_url
                val intent = Intent().apply {
                    putExtra("musicTitle", listItems[position].name)
                    putExtra("musicArtists", listItems[position].artistNames)
                    putExtra("musicURL", musicURL)
                }
                setResult(Activity.RESULT_OK, intent)
                onBackPressedDispatcher.onBackPressed()
                overridePendingTransition(R.anim.none, R.anim.horizontal_exit)
            }
        })

        binding.btnSearch.setOnClickListener {
            keyword = binding.searchEditText.text.toString()
            searchKeyword(keyword)
            hideKeyboard()
        }

        binding.backButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.none, R.anim.horizontal_exit)
        }
    }

    private suspend fun getToken() {
        val retrofit = Retrofit.Builder()
                .baseUrl(TOKEN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(SpotifyAuthService::class.java)

        val response = service.getAccessToken("client_credentials", CLIENT_ID, CLIENT_SECRET)
        accessToken = "Bearer " + response.access_token
    }

    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder() // Retrofit 구성
                .baseUrl(MusicSearchActivity.SEARCH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(SpotifyAPI::class.java) // 통신 인터페이스를 객체로 생성
        val call = api.searchTracks(accessToken, keyword) // 검색 조건 입력
        System.out.println(call.request().url().toString())
// API 서버에 요청
        call.enqueue(object: Callback<ResultMusicSearchKeyword> {
            override fun onResponse(call: Call<ResultMusicSearchKeyword>, response: Response<ResultMusicSearchKeyword>) {
// 통신 성공
                addItems(response.body())
            }

            override fun onFailure(call: Call<ResultMusicSearchKeyword>, t: Throwable) {
// 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")

            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItems(searchResult: ResultMusicSearchKeyword?) {
        if (!searchResult?.tracks?.items.isNullOrEmpty()) {
// 검색 결과 있음
            listItems.clear() // 리스트 초기화
            for (track in searchResult!!.tracks!!.items) {

// 결과를 리사이클러 뷰에 추가
                val item = MusicListLayout(track.name,
                        track.artists,
                        "",
                        track.preview_url)
                listItems.add(item)
            }

            musicListAdapter.notifyDataSetChanged()
        } else {
// 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.getWindowToken(), 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}