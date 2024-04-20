package com.apprestaurante

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.apprestaurante.adapter.MenuItemAdapter
import com.apprestaurante.databinding.ActivityAllItemBinding
import com.apprestaurante.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class AllItemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase
    private var menuItems: ArrayList<AllMenu> = ArrayList()

    private val binding : ActivityAllItemBinding by lazy{
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Inicializar Firebase
        auth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance().reference
        retrieveMenuItem()

        binding.backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun retrieveMenuItem() {
        userId = auth.currentUser?.uid?:""
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("user").child(userId).child("menu")

        //Buscar datos en el database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Limpiar datos antes de llamarlos
                menuItems.clear()

                //bucle para recorrer los platillos agregados en el database
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    private fun setAdapter() {

        val adapter = MenuItemAdapter(this@AllItemActivity, menuItems, databaseReference){ position ->
            deleteMenuItems(position)
        }
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecyclerView.adapter=adapter
    }

    private fun deleteMenuItems(position: Int) {
        val menuItemToDelete = menuItems[position]
        val menuItemKey = menuItemToDelete.key
        val foodMenuReference = database.reference.child("menu").child(menuItemKey!!)
        val foodRef: DatabaseReference = database.reference.child("user").child(userId).child("menu").child(menuItemKey)

        foodRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful){
                menuItems.removeAt(position)
                binding.MenuRecyclerView.adapter?.notifyItemRemoved(position)
            } else {
                Toast.makeText(this, "Error al eliminar el platillo", Toast.LENGTH_SHORT).show()
            }
        }
    }

}