package com.app_repartidor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app_repartidor.databinding.ActivityLoginBinding
import com.app_repartidor.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class Login : AppCompatActivity() {

    private var userName: String ?= null
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("207123066809-lf8emgknefeb1b8ncanccrrs7kdddo43.apps.googleusercontent.com")
            .requestEmail().build()

        //inicialización de firebase autentication
        auth = Firebase.auth

        //inicialización de firebase database
        database = Firebase.database.reference

        //Inicialización de google autentication
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        googleSignInClient.revokeAccess()

        //login with email and password
        binding.loginButton.setOnClickListener {
            //obtener datos de los editTexts
            email = binding.emailLogin.text.toString().trim()
            password = binding.passwordLogin.text.toString().trim()

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                createUser()
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            }

        }

        binding.TxtCrearCuenta.setOnClickListener {
            val intent = Intent(this, SingUp::class.java)
            startActivity(intent)
        }

        //inicio de sesión con google
        binding.googleButtonLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        binding.btnForgotPassword.setOnClickListener {
            val intent = Intent(this, ResetPassword::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                        task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else{
            Toast.makeText(this, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUser() {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->
            if(task.isSuccessful){
                val user = auth.currentUser
                updateUI(user)
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        task ->
                    if(task.isSuccessful){
                        saveUserData()
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        //obtener datos de los editTexts
        email = binding.emailLogin.text.toString().trim()
        password = binding.passwordLogin.text.toString().trim()

        val user = UserModel(userName, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        //guardar datos en el database
        database.child("user").child(userId).setValue(user)
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}