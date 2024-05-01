package com.appcliente.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcliente.CongratsBottomSheet
import com.appcliente.PayOutActivity
import com.appcliente.R
import com.appcliente.adapter.CartAdapter
import com.appcliente.databinding.FragmentCartBinding
import com.appcliente.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {
    private lateinit var binding:FragmentCartBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodRestaurant: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        //Inicializar la autenticación de firebase
        auth = FirebaseAuth.getInstance()
        retrieveCartItems()

        binding.proceedButton.setOnClickListener {

            //Obtener datos de los platillos de la orden antes de hacer el pedido
            getOrderItemsDetails()

        }

        return binding.root
    }

    private fun getOrderItemsDetails() {

        val orderIdReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")


        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodRestaurant = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()

        //obtener la cantidad de elementos
        val foodQuantities = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    // obtener los elementos del carrito a su respectiva lista
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)

                    //agregar información de los elementos a la lista
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }
                    orderItems?.foodRestaurant?.let { foodRestaurant.add(it) }
                }
                if(foodName.size != 0){
                    val itemsSize = foodRestaurant.size
                    var different = 0
                    //verifica que los platillos que el cliente pidan vengan sean del mismo restaurante
                    if(itemsSize > 1){
                        for (i in 0 until itemsSize){
                            if(foodRestaurant[0] != foodRestaurant[i]) {
                                different += 1
                            }
                        }
                        if (different == 0){
                            orderNow(foodName, foodPrice, foodDescription, foodImage, foodIngredient, foodQuantities, foodRestaurant)
                        } else {
                            Toast.makeText(requireContext(), "Los platillos deben ser del mismo restaurante", Toast.LENGTH_LONG).show()
                        }
                    } else{
                        orderNow(foodName, foodPrice, foodDescription, foodImage, foodIngredient, foodQuantities, foodRestaurant)
                    }

                } else{
                    Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al realizar el pedido", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int>,
        foodRestaurant: MutableList<String>
    ) {

        if(isAdded && context != null){
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("foodItemName", foodName as ArrayList<String>)
            intent.putExtra("foodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("foodItemImage", foodImage as ArrayList<String>)
            intent.putExtra("foodItemDescription", foodDescription as ArrayList<String>)
            intent.putExtra("foodItemIngredient", foodIngredient as ArrayList<String>)
            intent.putExtra("foodItemQuantities", foodQuantities as ArrayList<Int>)
            intent.putExtra("foodItemRestaurant", foodRestaurant as ArrayList<String>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {

        //referencia del database a Firebase
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid?:""
        val foodReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")

        //listar para almacenar los items del carrito
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodImagesUri = mutableListOf()
        foodIngredients = mutableListOf()
        foodRestaurant = mutableListOf()
        quantity = mutableListOf()

        //buscar datos del database
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){

                    //obtener los items del carrito del nodo hijo
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)

                    //agregar información de los platillos a la lista
                    cartItems?.foodName?.let { foodNames.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItems?.foodImage?.let { foodImagesUri.add(it) }
                    cartItems?.foodQuantity?.let { quantity.add(it) }
                    cartItems?.foodIngredient?.let { foodIngredients.add(it) }
                    cartItems?.foodRestaurant?.let { foodRestaurant.add(it) }
                }

                setAdapter()
            }

            private fun setAdapter() {
                cartAdapter = CartAdapter(requireContext(), foodNames, foodPrices, foodDescriptions, foodImagesUri, quantity, foodIngredients, foodRestaurant)
                binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Datos no encontrados", Toast.LENGTH_SHORT).show()
            }

        })
    }

    companion object {

    }
}