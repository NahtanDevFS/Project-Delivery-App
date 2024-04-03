package com.appcliente

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appcliente.databinding.ActivityLocationScreenBinding

enum class ProviderType {
    BASIC,
    GOOGLE
}

class LocationPhoneScreen : AppCompatActivity() {
    private val binding: ActivityLocationScreenBinding by lazy {
        ActivityLocationScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_location_phone_screen)
        setContentView(binding.root)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val locationList = arrayOf("Cabañas", "Estanzuela", "Gualan", "Huité", "La Unión", "Río Hondo", "San Diego", "San Jorge", "Teculután", "Usumatlán", "Zacapa")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        val autoCompleteTextView = binding.ListOfLocation
        autoCompleteTextView.setAdapter(adapter)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}