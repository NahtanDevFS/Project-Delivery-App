package com.apprepartidor

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apprepartidor.databinding.ActivityHistorialEntregasBinding
import com.apprepartidor.databinding.ActivityPrincipalBinding

class historial_entregas : AppCompatActivity() {
    private lateinit var binding: ActivityHistorialEntregasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        binding = ActivityHistorialEntregasBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val recyclerView = findViewById<RecyclerView>(R.id.recy)
        val adapter = CustomAdapter2()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navegacion1.setOnItemSelectedListener {menuItem ->

            when(menuItem.itemId){
                R.id.histori -> {
                    startActivity(Intent(this, historial_entregas::class.java))
                }
                R.id.pedido -> {
                    startActivity(Intent(this, principal::class.java))
                }

                R.id.ajust -> {
                    startActivity(Intent(this, menu::class.java))
                }

                else ->{

                }
            }
            true
        }

    }
}