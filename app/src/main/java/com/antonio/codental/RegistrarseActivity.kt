package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class RegistrarseActivity : AppCompatActivity() {

    //Se declaran los botones
    lateinit var btnIniciarSesion : Button
    lateinit var btnCrearCuenta : Button
    lateinit var nombre : EditText
    lateinit var correo : EditText
    lateinit var contrasena : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "CO. Dental"

        //Se inicializan los botones y los inputs
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)
        nombre = findViewById(R.id.etNombre)
        correo = findViewById(R.id.etCorreo)
        contrasena = findViewById(R.id.etContrasena)

        //Función para cuando se le de clic al boton de "Crear Cuenta" evaluar que los campos no estén vacíos
        btnCrearCuenta.setOnClickListener{
            if(nombre.text.isEmpty() || correo.text.isEmpty() || contrasena.text.isEmpty())
            {
                Toast.makeText(this, "Error, no debe dejar campos vacíos.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "Calis. Cuenta creada con éxito.", Toast.LENGTH_SHORT).show()
            }
        }

        //Función para cuando se le de clic al boton de "Iniciar Sesión" que abre la actividad del login
        btnIniciarSesion.setOnClickListener{
            val intent = Intent(this,IniciarSesionActivity::class.java)
            startActivity(intent)

        }

    }
}