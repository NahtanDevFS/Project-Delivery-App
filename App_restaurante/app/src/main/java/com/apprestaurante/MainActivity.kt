package com.apprestaurante

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var valueEventListener: ValueEventListener

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var adminReference: DatabaseReference
    private lateinit var completedOrderReference: DatabaseReference
    private lateinit var newOrderReference: DatabaseReference
    private var restaurant = ""
    val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //inicializando del database
        database = FirebaseDatabase.getInstance()

        auth = FirebaseAuth.getInstance()

        adminReference = database.reference.child("user")

        createChannel()

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
            updateDatabaseReference()

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
        newOrders()

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

    override fun onDestroy() {
        super.onDestroy()
        // Remove ValueEventListener when activity stops to prevent memory leaks
        newOrderReference.removeEventListener(valueEventListener)
    }

    private fun updateDatabaseReference(){
        newOrderReference.removeEventListener(valueEventListener)
        newOrderReference = FirebaseDatabase.getInstance().reference.child("dasdasdas")
        var numOrders = 0
        valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children.count()
                if (numOrders == 0 && children == 0){
                    numOrders = children
                } else if (children > numOrders){
                    numOrders = children
                    createSimpleNotification()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        newOrderReference.addListenerForSingleValueEvent(valueEventListener)
        newOrderReference.removeEventListener(valueEventListener)
    }

    private fun newOrders() {
        newOrderReference = FirebaseDatabase.getInstance().reference.child("OrderDetails")
        valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numOrders = 0
                for (completedSnapshot in snapshot.children) {
                    val orderUid = completedSnapshot.key.toString()
                    var orderRestaurant =
                        snapshot.child(orderUid).child("foodRestaurant").child("0").getValue()
                    if(orderRestaurant == restaurant){
                        numOrders += 1
                    }
                }
                if (numOrders != 0){
                    createSimpleNotification()
                }
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
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ordenes"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }


    fun createSimpleNotification() {

        val intent = Intent(this, PendingOrderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            updateDatabaseReference()
        }

        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent:PendingIntent = PendingIntent.getActivity(this, 0, intent, flag)

        var builder = NotificationCompat.Builder(this, MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Nueva orden")
            .setContentText("Hay una nueva orden")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("¡Hay órdenes pendientes!")
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(1, builder.build())
            } else{
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(settingsIntent)
                Toast.makeText(this@MainActivity, "Por favor, habilita las notificaciones", Toast.LENGTH_SHORT).show()
            }
        }
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
