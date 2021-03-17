package com.example.hoxnaco

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_record.view.*

/**
 * A simple [Fragment] subclass.
 */
class RecordFragment : Fragment() {

    lateinit var fragment :MyMap2Fragment
    lateinit var madapter :MyPlaceAdapter
    lateinit var myDBHelper :MyDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        init(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        if(activity != null) {
            val myActivity = activity as MainActivity
            activity?.toolbarText?.text = "동선 기록"
        }
        myDBHelper.getAllRecord()
        madapter.items = myDBHelper.myArray
        madapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        fragmentManager?.popBackStack() // 네이버 지도 버그(?) 문제..
    }

    private fun init(view :View) {
        myDBHelper = MyDBHelper(requireContext())
        view.placeList.layoutManager = LinearLayoutManager(context)
        //myDBHelper.getAllRecord()
        madapter = MyPlaceAdapter(myDBHelper.myArray)
        view.placeList.adapter = madapter

        // 클릭 리스너
        view.currentLocationBtn.setOnClickListener {
            createMap()
        }
        madapter.itemClickListener = object :MyPlaceAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: MyPlaceAdapter.MyViewHolder,
                view: View,
                data: MyPlaces,
                position: Int
            ) {
                val listItems = arrayOf<String>("메모 삽입/수정", "동선 삭제")
                //final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                val builder = AlertDialog.Builder(requireContext());
                builder.setTitle("옵션 선택");
                builder.setItems(listItems) { _, which ->
                    when(which) {
                        0 -> {
                            editText(data)
                        }
                        1 -> {
                            myDBHelper.deletePlace(data.date)
                            myDBHelper.getAllRecord()
                            madapter.notifyDataSetChanged()
                        }
                    }
                }
                builder.show()
            }
        }
    }

    private fun editText(data :MyPlaces) {
        val builder = AlertDialog.Builder(requireContext());
        val editText = EditText(requireContext())
        builder.setTitle("옵션 선택");
        builder.setMessage("삽입/수정 할 메모를 입력하세요")
        builder.setView(editText)

        builder.setPositiveButton("확인") { _, _ ->
            data.memo = editText.text.toString()
            myDBHelper.updatePlace(data)
            myDBHelper.getAllRecord()
            madapter.notifyDataSetChanged()
        }
        builder.setNegativeButton("취소") { _, _ ->
        }
        builder.show()
    }

    private fun createMap() {
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragment = MyMap2Fragment()
        fragmentTransaction?.replace(R.id.record_fragment, fragment)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

}
