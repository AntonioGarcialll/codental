package com.antonio.codental

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.antonio.codental.databinding.ActivityInfoTratamientoBinding
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class InfoTratamientoActivity : AppCompatActivity() {
    //Declaración de variables
    private lateinit var binding: ActivityInfoTratamientoBinding
    var idTratamiento: String? = null
    var foto1: String? = null
    var foto2: String? = null
    var veces = 0
    var mensajeBorrar = ""
    private val idsAbonos = arrayListOf<String>()

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore

    var abonos: MutableList<Abonos> = mutableListOf()
    var tempArrayList: ArrayList<Abonos> = arrayListOf<Abonos>()
    var tratamientoRecibido: Tratamiento? = null
    var tratamientoRecibidoSegundo: Tratamiento? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityInfoTratamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recibimos el tratamiento seleccionado por medio del intent extra

        tratamientoRecibido = intent.getSerializableExtra("tratamientoEnviado") as Tratamiento

        //Obtengo el ID del Tratamiento al que se le dio clic
        idTratamiento = tratamientoRecibido?.idTratamiento

        //Obtengo el saldoAtual del tratamiento

        abonos = mutableListOf()
        tempArrayList = arrayListOf<Abonos>()

        //Barra
        title = "${tratamientoRecibido?.nombreTratamiento}"

        //Llenamos los campos del xml con los datos que llegan
        binding.tvFecha.text = tratamientoRecibido?.fecha
        binding.tvTratamiento.text = tratamientoRecibido?.nombreTratamiento
        binding.tvCosto.text = tratamientoRecibido?.costo
        foto1 = tratamientoRecibido?.fotos //string con el nombre de la foto1
        foto2 = tratamientoRecibido?.fotos2 //string con el nombre de la foto 2
        bajarImg(foto1) //se pinta la foto 1 en su imageView
        bajarImg2(foto2) // se pinta la foto 2 en su imageView
        db = FirebaseFirestore.getInstance()

        binding.imgTratamiento.setOnClickListener {
            ponerImagenZoom(foto1)
        }

        binding.imgTratamiento2.setOnClickListener {
            ponerImagenZoom(foto2)
        }

        obtenerAbonos(idTratamiento!!)


        /*//Se pinta la imagen 1
        Glide.with(this).load(foto1)
            .placeholder(R.drawable.ic_baseline_cloud_download_24)
            .error(R.drawable.ic_baseline_error_24).into(binding.imgTratamiento)

        //Se pinta la imagen 2
        Glide.with(this).load(foto2)
            .placeholder(R.drawable.ic_baseline_cloud_download_24)
            .error(R.drawable.ic_baseline_error_24).into(binding.imgTratamiento2)*/
    }

    fun bajarImg(nombreImagen: String?) {
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
    }

    fun ponerImagenZoom(nombreImagen: String?) {
        val mBuilder = AlertDialog.Builder(this@InfoTratamientoActivity)
        val mView: View = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
        val photoView: PhotoView = mView.findViewById(R.id.imageViewDialog)
        val imageName = nombreImagen
        val storageRef =
            FirebaseStorage.getInstance().reference.child("tratamientosfolder/$imageName")

        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            photoView.setImageBitmap(bitmap)
            mBuilder.setView(mView)
            val mDialog = mBuilder.create()
            mDialog.show()

        }.addOnFailureListener {
        }
    }

    fun bajarImg2(nombreImagen: String?) {
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
    }

    //Función para sobreescribir el menú por el que ya tiene los iconos de editar, eliminar y abonos
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.antonio.codental.R.menu.tratamiento_menu, menu)
        return super.onCreateOptionsMenu(menu)
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


    //Switch para hacer acciones al dar clic a los iconos de editar, eliminar o abonos.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.abonos_item -> {
                //Se abre la activity de abonos
                val intent = Intent(this, AbonosActivity::class.java)
                intent.putExtra("idTratamiento", idTratamiento)
                intent.putExtra("tratamiento", binding.tvTratamiento.text.toString())
                intent.putExtra("costo", binding.tvCosto.text.toString().toDouble())
                startActivity(intent)
                //finish()
            }

            R.id.edit_item -> {
                //Se abre la activity de NuevoTratamientoActivity
                val intent2 = Intent(this, NuevoTratamientoActivity::class.java)
                intent2.putExtra("idTratamiento", idTratamiento)
                intent2.putExtra("fecha", binding.tvFecha.text.toString())
                intent2.putExtra("tratamiento", binding.tvTratamiento.text.toString())
                intent2.putExtra("costo", binding.tvCosto.text.toString().toDouble())
                intent2.putExtra("foto1", foto1.toString())
                intent2.putExtra("foto2", foto2.toString())
                intent2.putExtra("actualizarObtenido", "actualizar")
                startActivity(intent2)
                finish()
            }

            R.id.delete_item -> {
                if (veces == 0) {
                    mensajeBorrar =
                        "Está a punto de eliminar un tratamiento.\nPor seguridad, vuelva a dar clic al icono de eliminar por favor."

                } else {
                    mensajeBorrar =
                        "¿Está seguro que desea eliminar este tratamiento?\nSe borrarán todos sus datos, entre ellos sus abonos de manera permanente"
                }
                //AlerDialog para eliminar paciente
                AlertDialog.Builder(this).apply {
                    setTitle("Eliminar Tratamiento")
                    setMessage(mensajeBorrar)
                    setPositiveButton("Aceptar") { _: DialogInterface, _: Int ->
                        if (veces > 0) {
                            val db = Firebase.firestore
                            idsAbonos.forEach { idAbono ->
                                db.collection("tratamientos").document(idTratamiento!!)
                                    .collection("abonos").document(idAbono)
                                    .delete()
                                    .addOnSuccessListener {

                                    }.addOnFailureListener {

                                    }
                            }
                            borrarImg(foto1)
                            borrarImg(foto2)
                            db.collection("tratamientos").document(idTratamiento!!)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@InfoTratamientoActivity,
                                        "Tratamiento Eliminado Correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val i =
                                        Intent(applicationContext, PacientesActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        this@InfoTratamientoActivity,
                                        "Ocurriò un error al eliminar el tratamiento",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
        }
        return super.onOptionsItemSelected(item)
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
}