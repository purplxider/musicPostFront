package com.example.musicpost

import KakaoAPI
import LocationListLayout
import ResultLocationSearchKeyword
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicpost.databinding.ActivityLocationSearchBinding
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LocationSearchActivity : AppCompatActivity(), LocationListener {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 167a97c75a92a1ecafc82ee8107258bc" // REST API 키
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var binding : ActivityLocationSearchBinding
    private val listItems = arrayListOf<LocationListLayout>() // 리사이클러 뷰 아이템
    private val locationListAdapter = LocationListAdapter(listItems) // 리사이클러 뷰 어댑터
    private var pageNumber = 1 // 검색 페이지 번호
    private var keyword = "" // 검색 키워드
    private var lat = 0.0
    private var lon = 0.0
    private var name = ""
    private var address = ""
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the location manager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }

        binding = ActivityLocationSearchBinding.inflate(layoutInflater)
        val view = binding.root
        binding.etSearchField.setBackgroundColor(Color.TRANSPARENT)
        setContentView(view)

// 리사이클러 뷰
        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = locationListAdapter
// 리스트 아이템 클릭 시 해당 위치로 이동
        locationListAdapter.setItemClickListener(object: LocationListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
                name = listItems[position].name
                address = listItems[position].address

                val intent = Intent().apply {
                    putExtra("name", name)
                    putExtra("address", address)
                    putExtra("source", "locationSearch")
                }
                setResult(Activity.RESULT_OK, intent)
                onBackPressedDispatcher.onBackPressed()
                overridePendingTransition(R.anim.none, R.anim.horizontal_exit)
            }
        })

// 검색 버튼
        binding.btnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber, lon, lat, 100)
            hideKeyboard()
        }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.none, R.anim.horizontal_exit)
        }
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int, x: Double, y: Double, radius: Int) {
        val retrofit = Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(KakaoAPI::class.java) // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, page, x, y, radius) // 검색 조건 입력
// API 서버에 요청
        call.enqueue(object: Callback<ResultLocationSearchKeyword> {
            override fun onResponse(call: Call<ResultLocationSearchKeyword>, response: Response<ResultLocationSearchKeyword>) {
// 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultLocationSearchKeyword>, t: Throwable) {
// 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")

            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultLocationSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
// 검색 결과 있음
            listItems.clear() // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            binding.constraintLayout.visibility = View.VISIBLE
            for (document in searchResult!!.documents) {

// 결과를 리사이클러 뷰에 추가
                val item = LocationListLayout(document.place_name,
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
            locationListAdapter.notifyDataSetChanged()

        } else {
// 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearchField.getWindowToken(), 0)
    }

    override fun onLocationChanged(location: Location) {
        lat = location.latitude
        lon = location.longitude
        val locationPoint = MapPoint.mapPointWithGeoCoord(lat, lon)
        val point = MapPOIItem()
        point.apply {
            itemName = "current"
            mapPoint = locationPoint
            markerType = MapPOIItem.MarkerType.YellowPin
        }
        val radius = MapCircle(locationPoint, 50, Color.argb(0, 0,0,0), Color.argb(30,85,91,100))
        binding.mapView.removeAllCircles()
        binding.mapView.removeAllPOIItems()
        binding.mapView.addPOIItem(point)
        binding.mapView.addCircle(radius)
        binding.mapView.setMapCenterPointAndZoomLevel(locationPoint, 0, true)
    }
}