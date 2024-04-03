package com.apprestaurante

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprestaurante.databinding.ActivityMainBinding

enum class ProviderType{
    BASIC,
    GOOGLE
}
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.addMenu.setOnClickListener{
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }
        binding.allItemMenu.setOnClickListener{
            val intent = Intent(this, AllItemActivity::class.java)
            startActivity(intent)
        }
        binding.outForDeliveryButton.setOnClickListener{
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }
        binding.perfil.setOnClickListener{
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }
        binding.crearUser.setOnClickListener{
            val intent = Intent(this, CrearUsuarioActivity::class.java)
            startActivity(intent)
        }
        binding.ordenPendienteTextView.setOnClickListener{
            val intent = Intent(this, OrdenesPendientesActivity::class.java)
            startActivity(intent)
        }
    }
}
