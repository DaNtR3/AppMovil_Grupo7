package com.proyecto.sistemaventaspropat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.sistemaventaspropat.models.CartItem
import com.proyecto.sistemaventaspropat.databinding.ActivityViewholderCartBinding

class CartAdapter :
    ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ActivityViewholderCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
    }

    class CartViewHolder(private val binding: ActivityViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.cartItem = cartItem
            binding.executePendingBindings()
        }
    }

    private class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}