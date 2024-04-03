package com.apprestaurante.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apprestaurante.databinding.ItemOrdenesPendientesBinding

class OrdenesPendientesAdapter(private val clientesNombres:ArrayList<String>,
                               private val cantidad:ArrayList<String>,
                               private val comidaImagen:ArrayList<Int>,
                               private val context: Context
) :RecyclerView.Adapter<OrdenesPendientesAdapter.OrdenesPendientesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenesPendientesViewHolder {
        val binding =
            ItemOrdenesPendientesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdenesPendientesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdenesPendientesViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = clientesNombres.size
    inner class OrdenesPendientesViewHolder(private val binding: ItemOrdenesPendientesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position: Int) {
            binding.apply {
                clienteNombre.text = clientesNombres[position]
                ordenPendientecantidad.text = cantidad[position]
                ordenComidaImagen.setImageResource(comidaImagen[position])

                ordenAceptadabutton.apply {
                    if (!isAccepted) {
                        text = "Aceptado"
                    } else {
                        text = "Despachado"
                    }
                    setOnClickListener {
                        if (!isAccepted) {
                            text = "Despachado"
                            isAccepted = true
                            showToast("La Orden fue Aceptada")
                        } else {
                            clientesNombres.removeAt(adapterPosition)
                             notifyItemRemoved(adapterPosition)
                             showToast("La Orden fue Despachada")
                        }
                    }
                }
            }
        }
       private fun showToast(message: String){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}