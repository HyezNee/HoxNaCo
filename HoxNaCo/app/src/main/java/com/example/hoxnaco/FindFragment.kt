package com.example.hoxnaco

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * A simple [Fragment] subclass.
 */
class FindFragment : Fragment(){
    lateinit var fragment :MyMapFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_find, container, false)
        Log.i("맵 상태", "createview")
        return view
    }

    override fun onResume() {
        super.onResume()
        createMap()
        if(activity != null) {
            val myActivity = activity as MainActivity
            activity?.toolbarText?.text = "근처 선별 진료소 찾기"
        }
    }

    override fun onPause() {
        super.onPause()
        fragmentManager?.popBackStack() // 네이버 지도 버그(?) 문제..
    }

    private fun createMap() {
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragment = MyMapFragment()
        fragmentTransaction?.replace(R.id.find_fragment, fragment)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

}
