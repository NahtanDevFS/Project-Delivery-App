package com.apprestaurante

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.apprestaurante.databinding.ActivityLoginBinding
import com.apprestaurante.model.UserModel
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

class LoginActivity : AppCompatActivity() {

    private var userName : String ?= null
    private var nameOfRestaurant : String ?= null
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
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("207123066809-lf8emgknefeb1b8ncanccrrs7kdddo43.apps.googleusercontent.com")
            .requestEmail().build()

        //Inicializar la autenticación de firebase
        auth = Firebase.auth
        //inicializar la database de firebase
        database = Firebase.database.reference
        //Inicializar el inicio de sesión con Google
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnlogin.setOnClickListener{
            //obtener texto de los editTexts
            email = binding.emailLogin.text.toString().trim()
            password = binding.passwordLogin.text.toString().trim()

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            } else{
                createUserAccount(email, password)
            }

        }

        binding.googleButton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.btnNoCuenta.setOnClickListener{
            val intent= Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                val user = auth.currentUser
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                updateUI(user)
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser
                        Toast.makeText(this, "Cuenta y sesión creadas exitosamente", Toast.LENGTH_SHORT).show()
                        saveUserData()
                        updateUI(user)
                    } else{
                        Toast.makeText(this, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                        Log.d("Account", "createUserAccount: Autenticación fallida", task.exception)
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        //obtener texto de los editTexts
        email = binding.emailLogin.text.toString().trim()
        password = binding.passwordLogin.text.toString().trim()

        val user = UserModel(userName, nameOfRestaurant, email, password)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId.let {
            it?.let { it1 -> database.child("user").child(it1).setValue(user) }
        }
    }

    //launcher para inicio de sesión con Google
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account : GoogleSignInAccount = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    authTask ->
                    if(authTask.isSuccessful){
                        // Sesión con Google iniciada exitosamente
                        Toast.makeText(this, "Sesión con Google iniciada exitosamente", Toast.LENGTH_SHORT).show()
                        updateUI(authTask.result?.user)
                        finish()

                    } else {
                        Toast.makeText(this, "Sesión con Google fallido", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Sesión con Google fallido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Revisar si el usuario ya se encuentra logeado
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
