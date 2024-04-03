package com.appcliente

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appcliente.databinding.ActivitySingScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SingScreen : AppCompatActivity() {
    private val binding: ActivitySingScreenBinding by lazy {
        ActivitySingScreenBinding.inflate(layoutInflater)
    }
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(binding.root)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        //Inicializar firebase autentication
        auth = Firebase.auth

        //Inicializar Firebase Database
        database = Firebase.database.reference

        binding.CreateAccoutButton.setOnClickListener {
            username = binding.userName.text.toString()
            email = binding.EmailAddress.text.toString().trim()
            password = binding.password.text.toString().trim()

            if(username.isBlank() || email.isEmpty() || password.isBlank()){
                Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                createAccout(email, password)
            }

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

    private fun createAccout(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    private fun setup(){
        title = "Autenticacion"

        binding.CreateAccoutButton.setOnClickListener {
            if (binding.EmailAddress.text.isNotEmpty() && binding.password.text.isNotEmpty() && binding.userName.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.EmailAddress.text.toString(),
                    binding.password.text.toString()).addOnCompleteListener {
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