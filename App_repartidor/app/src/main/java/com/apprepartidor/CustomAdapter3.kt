package com.apprepartidor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.apprepartidor.CustomAdapter.ViewHolder

class CustomAdapter3 {

    val images = intArrayOf(R.drawable.user32x32)


    fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false)
        return ViewHolder(v)
    }
    fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.itemImagen.setImageResource(images[i])
    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var itemImagen : ImageView


        init {
            itemImagen = itemView.findViewById(R.id.item_imagen)

        }
    }
}
