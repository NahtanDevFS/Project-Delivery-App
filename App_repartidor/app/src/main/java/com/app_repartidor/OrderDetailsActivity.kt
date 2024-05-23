package com.app_repartidor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_repartidor.adapter.OrderDetailsAdapter
import com.app_repartidor.databinding.ActivityOrderDetailsBinding
import com.app_repartidor.model.OrderDetails
import com.app_repartidor.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailsActivity : AppCompatActivity() {

    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    private var userName: String? = null
    private var address: String? = null
    private var phoneNumber: String? = null
    private var totalPrice: String? = null
    private var itemPushKey: String? = null
    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodQuantity: ArrayList<Int> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent
            startActivity(intent)
            finish()
        }

        binding.copyButton.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", binding.address.text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Dirección copiada en el portapapeles", Toast.LENGTH_SHORT).show()
        }

        binding.paidReceivedButton.setOnClickListener {
            updateOrderStatus()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        getDataFromIntent()

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addPayment() {
        val userId = auth.currentUser?.uid
        var totalPayment = 0

        if(userId != null) {
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val pay = snapshot.child("payment").getValue()
                        if(pay != null){
                            val strPay = pay.toString()
                            totalPayment = strPay.toInt() + 8
                        } else{
                            totalPayment = 8
                        }
                        database.reference.child("user").child(userId).child("payment").setValue(totalPayment.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    private fun updateOrderStatus() {
        val completeOrderReference = database.reference.child("CompleteOrder").child(itemPushKey!!)
        completeOrderReference.child("paymentReceived").setValue(true)
        addPayment()
        Toast.makeText(this, "Pago exitoso", Toast.LENGTH_SHORT).show()
    }

    private fun getDataFromIntent() {
        val recievedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as OrderDetails
        recievedOrderDetails?.let { orderDetails ->
            userName = recievedOrderDetails.userName
            foodNames = recievedOrderDetails.foodNames as ArrayList<String>
            foodImages = recievedOrderDetails.foodImages as ArrayList<String>
            foodQuantity = recievedOrderDetails.foodQuantities as ArrayList<Int>
            address = recievedOrderDetails.address
            phoneNumber = recievedOrderDetails.phoneNumber
            foodPrices = recievedOrderDetails.foodPrices  as ArrayList<String>
            totalPrice = recievedOrderDetails.totalPrice
            itemPushKey = recievedOrderDetails.itemPushKey

            setUserDetail()
            setAdapter()
        }
    }

    private fun setAdapter() {
        binding.orderDetailRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(this, foodNames, foodImages, foodQuantity, foodPrices)
        binding.orderDetailRecyclerView.adapter = adapter
    }

    private fun setUserDetail() {
        binding.name.text = userName
        binding.address.text = address
        binding.phone.text = phoneNumber
        binding.totalPay.text = totalPrice
    }
}