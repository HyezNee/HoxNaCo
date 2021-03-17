package com.example.hoxnaco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyNewsAdapter(var items: ArrayList<MyNewsData>)
    : RecyclerView.Adapter<MyNewsAdapter.MyViewHolder>() {

    interface OnItemClickListener{  // main에서 구현하기 위한 클릭이벤트
        fun OnItemClick(holder: MyViewHolder, view: View, data: MyNewsData, position: Int)
    }

    var itemClickListener: OnItemClickListener ?= null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title: TextView = itemView.findViewById(R.id.newsTitle)
        var content: TextView = itemView.findViewById(R.id.newsContent)
        init{
            itemView.setOnClickListener{
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNewsAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row1, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyNewsAdapter.MyViewHolder, position: Int) {
        holder.title.text = items[position].title
        holder.content.text = items[position].content
    }

}