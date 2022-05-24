package com.antonio.codental

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.antonio.codental.databinding.ActivityInfoTratamientoBinding
import com.bumptech.glide.Glide

class InfoTratamientoActivity : AppCompatActivity() {
    //Declaración de variables
    private lateinit var binding: ActivityInfoTratamientoBinding
    var idTratamiento : String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityInfoTratamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recibimos el tratamiento seleccionado por medio del intent extra
        val tratamientoRecibido = intent.getSerializableExtra("tratamientoEnviado") as Tratamiento

        //Obtengo el ID del Tratamiento al que se le dio clic
        idTratamiento = tratamientoRecibido.idTratamiento

        //Obtengo el saldoAtual del tratamiento


        //Barra
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "${tratamientoRecibido.nombreTratamiento}"

        //Llenamos los campos del xml con los datos que llegan
        binding.tvFecha.text = tratamientoRecibido.fecha
        binding.tvTratamiento.text = tratamientoRecibido.nombreTratamiento
        binding.tvCosto.text = tratamientoRecibido.costo
        Glide.with(this).load(tratamientoRecibido.fotos)
            .placeholder(R.drawable.ic_baseline_cloud_download_24)
            .error(R.drawable.ic_baseline_error_24).into(binding.imgTratamiento)

    }

    //Función para sobreescribir el menú por el que ya tiene los iconos de editar, eliminar y abonos
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tratamiento_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Switch para hacer acciones al dar clic a los iconos de editar, eliminar o abonos.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.abonos_item ->
            {
                //Se abre la activity de abonos
                val intent = Intent(this,AbonosActivity::class.java)
                intent.putExtra("idTratamiento",idTratamiento)
                intent.putExtra("tratamiento",binding.tvTratamiento.text.toString())
                intent.putExtra("costo",binding.tvCosto.text.toString().toDouble())
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}