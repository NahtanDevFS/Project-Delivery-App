package com.apprestaurante

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprestaurante.databinding.ActivityMainBinding
import com.apprestaurante.model.OrderDetails
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var adminReference: DatabaseReference
    private lateinit var completedOrderReference: DatabaseReference
    private var restaurant = ""
    val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //inicializando del database
        database = FirebaseDatabase.getInstance()

        auth = FirebaseAuth.getInstance()

        adminReference = database.reference.child("user")

        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.allItemMenu.setOnClickListener {
            val intent = Intent(this, AllItemActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.outForDeliveryButton.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.perfil.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ordenPendienteTextView.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
            finish()
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val currentUserUid = auth.currentUser?.uid
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

        timer.schedule(object : TimerTask() {
            override fun run() {
                // Your function to execute after the timer ends
                completedOrders()
                pendingOrders()
                wholeTimeEarning()
            }
        }, 1600)

        //completedOrders()


    }

    private fun wholeTimeEarning() {

        var listOfTotalPay = mutableListOf<Int>()

        completedOrderReference = FirebaseDatabase.getInstance().reference.child("CompleteOrder")

        //completedOrderReference definida a nivel de clase
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderUid = orderSnapshot.key.toString()
                    var orderRestaurant =
                        snapshot.child(orderUid).child("foodRestaurant").child("0").getValue()

                    var payment = snapshot.child(orderUid).child("paymentReceived").getValue()

                    if (orderRestaurant == restaurant) {
                        if(payment == true){
                            var completedOrder = orderSnapshot.getValue(OrderDetails::class.java)
                            completedOrder?.totalPrice?.replace("Q", "")?.toIntOrNull()
                                ?.let { i ->
                                    listOfTotalPay.add(i)
                                }
                        }

                    }
                }
                binding.wholeTimeEarning.text = "Q" + listOfTotalPay.sum().toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun completedOrders() {

        //completedOrderReference definida a nivel de funcion
        completedOrderReference = FirebaseDatabase.getInstance().reference.child("CompleteOrder")
        var completedOrderItemCount = 0

        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (completedSnapshot in snapshot.children) {
                    val orderUid = completedSnapshot.key.toString()
                    var orderRestaurant =
                        snapshot.child(orderUid).child("foodRestaurant").child("0").getValue()

                    if (orderRestaurant == restaurant) {
                        completedOrderItemCount += 1
                    }
                }
                binding.completedOrders.text = completedOrderItemCount.toString()
                //completedOrderItemCount = snapshot.childrenCount.toInt()
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun pendingOrders() {
        database = FirebaseDatabase.getInstance()
        val pendingOrderReference = database.reference.child("OrderDetails")
        var pendingOrderItemCount = 0
        pendingOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (completedSnapshot in snapshot.children) {
                    val orderUid = completedSnapshot.key.toString()
                    var orderRestaurant =
                        snapshot.child(orderUid).child("foodRestaurant").child("0").getValue()

                    if (orderRestaurant == restaurant) {
                        pendingOrderItemCount += 1
                    }
                }

                binding.pendingOrders.text = pendingOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
