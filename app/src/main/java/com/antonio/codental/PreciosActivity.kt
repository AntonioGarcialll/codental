package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antonio.codental.databinding.ActivityAbonosBinding
import com.antonio.codental.databinding.ActivityPreciosBinding

//import kotlinx.android.synthetic.main.activity_pacientes.*

class PreciosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreciosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_precios)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Mis Precios"

        //Listener para el floatingActionButton de agregar
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, NuevoPrecioActivity::class.java)
            startActivity(intent)
        }
    }
}