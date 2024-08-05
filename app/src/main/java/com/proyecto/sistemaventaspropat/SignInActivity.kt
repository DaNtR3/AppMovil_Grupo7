package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignin.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }
        binding.txtSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.btnSignInBackbutton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}