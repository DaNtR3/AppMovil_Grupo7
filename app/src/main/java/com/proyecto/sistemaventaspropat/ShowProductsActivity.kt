package com.proyecto.sistemaventaspropat

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto.sistemaventaspropat.databinding.ActivityShowProductsBinding
import com.proyecto.sistemaventaspropat.databinding.ActivityViewholderProductListBinding
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ShowProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowProductsBinding
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Execute background task to fetch products
        FetchProductsTask(this).execute()

        binding.linearLayoutInicio.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

    }

    private inner class FetchProductsTask(val context: Context) : AsyncTask<Void, Void, List<Product>>() {
        override fun doInBackground(vararg params: Void?): List<Product> {
            val products = mutableListOf<Product>()
            val query = """
                SELECT ID_Producto, Nombre, imagen_producto, CostoConIVA
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

                    products.add(Product(id, name, base64Image, costWithIva))
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
            productAdapter = ProductAdapter(products)
            binding.recyclerviewPopularProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerviewPopularProducts.adapter = productAdapter
        }
    }

    private class ProductAdapter(private val products: List<Product>) :
        RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        class ProductViewHolder(val binding: ActivityViewholderProductListBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val binding = ActivityViewholderProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = products[position]
            holder.binding.product = product
            holder.binding.executePendingBindings()
        }

        override fun getItemCount() = products.size
    }

    data class Product(
        val id: Int,
        val name: String,
        val base64Image: String?,
        val costWithIva: Double
    )
}
