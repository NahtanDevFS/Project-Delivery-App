package com.app_repartidor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_repartidor.OrderDetailsActivity
import com.app_repartidor.R
import com.app_repartidor.adapter.OrdersAdapter
import com.app_repartidor.databinding.FragmentOrdersBinding
import com.app_repartidor.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Orders : Fragment(), OrdersAdapter.OnItemClicked {

    private lateinit var binding: FragmentOrdersBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfIsAccepted: MutableList<Boolean> = mutableListOf()
    private var listOfRestaurantItem: MutableList<String> = mutableListOf()
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adminReference: DatabaseReference
    public var isOrderAccepted = false
    private lateinit var databaseOrderRestaurant: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        //inicializando del database
        database = FirebaseDatabase.getInstance()
        //inicializacion de la referencia de la base de datos
        databaseOrderDetails = database.reference.child("CompleteOrder")

        auth = FirebaseAuth.getInstance()

        adminReference = database.reference.child("user")

        getOrdersDetails()

        return binding.root
    }

    private fun getOrdersDetails() {
        //recuperar los detalles de las órdenes de la base de datos de firebase
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnapshot in snapshot.children){

                    val orderUid = orderSnapshot.key.toString()
                    //var orderRestaurant = snapshot.child(orderUid).child("foodRestaurant").child("0").getValue()
                    var paidReceived = snapshot.child(orderUid).child("paymentReceived").getValue()

                    if (paidReceived == false){
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
            orderItem.foodRestaurant?.filterNot { it.isEmpty() }?.forEach {
                listOfRestaurantItem.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OrdersAdapter(requireContext(), listOfName, listOfTotalPrice, listOfImageFirstFoodOrder, listOfRestaurantItem, this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    companion object {

    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails)
        startActivity(intent)
    }
}