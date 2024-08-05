package com.proyecto.sistemaventaspropat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.proyecto.sistemaventaspropat.databinding.ActivitySignupBinding


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private var connectSql = ConnectionSQLServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnSignup.setOnClickListener {
            // Validate inputs
            val firstname = binding.txtFirstname.text.toString()
            val lastName = binding.txtLastname.text.toString()
            val idNumber = binding.txtId.text.toString()
            val email = binding.txtId.text.toString()
            val password = binding.txtPassword.text.toString()

            if (firstname.isEmpty() || lastName.isEmpty() || idNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Perform sign-up action (e.g., save data, call API, etc.)
                // For demonstration, show a Toast message
                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()

                // Return result to MainActivity
                val resultIntent = Intent().apply {
                    putExtra("signup_result", "Signup successful!")
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Close SignUpActivity and return to MainActivity
            }
        }
    }

}

