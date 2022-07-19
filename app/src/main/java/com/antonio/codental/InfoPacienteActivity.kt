package com.antonio.codental

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.antonio.codental.databinding.ActivityInfoPacienteBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class InfoPacienteActivity : AppCompatActivity() {

    private val idsTratamientos = arrayListOf<String>()
    private val fotos1Tratamientos = arrayListOf<String>()
    private val fotos2Tratamientos = arrayListOf<String>()
    private val idsAbonos = arrayListOf<String>()
    var objeto: InfoTratamientoActivity = InfoTratamientoActivity()
    private lateinit var binding: ActivityInfoPacienteBinding
    lateinit var miIdPaciente: String
    private lateinit var db: FirebaseFirestore
    var midb = FirebaseFirestore.getInstance()
    private lateinit var tempArrayList: ArrayList<Tratamiento>
    lateinit var tratamientoss: MutableList<Tratamiento>
    lateinit var abonos: MutableList<Abonos>
    var veces = 0
    var mensajeBorrar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityInfoPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        abonos = mutableListOf()

        tratamientoss = mutableListOf()
        tempArrayList = arrayListOf<Tratamiento>()

        //Recibimos el paciente seleccionado por medio del intent extra
        val pacienteRecibido = intent.getSerializableExtra("contactoEnviado") as Paciente

        //Le asignamos los valores del paciente seleccionado a los campos correspondientes de esta activity
        binding.tvFecha.text = pacienteRecibido.fecha
        binding.tvPaciente.text = pacienteRecibido.paciente
        binding.tvDoctor.text = pacienteRecibido.doctor
        miIdPaciente = pacienteRecibido.idPaciente!! //id del paciente al que se le dió clic

        getTratamientos {
            idsTratamientos.forEach { id ->
                obtenerAbonos(id)
            }
        }

    }

    //Función para sobreescribir el menú por el que ya tiene los iconos de editar, eliminar y abonos
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.paciente_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Switch para hacer acciones al dar clic a los iconos de editar, eliminar o abonos.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_item -> {
                //Se abre la activity para editar los datos, y se le pasa el paciente por el extra.
                val intent = Intent(this, NuevoPacienteActivity::class.java)
                intent.putExtra("tvFecha", binding.tvFecha.text.toString())
                intent.putExtra("tvPaciente", binding.tvPaciente.text.toString())
                intent.putExtra("tvDoctor", binding.tvDoctor.text.toString())
                intent.putExtra("actualizar", "actualizame")
                intent.putExtra("miIdPaciente", miIdPaciente)
                startActivity(intent)
            }

            R.id.delete_item -> {
                if (veces == 0) {
                    mensajeBorrar =
                        "Está a punto de eliminar un paciente.\nPor seguridad, vuelva a dar clic al icono de eliminar por favor."

                } else {
                    mensajeBorrar =
                        "¿Está seguro que desea eliminar este paciente?\nSe borrarán todos sus datos, entre ellos sus tratamientos y abonos de manera permanente"
                }
                //AlerDialog para eliminar paciente
                AlertDialog.Builder(this).apply {
                    setTitle("Eliminar Paciente")
                    setMessage(mensajeBorrar)
                    setPositiveButton("Aceptar") { _: DialogInterface, _: Int ->
                        if (veces > 0) {
                            //Se elimina el paciente, tratamientos y abonos
                            enableUI(false)
                            val db = Firebase.firestore
                            val pacienteRef = db.collection("pacientes").document(miIdPaciente)
                            val tratamientosRefList = mutableListOf<DocumentReference>()
                            val abonosRefList = mutableListOf<DocumentReference>()

                            //Se eliminan las fotos 1 del tratamiento
                            fotos1Tratamientos.forEach { foto1 ->
                                borrarImg(foto1)
                            }

                            //Se eliminan las fotos 2 del tratamiento
                            fotos2Tratamientos.forEach { foto2 ->
                                borrarImg(foto2)
                            }

                            idsTratamientos.forEach { traId ->
                                val miRef = db.collection("tratamientos").document(traId)
                                tratamientosRefList.add(miRef)
                                idsAbonos.forEach { abonoId ->
                                    val miRefAbonos =
                                        db.collection("tratamientos").document(traId)
                                            .collection("abonos")
                                            .document(abonoId)
                                    abonosRefList.add(miRefAbonos)
                                }
                            }
                            db.runBatch { batch ->
                                tratamientosRefList.forEach {
                                    batch.delete(it)
                                }
                                abonosRefList.forEach {
                                    batch.delete(it)
                                }
                                batch.delete(pacienteRef)
                            }.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this@InfoPacienteActivity,
                                        "Paciente Eliminado Correctamente",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    val i = Intent(
                                        applicationContext,
                                        PacientesActivity::class.java
                                    )        // Specify any activity here e.g. home or splash or login etc
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    i.putExtra("EXIT", true)
                                    startActivity(i)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@InfoPacienteActivity,
                                        "Ocurrió Un Error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val i = Intent(
                                        applicationContext,
                                        PacientesActivity::class.java
                                    )        // Specify any activity here e.g. home or splash or login etc
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    i.putExtra("EXIT", true)
                                    startActivity(i)
                                    finish()
                                }
                                enableUI(true)
                            }
                        } else {
                            veces++
                        }
                    }
                    setNegativeButton("Cancelar") { _: DialogInterface, _: Int ->
                        veces = 0
                    }
                }.show()
            }

            R.id.tratamientos_item -> {
                //Voy a tener que mandar el id del paciente para poder ver sus abonos.
                val intent = Intent(this, TratamientosActivity::class.java)
                intent.putExtra("miIdPaciente", miIdPaciente)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun enableUI(enable: Boolean) {
        with(binding) {
            pbBrand.visibility = if (enable) View.GONE else View.VISIBLE
            tvWaitAMoment.visibility = if (enable) View.GONE else View.VISIBLE
        }
    }

    private fun getTratamientos(isFinish: () -> Unit = {}) {
        val db = Firebase.firestore
        db.collection("tratamientos")
            .whereEqualTo("idPaciente", miIdPaciente)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.getString("idTratamiento")?.let { id ->
                        idsTratamientos.add(id)
                    }
                    document.getString("fotos")?.let { foto1 ->
                        fotos1Tratamientos.add(foto1)
                    }
                    document.getString("fotos2")?.let { foto2 ->
                        fotos2Tratamientos.add(foto2)
                    }
                }
                isFinish()
            }
            .addOnFailureListener { exception ->
                isFinish()
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
    }

    fun obtenerAbonos(idTratatamiento: String) {
        val db = Firebase.firestore
        db.collection("tratamientos").document(idTratatamiento).collection("abonos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.getString("miIdAbono")?.let { id ->
                        idsAbonos.add(id)

                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
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