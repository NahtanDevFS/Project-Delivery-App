package com.apprestaurante

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.apprestaurante.databinding.ActivityAddMenuBinding
import com.apprestaurante.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {

    //Datos de los platillos
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredient: String
    private lateinit var foodRestaurant: String
    private lateinit var userId: String
    private var foodImageUri: Uri? = null

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference

    private val binding: ActivityAddMenuBinding by lazy{
        ActivityAddMenuBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        //Inicializar la instancia de la database de firebase
        database = FirebaseDatabase.getInstance()
        //Referencia a users en firebase database
        adminReference = database.reference.child("user")

        retrieveRestaurant()

        binding.addItemButton.setOnClickListener {
            // Obtener datos de los campos
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredient.text.toString().trim()
            foodRestaurant = binding.restaurant.text.toString().trim()

            if (!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() || foodIngredient.isBlank() || foodRestaurant.isBlank())){
                uploadData()
                Toast.makeText(this, "Platillo agregado exitosamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show()
            }

        }

        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun retrieveRestaurant() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        var restaurant = snapshot.child("nameOfRestaurant").getValue()

                        if(restaurant != null){
                            binding.restaurant.setText(restaurant.toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun uploadData() {
        userId = auth.currentUser?.uid?:""
        //obtener una referencia al nodo "menu" en el database
        //val menuRef = database.getReference("menu")
        val menuRef = database.getReference().child("user").child(userId).child("menu")
        //generar una llave única para el nuevo platillo
        val newItemKey = menuRef.push().key

        if(foodImageUri != null){

            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    downloadUrl ->
                    //Crear un nuevo platillo
                    val newItem = AllMenu(
                        newItemKey,
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodIngredient = foodIngredient,
                        foodRestaurant = foodRestaurant,
                        foodImage = downloadUrl.toString(),
                    )
                    newItemKey?.let {
                        key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "Datos subidos exitosamente", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener {
                    Toast.makeText(this, "Error al subir los datos", Toast.LENGTH_SHORT).show()
                }

        } else {
                Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private val pickImage=registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        if(uri != null) {
            binding.selectedImage.setImageURI(uri)
            foodImageUri = uri
        }
    }
}