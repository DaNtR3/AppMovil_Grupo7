package com.proyecto.sistemaventaspropat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.sistemaventaspropat.databinding.ActivityViewholderProductListBinding
import com.proyecto.sistemaventaspropat.models.Product

//-------------------------------Product adapter START-------------------------------

class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ActivityViewholderProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, clickListener: (Product) -> Unit) {
            binding.product = product
            binding.executePendingBindings()
            itemView.setOnClickListener { clickListener(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ActivityViewholderProductListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product, onItemClick)
    }

    override fun getItemCount() = products.size
}

//-------------------------------Product adapter END-------------------------------