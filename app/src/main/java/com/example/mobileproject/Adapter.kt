package com.example.mobileproject
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


class Adapterr (private val buttons: ArrayList<Filters>) : RecyclerView.Adapter<Adapterr.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView=itemView.findViewById(R.id.imageView)as ImageView
        val textView = itemView.findViewById(R.id.textView) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.buttonfilter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val but=buttons[position]
        holder.imageView.setImageResource(but.image)
        holder.textView.text=but.name
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

}
