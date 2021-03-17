package com.example.hoxnaco

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_check.view.*

/**
 * A simple [Fragment] subclass.
 */
class CheckFragment : Fragment() {

    var commonNum = 0
    var rareNum = 0
    var seriousNum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_check, container, false)
        init(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun init(view :View) {
        val common = arrayOf<CheckBox>(view.q1, view.q2, view.q3)
        val rare = arrayOf<CheckBox>(view.q4, view.q5, view.q6, view.q7, view.q8, view.q9, view.q10)
        val serious = arrayOf<CheckBox>(view.q11, view.q12, view.q13)

        for(btn in common) {
            with(btn) {
                setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked)
                        commonNum++
                    else
                        commonNum--
                }
            }
        }
        for(btn in rare) {
            with(btn) {
                setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked)
                        rareNum++
                    else
                        rareNum--
                }
            }
        }
        for(btn in serious) {
            with(btn) {
                setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked)
                        seriousNum++
                    else
                        seriousNum--
                }
            }
        }

        view.resultBtn.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
            var result1 = "$commonNum 건"
            var result2 = "$rareNum 건"
            var result3 = "$seriousNum 건"
            val fragment = CheckResultFragment.makeCheckResultFragment(result1, result2, result3)
            fragmentTransaction?.replace(R.id.fragment_check, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit();
        }
    }

    override fun onResume() {
        super.onResume()
        if(activity != null) {
            val myActivity = activity as MainActivity
            activity?.toolbarText?.text = "간이 자가 진단"
        }
    }

}
