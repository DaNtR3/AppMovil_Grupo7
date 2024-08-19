package com.proyecto.sistemaventaspropat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.sistemaventaspropat.databinding.ActivityHomeBinding

class HomePageActivity : AppCompatActivity(){

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtSignout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.linearLayoutInicio.setOnClickListener{
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        binding.linearlayoutAdminProductos.setOnClickListener{
            startActivity(Intent(this, CRUDProductsActivity::class.java))
        }
        binding.linearlayoutAdminClients.setOnClickListener{
            startActivity(Intent(this, CRUDClientsActivity::class.java))
        }

        binding.linearlayoutAdminPasswordreset.setOnClickListener {
            startActivity(Intent(this, PasswordResetActivity::class.java))
        }

        binding.linearlayoutAboutUs.setOnClickListener{
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        binding.linearlayoutMission.setOnClickListener{
            startActivity(Intent(this, MissionActivity::class.java))
        }

        binding.linearlayoutVision.setOnClickListener{
            startActivity(Intent(this, VisionActivity::class.java))
        }

        binding.linearlayoutContacts.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        binding.linearlayoutProductos.setOnClickListener {
            startActivity(Intent(this, ShowProductsActivity::class.java))
        }
    }
}