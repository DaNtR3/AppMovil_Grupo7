package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivityCrudClientsBinding
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CRUDClientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrudClientsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrudClientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateSpinner()

        binding.btnBackHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        binding.btnAddUser.setOnClickListener {

            // Get values from input fields
            val userIdStr = binding.txtId.text.toString()
            val firstName = binding.txtFirstname.text.toString()
            val lastName = binding.txtLastname.text.toString()
            val email = binding.txtEmail.text.toString()
            val address = binding.txtAddress.text.toString()
            val phone = binding.txtPhone.text.toString()
            val privilegedUser = binding.spinnerPrivileged.selectedItem.toString()
            val userId = userIdStr.toIntOrNull()
            val hashedPassword = BCrypt.hashpw(generateRandomPassword(), BCrypt.gensalt())
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDateTime = currentDateTime.format(formatter)

            try {
                if (userIdStr.isBlank() || firstName.isBlank() || lastName.isBlank() ||
                    email.isBlank() || address.isBlank() || phone.isBlank() || privilegedUser.isBlank()
                ) {
                    Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    try {
                        if (userId == null) {
                            Toast.makeText(
                                this,
                                "Cedula inválida",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            if (!isValidEmail(email)) {
                                Toast.makeText(this, "Correo inválido.", Toast.LENGTH_SHORT).show()

                            } else {

                                addUser(
                                    userId,
                                    firstName,
                                    lastName,
                                    email,
                                    address,
                                    phone,
                                    getPrivilegesValue().toString(),
                                    formattedDateTime,
                                    hashedPassword
                                )
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
        binding.btnDeleteUser.setOnClickListener {
            val userID = binding.txtId.text.toString()
            if (userID.isBlank()) {
                Toast.makeText(
                    this,
                    "La cédula del usuario esta vacia",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (doesUserExist(userID.toInt())){
                    deleteUser(userID.toInt())
                }else{
                    Toast.makeText(
                        this,
                        "No se puede borrar porque el usuario no existe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnUpdateUser.setOnClickListener {
            // Get values from input fields
            val userIdStr = binding.txtId.text.toString()
            val firstName = binding.txtFirstname.text.toString()
            val lastName = binding.txtLastname.text.toString()
            val email = binding.txtEmail.text.toString()
            val address = binding.txtAddress.text.toString()
            val phone = binding.txtPhone.text.toString()
            val privilegedUser = binding.spinnerPrivileged.selectedItem.toString()
            val userId = userIdStr.toIntOrNull()
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDateTime = currentDateTime.format(formatter)


            if (userIdStr.isBlank() || firstName.isBlank() || lastName.isBlank() ||
                email.isBlank() || address.isBlank() || phone.isBlank() || privilegedUser.isBlank()
            ) {
                Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT)
                    .show()

            } else {
                try {
                    if (userId == null) {
                        Toast.makeText(
                            this,
                            "Cedula inválida",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        if (!isValidEmail(email)) {
                            Toast.makeText(this, "Correo inválido.", Toast.LENGTH_SHORT).show()

                        } else {

                            updateUser(
                                userId,
                                firstName,
                                lastName,
                                email,
                                address,
                                phone,
                                getPrivilegesValue().toString(),
                                formattedDateTime
                            )

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
        }

        binding.btnSearchUser.setOnClickListener {
            val userID = binding.txtId.text.toString().toIntOrNull()
            if (userID != null) {
                populateFieldsIfUserExists(userID)
            } else {
                Toast.makeText(
                    this,
                    "Por favor ingrese la cédula de usuario válido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun populateSpinner() {
        val categories = listOf(
            "Administrador", "Cliente"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrivileged.adapter = adapter
    }

    private fun getPrivilegesValue(): Int {
        val selectedItem = binding.spinnerPrivileged.selectedItem.toString()
        return if (selectedItem == "Administrador") {
            1
        } else {
            0
        }
    }

    private fun addUser(
        userId: Int,
        firstName: String,
        lastName: String,
        email: String,
        address: String,
        phone: String,
        privileged: String,
        date: String,
        hashedPassword: String
    ) {
        if (doesUserExist(userId)) {
            Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
        } else {
            val connectionsql = ConnectionSQLServer()
            try {
                val addUser: PreparedStatement =
                    connectionsql.dbConn()?.prepareStatement(
                        "INSERT INTO users (user_id, privileged_user, firstname, lastname, email, address_details, phone, created_at, updated_at, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    )!!
                addUser.setInt(1, userId)
                addUser.setString(2, privileged)
                addUser.setString(3, firstName)
                addUser.setString(4, lastName)
                addUser.setString(5, email)
                addUser.setString(6, address)
                addUser.setString(7, phone)
                addUser.setString(8, date)
                addUser.setString(9, date)
                addUser.setString(10, hashedPassword)
                addUser.executeUpdate()
                Toast.makeText(
                    this,
                    "Usuario registrado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                clearFields()
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

    private fun deleteUser(userID: Int) {
        if (doesUserExist(userID)) {
            if (userID == 1) {
                Toast.makeText(
                    this,
                    "Este usuario no se puede borrar",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val query = "DELETE FROM users WHERE user_id = ?"
                try {
                    val connection = ConnectionSQLServer().dbConn()
                    val statement = connection?.prepareStatement(query)
                    statement?.setInt(1, userID)
                    statement?.executeUpdate()
                    Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT)
                        .show()
                    clearFields()
                } catch (e: SQLException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Error al eliminar el usuario: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUser(
        userId: Int,
        firstName: String,
        lastName: String,
        email: String,
        address: String,
        phone: String,
        privileged: String,
        date: String
    ) {
        if (doesUserExist(userId)) {
            if (userId == 1) {
                Toast.makeText(
                    this,
                    "Este usuario no se puede modificar",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val query = """
                    UPDATE users
                    SET privileged_user = ?, firstname = ?, lastname = ?, email = ?, address_details = ?, phone = ?, updated_at = ?
                    WHERE user_id = ?
                    """
                try {
                    val connection = ConnectionSQLServer().dbConn()
                    val statement = connection?.prepareStatement(query)
                    statement?.setString(1, privileged)
                    statement?.setString(2, firstName)
                    statement?.setString(3, lastName)
                    statement?.setString(4, email)
                    statement?.setString(5, address) // CostoConIVA
                    statement?.setString(6, phone)
                    statement?.setString(7, date)
                    statement?.setInt(8, userId)
                    statement?.executeUpdate()
                    Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT)
                        .show()
                    clearFields()
                } catch (e: SQLException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Error al actualizar el usuario: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        }
    }


    private fun populateFieldsIfUserExists(userID: Int) {
        val query = """
        SELECT privileged_user, firstname, lastname, email, address_details, phone
        FROM users 
        WHERE user_id = ?
        """
        Thread {
            try {
                val connection = ConnectionSQLServer().dbConn()
                val statement = connection?.prepareStatement(query)
                statement?.setInt(1, userID)
                val resultSet = statement?.executeQuery()

                if (resultSet?.next() == true) {
                    val firstName = resultSet.getString("firstname")
                    val lastName = resultSet.getString("lastname")
                    val privileged = resultSet.getInt("privileged_user")
                    val email = resultSet.getString("email")
                    val address = resultSet.getString("address_details")
                    val phone = resultSet.getString("phone")

                    runOnUiThread {
                        binding.txtFirstname.setText(firstName)
                        binding.txtLastname.setText(lastName)
                        binding.txtEmail.setText(email)
                        binding.txtAddress.setText(address)
                        binding.txtPhone.setText(phone)
                        val roleIndex = getRoleIndex(privileged)
                        if (roleIndex != -1) {
                            binding.spinnerPrivileged.setSelection(roleIndex)
                        } else {
                            Log.e("CRUDClientsActivity", "Invalid role '$privileged' for userID $userID")
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                }

                resultSet?.close()
                statement?.close()
                connection?.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Error al buscar el usuario: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }


    private fun getRoleIndex(role: Int): Int {
        return when (role) {
            1 -> 0
            0 -> 1
            else -> -1
        }
    }

    //Custom functions
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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

    private fun generateRandomPassword(): String {
        // Define character sets
        val upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val specialCharacters = "!@#$%^&*()-_=+[]{}|;:,.<>?/`~"

        // Combine all character sets
        val allCharacters = upperCaseLetters + lowerCaseLetters + digits + specialCharacters

        // Fixed length for the password
        val passwordLength = 12

        // SecureRandom instance for cryptographically strong random number generation
        val random = SecureRandom()
        val password = StringBuilder(passwordLength)

        // Add one character from each character set
        password.append(upperCaseLetters[random.nextInt(upperCaseLetters.length)])
        password.append(lowerCaseLetters[random.nextInt(lowerCaseLetters.length)])
        password.append(digits[random.nextInt(digits.length)])
        password.append(specialCharacters[random.nextInt(specialCharacters.length)])

        // Fill the rest of the password length with random characters from all sets
        for (i in 4 until passwordLength) {
            password.append(allCharacters[random.nextInt(allCharacters.length)])
        }

        // Shuffle the result to avoid predictable patterns
        return password.toString().toCharArray().apply { randomize() }.concatToString()
    }

    // Helper function to shuffle characters in a char array
    private fun CharArray.randomize() {
        val random = SecureRandom()
        for (i in this.indices) {
            val randomIndex = random.nextInt(this.size)
            val temp = this[i]
            this[i] = this[randomIndex]
            this[randomIndex] = temp
        }
    }

    private fun clearFields() {
        // Clear text fields
        binding.txtId.text.clear()
        binding.txtFirstname.text.clear()
        binding.txtLastname.text.clear()
        binding.txtEmail.text.clear()
        binding.txtPhone.text.clear()
        binding.txtAddress.text.clear()

        // Reset spinner to default selection
        binding.spinnerPrivileged.setSelection(0)

    }
}