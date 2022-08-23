package com.antonio.codental

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.antonio.codental.databinding.ActivityInicioBinding
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class InicioActivity : AppCompatActivity() {

    private var mainMenu: Menu? = null
    var arrayListDoctores = arrayListOf<Doctores>()
    val options = arrayOf(
        "SELECCIONE UNA OPCIÓN",
        "● Cambiar nombre de usuario",
        "● Cambiar código de seguridad",
        "● Cerrar sesión"
    )
    var arrayListFotos = arrayListOf<FotosABorrar>()
    private var idDoctor: String? = null
    private var nombreDoctor: String? = null
    private var codigoSeg: String? = null
    private lateinit var binding: ActivityInicioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val response = IdpResponse.fromResultIntent(result.data)

            if (result.resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val i = Intent(
                        applicationContext,
                        Codigo::class.java
                    )
                    startActivity(i)
                    finish()
                }
            } else {
                if (response == null) {
                    Toast.makeText(this, "Hasta pronto", Toast.LENGTH_SHORT).show()
                    finishAndRemoveTask()
                } else {
                    response.error?.let { error ->
                        if (error.errorCode == ErrorCodes.NO_NETWORK) {
                            Toast.makeText(this, "Sin red", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                this, "Código de error: ${error.errorCode}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }


    private fun showLoadComponents(loading: Int, container: Int) {
        with(binding) {
            pbBrand.visibility = loading
            tvWaitAMoment.visibility = loading
            root.visibility = container
        }
    }

    fun getDatos(isFinish: () -> Unit = {}) {
        val db = Firebase.firestore
        db.collection("doctores")
            .whereEqualTo("miIdDoctor", FirebaseAuth.getInstance().currentUser?.let { it.uid })
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
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Borro las fotos que se quedaron en Storage y los documentos de Firestore
        borarImagenesDeStorage {
            arrayListFotos.forEach {
                //Se borran las fotos de Storage
                borrarImg(it.foto1)
                borrarImg(it.foto2)

                //Se borra el documento en Firestore
                borrarDocumentosDefotos(it.idFotosABorrar.toString())
            }
        }

        getDatos {
            arrayListDoctores.forEach {
                nombreDoctor = it.nombre
                codigoSeg = it.codigoSeguridad
                binding.tvBienvenida.text = "Bienvenido Dr. " + nombreDoctor
            }
        }

        setupAuth()


        //Enlazo botón de Registro Pacientes
        binding.btnPacientes.setOnClickListener {
            val intent = Intent(this, PacientesActivity::class.java)
            intent.putExtra("nombreDoctor", nombreDoctor)
            startActivity(intent)
        }

        //Enlazo botón de Gastos y Egresos
        binding.btnGastosEgresos.setOnClickListener {
            val intent = Intent(this, GastosYEgresosActivity::class.java)
            startActivity(intent)
        }

        //Enlazo botón de Mis Precios
        binding.btnPrecios.setOnClickListener {
            val intent = Intent(this, PreciosActivity::class.java)
            startActivity(intent)
        }

        binding.btnGuardar.setOnClickListener {
            if (binding.etNuevoNombre.text!!.isEmpty()) {
                Toast.makeText(this, "No debe dejar campos vacíos", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.etNuevoNombre.text.toString().equals(nombreDoctor)) {
                    Toast.makeText(this, "No realizó cambios en el nombre", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val db = Firebase.firestore
                    db.collection("doctores")
                        .document(FirebaseAuth.getInstance().currentUser?.let { it.uid }!!).update(
                        "nombre", binding.etNuevoNombre.text.toString()
                    )
                    Toast.makeText(this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT)
                        .show()
                    getDatos {
                        arrayListDoctores.forEach {
                            nombreDoctor = it.nombre
                            binding.tvBienvenida.text = "Bienvenido Dr. " + nombreDoctor
                        }
                    }
                    prenderInicioUI()
                }
            }
        }

        binding.btnGuardarCodSeg.setOnClickListener {
            if (binding.etNuevoCodSeg.text!!.isEmpty()) {
                Toast.makeText(this, "No debe dejar campos vacíos", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.etNuevoCodSeg.text.toString().equals(codigoSeg)) {
                    Toast.makeText(
                        this,
                        "No realizó cambios en el código de seguridad",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val db = Firebase.firestore
                    db.collection("doctores")
                        .document(FirebaseAuth.getInstance().currentUser?.let { it.uid }!!).update(
                        "codigoSeguridad", binding.etNuevoCodSeg.text.toString()
                    )
                    Toast.makeText(
                        this,
                        "Código de seguridad actualizado correctamente",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    getDatos {
                        arrayListDoctores.forEach {
                            nombreDoctor = it.nombre
                            binding.tvBienvenida.text = "Bienvenido Dr. " + nombreDoctor
                        }
                    }
                    prenderInicioParaCodigoUI()
                }
            }
        }

        binding.tvRegresar.setOnClickListener {
            getDatos {
                arrayListDoctores.forEach {
                    nombreDoctor = it.nombre
                    binding.tvBienvenida.text = "Bienvenido Dr. " + nombreDoctor
                }
            }
            prenderInicioUI()
        }

        binding.tvRegresarCodSeg.setOnClickListener {
            getDatos {
                arrayListDoctores.forEach {
                    nombreDoctor = it.nombre
                    binding.tvBienvenida.text = "Bienvenido Dr. " + nombreDoctor
                }
            }
            prenderInicioParaCodigoUI()
        }
    }

    private fun setupAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {
                showLoadComponents(View.GONE, View.VISIBLE)
                //supportActionBar?.title = "Bienvenido Dr ${auth.currentUser!!.displayName}"
                idDoctor = auth.currentUser!!.uid
            } else {
                val providers = listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )
                val loginView = AuthMethodPickerLayout.Builder(R.layout.login)
                    .setEmailButtonId(R.id.btn_email).setGoogleButtonId(R.id.btn_google)
                    .build()
                authLauncher.launch(
                    AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers).setIsSmartLockEnabled(false)
                        .setAuthMethodPickerLayout(loginView)
                        .setTheme(R.style.Theme_CodentalNoActionBar).setLogo(R.drawable.logo)
                        .build()
                )
            }
        }
    }

    fun activarSpinner() {
        //Código para el spinner de opciones en el menú
        binding.spinner.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {

                } else if (p2 == 1) {
                    apagarInicioUI()
                    mainMenu?.findItem(R.id.menu_options)?.isVisible = false

                    //Toast.makeText(this@InicioActivity, "Nombre", Toast.LENGTH_SHORT).show()
                } else if (p2 == 2) {
                    apagarInicioParaCodigoUI()
                    mainMenu?.findItem(R.id.menu_options)?.isVisible = false
                    //Toast.makeText(this@InicioActivity, "Código", Toast.LENGTH_SHORT).show()
                } else if (p2 == 3) {
                    //Toast.makeText(this@InicioActivity, "Cerrar sesión", Toast.LENGTH_SHORT).show()
                    signOut()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun apagarInicioUI() {
        with(binding) {
            //Apagamos
            tvBienvenida.visibility = View.GONE
            btnPacientes.visibility = View.GONE
            btnGastosEgresos.visibility = View.GONE
            btnPrecios.visibility = View.GONE

            //Prendemos
            tvNuevoNombre.visibility = View.VISIBLE
            etNuevoNombre.visibility = View.VISIBLE
            etNuevoNombre.setText(nombreDoctor)
            nuevoNombre.visibility = View.VISIBLE
            btnGuardar.visibility = View.VISIBLE
            tvRegresar.visibility = View.VISIBLE
        }
    }

    private fun prenderInicioUI() {
        with(binding) {
            //Apagamos
            tvNuevoNombre.visibility = View.GONE
            etNuevoNombre.visibility = View.GONE
            nuevoNombre.visibility = View.GONE
            btnGuardar.visibility = View.GONE
            tvRegresar.visibility = View.GONE

            //Prendemos
            tvBienvenida.visibility = View.VISIBLE
            btnPacientes.visibility = View.VISIBLE
            btnGastosEgresos.visibility = View.VISIBLE
            btnPrecios.visibility = View.VISIBLE
            mainMenu?.findItem(R.id.menu_options)?.isVisible = true
        }
    }

    private fun apagarInicioParaCodigoUI() {
        with(binding) {
            //Apagamos
            tvBienvenida.visibility = View.GONE
            btnPacientes.visibility = View.GONE
            btnGastosEgresos.visibility = View.GONE
            btnPrecios.visibility = View.GONE
            imageView.visibility = View.GONE

            //Prendemos
            tvNuevoCodSeg.visibility = View.VISIBLE
            etNuevoCodSeg.visibility = View.VISIBLE
            nuevoCodSeg.visibility = View.VISIBLE
            btnGuardarCodSeg.visibility = View.VISIBLE
            imageViewCodSeg.visibility = View.VISIBLE
            tvRegresarCodSeg.visibility = View.VISIBLE
        }
    }


    private fun prenderInicioParaCodigoUI() {
        with(binding) {
            //Apagamos
            tvNuevoCodSeg.visibility = View.GONE
            etNuevoCodSeg.visibility = View.GONE
            nuevoCodSeg.visibility = View.GONE
            btnGuardarCodSeg.visibility = View.GONE
            imageViewCodSeg.visibility = View.GONE
            tvRegresarCodSeg.visibility = View.GONE

            //Prendemos
            tvBienvenida.visibility = View.VISIBLE
            btnPacientes.visibility = View.VISIBLE
            btnGastosEgresos.visibility = View.VISIBLE
            btnPrecios.visibility = View.VISIBLE
            imageView.visibility = View.VISIBLE
            mainMenu?.findItem(R.id.menu_options)?.isVisible = true
        }


    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    //Función para sobreescribir el menú por el que ya tiene los iconos de editar, eliminar y abonos
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        mainMenu = menu
        return super.onCreateOptionsMenu(menu)
    }


    //Switch para hacer acciones al dar clic a los iconos de editar, eliminar o abonos.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_options -> {
                activarSpinner()
                binding.spinner.performClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Método para cuando cierre sesión
    private fun signOut() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.sign_off))
            .setMessage(getString(R.string.are_you_sure_to_take_this_action))
            .setPositiveButton(getString(R.string.sign_off)) { _, _ ->
                AuthUI.getInstance().signOut(this).addOnSuccessListener {
                    showLoadComponents(View.VISIBLE, View.GONE)
                    Toast.makeText(
                        this,
                        getString(R.string.you_have_logged_out),
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    Toast.makeText(this, getString(R.string.failed_to_log_out), Toast.LENGTH_SHORT)
                        .show()
                }
            }.setNegativeButton(getString(R.string.cancel), null).show()
    }

    fun borarImagenesDeStorage(isFinish: () -> Unit = {}) {
        val db = Firebase.firestore
        db.collection("fotosABorrar")
            .get()
            .addOnSuccessListener { result ->
                //Toast.makeText(this, "sí entré", Toast.LENGTH_SHORT).show()
                for (document in result) {
                    val foto = document.toObject<FotosABorrar>()
                    foto.idFotosABorrar = document.id
                    arrayListFotos.add(foto)
                }
                isFinish()
            }
            .addOnFailureListener { exception ->
                isFinish()

            }
    }

    fun borrarDocumentosDefotos(idDocumento: String) {
        val db = Firebase.firestore
        db.collection("fotosABorrar").document(idDocumento)
            .delete()
            .addOnSuccessListener {
                //Toast.makeText(this, "Borré 1 documento", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {}
    }

    fun borrarImg(nombreImagen: String?) {
        val imageName = nombreImagen
        val storageRef =
            FirebaseStorage.getInstance().reference.child("tratamientosfolder/$imageName")
        storageRef.delete().addOnSuccessListener {
            //Toast.makeText(this, "Foto eliminada con éxito", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            //Toast.makeText(this, "Falló", Toast.LENGTH_SHORT).show()
        }
    }

}