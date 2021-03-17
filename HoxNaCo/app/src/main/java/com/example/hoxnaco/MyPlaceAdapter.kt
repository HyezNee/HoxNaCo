package com.example.hoxnaco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyPlaceAdapter(var items: ArrayList<MyPlaces>)
    : RecyclerView.Adapter<MyPlaceAdapter.MyViewHolder>() {

    interface OnItemClickListener{  // main에서 구현하기 위한 클릭이벤트
        fun OnItemClick(holder: MyViewHolder, view: View, data: MyPlaces, position: Int)
    }

    var itemClickListener: OnItemClickListener ?= null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var address: TextView = itemView.findViewById(R.id.address)
        var memo: TextView = itemView.findViewById(R.id.memo)
        var date: TextView = itemView.findViewById(R.id.dateTime)
        init{
            itemView.setOnLongClickListener{
                val size = items.size - 1
                itemClickListener?.OnItemClick(this, it, items[size - adapterPosition], size - adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlaceAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row3, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val size = items.size - 1
        holder.address.text = items[size - position].address
        holder.memo.text = items[size - position].memo
        holder.date.text = items[size - position].date
    }
}