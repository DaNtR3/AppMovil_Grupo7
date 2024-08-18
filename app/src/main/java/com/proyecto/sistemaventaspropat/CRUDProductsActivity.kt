package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivityCrudProductsBinding
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.sql.PreparedStatement
import java.sql.SQLException

class CRUDProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrudProductsBinding
    private var imageUri: Uri? = null

    private val imagePickerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val data = result.data
                imageUri = data?.data
                imageUri?.let {
                    displayImage(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrudProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateSpinner()

        binding.btnProductsBack.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        binding.btnUploadImage.setOnClickListener {
            openImageChooser()
        }

        binding.btnAddProduct.setOnClickListener {
            val productName = binding.txtProductName.text.toString()
            val productCost = binding.txtCost.text.toString()
            val productAmount = binding.txtAmount.text.toString()
            val productColor = binding.spinnerColors.selectedItem.toString()

            if (productName.isBlank() || productCost.isBlank() || productAmount.isBlank() || imageUri == null || productColor.isBlank()) {
                Toast.makeText(
                    this,
                    "Por favor rellena todos los campos y selecciona una imagen",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val base64String = imageToBase64String(imageUri!!)
                if (base64String != null) {
                    addProduct(
                        productName,
                        productCost.toDouble(),
                        base64String,
                        productColor,
                        productAmount.toInt()
                    )
                } else {
                    Toast.makeText(this, "Error al convertir la imagen...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        binding.btnUpdateProduct.setOnClickListener {
            val productName = binding.txtProductName.text.toString()
            val productCost = binding.txtCost.text.toString()
            val productAmount = binding.txtAmount.text.toString()
            val productColor = binding.spinnerColors.selectedItem.toString()
            val productId = binding.txtId.text.toString()

            if (productName.isBlank() || productCost.isBlank() || productAmount.isBlank() || imageUri == null || productColor.isBlank() || productId.isBlank()) {
                Toast.makeText(
                    this,
                    "Por favor rellena todos los campos y selecciona una imagen",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val base64String = imageToBase64String(imageUri!!)
                if (base64String != null) {
                    updateProduct(
                        productId.toInt(),
                        productName,
                        productCost.toDouble(),
                        base64String,
                        productColor,
                        productAmount.toInt()
                    )
                } else {
                    Toast.makeText(this, "Error al convertir la imagen...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.btnDeleteProduct.setOnClickListener {

            val productId = binding.txtId.text.toString()
            if (productId.isBlank()) {
                Toast.makeText(
                    this,
                    "El ID de producto esta vacio",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                deleteProduct(productId.toInt())
            }
        }

        binding.btnSearch.setOnClickListener {
            val productId = binding.txtId.text.toString().toIntOrNull()
            if (productId != null) {
                populateFieldsIfProductExists(productId)
            } else {
                Toast.makeText(this, "Por favor ingrese un ID de producto válido", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun displayImage(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding.previewImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar la imagen.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateSpinner() {
        val categories = listOf(
            "Blanco", "Negro", "Rojo", "Verde", "Azul",
            "Amarillo", "Cian", "Magenta", "Gris", "Rosa",
            "Naranja", "Marrón", "Violeta", "Turquesa", "Beige"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerColors.adapter = adapter
    }

    private fun imageToBase64String(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.let {
                val bitmap = BitmapFactory.decodeStream(it)
                it.close() // Ensure to close the input stream
                encodeBitmapToBase64(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        outputStream.close() // Ensure to close the output stream
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            // Decode the Base64 string into a byte array
            val decodedString = Base64.decode(base64String, Base64.DEFAULT)
            // Convert the byte array into a Bitmap
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun displayImageFromBase64(base64String: String) {
        // Decode the Base64 string to a Bitmap
        val bitmap = decodeBase64ToBitmap(base64String)
        // Set the Bitmap to the ImageView
        binding.previewImage.setImageBitmap(bitmap)
    }


    private fun addProduct(
        name: String,
        cost: Double,
        base64Image: String,
        color: String,
        amount: Int
    ) {
        val connectionsql = ConnectionSQLServer()
        val iva: Double = cost * 1.13
        val query = """
            INSERT INTO productos (Nombre, imagen_producto, Colores, Cantidad_Disponible, CostoSinIVA, CostoConIVA)
            VALUES (?, ?, ?, ?, ?, ?);
        """
        try {
            val addProduct: PreparedStatement = connectionsql.dbConn()?.prepareStatement(query)!!
            addProduct.setString(1, name)
            addProduct.setString(2, base64Image) // Store Base64 encoded string
            addProduct.setString(3, color)
            addProduct.setInt(4, amount)
            addProduct.setDouble(5, cost)
            addProduct.setDouble(6, iva)
            addProduct.executeUpdate()
            Toast.makeText(this, "Producto ingresado correctamente", Toast.LENGTH_SHORT).show()
            clearFields()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al añadir el producto: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun updateProduct(
        id: Int,
        name: String,
        cost: Double,
        base64Image: String,
        color: String,
        amount: Int
    ) {
        if (doesProductExist(id)) {
            val query = """
            UPDATE productos
            SET Nombre = ?, imagen_producto = ?, Colores = ?, Cantidad_Disponible = ?, CostoSinIVA = ?, CostoConIVA = ?
            WHERE ID_Producto = ?
        """
            try {
                val connection = ConnectionSQLServer().dbConn()
                val statement = connection?.prepareStatement(query)
                statement?.setString(1, name)
                statement?.setString(2, base64Image)
                statement?.setString(3, color)
                statement?.setInt(4, amount)
                statement?.setDouble(5, cost)
                statement?.setDouble(6, cost * 1.13) // CostoConIVA
                statement?.setInt(7, id)
                statement?.executeUpdate()
                Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT)
                    .show()
                clearFields()
            } catch (e: SQLException) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Error al actualizar el producto: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteProduct(productId: Int) {
        if (doesProductExist(productId)) {
            val query = "DELETE FROM productos WHERE ID_Producto = ?"
            try {
                val connection = ConnectionSQLServer().dbConn()
                val statement = connection?.prepareStatement(query)
                statement?.setInt(1, productId)
                statement?.executeUpdate()
                Toast.makeText(this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
                clearFields()
            } catch (e: SQLException) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Error al eliminar el producto: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
        }
    }




    private fun doesProductExist(productId: Int): Boolean {
        val query = "SELECT COUNT(*) FROM productos WHERE ID_Producto = ?"
        return try {
            val connection = ConnectionSQLServer().dbConn()
            val statement = connection?.prepareStatement(query)
            statement?.setInt(1, productId)
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

    private fun populateFieldsIfProductExists(productId: Int) {
        val query = """
        SELECT Nombre, imagen_producto, Colores, Cantidad_Disponible, CostoSinIVA, CostoConIVA 
        FROM productos 
        WHERE ID_Producto = ?
    """
        Thread {
            try {
                val connection = ConnectionSQLServer().dbConn()
                val statement = connection?.prepareStatement(query)
                statement?.setInt(1, productId)
                val resultSet = statement?.executeQuery()

                if (resultSet?.next() == true) {
                    val productName = resultSet.getString("Nombre")
                    val base64Image = resultSet.getString("imagen_producto")
                    val productColor = resultSet.getString("Colores")
                    val quantity = resultSet.getInt("Cantidad_Disponible")
                    val costWithoutIva = resultSet.getDouble("CostoSinIVA")
                    val costWithIva = resultSet.getDouble("CostoConIVA")

                    runOnUiThread {
                        binding.txtProductName.setText(productName)
                        binding.txtCost.setText(costWithoutIva.toString())
                        binding.txtAmount.setText(quantity.toString())
                        binding.spinnerColors.setSelection(getColorIndex(productColor))
                        displayImageFromBase64(base64Image)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                }

                resultSet?.close()
                statement?.close()
                connection?.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error al buscar el producto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun getColorIndex(color: String): Int {
        val colorList = listOf(
            "Blanco", "Negro", "Rojo", "Verde", "Azul",
            "Amarillo", "Cian", "Magenta", "Gris", "Rosa",
            "Naranja", "Marrón", "Violeta", "Turquesa", "Beige"
        )
        return colorList.indexOf(color)
    }



    private fun clearFields() {
        // Clear text fields
        binding.txtProductName.text.clear()
        binding.txtCost.text.clear()
        binding.txtAmount.text.clear()
        binding.txtId.text.clear()

        // Reset spinner to default selection
        binding.spinnerColors.setSelection(0)

        // Clear image preview
        binding.previewImage.setImageDrawable(null)

        // Reset the image URI
        imageUri = null

    }


}
