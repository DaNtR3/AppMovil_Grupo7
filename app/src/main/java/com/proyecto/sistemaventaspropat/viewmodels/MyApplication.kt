package com.proyecto.sistemaventaspropat.viewmodels

import android.app.Application

class MyApplication : Application() {
    // Lazy initialization of the CartViewModel
    val cartViewModel: CartViewModel by lazy {
        CartViewModel()
    }
}
