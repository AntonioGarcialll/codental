package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class NuevoPrecioActivity : AppCompatActivity() {

    //Declaración de variables
    lateinit var servicio : EditText
    lateinit var precio : EditText
    lateinit var btnGuardar : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_precio)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Agregar Nuevo Servicio"

        //Enlazamos variables
        servicio = findViewById(R.id.etServicio)
        precio = findViewById(R.id.etPrecio)
        btnGuardar = findViewById(R.id.btnGuardar)

        //Listener para el clic al botón de guardar
        btnGuardar.setOnClickListener {
            if(servicio.text.isEmpty() || precio.text.isEmpty())
            {
                Toast.makeText(this, "No debe dejar campos vacíos", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "Servicio y Precio guardado correctamente (prueba)", Toast.LENGTH_SHORT).show()

                val i = Intent(
                    applicationContext,
                    PreciosActivity::class.java
                )        // Specify any activity here e.g. home or splash or login etc
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.putExtra("EXIT", true)
                startActivity(i)
                finish()
            }
        }



    }
}