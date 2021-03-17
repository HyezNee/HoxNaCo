package com.example.hoxnaco

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_inform.view.*

/**
 * A simple [Fragment] subclass.
 */
class InformFragment : Fragment() {

    lateinit var madapter: MyNewsAdapter    // 뉴스
    lateinit var madapter2: MyInformAdapter   // 질본 보도자료
    var itemArr = ArrayList<MyNewsData>()
    var itemArr2 = ArrayList<MyNewsData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inform, container, false)
        Log.i("과정", "createView")

        // Set the adapter. progress bar
        // 뉴스 부분
        view.recyclerNews.layoutManager = LinearLayoutManager(context)
        view.recyclerNews.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))   // 구분선 달기
        // 질본 보도자료 부분
        view.recyclerJilbon.layoutManager = LinearLayoutManager(context)
        view.recyclerJilbon.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))   // 구분선 달기

        // 쓰레드가 끝나기 전 5번 탭이 호출되었을 때 -> progressbar 후 data 표시
        if(activity != null) {
            val myActivity = activity as MainActivity
            // 뉴스 부분
            if(myActivity.task2.isFinished)
                itemArr = myActivity.task2.newsArr
            else
                view.progressBar.visibility = View.VISIBLE
            myActivity.task2.callback = object :MainActivity.MyNewsAsyncTask.dataChangedCallback {
                override fun dataChangedCallback() {
                    madapter.items = myActivity.task2.newsArr
                    madapter.notifyDataSetChanged()
                }
            }
            // 질본 보도자료 부분
            if(myActivity.task3.isFinished)
                itemArr2 = myActivity.task3.newsArr
            else
                view.progressBar2.visibility = View.VISIBLE
            myActivity.task3.callback = object :
                MainActivity.MyJilbonAsyncTask.dataChangedCallback {
                override fun dataChangedCallback() {
                    madapter2.items = myActivity.task3.newsArr
                    madapter2.notifyDataSetChanged()
                }
            }
        }
        madapter = MyNewsAdapter(itemArr)
        view.recyclerNews.adapter = madapter
        madapter2 = MyInformAdapter(itemArr2)
        view.recyclerJilbon.adapter = madapter2


        // 클릭 listener 달기
        madapter.itemClickListener = object :MyNewsAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: MyNewsAdapter.MyViewHolder,
                view: View,
                data: MyNewsData,
                position: Int
            ) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(madapter.items[position].url))
                startActivity(intent)
            }
        }
        madapter2.itemClickListener = object :MyInformAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: MyInformAdapter.MyViewHolder,
                view: View,
                data: MyNewsData,
                position: Int
            ) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(madapter2.items[position].url))
                startActivity(intent)
            }


        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("과정", "attach")
    }

    override fun onResume() {
        super.onResume()
        if(activity != null) {
            val myActivity = activity as MainActivity
            activity?.toolbarText?.text = "관련 기사 및 정보"
        }
        Log.i("과정", "resume")
    }

    override fun onStart() {
        super.onStart()
        Log.i("과정", "start")
    }
}
