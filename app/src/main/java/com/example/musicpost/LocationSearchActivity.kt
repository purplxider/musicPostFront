package com.example.musicpost

import KakaoAPI
import ListLayout
import ResultSearchKeyword
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicpost.databinding.ActivityLocationSearchBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LocationSearchActivity : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 167a97c75a92a1ecafc82ee8107258bc" // REST API 키
    }

    private lateinit var binding : ActivityLocationSearchBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val listItems = arrayListOf<ListLayout>() // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems) // 리사이클러 뷰 어댑터
    private var pageNumber = 1 // 검색 페이지 번호
    private var keyword = "" // 검색 키워드
    private var lat = 0.0
    private var lon = 0.0
    private var name = ""
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSearchBinding.inflate(layoutInflater)
        val view = binding.root
        overridePendingTransition(R.anim.horizontal_enter, R.anim.horizontal_exit)
        animate()
        setContentView(view)

// 리사이클러 뷰
        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter
// 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
                name = listItems[position].name
                address = listItems[position].address
            }
        })

// 검색 버튼
        binding.btnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            lat = 37.5661
            lon = 126.9788
            searchKeyword(keyword, pageNumber, lon, lat, 100)
        }

// 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber, lon, lat, 100)
        }

// 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber, lon, lat, 100)
        }

        binding.btnSaveLocation.setOnClickListener {
            val intent = Intent(this@LocationSearchActivity, PostActivity::class.java).apply {
                putExtra("name", name)
                putExtra("address", address)
                putExtra("source", "locationSearch")
            }
            startActivity(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int, x: Double, y: Double, radius: Int) {
        val retrofit = Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(KakaoAPI::class.java) // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, page, x, y, radius) // 검색 조건 입력
        System.out.println(call.request().url().toString())
// API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
// 통신 성공
                binding.constraintLayout.visibility = View.VISIBLE
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
// 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")

            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
// 검색 결과 있음
            listItems.clear() // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {

// 결과를 리사이클러 뷰에 추가
                val item = ListLayout(document.place_name,
                        document.road_address_name,
                        document.address_name,
                        document.x.toDouble(),
                        document.y.toDouble())
                listItems.add(item)

// 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                            document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1 // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
// 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocation() {
        // Checking Location Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, OnSuccessListener { location: Location? ->
            if (location != null) {
                lat = location.latitude
                lon = location.longitude
            }
        })
        //val mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon)
        val mapPoint = MapPoint.mapPointWithGeoCoord(37.5661, 126.9788)

        binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
    }

    fun animate() {
        overridePendingTransition(R.anim.horizontal_enter, R.anim.none)
    }

    private val resultLauncher = registerForActivityResult<String, Boolean>(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

}