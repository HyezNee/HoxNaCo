package com.example.hoxnaco

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    val myActivity = fragmentActivity as MainActivity

        override fun getItemCount(): Int {
        return 5
    }

    /*interface taskListener {
        fun onTask()
    }

    var myTaskListener :taskListener ?= null*/


    override fun createFragment(position: Int): Fragment {
        //TODO("Not yet implemented")
        /*if(position == 0) {
            myTaskListener?.onTask()
        }*/

        Log.i("탭", "create")
        return when(position){
            0 -> myActivity.mainFragment
            1 -> myActivity.checkFragment
            2 -> myActivity.findFragment
            3 -> myActivity.recordFragment
            4 -> myActivity.informFragment
            else -> myActivity.mainFragment
        }
    }

    /*interface createFragmentListener {
        fun createFragment(position :Int) :Fragment
    }*/

}