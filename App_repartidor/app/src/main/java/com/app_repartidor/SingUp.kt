package com.app_repartidor

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app_repartidor.databinding.ActivitySingUpBinding
import com.app_repartidor.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SingUp : AppCompatActivity() {

    private val binding: ActivitySingUpBinding by lazy {
        ActivitySingUpBinding.inflate(layoutInflater)
    }
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

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
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAccout(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this, Login::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show()

                if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."){
                    Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show()
                }
                if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]"){
                    Toast.makeText(this, "La contraseña debe ser de al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserData() {
        //recuperar datos de los editTexts
        username = binding.userName.text.toString()
        email = binding.EmailAddress.text.toString().trim()
        password = binding.password.text.toString().trim()

        val user = UserModel(username, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //guardar datos del usuario en Firebase database
        database.child("user").child(userId).setValue(user)
    }

}