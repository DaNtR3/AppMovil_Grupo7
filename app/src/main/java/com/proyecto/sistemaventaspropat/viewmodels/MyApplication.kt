package com.proyecto.sistemaventaspropat.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModelProvider

class MyApplication : Application() {

    // Lazy initialization of the CartViewModel
    val cartViewModel: CartViewModel by lazy {
        CartViewModel()
    }

    val userViewModel: UserViewModel by lazy {
        UserViewModel()
    }
}
