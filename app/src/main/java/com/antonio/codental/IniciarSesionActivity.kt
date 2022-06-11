package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class IniciarSesionActivity : AppCompatActivity() {

    lateinit var btnIngresar : Button
    lateinit var correo : EditText
    lateinit var contrasena : EditText
    lateinit var btnCrearCuenta : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "CO. Dental"

        //Variables
        var nombreDoctor : String

        //Se inicializan los botones y los inputs
        btnIngresar = findViewById(R.id.btnIngresar)
        correo = findViewById(R.id.etCorreoIniciarSesion) //= "jairo@gmail.com"
        contrasena = findViewById(R.id.etContrasenaIniciarSesion) //= "12345"
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)

        //Función para cuando se le de clic al boton de "Ingresar" evaluar que los campos no estén vacíos
        btnIngresar.setOnClickListener{
            if(correo.text.isEmpty() || contrasena.text.isEmpty())
            {
                Toast.makeText(this, "Error, no debe dejar campos vacíos.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(correo.text.toString().equals("jairo@gmail.com") && contrasena.text.toString().equals("1234"))
                {
                    //Los datos son correctos y se abre la activity de inicio con el nombre del Dr
                    nombreDoctor = "Jairo"
                    val intent = Intent(this,InicioActivity::class.java)
                    intent.putExtra("nombreDoctor",nombreDoctor) // se le pasa el nombre del doctor a la otra activity
                    startActivity(intent)
                    finish()
                }
                else if(correo.text.toString().equals("fulanito@gmail.com") && contrasena.text.toString().equals("5678"))
                {
                    //Los datos son correctos y se abre la activity de inicio con el nombre del Dr
                    nombreDoctor = "Fulanito"
                    val intent = Intent(this,InicioActivity::class.java)
                    intent.putExtra("nombreDoctor",nombreDoctor) // se le pasa el nombre del doctor a la otra activity
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Error, datos no encontrados en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //Funcion para el clic del boton crearCuenta y abra la pantalla para crearla.
        btnCrearCuenta.setOnClickListener {
            val intent = Intent(this,RegistrarseActivity::class.java)
            startActivity(intent)
        }

    }
}