package com.apprestaurante

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprestaurante.databinding.ActivityAddMenuBinding

class AddMenuActivity : AppCompatActivity() {
    private val binding: ActivityAddMenuBinding by lazy{
        ActivityAddMenuBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.seleccionarimagen.setOnClickListener{
            pickimage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.backButton.setOnClickListener{
            finish()
        }
    }
    val pickimage=registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
        if(uri != null)
        {
            binding.imagenSeleccionada.setImageURI(uri)
        }
    }
}