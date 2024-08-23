package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivitySigninBinding
import com.proyecto.sistemaventaspropat.dbconnection.ConnectionSQLServer
import com.proyecto.sistemaventaspropat.models.CartItem
import com.proyecto.sistemaventaspropat.models.Product
import com.proyecto.sistemaventaspropat.models.User
import com.proyecto.sistemaventaspropat.viewmodels.MyApplication
import com.proyecto.sistemaventaspropat.viewmodels.UserViewModel
import org.mindrot.jbcrypt.BCrypt
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var userViewModel: UserViewModel
    private var connectionsql = ConnectionSQLServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain UserViewModel from Application class
        val application = application as MyApplication
        userViewModel = application.userViewModel


        binding.btnSignin.setOnClickListener {
            val id = binding.txtId.text.toString()
            val password = binding.txtPassword.text.toString()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                validateUser(id, password)

            }
        }

        binding.txtSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnSignInBackbutton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun validateUser(userId: String, password: String) {
        val query = "SELECT user_id, password_hash FROM users WHERE user_id = ?"
        val connection = connectionsql.dbConn()
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        try {
            preparedStatement = connection?.prepareStatement(query)
            preparedStatement?.setString(1, userId)
            resultSet = preparedStatement?.executeQuery()

            if (resultSet?.next() == true) {
                // User exists and now we are going to check if the passwords match
                val storedHash = resultSet.getString("password_hash")
                if (checkPassword(password, storedHash)) {
                    // Password matches
                    fetchUserDetails(userId)
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomePageActivity::class.java))
                } else {
                    // Password does not match
                    Toast.makeText(this, "El password no es válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                // User does not exist
                Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: SQLException) {
            Toast.makeText(
                this,
                "Error realizando la consulta a la base de datos",
                Toast.LENGTH_SHORT
            ).show()
            ex.printStackTrace()
        } finally {
            resultSet?.close()
            preparedStatement?.close()
            connection?.close()
        }
    }

    // Function to check if the provided password matches the stored hash
    private fun checkPassword(password: String, storedHash: String): Boolean {
        return BCrypt.checkpw(password, storedHash)
    }


    private fun fetchUserDetails(userId: String) {
        val query =
            "SELECT user_id, privileged_user, firstname, lastname, email, phone, address_details FROM users WHERE user_id = ?"
        val connection = connectionsql.dbConn()
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        try {
            preparedStatement = connection?.prepareStatement(query)
            preparedStatement?.setString(1, userId)
            resultSet = preparedStatement?.executeQuery()

            if (resultSet?.next() == true) {
                // User details fetched, create User object
                val user = User(
                    id = resultSet.getInt("user_id"),
                    role = resultSet.getInt("privileged_user"),
                    firstname = resultSet.getString("firstname"),
                    lastname = resultSet.getString("lastname"),
                    email = resultSet.getString("email"),
                    phone = resultSet.getString("phone"),
                    address = resultSet.getString("address_details")
                )

                // Log user details
                Log.d("SignInActivity", "Fetched user details: $user")

                // Update UserViewModel with user details
                userViewModel.setUser(user)

                // Log UserViewModel state
                userViewModel.user.observe(this) { observedUser ->
                    Log.d("SignInActivity", "UserViewModel updated: $observedUser")
                }
            } else {
                Log.d("SignInActivity", "Error fetching user details for user ID: $userId")
                Toast.makeText(this, "Error al obtener detalles del usuario", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (ex: SQLException) {
            Toast.makeText(
                this,
                "Error realizando la consulta a la base de datos",
                Toast.LENGTH_SHORT
            ).show()
            ex.printStackTrace()
        } finally {
            resultSet?.close()
            preparedStatement?.close()
            connection?.close()
        }
    }

}
