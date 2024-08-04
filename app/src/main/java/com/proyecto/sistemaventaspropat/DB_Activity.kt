package com.proyecto.sistemaventaspropat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import com.proyecto.sistemaventaspropat.databinding.ActivityProductsBinding
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException


class DBActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductsBinding

    private var connectSql = ConnectionSQLServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {


        }  //regresar
        binding.imageButton.setOnClickListener {
            binding.txtName.text.clear()
            binding.txtEmail.text.clear()
        }
    }




}