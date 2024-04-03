package com.apprestaurante

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.apprestaurante.adapter.DeliveryAdapter
import com.apprestaurante.adapter.OrdenesPendientesAdapter
import com.apprestaurante.databinding.ActivityOrdenesPendientesBinding
import com.apprestaurante.databinding.ItemOrdenesPendientesBinding

class OrdenesPendientesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrdenesPendientesBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityOrdenesPendientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }
        val ordenNombreCliente = arrayListOf(
            "Juan González",
            "Brenner Granados",
            "Dennys Herrera",
        )
        val cantidadOrdenada = arrayListOf(
            "8",
            "6",
            "5",
        )
        val imagenComidaOrdenada = arrayListOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3)
        val adapter= OrdenesPendientesAdapter(ordenNombreCliente,cantidadOrdenada,imagenComidaOrdenada, this)
        binding.ordenesPendientesRecyclerView.adapter = adapter
        binding.ordenesPendientesRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}