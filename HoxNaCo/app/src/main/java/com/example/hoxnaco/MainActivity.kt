package com.example.hoxnaco

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_inform.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    val icons = arrayOf<Int>(R.drawable.ic_group_black_48dp, R.drawable.ic_done_black_48dp,
        R.drawable.ic_local_hospital_black_48dp, R.drawable.ic_directions_walk_black_48dp, R.drawable.ic_add_black_48dp)
    val addressArr = ArrayList<MyAddress>()
    lateinit var task :MyAsyncTask
    lateinit var task2 :MyNewsAsyncTask
    lateinit var task3 :MyJilbonAsyncTask
    lateinit var task4 :MyHospitalAsyncTask
    //lateinit var geo1 :MyHospitalAsyncTask2

    val mainFragment = MainFragment()
    val checkFragment = CheckFragment()
    val findFragment = FindFragment()
    val recordFragment = RecordFragment()
    val informFragment = InformFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        //supportActionBar!!.setTitle("혹나코")
        if (savedInstanceState == null) {            // 애니메이션 추가
            this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
        }
        startTask()
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {   // 앱 바 이벤트 처리
        when(item.itemId) {
            R.id.settings -> {
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun startTask() {
        task = MyAsyncTask(this)
        task.execute(URL("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson"))
        task2 = MyNewsAsyncTask(this)
        task2.execute(URL("http://newsapi.org/v2/top-headlines"))
        task3 = MyJilbonAsyncTask(this)
        task3.execute(URL("http://ncov.mohw.go.kr/tcmBoardList.do?brdId=3&brdGubun="))
        task4 = MyHospitalAsyncTask(this)
        task4.execute()
        //geo1 = MyHospitalAsyncTask2(this)
        //geo1.execute(0, 597)
    }

    private fun init() {
        // 탭 달기
        contents.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(tab, contents){ tab, position ->
            tab.setIcon(icons[position])
        }.attach()
        contents.isUserInputEnabled = false // 스트롤해서 탭 넘기는 기능 삭제
        //contents.currentItem = 2
    }


    class MyAsyncTask(context :MainActivity) : AsyncTask<URL, Unit, Unit>() {
        val activityReference = WeakReference(context)
        val sums = ArrayList<String>()
        var newSumsR = mutableListOf<String>()  // 시도별 추가 확진자 수
        var allSumsR = mutableListOf<String>()  // 시도별 격리자 수
        var isFinished = false // 플래그(쓰레드 작업 끝났는지)
        var isConnected = true // 플래그(인터넷 연결)

        override fun doInBackground(vararg params: URL?) {  // layout 변경하는 작업은 못하는듯..
            val activity = activityReference.get()

            // 현재 날짜 받아오기
            val current = LocalDate.now()
            val nowDate = current.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val yesterday = current.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))

            // html 소스 가져오기
            lateinit var doc : Document
            try {
                // doc = Jsoup.connect(params[0].toString()).get()

                val url = params[0].toString() + "?serviceKey=${BuildConfig.Corona_Num_Key}&pageNo=1&numOfRows=1&startCreateDt=$yesterday&endCreateDt=$nowDate"
                doc = Jsoup.connect(url).parser(Parser.xmlParser()).get()
            } catch(e : IOException) {
                Log.e("MainActivity", "api 불러오기 오류")
                isConnected = false
                return
            }

            val rows = doc.select("item")   // 각 row

            for(row in rows) {
                newSumsR.add(row.select("incDec").text())
                allSumsR.add(row.select("isolIngCnt").text())
            }
            newSumsR = newSumsR.subList(0, 18)  // 지역 총합 ~ 어제 것도 있을 경우 자르기
            newSumsR.removeAt(0)    // 검역
            allSumsR.removeAt(0)

            sums.add(rows[18].select("incDec").text())
            sums.add(rows[18].select("defCnt").text())
            sums.add(rows[18].select("isolIngCnt").text())
            sums.add(rows[18].select("isolClearCnt").text())
            sums.add(rows[18].select("deathCnt").text())
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if(activity == null || activity.isFinishing)    // activity가 사라졌을 경우 함수 종료
                return
            if(!isConnected){
                Toast.makeText(activity, "데이터 불러오기 실패. 인터넷 연결 확인", Toast.LENGTH_LONG).show()
                return
            }
            if(activity.rp17 == null)
                return
                //Thread.sleep(1000)
            val newText = arrayOf<TextView>(activity.rp17, activity.rp11, activity.rp9, activity.rp15, activity.rp14, activity.rp6, activity.rp5,
                                                            activity.rp4, activity.rp2, activity.rp8, activity.rp13, activity.rp7, activity.rp16, activity.rp3,
                                                            activity.rp10, activity.rp12, activity.rp1)
            val allText = arrayOf<TextView>(activity.rs17, activity.rs11, activity.rs9, activity.rs15, activity.rs14, activity.rs6, activity.rs5,
                                                            activity.rs4, activity.rs2, activity.rs8, activity.rs13, activity.rs7, activity.rs16, activity.rs3,
                                                            activity.rs10, activity.rs12, activity.rs1)

            activity.p1.text = "+" + sums[0] + " "
            activity.c1.text = sums[1]
            activity.c2.text = sums[2]
            activity.c3.text = sums[3]
            activity.c4.text = sums[4]

            for(i in newText.indices)
                newText[i].text = "+" + newSumsR[i] + " "
            for(i in allText.indices)
                allText[i].text = allSumsR[i]

            isFinished = true
        }
    }

    class MyNewsAsyncTask(context :MainActivity) : AsyncTask<URL, Unit, Unit>() {
        val activityReference = WeakReference(context)
        val newsArr = ArrayList<MyNewsData>()
        var isFinished = false // 플래그
        var callback :dataChangedCallback ?= null
        var isConnected = true // 플래그(인터넷 연결)

        override fun doInBackground(vararg params: URL?) {  // layout 변경하는 작업은 못하는듯..
            val activity = activityReference.get()

            // html 소스 가져오기
            lateinit var doc1 : Document
            lateinit var doc2 : Document
            try {
                // doc = Jsoup.connect(params[0].toString()).get()
                val url1 = params[0].toString() + "?country=kr&q=" + URLEncoder.encode("코로나", "UTF-8") +
                        "&apiKey=" + BuildConfig.Corona_News_Key
                val url2 = params[0].toString() + "?country=kr&q=" + URLEncoder.encode("확진", "UTF-8") +
                        "&apiKey=" + BuildConfig.Corona_News_Key
                doc1 = Jsoup.connect(url1).ignoreContentType(true).get()
                doc2 = Jsoup.connect(url2).ignoreContentType(true).get()
            } catch(e : IOException) {
                Log.e("MainActivity", "api 불러오기 오류")
                isConnected = false
                return
            }

            var rows1 = try {
                JSONObject(doc1.text()).getJSONArray("articles")   // 각 row
            } catch (e: JSONException) {
                null
            }
            if(rows1 != null) {
                for (i in 0 until rows1.length()) {
                    val title =
                        rows1.getJSONObject(i).getString("title").split("/")[0].split("-")[0]
                    val content = rows1.getJSONObject(i).getString("description")
                    val url = rows1.getJSONObject(i).getString("url")
                    newsArr.add(MyNewsData(title, content, url))
                }
            }

            val rows2 = try {
                JSONObject(doc2.text()).getJSONArray("articles")
            } catch (e: JSONException) {
                null
            }
            if(rows2 != null) {
                for (i in 0 until rows2.length()) {
                    val title =
                        rows2.getJSONObject(i).getString("title").split("/")[0].split("-")[0]
                    val content = rows2.getJSONObject(i).getString("description")
                    val url = rows2.getJSONObject(i).getString("url")
                    newsArr.add(MyNewsData(title, content, url))
                }
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if(activity == null || activity.isFinishing)    // activity가 사라졌을 경우 함수 종료
                return
            if(!isConnected){
                Toast.makeText(activity, "데이터 불러오기 실패. 인터넷 연결 확인", Toast.LENGTH_LONG).show()
                return
            }
            if(activity.progressBar != null)
                activity.progressBar.visibility = View.GONE
            if(callback != null)
                callback?.dataChangedCallback()
            isFinished = true
        }

        interface dataChangedCallback {
            fun dataChangedCallback()
        }
    }

    class MyJilbonAsyncTask(context :MainActivity) : AsyncTask<URL, Unit, Unit>() {
        val activityReference = WeakReference(context)
        val newsArr = ArrayList<MyNewsData>()
        var isFinished = false // 플래그
        var callback :dataChangedCallback ?= null
        var isConnected = true // 플래그(인터넷 연결)

        override fun doInBackground(vararg params: URL?) {  // layout 변경하는 작업은 못하는듯..
            val activity = activityReference.get()

            // html 소스 가져오기
            lateinit var doc : Document
            try {
                doc = Jsoup.connect(params[0].toString()).get()
            } catch(e : IOException) {
                Log.e("MainActivity", "api 불러오기 오류")
                isConnected = false
                return
            }
            val news = doc.select("table>tbody>tr>td.ta_l>a.bl_link")
            for(data in news) {
                // onClick="fn_tcm_boardView('/tcmBoardView.do','','','355140','', 'ALL');"
                val idx = data.attr("onClick").indexOf("(")
                val idx2 = data.attr("onClick").indexOf(")")
                var value = data.attr("onClick").substring(idx + 1, idx2).split(",")[3]
                value = value.substring(1, value.length - 1)
                val url = "http://ncov.mohw.go.kr/tcmBoardView.do?brdId=&brdGubun=&dataGubun=&ncvContSeq=$value&contSeq=$value&board_id=&gubun=ALL"
                //Log.i("주소", url)
                newsArr.add(MyNewsData(data.text(), "", url))
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if(activity == null || activity.isFinishing)    // activity가 사라졌을 경우 함수 종료
                return
            if(!isConnected){
                Toast.makeText(activity, "데이터 불러오기 실패. 인터넷 연결 확인", Toast.LENGTH_LONG).show()
                return
            }
            if(activity.progressBar2 != null)
                activity.progressBar2.visibility = View.GONE
            if(callback != null)
                callback?.dataChangedCallback()
            isFinished = true
        }

        interface dataChangedCallback {
            fun dataChangedCallback()
        }
    }

    class MyHospitalAsyncTask(context :MainActivity) : AsyncTask<Unit, Unit, Unit>() {
        val activityReference = WeakReference(context)
        var isFinished = false // 플래그
        var isConnected = true // 플래그(인터넷 연결)

        override fun doInBackground(vararg params: Unit) {  // layout 변경하는 작업은 못하는듯..
            val activity = activityReference.get()
            readFile(activity)
            readXY(activity)
            //activity?.addMarker()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if(activity == null || activity.isFinishing)    // activity가 사라졌을 경우 함수 종료
                return
            //activity.addMarker()
            isFinished = true
        }

        private fun readFile(activity :MainActivity?) {
            val scan = Scanner(activity?.resources?.openRawResource(R.raw.hospitallist2))
            while (scan.hasNextLine()) {
                val line = scan.nextLine()
                val lineArr = line.split("\t")
                val name = lineArr[4]
                var address = lineArr[5]
                if (address[0] == '\"')    // 따옴표로 시작
                    address = address.split("\"")[1]
                address = address.split("(")[0].split(",")[0]
                var tel = lineArr[6]
                if (tel[0] == '\"')
                    tel = tel.split("\"")[1]
                activity?.addressArr?.add(MyAddress(name, address, tel))
                //Log.i("파싱한 것", "$name / $address / $tel")
                //Log.i("파싱한 것", address)
            }
            scan.close()
        }

        private fun readXY(activity: MainActivity?) {
            val scan = Scanner(activity?.resources?.openRawResource(R.raw.xy))
            var idx = 0
            while (scan.hasNextLine()) {
                val x = scan.nextLine().toDouble()
                val y = scan.nextLine().toDouble()
                activity?.addressArr?.get(idx)?.x = y
                activity?.addressArr?.get(idx)?.y = x
                //Log.i("객체", activity?.addressArr?.get(idx)?.x.toString())
                idx++
            }
            scan.close()
        }
    }



    /*class MyHospitalAsyncTask2(context :MainActivity) : AsyncTask<Int, Unit, Unit>() {
        val activityReference = WeakReference(context)
        var isFinished = false // 플래그

        override fun doInBackground(vararg params: Int?) {  // layout 변경하는 작업은 못하는듯..
            val activity = activityReference.get()

            for(i in params[0]!! until params[1]!!)
                activity?.geoCoding(activity!!.addressArr[i].address)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if(activity == null || activity.isFinishing)    // activity가 사라졌을 경우 함수 종료
                return
        }
    }*/

    /*private fun geoCoding(address :String) {  // 비운의 코드...
        try {
            var addr = URLEncoder.encode(address, "UTF-8")
            var link = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$addr"
            var url = URL(link);
            var connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", NAVER_MAP_ID)
            connection.setRequestProperty("X-NCP-APIGW-API-KEY", NAVER_MAP_SECRET)
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

            val rows1 = JSONObject(result).getJSONArray("addresses")
            val x = rows1.getJSONObject(0).getString("x")
            val y = rows1.getJSONObject(0).getString("y")

            // 좌표값 파일에 쓰기
            val output = PrintStream(openFileOutput("xy.txt", Context.MODE_APPEND))
            output.println(x)
            output.println(y)
            output.close()
        } catch (e: IOException) {
            Log.e("I/O 오류", e.message)
            return
        }
    }*/
}
