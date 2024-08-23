package com.proyecto.sistemaventaspropat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyecto.sistemaventaspropat.models.CartItem
import kotlin.math.round

class CartViewModel : ViewModel() {

    // LiveData to observe cart items
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    // LiveData for calculated values
    private val _subtotal = MutableLiveData<Double>(0.0)
    val subtotal: LiveData<Double> get() = _subtotal

    private val _iva = MutableLiveData<Double>(0.0)
    val iva: LiveData<Double> get() = _iva

    private val _shippingCost = MutableLiveData<Double>(2500.0) // Default shipping cost
    val shippingCost: LiveData<Double> get() = _shippingCost

    private val _total = MutableLiveData<Double>(0.0)
    val total: LiveData<Double> get() = _total

    // Add item to cart
    fun addItemToCart(item: CartItem) {
        val currentItems = _cartItems.value.orEmpty()
        val updatedItems = currentItems.toMutableList().apply {
            val index = indexOfFirst { it.id == item.id }
            if (index != -1) {
                val existingItem = get(index)
                set(index, existingItem.copy(quantity = existingItem.quantity + item.quantity))
            } else {
                add(item)
            }
        }
        _cartItems.value = updatedItems
        calculateTotals()
        Log.d("CartViewModel", "Updated items: $updatedItems")
    }

    // Remove item from cart
    fun removeItemFromCart(itemId: Int) {
        val currentItems = _cartItems.value.orEmpty()
        val updatedItems = currentItems.filterNot { it.id == itemId }
        _cartItems.value = updatedItems
        calculateTotals()
    }

    // Clear all items in the cart
    fun clearCart() {
        _cartItems.value = emptyList()
        calculateTotals()
    }

    // Update quantity of a specific item
    fun updateItemQuantity(itemId: Int, newQuantity: Int) {
        val currentItems = _cartItems.value.orEmpty()
        val updatedItems = currentItems.toMutableList().apply {
            val index = indexOfFirst { it.id == itemId }
            if (index != -1) {
                val existingItem = get(index)
                if (newQuantity > 0) {
                    set(index, existingItem.copy(quantity = newQuantity))
                } else {
                    removeAt(index)
                }
            }
        }
        _cartItems.value = updatedItems
        calculateTotals()
    }

    // Calculate totals
    private fun calculateTotals() {
        val items = _cartItems.value.orEmpty()
        val subtotalValue = items.sumOf { it.costWithoutIva * it.quantity }
        val ivaValue = subtotalValue * 0.13
        val shippingCostValue = _shippingCost.value ?: 0.0
        val totalValue = subtotalValue + ivaValue + shippingCostValue

        _subtotal.value = round(subtotalValue * 100) / 100
        _iva.value = round(ivaValue * 100) / 100
        _total.value = round(totalValue * 100) / 100
    }
}
