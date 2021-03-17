package com.example.hoxnaco

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {
    //lateinit var nums: Elements

    /*companion object {   // static 함수 -> 인자를 받아 fragment 생성 위해
        fun newAllVocFragment(sums: Elements) :MainFragment {
            val mainFragment = MainFragment()
            mainFragment.nums = nums
            return mainFragment
        }
    }*/

    /*interface taskListener2 {
        fun onTask()
    }
    var myTaskListener :taskListener2 ?= null*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(activity != null) {
            val myActivity = activity as MainActivity
            activity?.toolbarText?.text = "혹나코"
            if (myActivity.task.isFinished) {
                // 4, 5번째 탭으로 넘어갔다 현재 탭으로 다시 돌아오면 웹파싱 초기화되는 버그 해결
                val sums = myActivity.task.sums
                val newSumsR = myActivity.task.newSumsR
                val allSumsR = myActivity.task.allSumsR
                val newText = arrayOf<TextView>(myActivity.rp17, myActivity.rp11, myActivity.rp9, myActivity.rp15, myActivity.rp14, myActivity.rp6, myActivity.rp5,
                    myActivity.rp4, myActivity.rp2, myActivity.rp8, myActivity.rp13, myActivity.rp7, myActivity.rp16, myActivity.rp3,
                    myActivity.rp10, myActivity.rp12, myActivity.rp1)
                val allText = arrayOf<TextView>(myActivity.rs17, myActivity.rs11, myActivity.rs9, myActivity.rs15, myActivity.rs14, myActivity.rs6, myActivity.rs5,
                    myActivity.rs4, myActivity.rs2, myActivity.rs8, myActivity.rs13, myActivity.rs7, myActivity.rs16, myActivity.rs3,
                    myActivity.rs10, myActivity.rs12, myActivity.rs1)

                myActivity.p1.text = "+" + sums[0] + " "
                myActivity.c1.text = sums[1]
                myActivity.c2.text = sums[2]
                myActivity.c3.text = sums[3]
                myActivity.c4.text = sums[4]

                for (i in newText.indices)
                    newText[i].text = "+" + newSumsR[i] + " "
                for (i in allText.indices)
                    allText[i].text = allSumsR[i]
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 4, 5번째 탭으로 넘어갔다 현재 탭으로 다시 돌아오면 웹파싱 초기화되는 버그 해결
        if(activity != null) {
            val myActivity = activity as MainActivity
            if (myActivity.task.isFinished) {
                val sums = myActivity.task.sums
                val newSumsR = myActivity.task.newSumsR
                val allSumsR = myActivity.task.allSumsR
                val newText = arrayOf<TextView>(myActivity.rp17, myActivity.rp11, myActivity.rp9, myActivity.rp15, myActivity.rp14, myActivity.rp6, myActivity.rp5,
                    myActivity.rp4, myActivity.rp2, myActivity.rp8, myActivity.rp13, myActivity.rp7, myActivity.rp16, myActivity.rp3,
                    myActivity.rp10, myActivity.rp12, myActivity.rp1)
                val allText = arrayOf<TextView>(myActivity.rs17, myActivity.rs11, myActivity.rs9, myActivity.rs15, myActivity.rs14, myActivity.rs6, myActivity.rs5,
                    myActivity.rs4, myActivity.rs2, myActivity.rs8, myActivity.rs13, myActivity.rs7, myActivity.rs16, myActivity.rs3,
                    myActivity.rs10, myActivity.rs12, myActivity.rs1)

                myActivity.p1.text = "+" + sums[0] + " "
                myActivity.c1.text = sums[1]
                myActivity.c2.text = sums[2]
                myActivity.c3.text = sums[3]
                myActivity.c4.text = sums[4]

                for (i in newText.indices)
                    newText[i].text = "+" + newSumsR[i] + " "
                for (i in allText.indices)
                    allText[i].text = allSumsR[i]
            }
        }
    }
}
