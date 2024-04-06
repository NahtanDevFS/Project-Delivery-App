package com.apprepartidor


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter2 : RecyclerView.Adapter<CustomAdapter2.ViewHolder>(){

    val titles = arrayOf("Jonathan",
        "Brenner",
        "Yefferson")

    val details = arrayOf("Pedido Entregado",
        "Pedido Entregado",
        "Pedido Entregado")

    val images = intArrayOf(R.drawable.user32x32,
        R.drawable.user32x32,
        R.drawable.user32x32)

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = titles[i]
        viewHolder.itemDetail.text = details[i]
        viewHolder.itemImagen.setImageResource(images[i])
    }

    override fun getItemCount(): Int {
        return titles.size
    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var itemImagen : ImageView
        var itemTitle : TextView
        var itemDetail : TextView

        init {
            itemImagen = itemView.findViewById(R.id.item_imagen)
            itemTitle = itemView.findViewById(R.id.title_item)
            itemDetail = itemView.findViewById(R.id.title2_item)
        }
    }
}