package com.antonio.codental

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NuevoPrecioActivity : AppCompatActivity() {

    //Declaración de variables
    lateinit var servicio: EditText
    lateinit var precio: EditText
    lateinit var btnGuardar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_precio)


        //Obtengo los datos del precio que me mandan
        val objIntent: Intent = intent
        var tvServicioObtenido = objIntent.getStringExtra("tvServicio")
        var tvPrecioObtenido = objIntent.getStringExtra("tvPrecio")
        var actualizarObtenido = objIntent.getStringExtra("actualizar")
        var miIdPrecioObtenido = objIntent.getStringExtra("miIdPrecio")


        //Enlazamos variables
        servicio = findViewById(R.id.etServicio)
        precio = findViewById(R.id.etPrecio)
        btnGuardar = findViewById(R.id.btnGuardar)

        if (actualizarObtenido == "actualizame") {
            supportActionBar!!.title = "Editar Servicio"
            //Asigna los valores a las cajas de texto para que puedan ser modificadas
            servicio.setText(tvServicioObtenido)
            precio.setText(tvPrecioObtenido)
        } else {
            supportActionBar!!.title = "Agregar Servicio"
        }

        //Variable para la base de datos de Firebase
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        //Listener para el clic al botón de guardar
        btnGuardar.setOnClickListener {
            if (servicio.text.isEmpty() || precio.text.isEmpty()) {
                Toast.makeText(this, "No debe dejar campos vacíos", Toast.LENGTH_SHORT).show()
            } else {
                //Se quiere actualizar los datos del paciente
                if (actualizarObtenido == "actualizame") {
                    db.collection("servicios").document(miIdPrecioObtenido!!).update(
                        "servicio", servicio.text.toString(),
                        "precio", precio.text.toString()
                    )
                    Toast.makeText(this, "Datos del Servicio Actualizados", Toast.LENGTH_SHORT)
                        .show()
                    //Se cierra actividad y regresa a la activity de la lista de los pacientes
                    val i = Intent(applicationContext, PreciosActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    //No se le dió clic al botón del lápiz para editar, o sea que se requiere agregar un nuevo precio.
                    //Se guarda el nuevo precio en la base de datos
                    val data = hashMapOf(
                        "servicio" to servicio.text.toString(),
                        "precio" to precio.text.toString(),
                        "idServicio" to "a",
                        "idDoctor" to FirebaseAuth.getInstance().currentUser?.let { it.uid }
                    )
                    db.collection("servicios").add(data)
                        .addOnSuccessListener { documentReference ->

                            var miId = documentReference.id

                            db.collection("servicios").document(miId)
                                .update("idServicio", miId)
                            Toast.makeText(
                                this,
                                "Servicio y Precio Agregado Correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = Intent(
                                applicationContext,
                                PreciosActivity::class.java
                            )        // Specify any activity here e.g. home or splash or login etc
                            startActivity(i)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Ocurrió un error: $e", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}