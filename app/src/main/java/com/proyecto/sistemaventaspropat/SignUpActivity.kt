package com.proyecto.sistemaventaspropat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.proyecto.sistemaventaspropat.databinding.ActivitySignupBinding


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUpBackbutton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java ))
        }
        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java ))
        }
        binding.txtSignin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java ))
        }
    }

}

