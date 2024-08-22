package com.proyecto.sistemaventaspropat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyecto.sistemaventaspropat.models.CartItem

class CartViewModel : ViewModel() {

    // LiveData to observe cart items
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

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
        Log.d("CartViewModel", "Updated items: $updatedItems")
    }

    // Remove item from cart
    fun removeItemFromCart(itemId: Int) {
        val currentItems = _cartItems.value.orEmpty()
        val updatedItems = currentItems.filterNot { it.id == itemId }
        _cartItems.value = updatedItems
    }

    // Clear all items in the cart
    fun clearCart() {
        _cartItems.value = emptyList()
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
    }
}
