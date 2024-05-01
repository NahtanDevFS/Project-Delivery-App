package com.apprestaurante.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apprestaurante.databinding.ItemItemBinding
import com.apprestaurante.model.AllMenu
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener: (position :Int) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>(){

    private  val itemQuantities = IntArray(menuList.size){1}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddItemViewHolder(private val binding: ItemItemBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                val menuItem = menuList[position]
                val uriString = menuItem.foodImage
                val uri = Uri.parse(uriString)

                nombreComidaTextView.text = menuItem.foodName
                priceTextView.text = menuItem.foodPrice

                //Librería glide para cargar imágenes
                Glide.with(context).load(uri).into(imagenComidaView)
//                quantityTextView.text = quantity.toString()

//                minusButton.setOnClickListener{
//                    decreaseQuantity(position)
//                }
                deleteButton.setOnClickListener{
                    onDeleteClickListener(position)
                    //deleteQuantity(position)
                }
//                pluseButton.setOnClickListener{
//                    increaseQuantity(position)
//                }

            }
        }

//        private fun increaseQuantity(position: Int) {
//           if(itemQuantities[position]<10){
//               itemQuantities[position]++
//               binding.quantityTextView.text=itemQuantities[position].toString()
//           }
//        }
//        private fun decreaseQuantity(position: Int) {
//            if(itemQuantities[position]>1){
//                itemQuantities[position]--
//                binding.quantityTextView.text=itemQuantities[position].toString()
//            }
//        }
        private fun deleteQuantity(position: Int) {
            menuList.removeAt(position)
            menuList.removeAt(position)
            menuList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,menuList.size)
        }
    }
}