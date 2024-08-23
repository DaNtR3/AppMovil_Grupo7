package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto.sistemaventaspropat.adapters.OrderAdapter
import com.proyecto.sistemaventaspropat.databinding.ActivityShowOrdersBinding
import com.proyecto.sistemaventaspropat.dbconnection.ConnectionSQLServer
import com.proyecto.sistemaventaspropat.models.Order
import com.proyecto.sistemaventaspropat.models.OrderProduct

class ShowOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowOrdersBinding
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Execute background task to fetch orders
        FetchOrdersTask().execute()

        // Set up bottom app bar actions
        binding.linearLayoutInicio.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        binding.linearLayoutCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private inner class FetchOrdersTask : AsyncTask<Void, Void, List<Order>>() {
        override fun doInBackground(vararg params: Void?): List<Order> {
            val orders = mutableListOf<Order>()
            val orderMap = mutableMapOf<Int, Order>()
            val productMap = mutableMapOf<Int, MutableList<OrderProduct>>()

            val query = """
            SELECT
                u.user_id,
                u.firstname,
                u.lastname,
                u.email,
                u.address_details,
                u.phone,
                p.PedidoID,
                p.FechaPedido,
                p.Estado,
                p.Total,
                e.FechaEnvio,
                e.MetodoEnvio,
                e.NumeroGuia,
                e.EstadoEnvio,
                e.DireccionEnvio,
                dp.ID_Producto,
                dp.Cantidad,
                dp.PrecioUnitario,
                dp.TotalProducto AS ProductTotal
            FROM
                users u
                INNER JOIN Pedidos p ON u.user_id = p.ClienteID
                INNER JOIN Envios e ON p.PedidoID = e.PedidoID
                INNER JOIN DetallesPedido dp ON p.PedidoID = dp.PedidoID
            ORDER BY
                p.FechaPedido DESC;
            """

            try {
                val connection = ConnectionSQLServer().dbConn()
                val statement = connection?.prepareStatement(query)
                val resultSet = statement?.executeQuery()

                while (resultSet?.next() == true) {
                    val userId = resultSet.getInt("user_id")
                    val firstname = resultSet.getString("firstname")
                    val lastname = resultSet.getString("lastname")
                    val email = resultSet.getString("email")
                    val address = resultSet.getString("address_details")
                    val phone = resultSet.getString("phone")
                    val pedidoId = resultSet.getInt("PedidoID")
                    val fechaPedido = resultSet.getString("FechaPedido")
                    val estado = resultSet.getString("Estado")
                    val total = resultSet.getDouble("Total")
                    val fechaEnvio = resultSet.getString("FechaEnvio")
                    val metodoEnvio = resultSet.getString("MetodoEnvio")
                    val numeroGuia = resultSet.getString("NumeroGuia")
                    val estadoEnvio = resultSet.getString("EstadoEnvio")
                    val direccionEnvio = resultSet.getString("DireccionEnvio")
                    val productId = resultSet.getInt("ID_Producto")
                    val quantity = resultSet.getInt("Cantidad")
                    val pricePerUnit = resultSet.getDouble("PrecioUnitario")
                    val productTotal = resultSet.getDouble("ProductTotal")

                    // Create or update order in the map
                    val order = orderMap.getOrPut(pedidoId) {
                        Order(
                            userId,
                            firstname,
                            lastname,
                            email,
                            address,
                            phone,
                            pedidoId,
                            fechaPedido,
                            estado,
                            total,
                            fechaEnvio,
                            metodoEnvio,
                            numeroGuia,
                            estadoEnvio,
                            direccionEnvio,
                            listOf()
                        )
                    }

                    // Add product details to the product map
                    val product = OrderProduct(productId, quantity, pricePerUnit, productTotal)
                    productMap.computeIfAbsent(pedidoId) { mutableListOf() }.add(product)
                }

                resultSet?.close()
                statement?.close()
                connection?.close()

                // Combine orders and products
                return orderMap.values.map { order ->
                    order.copy(productos = productMap[order.pedidoId] ?: emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return emptyList()
            }
        }

        override fun onPostExecute(orders: List<Order>) {
            super.onPostExecute(orders)
            orderAdapter = OrderAdapter(orders) { order ->
                val intent = Intent(this@ShowOrdersActivity, ShowOrderDetailsActivity::class.java).apply {
                    putExtra("ORDER_ID", order.pedidoId)
                }
                startActivity(intent)
            }
            binding.recyclerviewOrders.layoutManager = LinearLayoutManager(this@ShowOrdersActivity)
            binding.recyclerviewOrders.adapter = orderAdapter
        }
    }
}
