package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.antonio.codental.databinding.ActivityGastosYegresosBinding
import com.antonio.codental.databinding.ActivityPacientesBinding

//import kotlinx.android.synthetic.main.activity_pacientes.*

class GastosYEgresosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGastosYegresosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Cosas de binding
        binding = ActivityGastosYegresosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_gastos_yegresos)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Gastos y Egresos"

        //Listener para el floatingActionButton de agregar
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this,NuevoGastoYEgresoActivity::class.java)
            startActivity(intent)
        }


    }
}