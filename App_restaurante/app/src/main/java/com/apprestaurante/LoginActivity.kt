package com.apprestaurante

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apprestaurante.databinding.ActivityLoginBinding
import com.apprestaurante.SignActivity

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnlogin.setOnClickListener{
            val intent= Intent(this, SignActivity::class.java)
            startActivity(intent)
        }
        binding.btnNoCuenta.setOnClickListener{
            val intent= Intent(this,SignActivity::class.java)
            startActivity(intent)
        }
    }
}
