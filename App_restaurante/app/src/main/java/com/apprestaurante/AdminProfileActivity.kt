package com.apprestaurante

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprestaurante.databinding.ActivityAdminProfileBinding
import com.apprestaurante.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("user")


        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.saveInfoButton.setOnClickListener {
            updateUserData()
        }

        binding.nombre.isEnabled = false
        binding.direccion.isEnabled = false
        binding.email.isEnabled = false
        binding.telefono.isEnabled = false
        binding.contrasena.isEnabled = false
        binding.userRestaurant.isEnabled = false
        binding.saveInfoButton.isEnabled = false
        binding.nombre.setTextColor(Color.parseColor("#808080"))
        binding.direccion.setTextColor(Color.parseColor("#808080"))
        binding.email.setTextColor(Color.parseColor("#808080"))
        binding.telefono.setTextColor(Color.parseColor("#808080"))
        binding.contrasena.setTextColor(Color.parseColor("#808080"))
        binding.userRestaurant.setTextColor(Color.parseColor("#808080"))
        binding.saveInfoButton.setTextColor(Color.parseColor("#808080"))

        var isEnable = false
        binding.editButton.setOnClickListener {
            isEnable = !isEnable
            binding.nombre.isEnabled = isEnable
            binding.direccion.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.telefono.isEnabled = isEnable
            binding.userRestaurant.isEnabled = isEnable
            binding.contrasena.isEnabled = isEnable

            if(binding.nombre.isEnabled == false){
                binding.nombre.setTextColor(Color.parseColor("#808080"))
                binding.direccion.setTextColor(Color.parseColor("#808080"))
                binding.email.setTextColor(Color.parseColor("#808080"))
                binding.telefono.setTextColor(Color.parseColor("#808080"))
                binding.contrasena.setTextColor(Color.parseColor("#808080"))
                binding.userRestaurant.setTextColor(Color.parseColor("#808080"))
                binding.saveInfoButton.setTextColor(Color.parseColor("#808080"))
            } else {
                binding.nombre.setTextColor(Color.parseColor("#ffffff"))
                binding.direccion.setTextColor(Color.parseColor("#ffffff"))
                binding.email.setTextColor(Color.parseColor("#ffffff"))
                binding.telefono.setTextColor(Color.parseColor("#ffffff"))
                binding.contrasena.setTextColor(Color.parseColor("#ffffff"))
                binding.userRestaurant.setTextColor(Color.parseColor("#ffffff"))
                binding.saveInfoButton.setTextColor(Color.parseColor("#ffc107"))

            }

            if (isEnable) {
                binding.nombre.requestFocus()
            }
            binding.saveInfoButton.isEnabled = isEnable
        }

        retrieveUserData()

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun retrieveUserData() {

        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var ownerName = snapshot.child("name").getValue()
                        var email = snapshot.child("email").getValue()
                        var password = snapshot.child("password").getValue()
                        var address = snapshot.child("address").getValue()
                        var phone = snapshot.child("phone").getValue()
                        var restaurant = snapshot.child("nameOfRestaurant").getValue()

                        setDataToTextView(ownerName, email, password, address, phone, restaurant)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }

    private fun setDataToTextView(
        ownerName: Any?,
        email: Any?,
        password: Any?,
        address: Any?,
        phone: Any?,
        restaurant: Any?,
    ) {
        binding.nombre.setText(ownerName.toString())
        binding.email.setText(email.toString())
        binding.contrasena.setText(password.toString())
        binding.telefono.setText(phone.toString())
        binding.direccion.setText(address.toString())
        binding.userRestaurant.setText(restaurant.toString())
    }

    private fun updateUserData() {
        var updateName = binding.nombre.text.toString()
        var updateEmail = binding.email.text.toString()
        var updatePassword = binding.contrasena.text.toString()
        var updatePhone = binding.telefono.text.toString()
        var updateAddress = binding.direccion.text.toString()
        var updateRestaurant = binding.direccion.text.toString()
        val currentUserUid = auth.currentUser?.uid
        if(currentUserUid != null){
            val userReference = adminReference.child(currentUserUid)

            userReference.child("name").setValue(updateName)
            userReference.child("email").setValue(updateEmail)
            userReference.child("password").setValue(updatePassword)
            userReference.child("phone").setValue(updatePhone)
            userReference.child("address").setValue(updateAddress)
            userReference.child("nameOfRestaurant").setValue(updateRestaurant)


            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()

            //actualizar el email y contraseña para Firebase authentication
            auth.currentUser?.updateEmail(updateEmail)
            auth.currentUser?.updatePassword(updatePassword)
        }else {
            Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
        }
    }

}