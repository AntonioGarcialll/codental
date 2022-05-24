package com.antonio.codental

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.antonio.codental.databinding.ActivityNuevoTratamientoBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class NuevoTratamientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNuevoTratamientoBinding
    private var photoSelectedUri: Uri? = null
    private var miTratamiento: Tratamiento? = null
    lateinit var tvFecha: TextView
    lateinit var btnFecha: Button

    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    photoSelectedUri = fileUri
                    binding.imgTratamiento.setImageURI(fileUri)
                }
                ImagePicker.RESULT_ERROR -> Toast.makeText(
                    this,
                    ImagePicker.getError(data),
                    Toast.LENGTH_SHORT
                ).show()
                else -> Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Barra
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Agregar Nuevo Tratamiento"

        //Obtengo el id del paciente al cual le voy a crear o editar su tratamiento.
        val objIntent: Intent = intent
        var miIdPaciente = objIntent.getStringExtra("miIdPaciente")


        //Cosas de binding
        binding = ActivityNuevoTratamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgTratamiento.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024)
                .maxResultSize(1080, 1080).createIntent { intent -> imageResult.launch(intent) }
        }

        tvFecha = binding.tvFecha
        btnFecha = binding.btnFecha

        //Método para el click del botón del calendario
        btnFecha.setOnClickListener {
            val dialogFecha =
                NuevoPacienteActivity.DatePickerFragment { year, month, day ->
                    mostrarFecha(
                        year,
                        month,
                        day
                    )
                }
            dialogFecha.show(supportFragmentManager, "DatePicker")
        }

        binding.btnGuardar.setOnClickListener()
        {
            if(binding.tvFecha.text.isEmpty() || binding.etNombreTratamiento.text.isEmpty() || binding.etCosto.text.isEmpty() || imageResult.toString().equals(null))
            {
                Toast.makeText(this, "Error, no debe dejar campos vacíos.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                enableUI(false)
                uploadImage {
                    if (it.isSuccess) {
                        val tratamiento = Tratamiento(
                            fecha = binding.tvFecha.text.toString().trim(),
                            nombreTratamiento = binding.etNombreTratamiento.text.toString().trim(),
                            costo = binding.etCosto.text.toString().trim(),
                            fotos = it.imageUrl,
                            idPaciente = miIdPaciente.toString().trim(),
                            idTratamiento = it.documentId
                        )
                        create(tratamiento, it.documentId!!)
                    }
                }
            }
        }

    }

    private fun create(tratamiento: Tratamiento, documentId: String) {
        val db = Firebase.firestore
        db.collection("tratamientos").document(documentId).set(tratamiento).addOnSuccessListener {
            finish()
            Toast.makeText(this, "Se agregó correctamente el tratamiento. IDPaciente = ${tratamiento.idPaciente.toString()}", Toast.LENGTH_SHORT)
                .show()
        }.addOnFailureListener {
            enableUI(true)
            Toast.makeText(this, "Ocurrió un error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(callback: (EventPost) -> Unit) {
        val eventPost = EventPost()
        eventPost.documentId = Firebase.firestore.collection("tratamientos").document().id
        val imagesRef = Firebase.storage.reference.child("tratamientosfolder")
        val photoRef = imagesRef.child(eventPost.documentId!!)
        if (photoSelectedUri == null) {
            eventPost.isSuccess = true
            callback(eventPost)
        } else {
            photoRef.putFile(photoSelectedUri!!).addOnSuccessListener { snapshot ->
                snapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                    eventPost.isSuccess = true
                    eventPost.imageUrl = downloadUrl.toString().trim()
                    callback(eventPost)
                }.addOnFailureListener {
                    eventPost.isSuccess = false
                    callback(eventPost)
                    enableUI(true)
                    Toast.makeText(this, "Error al descargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                eventPost.isSuccess = false
                callback(eventPost)
                enableUI(true)
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableUI(enable: Boolean) {
        with(binding) {
            btnFecha.isEnabled = enable
            etNombreTratamiento.isEnabled = enable
            etCosto.isEnabled = enable
            imgTratamiento.isEnabled = enable
            btnGuardar.isEnabled = enable
            pbBrand.visibility = if (enable) View.GONE else View.VISIBLE
            tvWaitAMoment.visibility = if (enable) View.GONE else View.VISIBLE
        }
    }

    //Función para mostrar la fecha en el textView de fecha
    private fun mostrarFecha(year: Int, month: Int, day: Int) {
        tvFecha.text = "$day/$month/$year"
    }

    //Datos para el calendario
    class DatePickerFragment(val listener: (year: Int, month: Int, day: Int) -> Unit) :
        DialogFragment(), DatePickerDialog.OnDateSetListener {

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireActivity(), this, year, month, day)
        }

        //Los valores finales que tendrán las variables de la fecha
        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
            listener(year, month + 1, day)
        }
    }
}

