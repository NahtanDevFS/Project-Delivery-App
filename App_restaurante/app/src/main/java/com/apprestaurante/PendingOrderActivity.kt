package com.apprestaurante

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.apprestaurante.adapter.PendingOrderAdapter
import com.apprestaurante.databinding.ActivityOrdenesPendientesBinding
import com.apprestaurante.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {
    private lateinit var binding: ActivityOrdenesPendientesBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfIsAccepted: MutableList<Boolean> = mutableListOf()
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adminReference: DatabaseReference
    public var isOrderAccepted = false
    private lateinit var databaseOrderRestaurant: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityOrdenesPendientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inicializando del database
        database = FirebaseDatabase.getInstance()
        //inicializacion de la referencia de la base de datos
        databaseOrderDetails = database.reference.child("OrderDetails")

        auth = FirebaseAuth.getInstance()

        adminReference = database.reference.child("user")

        getOrdersDetails()

        binding.backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getOrdersDetails() {

        val currentUserUid = auth.currentUser?.uid
        var restaurant = ""
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        restaurant = snapshot.child("nameOfRestaurant").getValue().toString()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        //recuperar los detalles de las órdenes de la base de datos de firebase
        databaseOrderDetails.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnapshot in snapshot.children){

                    val orderUid = orderSnapshot.key.toString()
                    var orderRestaurant = snapshot.child(orderUid).child("foodRestaurant").child("0").getValue()

                    if (orderRestaurant == restaurant){
                        val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                        orderDetails?.let { listOfOrderItem.add(it) }
                    }

                    //databaseOrderRestaurant = database.reference.child("OrderDetails").child(orderUid).child("foodRestaurant").child("0")

                }
                addDataToListRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addDataToListRecyclerView() {
        for (orderItem in listOfOrderItem){
            //agregar los datos a sus respectivas listas para poblar el recyclerView
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach {
                listOfImageFirstFoodOrder.add(it)
            }

            orderItem.orderAccepted?.let { listOfIsAccepted.add(it) }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PendingOrderAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstFoodOrder, listOfIsAccepted, this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        // manejar la aceptacón del item y actualizar el database
        val childItemPushKey = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)

        updateOrderAcceptStatus(position)
    }

    override fun onItemDispatchClickListener(position: Int) {
        // manejar el despacho del item y actualizar el database
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
        val dispatchItemOrderReference = database.reference.child("CompleteOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderReference.setValue(listOfOrderItem[position])
            .addOnSuccessListener {
                deleteThisItemFromOrderDetails(dispatchItemPushKey)
            }
    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemsReference = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemsReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "La Orden fue Despachada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al despachar la orden", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        //actualizar la aceptación de la orden en el historial de compras del usuario y en el OrderDetails
        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
        val buyHistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory").child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }

}