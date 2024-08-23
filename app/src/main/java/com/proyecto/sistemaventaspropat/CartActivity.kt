package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto.sistemaventaspropat.adapters.CartAdapter
import com.proyecto.sistemaventaspropat.databinding.ActivityCartBinding
import com.proyecto.sistemaventaspropat.dbconnection.ConnectionSQLServer
import com.proyecto.sistemaventaspropat.viewmodels.CartViewModel
import com.proyecto.sistemaventaspropat.viewmodels.MyApplication
import com.proyecto.sistemaventaspropat.viewmodels.UserViewModel
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CartActivity : AppCompatActivity() {
    private lateinit var viewModel: CartViewModel
    private lateinit var usermodel: UserViewModel

    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain ViewModels from Application class
        val application = application as MyApplication
        viewModel = application.cartViewModel
        usermodel = application.userViewModel

        // Set the UserViewModel in the binding
        binding.usermodel = usermodel


        val cartAdapter = CartAdapter()
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCart.adapter = cartAdapter

        viewModel.cartItems.observe(this) { items ->
            Log.d("CartActivity", "Items observed: $items")
            if (items.isEmpty()) {
                // Show empty cart message
                binding.txtEmptyCart.visibility = android.view.View.VISIBLE
                binding.recyclerViewCart.visibility = android.view.View.GONE
            } else {
                // Show cart items and hide empty cart message
                binding.txtEmptyCart.visibility = android.view.View.GONE
                binding.recyclerViewCart.visibility = android.view.View.VISIBLE
                cartAdapter.submitList(items)
            }
        }



        binding.btnBackHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        // Observe and update subtotal
        viewModel.subtotal.observe(this) { subtotal ->
            binding.txtSubtotal.text = "Subtotal: $subtotal"
        }

        // Observe and update IVA
        viewModel.iva.observe(this) { iva ->
            binding.txtTotalIva.text = "IVA: $iva"
        }

        // Observe and update shipping cost
        viewModel.shippingCost.observe(this) { shippingCost ->
            binding.txtDelivery.text = "Envío: $shippingCost"
        }

        // Observe and update total
        viewModel.total.observe(this) { total ->
            binding.txtTotal.text = "Total: $total"
        }

        // Handle back button click
        binding.btnBackHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        binding.btnPay.setOnClickListener {
            processOrder()
        }

    }

    /* Test function
    private fun userdetails() {
        val application_user = application as MyApplication
        usermodel = application_user.userViewModel

        usermodel.user.observe(this) { user ->
            Log.d("CartActivity", "Users observed: $user")
        }
    }*/

    private fun processOrder() {
        // Check if the cart is empty
        val cartItems = viewModel.cartItems.value
        if (cartItems.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "El carrito está vacío. No se puede procesar la orden.",
                Toast.LENGTH_SHORT
            ).show()
            return // Exit early if the cart is empty
        }

        val user = usermodel.user.value ?: return
        val connection = ConnectionSQLServer()
        val dbConn = connection.dbConn() ?: return

        try {
            dbConn.autoCommit = false

            val currentDateTime = java.sql.Timestamp(System.currentTimeMillis())
            val shippingDate = currentDateTime.toString() // Set default or computed value
            val shippingMethod = "Entrega estandar" // Set a default method or computed value
            val trackingNumber = "TRACK${System.currentTimeMillis()}" // Generate a dummy tracking number
            val shippingStatus = "En proceso" // Default status

            // Insert into Pedidos
            val insertPedidoQuery = """
            INSERT INTO Pedidos (ClienteID, Total, FechaPedido, Estado)
            VALUES (?, ?, GETDATE(), ?);
        """
            val insertPedidoStmt: PreparedStatement =
                dbConn.prepareStatement(insertPedidoQuery, PreparedStatement.RETURN_GENERATED_KEYS)
            insertPedidoStmt.setInt(1, user.id)
            insertPedidoStmt.setDouble(2, viewModel.total.value ?: 0.0)
            insertPedidoStmt.setString(3, "Pendiente") // or fetch from UI if available
            insertPedidoStmt.executeUpdate()

            val generatedKeys: ResultSet = insertPedidoStmt.generatedKeys
            val pedidoID = if (generatedKeys.next()) generatedKeys.getInt(1) else return

            // Insert into Envios
            val insertEnvioQuery = """
            INSERT INTO Envios (PedidoID, FechaEnvio, MetodoEnvio, NumeroGuia, EstadoEnvio, DireccionEnvio)
            VALUES (?, ?, ?, ?, ?, ?);
        """
            val insertEnvioStmt: PreparedStatement = dbConn.prepareStatement(insertEnvioQuery)
            insertEnvioStmt.setInt(1, pedidoID)
            insertEnvioStmt.setString(2, shippingDate) // Use generated or default value
            insertEnvioStmt.setString(3, shippingMethod) // Use generated or default value
            insertEnvioStmt.setString(4, trackingNumber) // Use generated value
            insertEnvioStmt.setString(5, shippingStatus) // Use default value
            insertEnvioStmt.setString(6, user.address) // Use user's address
            insertEnvioStmt.executeUpdate()

            // Insert into DetallesPedido
            val insertDetalleQuery = """
            INSERT INTO DetallesPedido (PedidoID, ID_Producto, Cantidad, PrecioUnitario)
            VALUES (?, ?, ?, ?);
        """
            val insertDetalleStmt: PreparedStatement =
                dbConn.prepareStatement(insertDetalleQuery)
            viewModel.cartItems.value?.forEach { item ->
                insertDetalleStmt.setInt(1, pedidoID)
                insertDetalleStmt.setInt(2, item.id)
                insertDetalleStmt.setInt(3, item.quantity)
                insertDetalleStmt.setDouble(4, item.costWithIVA)
                insertDetalleStmt.addBatch()
            }
            insertDetalleStmt.executeBatch()

            // Commit transaction
            dbConn.commit()

            // Clear the cart
            viewModel.clearCart()

            // Navigate to HomePageActivity after successful order processing
            startActivity(Intent(this, HomePageActivity::class.java))
            Toast.makeText(this, "Orden procesada correctamente", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            try {
                dbConn.rollback()
            } catch (rollbackException: SQLException) {
                rollbackException.printStackTrace()
            }
            e.printStackTrace()
            Toast.makeText(this, "Error al procesar la orden: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            dbConn.autoCommit = true
            dbConn.close()
        }
    }

}





