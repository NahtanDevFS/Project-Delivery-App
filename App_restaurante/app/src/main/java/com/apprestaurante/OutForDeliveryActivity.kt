package com.apprestaurante

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.apprestaurante.adapter.DeliveryAdapter
import com.apprestaurante.databinding.ActivityOutForDeliveryBinding

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding:ActivityOutForDeliveryBinding by lazy{
     ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
binding.backButton.setOnClickListener{
    finish()
}
        val customerName = arrayListOf(
            "Juan González",
            "Brenner Granados",
            "Dennys Herrera",
        )
        val moneyStatus = arrayListOf(
            "recibido",
            "noRecibido",
            "Pendiente",
        )
        val adapter= DeliveryAdapter(customerName,moneyStatus)
        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}