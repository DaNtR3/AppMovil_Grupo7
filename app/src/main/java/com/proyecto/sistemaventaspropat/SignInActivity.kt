package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignin.setOnClickListener {
            val id = binding.txtId.text.toString()
            val password = binding.txtPassword.text.toString()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomePageActivity::class.java))
            }


        }
        binding.txtSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.btnSignInBackbutton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}