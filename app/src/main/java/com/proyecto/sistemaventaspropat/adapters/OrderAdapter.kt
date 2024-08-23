package com.proyecto.sistemaventaspropat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.sistemaventaspropat.databinding.ActivityViewholderOrdersBinding
import com.proyecto.sistemaventaspropat.models.Order

class OrderAdapter(
    private val orders: List<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ActivityViewholderOrdersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order, clickListener: (Order) -> Unit) {
            binding.order = order
            binding.executePendingBindings()
            itemView.setOnClickListener { clickListener(order) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ActivityViewholderOrdersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order, onItemClick)
    }

    override fun getItemCount() = orders.size
}
