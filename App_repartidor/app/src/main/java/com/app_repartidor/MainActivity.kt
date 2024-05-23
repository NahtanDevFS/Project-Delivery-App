package com.app_repartidor

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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.app_repartidor.databinding.ActivityMainBinding
import com.app_repartidor.fragment.Navigation
import com.app_repartidor.fragment.Orders
import com.app_repartidor.fragment.Profile
import com.app_repartidor.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var newOrderReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        replaceFragment(Orders())

        //createChannel()

        //newOrders()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.orders -> replaceFragment(Orders())
                R.id.navigation -> replaceFragment(Navigation())
                R.id.profile -> replaceFragment(Profile())

                else->{

                }
        }
            true
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        // Remove ValueEventListener when activity stops to prevent memory leaks
//        newOrderReference.removeEventListener(valueEventListener)
//    }

//    public fun newOrders() {
//        newOrderReference = FirebaseDatabase.getInstance().reference.child("CompleteOrder")
//        valueEventListener = object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("TAG", "COMPLETE ORDER CHANGED")
//                var numOrders = 0
//                for(orderSnapshot in snapshot.children){
//
//                    val orderUid = orderSnapshot.key.toString()
//                    var paidReceived = snapshot.child(orderUid).child("paymentReceived").getValue()
//
//                    if (paidReceived == false){
//                        numOrders +=1
//                    }
//                }
//                if (numOrders != 0){
//                    createSimpleNotification()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        }
//
//        newOrderReference.addValueEventListener(valueEventListener)
//    }

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


//    fun createSimpleNotification() {
//
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            updateDatabaseReference()
//        }
//
//        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, flag)
//
//        var builder = NotificationCompat.Builder(this, MY_CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_stat_name)
//            .setContentTitle("Nuevos pedidos")
//            .setContentText("Hay nuevos pedidos")
//            .setStyle(
//                NotificationCompat.BigTextStyle()
//                    .bigText("¡Hay pedidos por entregar!")
//            )
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        with(NotificationManagerCompat.from(this)) {
//            if (ActivityCompat.checkSelfPermission(
//                    this@MainActivity,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                notify(1, builder.build())
//            } else{
//                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//                startActivity(settingsIntent)
//                Toast.makeText(this@MainActivity, "Por favor, habilita las notificaciones", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
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
//        newOrderReference.addListenerForSingleValueEvent(valueEventListener)
//        newOrderReference.removeEventListener(valueEventListener)
//    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

//    private fun isLocationPermissionGranted(){
//        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestLocationPermission(){
//        if()
//    }

}