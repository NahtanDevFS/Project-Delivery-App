package com.apprestaurante

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.apprestaurante.LoginActivity
import com.apprestaurante.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {
    private val binding : ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnRegis.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnTengoCuenta.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val locationList = arrayOf("Zacapa","Rio Hondo", "Estanzuela", "Gualán", "Teculután","Usumatlán","Cabañas","San Diego", "La Unión", "Huité")
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView=binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
    }
}
