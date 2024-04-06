package com.apprepartidor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprepartidor.databinding.ActivityHistorialEntregasBinding
import com.apprepartidor.databinding.ActivityMenuBinding
import com.facebook.login.LoginManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth


enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}
@Suppress("DEPRECATION")
class menu : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navegacion2.setOnItemSelectedListener {menuItem ->

            when(menuItem.itemId){
                R.id.ajust -> {
                    startActivity(Intent(this, menu::class.java))
                }
                R.id.pedido -> {
                    startActivity(Intent(this, principal::class.java))
                }
                R.id.histori -> {
                    startActivity(Intent(this, historial_entregas::class.java))
                }


                else ->{

                }
            }
            true
        }
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Integración de FireBase completa")
        analytics.logEvent("InitScreen", bundle)


        // Setup
        val bundle1 = intent.extras
        val email = bundle1?.getString("email")
        val provider =bundle1?.getString("provider")
        setup(email ?: "", provider ?: "")

        //Guardar datos Google
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        val mapa = findViewById<TextView>(R.id.button5)

        mapa.setOnClickListener{
            val intent = Intent(this, pedidos_disponibles::class.java)
            startActivity(intent)
        }


}
    private fun setup(email: String, provider: String){
        title = "Inicio"
        val emailText = findViewById<TextView>(R.id.textView12)
        val providerText = findViewById<TextView>(R.id.textView13)
        val cerrar = findViewById<TextView>(R.id.button4)

        emailText.text = email
        providerText.text = provider

        cerrar.setOnClickListener{

            // Borrar Datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if(provider == ProviderType.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }

    }
}