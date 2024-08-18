package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivityPasswordResetBinding
import org.mindrot.jbcrypt.BCrypt
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PasswordResetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordResetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }
        binding.btnPasswordReset.setOnClickListener {
            val userIdStr = binding.txtId.text.toString()
            val password = binding.txtPassword.text.toString()
            val userId = userIdStr.toIntOrNull()
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDateTime = currentDateTime.format(formatter)

            if (userId == null) {
                Toast.makeText(this, "La cédula no puede estar vacía", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (doesUserExist(userId)) {
                    if (userId == 1) {
                        Toast.makeText(
                            this,
                            "No se pudo restablecer la contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (isValidPassword(password)) {
                            updatePassword(userId, password, formattedDateTime)
                        } else {
                            Toast.makeText(
                                this,
                                "La contraseña debe de ser de al menos 8 carácteres",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

    }

    private fun updatePassword(userID: Int, password: String, date: String) {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val query = """UPDATE users SET password_hash = ?, updated_at = ? WHERE user_id = ?"""
        try {
            val connection = ConnectionSQLServer().dbConn()
            val statement = connection?.prepareStatement(query)
            statement?.setString(1, hashedPassword)
            statement?.setString(2, date)
            statement?.setInt(3, userID)
            statement?.executeUpdate()
            Toast.makeText(this, "Contraseña restablecida correctamente", Toast.LENGTH_SHORT)
                .show()
            clearFields()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Error al actualizar la contraseña: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // Utility function to check password strength
    private fun isValidPassword(password: String): Boolean {

        return password.length >= 8 && password.isNotEmpty()
    }

    private fun doesUserExist(userID: Int): Boolean {
        val query = "SELECT COUNT(*) FROM users WHERE user_id = ?"
        return try {
            val connection = ConnectionSQLServer().dbConn()
            val statement = connection?.prepareStatement(query)
            statement?.setInt(1, userID)
            val resultSet = statement?.executeQuery()

            var exists = false
            if (resultSet?.next() == true) {
                exists = resultSet.getInt(1) > 0
            }

            resultSet?.close()
            statement?.close()
            connection?.close()

            exists
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    private fun clearFields() {
        // Clear text fields
        binding.txtId.text.clear()
        binding.txtPassword.text.clear()
    }

}


