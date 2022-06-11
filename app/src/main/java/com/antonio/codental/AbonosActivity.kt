package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ActivityAbonosBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//import kotlinx.android.synthetic.main.activity_pacientes.*

class AbonosActivity : AppCompatActivity(), AbonosInterfaz, AbonosInterfazAux {


    private lateinit var binding: ActivityAbonosBinding

    private var idTratamiento: String? = null

    private lateinit var firestoreListener: ListenerRegistration

    private var abonoSelected: Abonos? = null

    private var costoTratamiento: Double = 0.0

    private var ultimoActualizar: Boolean? = null


    /*//Copia del Array para guardar los pacientes
    private lateinit var tempArrayList: ArrayList<Abonos>

    //Copia del Array para guardar los pacientes
    private lateinit var tempArrayListPrimerSaldo: ArrayList<PrimerSaldo>*/

    //Adapter global
    private lateinit var adapter: AbonosAdapter

    private var abonoss = mutableListOf<Abonos>()
    private var primerosSaldos = mutableListOf<PrimerSaldo>()

    private var tratamiento: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Flecha para volver a atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Cosas de binding
        binding = ActivityAbonosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtengo el id del tratamiento
        val objIntent: Intent = intent
        idTratamiento = objIntent.getStringExtra("idTratamiento")

        tratamiento = objIntent.getStringExtra("tratamiento")


        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Abonos de $tratamiento"
        costoTratamiento = objIntent.getDoubleExtra("costo", 0.0)
        binding.tvSaldoPendiente.text = costoTratamiento.toString()

        val manager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, manager.orientation)
        adapter = AbonosAdapter(this, arrayListOf())
        binding.lista.apply {
            setHasFixedSize(true)
            layoutManager = manager
            adapter = this@AbonosActivity.adapter
            addItemDecoration(divider)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    when {
                        dy < 0 -> binding.fabAgregar.show()
                        dy > 0 -> binding.fabAgregar.hide()
                    }
                }
            })
        }


        //Listener para el floatingActionButton de agregar
        binding.fabAgregar.setOnClickListener {
            ultimoActualizar = null
            abonoSelected = null
            AddDialogFragment().show(
                supportFragmentManager,
                AddDialogFragment::class.java.simpleName
            )
        }
    }

    override fun onResume() {
        super.onResume()
        configFirestoreRealtime()
    }

    override fun onPause() {
        super.onPause()
        if (this::firestoreListener.isInitialized) firestoreListener.remove()
    }

    override fun click(abono: Abonos, ultimo: Boolean) {
        ultimoActualizar = ultimo
        abonoSelected = abono
        AddDialogFragment().show(supportFragmentManager, AddDialogFragment::class.java.simpleName)
    }

    override fun eliminar(abono: Abonos) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar Abono")
            .setMessage("¿Está seguro de eliminar este abono?")
            .setPositiveButton("Aceptar") { _, _ ->
                abono.idAbono?.let { id ->
                    Toast.makeText(this, "id abono es: $id", Toast.LENGTH_SHORT).show()
                    val db = Firebase.firestore
                    val productRef = db.collection("abonos")
                    productRef.document(id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Abono eliminado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al eliminar registro.", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun mostrarSaldo(sd: Double) {
        binding.tvSaldoPendiente.text = sd.toString()
        if (sd == 0.0) {
            binding.tvTratamientoPagado.visibility = View.VISIBLE
            binding.fabAgregar.isEnabled = false //ya no se pueden agregar nuevos abonos
        }
        else
        {
            binding.tvTratamientoPagado.visibility = View.GONE
            binding.fabAgregar.isEnabled = true //ya no se pueden agregar nuevos abonos
        }
    }


    /*fun getAbonos(idTratamiento: String): MutableList<Abonos> {

        val db = Firebase.firestore
        db.collection("abonos").whereEqualTo("idTratamiento", idTratamiento)
            .get()
            .addOnSuccessListener { value ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        abonoss.add(dc.document.toObject(Abonos::class.java))
                    }
                }


                //Se llena el temp con los pacientes desordenados alfabéticamente
                tempArrayList.addAll(abonoss)

                //Ordenación de la binding.lista de pacientes original
                abonoss.sortBy {
                    it.tratamiento
                }

                //Ordenación del temporal
                tempArrayList.sortBy {
                    it.tratamiento
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
        return tempArrayList
    }*/

    /*fun getPrimerSaldo(idTratamiento: String): String? {
        val db = Firebase.firestore
        var miSaldo: String? = null
        val docRef = db.collection("primerSaldo").document("rZ7kGTb6cesulxQ1J0u5")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    miSaldo = document.getString("primerSaldo")
                } else {
                    Toast.makeText(this, "El campo no existe", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Ocurrió un error", Toast.LENGTH_SHORT).show()
            }
        return miSaldo

        /*db = FirebaseFirestore.getInstance()
        db.collection("primerSaldo").whereEqualTo("idTratamiento", idTratamiento)
            .get()
            .addOnSuccessListener { value ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        primerosSaldos.add(dc.document.toObject(PrimerSaldo::class.java))
                    }
                }

                //Se llena el temp con los pacientes desordenados alfabéticamente
                tempArrayListPrimerSaldo.addAll(primerosSaldos)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
        return tempArrayListPrimerSaldo*/
    }*/

    override fun obtenerAbonoSeleccionado(): Abonos? = abonoSelected

    override fun obtenerIdTratamiento(): String? = idTratamiento

    override fun obtenerCosto(): Double = costoTratamiento

    override fun hayAbonos(): Boolean = adapter.itemCount > 0

    override fun obtenerSaldo(): Double = binding.tvSaldoPendiente.text.toString().toDouble()

    override fun obtenerTratamiento(): String? = tratamiento

    override fun obtenerUltimoActualizar(): Boolean? = ultimoActualizar

    private fun configFirestoreRealtime() {
        //Toast.makeText(this, "idtratamiento = $idTratamiento", Toast.LENGTH_SHORT).show()
        idTratamiento?.let {
            val db = Firebase.firestore
            firestoreListener = db.collection("tratamientos").document(it).collection("abonos")
                .orderBy("timestamp")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Toast.makeText(this, "Error al consultar datos.", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    for (snapshot in snapshots!!.documentChanges) {
                        val abonos = snapshot.document.toObject(Abonos::class.java)
                        abonos.idAbono = snapshot.document.id
                        when (snapshot.type) {
                            DocumentChange.Type.ADDED -> adapter.add(abonos)
                            DocumentChange.Type.MODIFIED -> adapter.update(abonos)
                            DocumentChange.Type.REMOVED -> adapter.delete(abonos)
                        }
                    }
                }
        }

    }

}