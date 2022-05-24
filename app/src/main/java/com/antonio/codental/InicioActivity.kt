package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InicioActivity : AppCompatActivity() {

    lateinit var btnResgistroPacientes : Button
    lateinit var btnGastosYEgresos : Button
    lateinit var btnMisPrecios : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))

        //Objeto para poder recibir lo que viene de la otra actividad
        val objIntent : Intent = intent

        //Recibo el nombre del Doctor de la activity de iniciar sesión
        val nombreDoctor : String? = objIntent.getStringExtra("nombreDoctor") // ya tengo el nombre del doctor
        title = "Bienvenido Dr. "+nombreDoctor // cambio el nombre del action bar con el mensaje de bienvenida.


        //Enlazo botón de Registro Pacientes
        btnResgistroPacientes = findViewById(R.id.btnResgistroPacientes)
        btnResgistroPacientes.setOnClickListener {
            val intent = Intent(this,PacientesActivity::class.java)
            startActivity(intent)
        }

        //Enlazo botón de Gastos y Egresos
        btnGastosYEgresos = findViewById(R.id.btnGastosYEgresos)
        btnGastosYEgresos.setOnClickListener {
            val intent = Intent(this,GastosYEgresosActivity::class.java)
            startActivity(intent)
        }

        //Enlazo botón de Mis Precios
        btnMisPrecios = findViewById(R.id.btnMisPrecios)
        btnMisPrecios.setOnClickListener {
            val intent = Intent(this,PreciosActivity::class.java)
            startActivity(intent)
        }

    }
}