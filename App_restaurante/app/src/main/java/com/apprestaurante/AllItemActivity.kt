package com.apprestaurante

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.apprestaurante.adapter.AddItemAdapter
import com.apprestaurante.databinding.ActivityAllItemBinding

class AllItemActivity : AppCompatActivity() {
    private val binding : ActivityAllItemBinding by lazy{
        ActivityAllItemBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    val menuFoodName = listOf("Hamburguesa","Sandwich","Empanada","item","Hamburguesa","Sandwich","Empanada")
       val menuItemPrice = listOf("Q5", "Q6", "Q8", "Q10", "Q10")
        val menuImage = listOf(
            R.drawable.menu1,
            R.drawable.menu2,
            R.drawable.menu3,
            R.drawable.menu4,
            R.drawable.menu5,
            R.drawable.menu6
        )
        binding.backButton.setOnClickListener{
            finish()
        }
        val adapter = AddItemAdapter(ArrayList(menuFoodName),
            ArrayList(menuItemPrice),ArrayList
        (menuImage)
        )
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecyclerView.adapter=adapter
    }

}