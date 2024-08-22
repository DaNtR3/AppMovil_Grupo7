package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.sistemaventaspropat.adapters.ProductAdapter
import com.proyecto.sistemaventaspropat.models.Product
import com.proyecto.sistemaventaspropat.databinding.ActivityShowProductsBinding
import com.proyecto.sistemaventaspropat.databinding.ActivityViewholderProductListBinding
import com.proyecto.sistemaventaspropat.viewmodels.CartViewModel
import com.proyecto.sistemaventaspropat.viewmodels.MyApplication

class ShowProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowProductsBinding
    private lateinit var productAdapter: ProductAdapter
    private val cartViewModel: CartViewModel
        get() = (application as MyApplication).cartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Execute background task to fetch products
        FetchProductsTask().execute()

        binding.linearLayoutInicio.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        binding.linearLayoutCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private inner class FetchProductsTask : AsyncTask<Void, Void, List<Product>>() {
        override fun doInBackground(vararg params: Void?): List<Product> {
            val products = mutableListOf<Product>()
            val query = """
                SELECT ID_Producto, Nombre, imagen_producto, CostoConIVA, Cantidad_Disponible, Colores
                FROM productos
            """
            try {
                val connection = ConnectionSQLServer().dbConn()
                val statement = connection?.prepareStatement(query)
                val resultSet = statement?.executeQuery()

                while (resultSet?.next() == true) {
                    val id = resultSet.getInt("ID_Producto")
                    val name = resultSet.getString("Nombre")
                    val base64Image = resultSet.getString("imagen_producto")
                    val costWithIva = resultSet.getDouble("CostoConIVA")
                    val amount = resultSet.getInt("Cantidad_Disponible")
                    val color = resultSet.getString("Colores")

                    products.add(Product(id, name, base64Image, costWithIva, amount, color))
                }

                resultSet?.close()
                statement?.close()
                connection?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return products
        }

        override fun onPostExecute(products: List<Product>) {
            super.onPostExecute(products)
            productAdapter = ProductAdapter(products) { product ->
                val intent =
                    Intent(this@ShowProductsActivity, ShowProductDetailsActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            }
            binding.recyclerviewPopularProducts.layoutManager =
                LinearLayoutManager(this@ShowProductsActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerviewPopularProducts.adapter = productAdapter
        }
    }



}
