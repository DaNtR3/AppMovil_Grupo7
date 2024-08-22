package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto.sistemaventaspropat.adapters.CartAdapter
import com.proyecto.sistemaventaspropat.databinding.ActivityCartBinding
import com.proyecto.sistemaventaspropat.viewmodels.CartViewModel
import com.proyecto.sistemaventaspropat.viewmodels.MyApplication

class CartActivity : AppCompatActivity() {
    private lateinit var viewModel: CartViewModel
    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain CartViewModel from Application class
        val application = application as MyApplication
        viewModel = application.cartViewModel

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

            binding.btnBackHome.setOnClickListener {
                startActivity(Intent(this, HomePageActivity::class.java))
            }
        }
    }
}
