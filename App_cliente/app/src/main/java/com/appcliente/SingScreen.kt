package com.appcliente

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appcliente.databinding.ActivitySingScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SingScreen : AppCompatActivity() {
    private val binding: ActivitySingScreenBinding by lazy {
        ActivitySingScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_sing_screen)
        setContentView(binding.root)
        binding.BtnCrearCuenta.setOnClickListener {
//            val intent = Intent(this, LocationPhoneScreen::class.java)
//            startActivity(intent)
        }
        binding.TxtYaTengoCuenta.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setup()
    }
    private fun setup(){
        title = "Autenticacion"

        binding.BtnCrearCuenta.setOnClickListener {
            if (binding.TxtCorreo.text.isNotEmpty() && binding.TxtClave.text.isNotEmpty() && binding.TxtNombre.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.TxtCorreo.text.toString(),
                    binding.TxtClave.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                            showLocation(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            showAlert()
                        }
                }
            } else {
                showLlenarCampos()
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autenticar el usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showLlenarCampos(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Debe llenar todos los campos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLocation(email: String, provider: ProviderType) {
        val intent = Intent(this, LocationPhoneScreen::class.java).apply {
            putExtra("Email", email)
            putExtra("Provider", provider.name)
        }
        startActivity(intent)
    }
}