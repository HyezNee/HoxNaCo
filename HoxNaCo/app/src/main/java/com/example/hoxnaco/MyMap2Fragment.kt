package com.example.hoxnaco

import android.graphics.Color
import android.os.AsyncTask
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
import kotlinx.android.synthetic.main.fragment_my_map2.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class MyMap2Fragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var myActivity: MainActivity
    private var currentX = 127.079321
    private var currentY = 37.540950
    lateinit var listener :Overlay.OnClickListener
    lateinit var myDBHelper: MyDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        init()
        return inflater.inflate(R.layout.fragment_my_map2, container, false)
    }

    private fun init() {
        myDBHelper = MyDBHelper(requireContext())
        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(37.540950, 127.079321), 10.0))

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_view2, it).commit()
            }
        mapFragment.getMapAsync(this)

        val sdk = NaverMapSdk.getInstance(requireContext())
        sdk.onAuthFailedListener = object : NaverMapSdk.OnAuthFailedListener {
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

        // 기록된 위치에 마커 달기
        myDBHelper.getAllRecord()
        for(data in myDBHelper.myArray) {
            val marker = Marker()
            marker.position = LatLng(data.y.toDouble(), data.x.toDouble())
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.rgb(255, 159, 28)
            marker.alpha = 0.7f
            marker.map = naverMap
            marker.tag = data.address + "\n" + data.memo + "\n" + data.date
            marker.onClickListener = listener
        }

        // 현재 위치 기록 버튼 리스너 달기
        myActivity.recordBtn.setOnClickListener {
            val task = MyRGAsyncTask(requireContext() as MainActivity)
            task.execute(currentX, currentY)
            //task.execute(128.537886, 35.819104)
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
        // 맵 위치가 변경되었을 때
        naverMap.addOnLocationChangeListener { location ->
            currentX = location.longitude
            currentY = location.latitude
            //Toast.makeText(requireContext(), "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
        }

        doWithMap(p0)
    }

    class MyRGAsyncTask(context :MainActivity) : AsyncTask<Double, Unit, Unit>() {
        val activityReference = WeakReference(context)
        var isFinished = false // 플래그
        lateinit var place :MyPlaces

        override fun doInBackground(vararg params: Double?) {  // layout 변경하는 작업은 못하는듯..
            val activity = activityReference.get()
            reverseGeoCoding(activity, params[0], params[1])
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if (activity == null || activity.isFinishing)    // activity가 사라졌을 경우 함수 종료
                return
            val myDBHelper = MyDBHelper(activity)
            myDBHelper.insertPlace(place)
            isFinished = true
        }

        private fun reverseGeoCoding(activity: MainActivity?, x: Double?, y:Double?) {
            try {
                val addr = "${x.toString()},${y.toString()}"
                var link = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=$addr&output=json"
                var url = URL(link)
                var connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", BuildConfig.Naver_Map_ID)
                connection.setRequestProperty("X-NCP-APIGW-API-KEY", BuildConfig.Naver_Map_Secret)
                var code = connection.responseCode
                lateinit var br: BufferedReader
                if (code == 200) // 정상 호출
                    br = BufferedReader(InputStreamReader(connection.inputStream))
                else { // 에러 발생
                    br = BufferedReader(InputStreamReader(connection.errorStream))
                    return
                }
                val result = br.readLine()
                br.close();
                //Log.i("찾은 좌표", result)

                val rows1 = JSONObject(result).getJSONArray("results")
                val region = rows1.getJSONObject(0).getJSONObject("region")
                var address =""
                for(i in 1..4){
                    address += region.getJSONObject("area$i").getString("name") + " "
                }
                val current = LocalDateTime.now()
                val nowDate = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"))

                place = MyPlaces(address, nowDate, x.toString(), y.toString())
                //Log.i("주소", "$address, $nowDate")
            } catch (e: IOException) {
                Log.e("I/O 오류", e.message)
                return
            }
        }
    }

}
