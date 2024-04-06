package com.apprestaurante.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apprestaurante.databinding.ItemOrdenesPendientesBinding
import com.bumptech.glide.Glide

class PendingOrderAdapter(
    private val context: Context,
    private val customerNames: MutableList<String>,
    private val quantity: MutableList<String>,
    private val foodImage: MutableList<String>,
    private val itemClicked: OnItemClicked,
) : RecyclerView.Adapter<PendingOrderAdapter.OrdenesPendientesViewHolder>() {

    interface OnItemClicked {
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenesPendientesViewHolder {
        val binding =
            ItemOrdenesPendientesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdenesPendientesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdenesPendientesViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size
    inner class OrdenesPendientesViewHolder(private val binding: ItemOrdenesPendientesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position: Int) {
            binding.apply {
                customerName.text = customerNames[position]
                ordenPendientecantidad.text = quantity[position]

                val uriString = foodImage[position]
                var uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(orderedFoodImage)


                ordenAceptadabutton.apply {
                    if (!isAccepted) {
                        text = "Aceptar"
                    } else {
                        text = "Despachar"
                    }
                    setOnClickListener {
                        if (!isAccepted) {
                            text = "Despachado"
                            isAccepted = true
                            showToast("La Orden fue Aceptada")
                            itemClicked.onItemAcceptClickListener(position)
                        } else {
                            customerNames.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showToast("La Orden fue Despachada")
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