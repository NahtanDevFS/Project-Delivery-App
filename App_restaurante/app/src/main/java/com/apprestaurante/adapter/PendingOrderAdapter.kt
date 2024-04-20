package com.apprestaurante.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apprestaurante.PendingOrderActivity
import com.apprestaurante.databinding.ItemOrdenesPendientesBinding
import com.apprestaurante.model.OrderDetails
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PendingOrderAdapter(
    private val context: Context,
    private val customerNames: MutableList<String>,
    private val quantity: MutableList<String>,
    private val foodImage: MutableList<String>,
    private val isAccepted: MutableList<Boolean>,
    private val itemClicked: OnItemClicked,
) : RecyclerView.Adapter<PendingOrderAdapter.OrdenesPendientesViewHolder>() {

    private var isAcceptedOrder = PendingOrderActivity()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()

    interface OnItemClicked {
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenesPendientesViewHolder {
        val binding =
            ItemOrdenesPendientesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //inicializando del database
        database = FirebaseDatabase.getInstance()
        //inicializacion de la referencia de la base de datos
        databaseOrderDetails = database.reference.child("OrderDetails")
        return OrdenesPendientesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdenesPendientesViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size
    inner class OrdenesPendientesViewHolder(private val binding: ItemOrdenesPendientesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var accepted = false
        private var flag = ""
        fun bind(position: Int) {
            binding.apply {
                customerName.text = customerNames[position]
                ordenPendientecantidad.text = quantity[position]

                val uriString = foodImage[position]
                var uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(orderedFoodImage)

                //verifica si la orden ya ha sido aceptada
                accepted = isAccepted[position]

                ordenAceptadabutton.apply {
                    if (!accepted) {
                        text = "Aceptar"
                    } else {
                        text = "Despachar"
                    }
                    setOnClickListener {
                        if (!accepted) {
                            text = "Despachar"
                            accepted = true
                            showToast("La Orden fue Aceptada")
                            itemClicked.onItemAcceptClickListener(position)
                        } else {
                            customerNames.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            //showToast("La Orden fue Despachada")
                            itemClicked.onItemDispatchClickListener(position)
                        }
                    }
                }
                itemView.setOnClickListener {
                    itemClicked.onItemClickListener(position)
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}