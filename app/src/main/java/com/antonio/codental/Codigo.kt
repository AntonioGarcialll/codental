package com.antonio.codental

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.antonio.codental.databinding.ActivityCodigoBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class Codigo : AppCompatActivity() {

    //Declaración de variables
    private lateinit var binding: ActivityCodigoBinding
    var arrayListDoctores = arrayListOf<Doctores>()
    var cantidadDoctores: Int? = null
    var copiaCantidadDoctores: Int? = null
    var mensaje = ""
    var bandera = false
    var indice = 0
    var salir = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityCodigoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Código de Seguridad"


        getDoctoresCount {
            if (arrayListDoctores.size <= 0) {
                binding.btnGuardar.text = "Guardar"
                mensaje =
                    "Para continuar, debe crear un código de seguridad de 4 dígitos por favor."
                binding.tvBienvenida.text = mensaje
                bandera = true
            } else {
                binding.btnGuardar.text = "Ingresar"
                var nombreDoctor = FirebaseAuth.getInstance().currentUser?.let { it.displayName }
                while (salir == false) {
                    //Ya hay doctores en la bdd y quiere crear una nueva cuenta
                    if (indice == arrayListDoctores.size) {
                        binding.btnGuardar.text = "Guardar"
                        mensaje =
                            "Para continuar, debe crear un código de seguridad de 4 dígitos por favor."
                        binding.tvBienvenida.text = mensaje
                        bandera = true
                        salir = true
                    } else {
                        if (FirebaseAuth.getInstance().currentUser?.let { it.uid }
                                .equals(arrayListDoctores.get(indice).miIdDoctor)) {
                            mensaje =
                                "!Hola Dr. " + nombreDoctor + "!\nIngrese su código de seguridad para continuar por favor."
                            binding.tvBienvenida.text = mensaje
                            bandera = false
                            salir = true
                        } else {
                            indice++
                        }
                    }
                }
            }
        }

        binding.btnGuardar.setOnClickListener {
            if (binding.etCodigo.text.toString().length < 4 || binding.etCodigo.text.toString().length > 4) {
                Toast.makeText(this, "El código debe ser de 4 dígitos", Toast.LENGTH_SHORT).show()
            } else {
                if (bandera == true) {
                    //Variable para la base de datos de Firebase
                    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                    val data = hashMapOf(
                        "nombre" to FirebaseAuth.getInstance().currentUser?.let { it.displayName },
                        "correo" to FirebaseAuth.getInstance().currentUser?.let { it.email },
                        "codigoSeguridad" to binding.etCodigo.text.toString(),
                        "miIdDoctor" to FirebaseAuth.getInstance().currentUser?.let { it.uid }
                    )

                    db.collection("doctores")
                        .document(FirebaseAuth.getInstance().currentUser?.let { it.uid }.toString())
                        .set(data)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(
                                this,
                                "Cuenta Creada Correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Ocurrió un error: $e", Toast.LENGTH_SHORT).show()
                        }
                    val i = Intent(
                        applicationContext,
                        InicioActivity::class.java
                    )        // Specify any activity here e.g. home or splash or login etc
                    startActivity(i)
                    finish()
                } else {
                    if (binding.etCodigo.text.toString()
                            .equals(arrayListDoctores.get(indice).codigoSeguridad)
                    ) {
                        val i = Intent(
                            applicationContext,
                            InicioActivity::class.java
                        )        // Specify any activity here e.g. home or splash or login etc
                        startActivity(i)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Código de seguridad incorrecto.\nIntente de nuevo por favor.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener {
            showLoadComponents(View.VISIBLE, View.GONE)
        }.addOnFailureListener {
        }
        super.onBackPressed()
    }

    private fun showLoadComponents(loading: Int, container: Int) {
        with(binding) {
            root.visibility = container
        }
    }

    fun getDoctoresCount(isFinish: () -> Unit = {}) {
        val db = Firebase.firestore
        db.collection("doctores")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val doctor = document.toObject<Doctores>()
                    doctor.miIdDoctor = document.id
                    arrayListDoctores.add(doctor)
                }
                isFinish()
            }
            .addOnFailureListener { exception ->
                isFinish()
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
    }
}