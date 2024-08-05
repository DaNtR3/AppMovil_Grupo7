package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignInMain.setOnClickListener {
            // Use a lambda to start the SignInActivity when the button is clicked
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.btnSignUpMain.setOnClickListener {
            // Use a lambda to start the SignInActivity when the button is clicked
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }
}
