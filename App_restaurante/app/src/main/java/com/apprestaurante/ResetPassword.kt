package com.apprestaurante

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprestaurante.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {

    private val binding: ActivityResetPasswordBinding by lazy {
        ActivityResetPasswordBinding.inflate(layoutInflater)
    }

    private var email = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnReset.setOnClickListener {

            email = binding.editTextEmailAddress.text.toString()

            if(!email.isEmpty()){
                progress = ProgressDialog(this).apply {
                    setMessage("Cargando...")
                    setCancelable(false)
                }
                progress.show()
                resetPassword()
            } else{
                Toast.makeText(this, "Debes ingresar tu correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener{
            finish()
        }

    }

    private fun resetPassword() {
        auth.setLanguageCode("es")
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            task->
            if (task.isSuccessful){
                Toast.makeText(this, "Se ha enviado un correo para restablecer tu contraseña", Toast.LENGTH_SHORT).show()

            } else{
                Toast.makeText(this, "No se pudo enviar el correo para restablecer contraseña", Toast.LENGTH_SHORT).show()
            }
            progress.dismiss()
        }
    }
}