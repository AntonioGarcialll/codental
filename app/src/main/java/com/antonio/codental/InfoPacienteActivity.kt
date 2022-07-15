package com.antonio.codental

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.antonio.codental.databinding.ActivityInfoPacienteBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

//import kotlinx.android.synthetic.main.activity_info_paciente.*

class InfoPacienteActivity : AppCompatActivity() {

    private val idsTratamientos = arrayListOf<String>()
    private val idsAbonos = arrayListOf<String>()

    var objeto: InfoTratamientoActivity = InfoTratamientoActivity()

    private lateinit var binding: ActivityInfoPacienteBinding

    //Variables globales
    lateinit var miIdPaciente: String

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore

    var midb = FirebaseFirestore.getInstance()

    private lateinit var tempArrayList: ArrayList<Tratamiento>
    lateinit var tratamientoss: MutableList<Tratamiento>
    lateinit var abonos: MutableList<Abonos>


    var veces = 0
    var mensajeBorrar = ""
    var bandera = true

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
        //Toast.makeText(this, pacienteRecibido.toString(), Toast.LENGTH_SHORT).show()
        binding.tvFecha.text = pacienteRecibido.fecha
        binding.tvPaciente.text = pacienteRecibido.paciente
        binding.tvDoctor.text = pacienteRecibido.doctor
        //binding.tvTratamiento.text = pacienteRecibido.tratamiento
        //binding.tvCosto.text = pacienteRecibido.costo
        //binding.tvFotos.text = pacienteRecibido.fotos
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
                //intent.putExtra("tvTratamiento",binding.tvTratamiento.text.toString())
                //intent.putExtra("tvCosto",binding.tvCosto.text.toString())
                //intent.putExtra("tvFotos",binding.tvFotos.text.toString())
                intent.putExtra("actualizar", "actualizame")
                intent.putExtra("miIdPaciente", miIdPaciente)

                startActivity(intent)
                //Toast.makeText(this, "Le di clic a editar", Toast.LENGTH_SHORT).show()
            }

            R.id.delete_item -> {
                enableUI(false)
                val db = Firebase.firestore
                val pacienteRef = db.collection("pacientes").document(miIdPaciente)
                Toast.makeText(this, "el id es: " + miIdPaciente, Toast.LENGTH_SHORT).show()
                val tratamientosRefList = mutableListOf<DocumentReference>()
                val abonosRefList = mutableListOf<DocumentReference>()
                idsTratamientos.forEach { traId ->
                    val miRef = db.collection("tratamientos").document(traId)
                    tratamientosRefList.add(miRef)
                    idsAbonos.forEach { abonoId ->
                        val miRefAbonos =
                            db.collection("tratamientos").document(traId).collection("abonos")
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
                        Toast.makeText(this, "Paciente Eliminado Correctamente", Toast.LENGTH_SHORT)
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
                        Toast.makeText(this, "Ocurrió Un Error", Toast.LENGTH_SHORT).show()
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
            }

            R.id.tratamientos_item -> {
                //Toast.makeText(this, "Le di clic a abonos", Toast.LENGTH_SHORT).show()
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


    fun eliminarAbonosYTratamientosYPacientes() {
        /*var arregloIdsTratamientos = arrayListOf<String>()
        var cadena = ""

        for (i in 0 until getTratamientos().size)
        {
            arregloIdsTratamientos.add(getTratamientos().get(i).idTratamiento!!)
        }

        for (i in 0 until arregloIdsTratamientos.size)
        {
            cadena = cadena + obtenerAbonos(arregloIdsTratamientos.get(i)).get(i).miIdAbono + "\n"
        }

        binding.tvDoctor.setText(cadena)*/


        //for (i in 0 until 1) {

        //Se llena la lista con los ids de los abonos del tratamiento
        /*midb.collection("tratamientos").document(getTratamientos().get(0).idTratamiento!!).collection("abonos")
            .get()
            .addOnSuccessListener { value ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        abonos.add(dc.document.toObject(Abonos::class.java))
                    }
                }
                //Obtengo los ids de los abonos del tratamiento en la posición i
                tempArrayListAbonos.addAll(abonos)
                for (j in 0 until  tempArrayListAbonos.size)
                {
                    cadena = cadena + tempArrayListAbonos.get(j).tratamiento + "\n"
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }*/


        //binding.tvDoctor.text = cadena
        /*
        var cadena = ""
        for (i in 0 until tempArrayListAbonos.size) {
            cadena = cadena + tempArrayListAbonos.get(i).miIdAbono + ","
        }
        binding.tvDoctor.text = cadena*/


        /*
        for (k in 0 until tempArrayListAbonos.size)
        {
            //Eliminos los abonos del tratamiento
            var idAbono = tempArrayListAbonos.get(k).miIdAbono
            db.collection("tratamientos").document(obtenerTratatamientos().get(i).idTratamiento!!)
                .collection("abonos").document(idAbono!!)
                .delete()
                .addOnSuccessListener {
                    idAbono = ""
                }.addOnFailureListener {

                }
        }


        //Eliminos las fotos deñ tratamiento
        borrarImg(obtenerTratatamientos().get(i).fotos)
        borrarImg(obtenerTratatamientos().get(i).fotos2)

        //Elimino el tratamiento
        db.collection("tratamientos").document(obtenerTratatamientos().get(i).idTratamiento!!)
            .delete()
            .addOnSuccessListener {
            }.addOnFailureListener {
            }*/
        //}


        /*
        //Ya que eliminó los tratamientos con sus abonos, elimino el paciente
        //Eliminamos el paciente con el id especificado
        db.collection("pacientes").document(miIdPaciente)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    this@InfoPacienteActivity,
                    "Paciente Eliminado Correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                //Se cierra actividad y regresa a la activity main
                val i = Intent(applicationContext, PacientesActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.putExtra("EXIT", true)
                startActivity(i)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@InfoPacienteActivity,
                    "Ocurrió un error",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
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