package com.apprestaurante

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprestaurante.databinding.ActivityAdminProfileBinding

class AdminProfileActivity : AppCompatActivity() {
    private val binding : ActivityAdminProfileBinding by lazy{
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }
        binding.nombre.isEnabled = false
        binding.direccion.isEnabled = false
        binding.email.isEnabled = false
        binding.telefono.isEnabled = false
        binding.contrasena.isEnabled = false

        var isEnable = false
        binding.editButton.setOnClickListener {
            isEnable =! isEnable
            binding.nombre.isEnabled = isEnable
            binding.direccion.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.telefono.isEnabled = isEnable
            binding.contrasena.isEnabled = isEnable
            if(isEnable){
                binding.nombre.requestFocus()
            }
        }
    }
}