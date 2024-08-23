package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.models.CartItem
import com.proyecto.sistemaventaspropat.models.Product
import com.proyecto.sistemaventaspropat.databinding.ActivityProductDetailsBinding
import com.proyecto.sistemaventaspropat.dbconnection.ConnectionSQLServer
import com.proyecto.sistemaventaspropat.viewmodels.CartViewModel
import com.proyecto.sistemaventaspropat.viewmodels.MyApplication

class ShowProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private val cartViewModel: CartViewModel
        get() = (application as MyApplication).cartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackHome.setOnClickListener {
            startActivity(Intent(this, ShowProductsActivity::class.java))
        }

        val productId = intent.getIntExtra("PRODUCT_ID", -1)

        if (productId != -1) {
            FetchProductDetailsTask().execute(productId)
        }

        binding.btnAddToCart.setOnClickListener {
            binding.product?.let { product ->
                addToCart(product)
            }
        }
    }

    private fun addToCart(product: Product) {
        val cartItem = CartItem(
            id = product.id,
            name = product.name,
            base64Image = product.base64Image,
            costWithIVA = product.costWithIva,
            costWithoutIva = product.costWithoutIva,
            quantity = 1
        )
        cartViewModel.addItemToCart(cartItem)
        Toast.makeText(this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
    }

    private inner class FetchProductDetailsTask : AsyncTask<Int, Void, Product?>() {
        override fun doInBackground(vararg params: Int?): Product? {
            val id = params.firstOrNull() ?: return null
            val query = """
                SELECT ID_Producto, Nombre, imagen_producto, CostoConIVA, Cantidad_Disponible, Colores, CostoSinIVA
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
                    val costWithoutIva = resultSet.getDouble("CostoSinIVA")

                    Product(id, name, base64Image, costWithIva, costWithoutIva, amount, color)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(product: Product?) {
            super.onPostExecute(product)
            if (product != null) {
                binding.product = product
                binding.executePendingBindings()
            } else {
                showError("No se pudo encontrar el producto")
            }
        }

        private fun showError(message: String) {
            Toast.makeText(this@ShowProductDetailsActivity, message, Toast.LENGTH_LONG).show()
        }
    }
}
