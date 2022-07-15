package com.antonio.codental

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NuevoTratamientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNuevoTratamientoBinding
    private var photoSelectedUri: Uri? = null
    private var photoSelectedUri2: Uri? = null
    private var miTratamiento: Tratamiento? = null
    lateinit var tvFecha: TextView
    lateinit var btnFecha: Button
    var errorFoto1: Boolean? = null //por defecto trae "error"
    var errorFoto2: Boolean? = null //por defecto trae "error"
    var tratamiento: Tratamiento? = null
    var copiaFoto1: String? = null
    var copiaFoto2: String? = null
    var copiaErrorFoto1: Boolean? = null


    //Variable imageResult para la imagen1
    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    photoSelectedUri = fileUri
                    binding.imgTratamiento.setImageURI(fileUri)
                    errorFoto1 = false //si seleccionó la foto 1
                    copiaErrorFoto1 = errorFoto1
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

    //Variable imageResult para la imagen2
    private val imageResult2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode2 = result.resultCode
            val data2 = result.data
            when (resultCode2) {
                Activity.RESULT_OK -> {
                    val fileUri2 = data2?.data!!
                    photoSelectedUri2 = fileUri2
                    binding.imgTratamiento2.setImageURI(fileUri2)
                    errorFoto2 = false // si seleccionó la foto 2
                }
                ImagePicker.RESULT_ERROR -> Toast.makeText(
                    this,
                    ImagePicker.getError(data2),
                    Toast.LENGTH_SHORT
                ).show()
                else -> Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    fun subirImg(): String? {
        if (photoSelectedUri == null) {
            return "foto1"
        } else {
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val fileName = formatter.format(now)
            val storageReference: StorageReference?
            storageReference =
                FirebaseStorage.getInstance().getReference("tratamientosfolder/$fileName")

            storageReference.putFile(photoSelectedUri!!).addOnSuccessListener {
                binding.imgTratamiento.setImageURI(null)
                //Toast.makeText(this, "Foto subida con exito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                enableUI(true)
                //Toast.makeText(this, "Falló", Toast.LENGTH_SHORT).show()
            }
            return fileName
        }

    }

    fun subirImg2(): String? {
        if (photoSelectedUri2 == null) {
            return "foto2"
        } else {
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val fileName = formatter.format(now)
            val storageReference: StorageReference?
            storageReference =
                FirebaseStorage.getInstance().getReference("tratamientosfolder/$fileName")

            storageReference.putFile(photoSelectedUri2!!).addOnSuccessListener {
                binding.imgTratamiento2.setImageURI(null)
                //Toast.makeText(this, "Foto subida con exito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                enableUI(true)
                //Toast.makeText(this, "Falló", Toast.LENGTH_SHORT).show()
            }
            return fileName
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

    fun bajarImg(nombreImagen: String?) {
        try {
            val imageName = nombreImagen
            val storageRef =
                FirebaseStorage.getInstance().reference.child("tratamientosfolder/$imageName")

            val localfile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgTratamiento.setImageBitmap(bitmap)
            }.addOnFailureListener {
                //Toast.makeText(this, "Foto 1 vacía", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {

        }
    }

    fun bajarImg2(nombreImagen: String?) {
        try {
            val imageName = nombreImagen
            val storageRef =
                FirebaseStorage.getInstance().reference.child("tratamientosfolder/$imageName")

            val localfile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgTratamiento2.setImageBitmap(bitmap)
            }.addOnFailureListener {
                //Toast.makeText(this, "Foto 2 vacía", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Cosas de binding
        binding = ActivityNuevoTratamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Variable para la base de datos de Firebase
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        //Obtengo el id del paciente al cual le voy a crear o editar su tratamiento.
        val objIntentPaciente: Intent = intent
        val objIntentInfo: Intent = intent

        var miIdPaciente = objIntentPaciente.getStringExtra("miIdPaciente")
        var idTratamiento = objIntentInfo.getStringExtra("idTratamiento")
        var mifecha = objIntentInfo.getStringExtra("fecha")
        var mitratamiento = objIntentInfo.getStringExtra("tratamiento")
        var micosto = objIntentInfo.getDoubleExtra("costo", 0.0)
        var miFoto1 = objIntentInfo.getStringExtra("foto1")
        var miFoto2 = objIntentInfo.getStringExtra("foto2")
        var actualizarObtenido = objIntentInfo.getStringExtra("actualizarObtenido")
        copiaFoto1 = miFoto1.toString()
        copiaFoto2 = miFoto2.toString()
        errorFoto1 = true
        errorFoto2 = true

        if (actualizarObtenido == "actualizar") {
            supportActionBar!!.title = "Editar Tratamiento"
            //Asigna los valores a las cajas de texto para que puedan ser modificadas
            binding.tvFecha.setText(mifecha)
            binding.etNombreTratamiento.setText(mitratamiento)
            binding.etCosto.setText(micosto.toString())
            //Se pinta la imagen 1
            bajarImg(miFoto1)
            //Se pinta la imagen 2
            bajarImg2(miFoto2)

        } else {
            supportActionBar!!.title = "Agregar Nuevo Tratamiento"
        }

        //Listener para el clic del imageView de la imagen 1
        binding.imgTratamiento.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024)
                .maxResultSize(1080, 1080).createIntent { intent -> imageResult.launch(intent) }
        }

        //Listener para el clic del imageView de la imagen 2
        binding.imgTratamiento2.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024)
                .maxResultSize(1080, 1080).createIntent { intent -> imageResult2.launch(intent) }
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
            if (binding.tvFecha.text!!.isEmpty() || binding.etNombreTratamiento.text!!.isEmpty() || binding.etCosto.text!!.isEmpty()) {
                Toast.makeText(
                    this,
                    "No debe dejar campos vacíos.\nNo es necesario agregar las fotos para crear un nuevo tratamiento",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (actualizarObtenido == "actualizar") {
                    /*//Hay que actualizar la info del tratamiento
                    errorFoto1 = copiaErrorFoto1
                    if(errorFoto1 == null)
                    {
                        //No seleccionaste la foto 1
                        errorFoto1 = true
                    }*/

                    var foto1 = "foto1"
                    var foto2 = "foto2"
                    if (errorFoto1 == false && errorFoto2 == false) {
                        //Actualizó las dos fotos
                        enableUI(false)

                        //Eliminamos las 2 fotos que tenía
                        borrarImg(copiaFoto1)
                        borrarImg(copiaFoto2)

                        foto1 = subirImg()!! //se sube la nueva foto 1
                        Thread.sleep(1000) // se espera 1 segundo
                        foto2 = subirImg2()!! //se sube la nueva foto 2
                        db.collection("tratamientos").document(idTratamiento!!).update(
                            "fecha", binding.tvFecha.text.toString(),
                            "costo", binding.etCosto.text.toString(),
                            "fotos", foto1.toString(),
                            "fotos2", foto2.toString(),
                            "nombreTratamiento", binding.etNombreTratamiento.text.toString()
                        )
                        enableUI(true)
                        Toast.makeText(
                            this,
                            "Tratamiento Actualizado Correctamente",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val i = Intent(applicationContext, PacientesActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra("EXIT", true)
                        startActivity(i)
                        finish()
                    } else if (errorFoto1 == false && errorFoto2 == true) {
                        //Sólo actualizó la foto 1
                        enableUI(false)

                        //Eliminamos la foto 1 que tenía
                        borrarImg(copiaFoto1)

                        foto1 = subirImg()!!
                        db.collection("tratamientos").document(idTratamiento!!).update(
                            "fecha", binding.tvFecha.text.toString(),
                            "costo", binding.etCosto.text.toString(),
                            "fotos", foto1.toString(),
                            "nombreTratamiento", binding.etNombreTratamiento.text.toString()
                        )
                        enableUI(true)
                        Toast.makeText(
                            this,
                            "Tratamiento Actualizado Correctamente",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val i = Intent(applicationContext, PacientesActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra("EXIT", true)
                        startActivity(i)
                        finish()
                    } else if (errorFoto1 == true && errorFoto2 == false) {
                        //Sólo actualizó la foto 2
                        enableUI(false)

                        //Eliminamos la foto 2 que tenía
                        borrarImg(copiaFoto2)

                        foto2 = subirImg2()!!
                        db.collection("tratamientos").document(idTratamiento!!).update(
                            "fecha", binding.tvFecha.text.toString(),
                            "costo", binding.etCosto.text.toString(),
                            "fotos2", foto2.toString(),
                            "nombreTratamiento", binding.etNombreTratamiento.text.toString()
                        )
                        enableUI(true)
                        Toast.makeText(
                            this,
                            "Tratamiento Actualizado Correctamente",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val i = Intent(applicationContext, PacientesActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra("EXIT", true)
                        startActivity(i)
                        finish()
                    } else {
                        //No actualizó ninguna foto
                        db.collection("tratamientos").document(idTratamiento!!).update(
                            "fecha", binding.tvFecha.text.toString(),
                            "costo", binding.etCosto.text.toString(),
                            "nombreTratamiento", binding.etNombreTratamiento.text.toString()
                        )
                        Toast.makeText(
                            this,
                            "Tratamiento Actualizado Correctamente",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val i = Intent(applicationContext, PacientesActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra("EXIT", true)
                        startActivity(i)
                        finish()
                    }
                } else {
                    //Hay que agregar un nuevo tratamiento
                    enableUI(false)
                    var foto1 = subirImg()


                    var foto2: String? = null
                    if (errorFoto2 == false) {
                        //Hay que guardar las 2 fotos y los datos del tratamiento
                        Thread.sleep(1000) //se espera 1 segundo para que pueda tener otro nombre diferente la segunda foto
                        foto2 = subirImg2()
                        val data = hashMapOf(
                            "fecha" to binding.tvFecha.text.toString(),
                            "costo" to binding.etCosto.text.toString(),
                            "fotos" to foto1.toString(),
                            "fotos2" to foto2.toString(),
                            "idPaciente" to miIdPaciente.toString(),
                            "nombreTratamiento" to binding.etNombreTratamiento.text.toString(),
                            "idTratamiento" to "a"
                        )
                        db.collection("tratamientos").add(data)
                            .addOnSuccessListener { documentReference ->
                                var miId = documentReference.id
                                db.collection("tratamientos").document(miId)
                                    .update("idTratamiento", miId)
                                enableUI(true)
                                Toast.makeText(
                                    this,
                                    "Tratamiento Agregado Correctamente",
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
                            .addOnFailureListener { e ->
                                enableUI(true)
                                Toast.makeText(this, "Ocurrió un error: $e", Toast.LENGTH_SHORT)
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
                            }
                    } else {
                        //Hay que guardar SOLO la foto 1 y los datos del tratamiento
                        val data = hashMapOf(
                            "fecha" to binding.tvFecha.text.toString(),
                            "costo" to binding.etCosto.text.toString(),
                            "fotos" to foto1,
                            "fotos2" to "foto2",
                            "idPaciente" to miIdPaciente.toString(),
                            "nombreTratamiento" to binding.etNombreTratamiento.text.toString(),
                            "idTratamiento" to "a"
                        )
                        db.collection("tratamientos").add(data)
                            .addOnSuccessListener { documentReference ->
                                var miId = documentReference.id
                                db.collection("tratamientos").document(miId)
                                    .update("idTratamiento", miId)
                                enableUI(true)
                                Toast.makeText(
                                    this,
                                    "Tratamiento Agregado Correctamente",
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
                            .addOnFailureListener { e ->
                                enableUI(true)
                                Toast.makeText(this, "Ocurrió un error: $e", Toast.LENGTH_SHORT)
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
                            }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    /*fun subirFotos(idPaciente: String, operacion: String) {
        //Se sube la imagen 1
        uploadImage(photoSelectedUri) {
            if (it.isSuccess) {
                miFoto = it.imageUrl.toString()
                miIdTratamiento = it.documentId.toString()

                if (errorFoto2 == false) {
                    //Se sube imagen 1 y 2
                    uploadImage(photoSelectedUri2) {
                        if (it.isSuccess) {
                            miFoto2 = it.imageUrl.toString()
                            tratamiento = Tratamiento(
                                fecha = binding.tvFecha.text.toString().trim(),
                                nombreTratamiento = binding.etNombreTratamiento.text.toString()
                                    .trim(),
                                costo = binding.etCosto.text.toString().trim(),
                                fotos = miFoto.toString().trim(),
                                fotos2 = miFoto2.toString().trim(),
                                idPaciente = idPaciente.toString().trim(),
                                idTratamiento = miIdTratamiento.toString().trim()
                            )
                            create(tratamiento!!, miIdTratamiento.toString(), operacion)
                        }
                    }
                } else {
                    //Se sube imagen 1
                    uploadImage(photoSelectedUri) {
                        if (it.isSuccess) {
                            miFoto = it.imageUrl.toString()
                            tratamiento = Tratamiento(
                                fecha = binding.tvFecha.text.toString().trim(),
                                nombreTratamiento = binding.etNombreTratamiento.text.toString()
                                    .trim(),
                                costo = binding.etCosto.text.toString().trim(),
                                fotos = miFoto.toString().trim(),
                                fotos2 = miFoto2.toString().trim(),
                                idPaciente = idPaciente.toString().trim(),
                                idTratamiento = miIdTratamiento.toString().trim()
                            )
                            create(tratamiento!!, miIdTratamiento.toString(), operacion)
                        }
                    }
                }
            }
        }
    }*/

    /*private fun create(tratamiento: Tratamiento, documentId: String, operacion: String) {
        var mensaje: String
        if (operacion == "agregar") {
            mensaje = "Tratamiento Agregado Correctamente"
        } else {
            mensaje = "Tratamiento Actualizado Correctamente"
        }
        val db = Firebase.firestore
        db.collection("tratamientos").document(documentId).set(tratamiento).addOnSuccessListener {
            finish()
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT)
                .show()
        }.addOnFailureListener {
            enableUI(true)
            Toast.makeText(this, "Ocurrió un error", Toast.LENGTH_SHORT).show()
        }
    }*/

    /*private fun uploadImage(miUri: Uri?, callback: (EventPost) -> Unit) {
        val eventPost = EventPost()
        eventPost.documentId = Firebase.firestore.collection("tratamientos").document().id
        val imagesRef = Firebase.storage.reference.child("tratamientosfolder")
        val photoRef = imagesRef.child(eventPost.documentId!!)
        if (miUri == null) {
            eventPost.isSuccess = true
            callback(eventPost)
        } else {
            photoRef.putFile(miUri).addOnSuccessListener { snapshot ->
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
    }*/

    private fun enableUI(enable: Boolean) {
        with(binding) {
            btnFecha.isEnabled = enable
            etNombreTratamiento.isEnabled = enable
            etCosto.isEnabled = enable
            imgTratamiento.isEnabled = enable
            imgTratamiento2.isEnabled = enable
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

