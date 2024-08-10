package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivityAddProductsBinding
import org.mindrot.jbcrypt.BCrypt
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductsBinding
    private var connectionsql = ConnectionSQLServer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProductsBack.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }
        /*binding.btnAddProduct.setOnClickListener {
            val productname = binding.txtProductName.text.toString()
            val productcost = binding.txtCost.text.toString()
            val productiva = binding.txtIva.text.toString()
            val productimageurl = binding.txtImageUrl.text.toString()


            try {
                if (productname.isBlank() || productcost.isBlank() || productiva.isBlank() ||
                    productimageurl.isBlank()
                ) {
                    Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT)
                        .show()

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
                        Toast.makeText(
                            this,
                            "Usuario registrado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } catch (ex: SQLException) {
                        Toast.makeText(
                            this,
                            "Error añadiendo el usuario a la base de datos: ${ex.message}",
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

        }*/

    }
}

