package com.appcliente.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.appcliente.LoginScreen
import com.appcliente.R
import com.appcliente.SingScreen
import com.appcliente.databinding.FragmentProfileBinding
import com.appcliente.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    //instanciar autenticación  y database de firebase
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.apply {

            nameProfile.isEnabled = false
            emailProfile.isEnabled = false
            addressProfile.isEnabled = false
            phoneProfile.isEnabled = false
            passwordProfile.isEnabled = false
            nameProfile.setTextColor(Color.parseColor("#808080"))
            emailProfile.setTextColor(Color.parseColor("#808080"))
            addressProfile.setTextColor(Color.parseColor("#808080"))
            phoneProfile.setTextColor(Color.parseColor("#808080"))
            passwordProfile.setTextColor(Color.parseColor("#808080"))

            binding.editButton.setOnClickListener {

                nameProfile.isEnabled = !nameProfile.isEnabled
                emailProfile.isEnabled = !emailProfile.isEnabled
                addressProfile.isEnabled = !addressProfile.isEnabled
                phoneProfile.isEnabled = !phoneProfile.isEnabled
                passwordProfile.isEnabled = ! passwordProfile.isEnabled

                if(nameProfile.isEnabled == false){
                    nameProfile.setTextColor(Color.parseColor("#808080"))
                    emailProfile.setTextColor(Color.parseColor("#808080"))
                    addressProfile.setTextColor(Color.parseColor("#808080"))
                    phoneProfile.setTextColor(Color.parseColor("#808080"))
                    passwordProfile.setTextColor(Color.parseColor("#808080"))
                } else{
                    nameProfile.setTextColor(Color.parseColor("#ffffff"))
                    emailProfile.setTextColor(Color.parseColor("#ffffff"))
                    addressProfile.setTextColor(Color.parseColor("#ffffff"))
                    phoneProfile.setTextColor(Color.parseColor("#ffffff"))
                    passwordProfile.setTextColor(Color.parseColor("#ffffff"))
                }
            }
        }

        binding.saveInfoButton.setOnClickListener {
            val name = binding.nameProfile.text.toString()
            val email = binding.emailProfile.text.toString()
            val address = binding.addressProfile.text.toString()
            val phone = binding.phoneProfile.text.toString()
            val password = binding.passwordProfile.text.toString()

            updateUserData(name, email, address, phone, password)

        }

        binding.logOutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginScreen::class.java)
            startActivity(intent)

        }

        setUserData()

        return binding.root
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String, password: String,) {

        val userId = auth.currentUser?.uid
        if (userId != null){
            //val userReference = database.getReference("user").child(userId)

            database.reference.child("user").child(userId).child("name").setValue(name)
            database.reference.child("user").child(userId).child("address").setValue(address)
            database.reference.child("user").child(userId).child("email").setValue(email)
            database.reference.child("user").child(userId).child("phone").setValue(phone)
            database.reference.child("user").child(userId).child("password").setValue(password)
            Toast.makeText(requireContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()

//            val userData = hashMapOf(
//                "name" to name,
//                "address" to address,
//                "email" to email,
//                "phone" to phone,
//                "password" to password,
//                )
//            userReference.setValue(userData).addOnSuccessListener {
//                Toast.makeText(requireContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
//            }
//                .addOnFailureListener {
//                    Toast.makeText(requireContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
//                }
        } else {
            Toast.makeText(requireContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if(userId != null){
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if(userProfile != null){
                            binding.nameProfile.setText(userProfile.name)
                            binding.addressProfile.setText(userProfile.address)
                            binding.emailProfile.setText(userProfile.email)
                            binding.phoneProfile.setText(userProfile.phone)
                            binding.passwordProfile.setText(userProfile.password)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    companion object {

    }
}