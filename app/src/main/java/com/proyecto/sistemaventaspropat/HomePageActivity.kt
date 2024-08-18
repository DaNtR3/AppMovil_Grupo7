package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivityHomeBinding

class HomePageActivity : AppCompatActivity(){

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtSignout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.linearLayoutInicio.setOnClickListener{
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        binding.linearLayoutProductosBar.setOnClickListener{
            startActivity(Intent(this, CRUDProductsActivity::class.java))
        }
    }
}