package com.app_repartidor.fragment

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_repartidor.MainActivity
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
import java.util.Timer
import java.util.TimerTask


class Orders : Fragment(), OrdersAdapter.OnItemClicked {

    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

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
    val timer = Timer()
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var newOrderReference: DatabaseReference

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

        createChannel()

        newOrders()

        //getOrdersDetails()

        timer.schedule(object : TimerTask() {
            override fun run() {
                getPayment()
            }
        }, 600)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove ValueEventListener when activity stops to prevent memory leaks
        newOrderReference.removeEventListener(valueEventListener)
    }

    fun newOrders() {
        newOrderReference = FirebaseDatabase.getInstance().reference.child("CompleteOrder")
        valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numOrders = 0
                for(orderSnapshot in snapshot.children){

                    val orderUid = orderSnapshot.key.toString()
                    var paidReceived = snapshot.child(orderUid).child("paymentReceived").getValue()

                    if (paidReceived == false){
                        val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                        orderDetails?.let { listOfOrderItem.add(it) }
                        numOrders +=1
                    }
                }
                if (numOrders != 0){
                    createSimpleNotification()
                }
                addDataToListRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        newOrderReference.addValueEventListener(valueEventListener)
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MainActivity.MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ordenes"
            }

            val notificationManager: NotificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }


    fun createSimpleNotification() {

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //updateDatabaseReference()
        }

        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, flag)

        var builder = NotificationCompat.Builder(requireContext(), MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Nuevos pedidos")
            .setContentText("Hay nuevos pedidos")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("¡Hay pedidos por entregar!")
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(1, builder.build())
            } else{
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, activity?.packageName)
                startActivity(settingsIntent)
                Toast.makeText(requireContext(), "Por favor, habilita las notificaciones", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private fun updateDatabaseReference(){
//        newOrderReference.removeEventListener(valueEventListener)
//        newOrderReference = FirebaseDatabase.getInstance().reference.child("dasdasdas")
//        var numOrders = 0
//        valueEventListener = object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val children = snapshot.children.count()
//                if (numOrders == 0 && children == 0){
//                    numOrders = children
//                } else if (children > numOrders){
//                    numOrders = children
//                    createSimpleNotification()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        }
//        newOrderReference.addValueEventListener(valueEventListener)
//        newOrderReference.removeEventListener(valueEventListener)
//    }

    private fun getPayment() {
        val userId = auth.currentUser?.uid

        if(userId != null) {
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val pay = snapshot.child("payment").getValue()
                        if(pay != null){
                            val strPay = pay.toString()
                            binding.payment.setText(strPay)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

//    private fun getOrdersDetails() {
//        //recuperar los detalles de las órdenes de la base de datos de firebase
//        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("TAG", "HAY NUEVAS ORDENES")
//                //createSimpleNotification()
//                for(orderSnapshot in snapshot.children){
//
//                    val orderUid = orderSnapshot.key.toString()
//                    //var orderRestaurant = snapshot.child(orderUid).child("foodRestaurant").child("0").getValue()
//                    var paidReceived = snapshot.child(orderUid).child("paymentReceived").getValue()
//
//                    if (paidReceived == false){
//                        val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
//                        orderDetails?.let { listOfOrderItem.add(it) }
//                    }
//
//                    //databaseOrderRestaurant = database.reference.child("OrderDetails").child(orderUid).child("foodRestaurant").child("0")
//
//                }
//                addDataToListRecyclerView()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//    }

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

    override fun onItemClickListener(position: Int) {
        requireActivity().finish()
        val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails)
        startActivity(intent)
    }
}