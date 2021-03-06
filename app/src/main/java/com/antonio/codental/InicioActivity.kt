package com.antonio.codental

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

    var arrayListFotos = arrayListOf<FotosABorrar>()
    private var idDoctor: String? = null
    private var nombreDoctor: String? = null
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
                                this, "C??digo de error: ${error.errorCode}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }


    private fun setupAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {
                showLoadComponents(View.GONE, View.VISIBLE)
                //supportActionBar?.title = "Bienvenido Dr ${auth.currentUser!!.displayName}"
                idDoctor = auth.currentUser!!.uid
                nombreDoctor = auth.currentUser!!.displayName
                //supportActionBar?.title = "Bienvenido Dr. " + nombreDoctor
                binding.tvBienvenida.text = "Bienvenido Dr. " + nombreDoctor
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

    private fun showLoadComponents(loading: Int, container: Int) {
        with(binding) {
            pbBrand.visibility = loading
            tvWaitAMoment.visibility = loading
            root.visibility = container
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

        setupAuth()

        //Enlazo bot??n de Registro Pacientes
        binding.btnPacientes.setOnClickListener {
            val intent = Intent(this, PacientesActivity::class.java)
            /*intent.putExtra("idDoctor", idDoctor)
            intent.putExtra("nombreDoctor", nombreDoctor)*/
            startActivity(intent)
        }

        //Enlazo bot??n de Gastos y Egresos
        binding.btnGastosEgresos.setOnClickListener {
            val intent = Intent(this, GastosYEgresosActivity::class.java)
            startActivity(intent)
        }

        //Enlazo bot??n de Mis Precios
        binding.btnPrecios.setOnClickListener {
            val intent = Intent(this, PreciosActivity::class.java)
            startActivity(intent)
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

    //Funci??n para sobreescribir el men?? por el que ya tiene los iconos de editar, eliminar y abonos
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.exit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Switch para hacer acciones al dar clic a los iconos de editar, eliminar o abonos.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit -> signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    //M??todo para cuando cierre sesi??n
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
                //Toast.makeText(this, "s?? entr??", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Borr?? 1 documento", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {}
    }

    fun borrarImg(nombreImagen: String?) {
        val imageName = nombreImagen
        val storageRef =
            FirebaseStorage.getInstance().reference.child("tratamientosfolder/$imageName")
        storageRef.delete().addOnSuccessListener {
            //Toast.makeText(this, "Foto eliminada con ??xito", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            //Toast.makeText(this, "Fall??", Toast.LENGTH_SHORT).show()
        }
    }
}