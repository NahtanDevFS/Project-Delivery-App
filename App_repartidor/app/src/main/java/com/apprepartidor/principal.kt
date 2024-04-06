package com.apprepartidor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apprepartidor.databinding.ActivityMainBinding
import com.apprepartidor.databinding.ActivityPrincipalBinding
import com.facebook.login.LoginManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth


@Suppress("DEPRECATION")
class principal : AppCompatActivity() {
    private lateinit var binding: ActivityPrincipalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val recyclerView = findViewById<RecyclerView>(R.id.recy)
        val adapter = CustomAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navegacion.setOnItemSelectedListener {menuItem ->

            when(menuItem.itemId){

                R.id.pedido -> {
                    startActivity(Intent(this, principal::class.java))
                }
                R.id.histori -> {
                    startActivity(Intent(this, historial_entregas::class.java))
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