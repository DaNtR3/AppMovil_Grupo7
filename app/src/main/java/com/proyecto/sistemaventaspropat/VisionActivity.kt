package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivityVisionBinding

class VisionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }
    }
}