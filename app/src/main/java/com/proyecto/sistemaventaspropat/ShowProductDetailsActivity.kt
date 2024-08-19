package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.proyecto.sistemaventaspropat.databinding.ActivityProductDetailsBinding

class ShowProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackHome.setOnClickListener {
            startActivity(Intent(this, ShowProductsActivity::class.java))
        }

        val productId = intent.getIntExtra("PRODUCT_ID", -1)

        if (productId != -1) {
            // Fetch product details
            FetchProductDetailsTask().execute(productId)
        }
    }

    private inner class FetchProductDetailsTask : AsyncTask<Int, Void, Product?>() {
        override fun doInBackground(vararg params: Int?): Product? {
            val id = params.firstOrNull() ?: return null
            val query = """
                SELECT ID_Producto, Nombre, imagen_producto, CostoConIVA, Cantidad_Disponible, Colores
                FROM productos
                WHERE ID_Producto = ?
            """
            return try {
                val connection = ConnectionSQLServer().dbConn()
                val statement = connection?.prepareStatement(query)
                statement?.setInt(1, id)
                val resultSet = statement?.executeQuery()
                if (resultSet?.next() == true) {
                    val name = resultSet.getString("Nombre")
                    val base64Image = resultSet.getString("imagen_producto")
                    val costWithIva = resultSet.getDouble("CostoConIVA")
                    val amount = resultSet.getInt("Cantidad_Disponible")
                    val color = resultSet.getString("Colores")

                    Product(id, name, base64Image, costWithIva, amount, color)
                } else {
                    Log.d("FetchProductDetailsTask", "No product found with ID: $id")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("FetchProductDetailsTask", "Error fetching product details", e)
                null
            }
        }

        override fun onPostExecute(product: Product?) {
            super.onPostExecute(product)
            if (product != null) {
                // Bind product to UI
                binding.product = product
                binding.executePendingBindings()
            } else {
                // Handle case where product is not found
                binding.product = null
                // You might want to show a message or handle the error gracefully
                showError("Product details not found.")
            }
        }

        private fun showError(message: String) {
            // Example of handling error gracefully
            // You could use a Toast, a Snackbar, or update the UI to show an error message
            Toast.makeText(this@ShowProductDetailsActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    data class Product(
        val id: Int,
        val name: String,
        val base64Image: String?,
        val costWithIva: Double,
        val amount: Int,
        val color: String
    )
}
