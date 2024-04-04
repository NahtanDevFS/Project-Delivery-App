package com.apprestaurante

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apprestaurante.databinding.ActivitySignBinding
import com.apprestaurante.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {

    private lateinit var userName : String
    private lateinit var nameOfRestaurant : String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference

    private val binding : ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // inicialización de firebase Auth
        auth = Firebase.auth

        // Inicialización de firebase database
        database = Firebase.database.reference


        binding.createUserButton.setOnClickListener {
            //obtener texto de los editTexts
            userName = binding.name.text.toString().trim()
            nameOfRestaurant = binding.restaurantName.text.toString().trim()
            email = binding.emailOrPhone.text.toString().trim()
            password = binding.password.text.toString().trim()

            if(userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            } else{
                createAccount(email, password)
            }
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)

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

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { 
            task ->
            if(task.isSuccessful){
                Toast.makeText(this, "cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Se produjo un error al crear la cuenta", Toast.LENGTH_SHORT).show()
                Log.d("Account", "crearCuenta: Fallido", task.exception)
            }
        }
    }

    //guardar información en el database
    private fun saveUserData() {
        //obtener texto de los editTexts
        userName = binding.name.text.toString().trim()
        nameOfRestaurant = binding.restaurantName.text.toString().trim()
        email = binding.emailOrPhone.text.toString().trim()
        password = binding.password.text.toString().trim()

        val user = UserModel(userName, nameOfRestaurant, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        //guardar la información del usuario en firebase database
        database.child("user").child(userId).setValue(user)
    }
}
