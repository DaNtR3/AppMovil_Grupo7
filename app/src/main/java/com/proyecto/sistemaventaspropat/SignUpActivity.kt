package com.proyecto.sistemaventaspropat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.proyecto.sistemaventaspropat.databinding.ActivitySignupBinding
import org.mindrot.jbcrypt.BCrypt
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var connectionsql = ConnectionSQLServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUpBackbutton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btnSignup.setOnClickListener {

            // Get values from input fields
            val userIdStr = binding.txtId.text.toString()
            val firstName = binding.txtFirstname.text.toString()
            val lastName = binding.txtLastname.text.toString()
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()
            val address = binding.txtAddress.text.toString()
            val phone = binding.txtPhone.text.toString()
            val userId = userIdStr.toIntOrNull()
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDateTime = currentDateTime.format(formatter)

            try {
                if (userIdStr.isBlank() || firstName.isBlank() || lastName.isBlank() ||
                    email.isBlank() || password.isBlank() || address.isBlank() || phone.isBlank()
                ) {
                    Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    try {
                        if (userId == null) {
                            Toast.makeText(this, "Cedula inválida", Toast.LENGTH_SHORT).show()

                        } else {
                            if (!isValidEmail(email)) {
                                Toast.makeText(this, "Correo inválido.", Toast.LENGTH_SHORT).show()

                            } else {
                                if (!isValidPassword(password)) {
                                    Toast.makeText(
                                        this,
                                        "La contraseña es muy corta",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    try {
                                        val addUser: PreparedStatement =
                                            connectionsql.dbConn()?.prepareStatement(
                                                "INSERT INTO users (user_id, privileged_user, firstname, lastname, email, password_hash, address_details, phone, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                                            )!!
                                        addUser.setInt(1, userId)
                                        addUser.setString(2, "0")
                                        addUser.setString(3, firstName)
                                        addUser.setString(4, lastName)
                                        addUser.setString(5, email)
                                        addUser.setString(6, hashedPassword)
                                        addUser.setString(7, address)
                                        addUser.setString(8, phone)
                                        addUser.setString(9, formattedDateTime)
                                        addUser.executeUpdate()

                                        startActivity(Intent(this, SignInActivity::class.java))
                                    } catch (ex: SQLException) {
                                        Toast.makeText(
                                            this,
                                            "Error añadiendo el usuario a la base de datos: ${ex.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        ex.printStackTrace()
                                    }
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        Toast.makeText(
                            this,
                            "Un error inesperado sucedió: ${ex.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        ex.printStackTrace()
                    }


                }
            } catch (ex: Exception) {
                Toast.makeText(
                    this,
                    "Un error inesperado sucedió: ${ex.message}",
                    Toast.LENGTH_SHORT
                ).show()
                ex.printStackTrace()
            }

        }
        binding.txtSignin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }

    //Custom functions
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Utility function to check password strength
    private fun isValidPassword(password: String): Boolean {

        return password.length >= 6
    }
}

