package com.example.hoxnaco

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_check_result.view.*

/**
 * A simple [Fragment] subclass.
 */
class CheckResultFragment : Fragment() {

    lateinit var result1 :String
    lateinit var result2 :String
    lateinit var result3 :String

    companion object {   // static 함수 -> 인자를 받아 fragment 생성 위해
        fun makeCheckResultFragment(result1 :String, result2: String, result3: String) :CheckResultFragment {
            val checkResultFragment = CheckResultFragment()
            checkResultFragment.result1 = result1
            checkResultFragment.result2 = result2
            checkResultFragment.result3 = result3
            return checkResultFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_check_result, container, false)

        view.result1.text = result1
        view.result2.text = result2
        view.result3.text = result3

        view.resultBackBtn.setOnClickListener {
            activity?.onBackPressed()
        }
        return view
    }

}
