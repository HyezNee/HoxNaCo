package com.example.hoxnaco

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

/**
 * A simple [Fragment] subclass.
 */
class MyMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var myActivity: MainActivity
    // val markerArr = ArrayList<Marker>()
    lateinit var listener :Overlay.OnClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        init()

        return inflater.inflate(R.layout.fragment_my_map, container, false)
    }

    private fun init() {
        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(37.540950, 127.079321), 10.0))

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        val sdk = NaverMapSdk.getInstance(requireContext())
        sdk.onAuthFailedListener = object :NaverMapSdk.OnAuthFailedListener {
            override fun onAuthFailed(p0: NaverMapSdk.AuthFailedException) {
                Log.i("api 인증 실패", p0.errorCode)
            }
        }

        if(activity != null)
            myActivity = activity as MainActivity

        // 클릭리스너 달기
        val infoWindow = InfoWindow()
        listener = Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker

            if (marker.infoWindow == null) {    // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.alpha = 0.8f
                infoWindow.open(marker)
            } else {    // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }
            true
        }

        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }
    }

    private fun doWithMap(naverMap: NaverMap) {
        locationSource = FusedLocationSource(this, 100)    // 권한 설정
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.uiSettings.isLocationButtonEnabled = true  // 위치 추적 모드 변경 버튼 부착
        naverMap.uiSettings.isZoomControlEnabled = false
        naverMap.minZoom = 7.0

        /*val testMarker = Marker()
        testMarker.position = LatLng(37.540950, 127.079321)
        testMarker.icon = MarkerIcons.BLACK
        testMarker.iconTintColor = Color.rgb(46, 196, 182)
        testMarker.map = naverMap*/
        if(myActivity.task4.isFinished)
            for(data in myActivity.addressArr) {
                val marker = Marker()
                marker.position = LatLng(data.x, data.y)
                marker.icon = MarkerIcons.BLACK
                marker.iconTintColor = Color.rgb(46, 196, 182)
                marker.alpha = 0.7f
                marker.map = naverMap
                marker.tag = data.name + "\n" + data.address + "\n" + data.tel
                marker.onClickListener = listener
                //markerArr.add(marker)
                //Log.i("맵좌표", data.x.toString() + "," + data.y.toString())
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(p0: NaverMap) {
        this.naverMap = p0
        doWithMap(p0)
    }

}
