package com.app_repartidor.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app_repartidor.databinding.ItemFragmentOrdersBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OrdersAdapter(
    private val context: Context,
    private val customerNames: MutableList<String>,
    private val quantity: MutableList<String>,
    private val foodImage: MutableList<String>,
    private val foodRestaurant: MutableList<String>,
    private val itemClicked: OnItemClicked,
) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    interface OnItemClicked {
        fun onItemClickListener(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersAdapter.OrdersViewHolder {
        val binding =
            ItemFragmentOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //inicializando del database
        database = FirebaseDatabase.getInstance()
        //inicializacion de la referencia de la base de datos
        databaseOrderDetails = database.reference.child("CompleteOrder")
        return OrdersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdersAdapter.OrdersViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int =customerNames.size

    inner class OrdersViewHolder(private val binding: ItemFragmentOrdersBinding) : RecyclerView.ViewHolder(binding.root) {
        //private var paid = false
        fun bind(position: Int) {
            binding.apply {
                customerName.text =customerNames[position]
                ordenPendientecantidad.text = quantity[position]
                restaurantText.text = foodRestaurant[position]

                val uriString = foodImage[position]
                var uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(orderedFoodImage)

                //verifica si la orden ya ha sido pagada
//                paid = isPaid[position]

//                if(paid == true){
//
//                }
            }

            itemView.setOnClickListener {
                itemClicked.onItemClickListener(position)
            }

        }

    }


}