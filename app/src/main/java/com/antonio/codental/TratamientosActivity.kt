package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonio.codental.databinding.ActivityTratamientosBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class TratamientosActivity : AppCompatActivity(), TratamientosInterfaz {

    private lateinit var binding: ActivityTratamientosBinding

    //Adapter global
    private lateinit var adapter: TratamientosAdapter

    private var tratamientoss = mutableListOf<Tratamiento>()

    //Copia del Array para guardar los pacientes
    private var tempArrayList = mutableListOf<Tratamiento>()

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Barra
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Tratamientos"

        //Obtengo el id del paciente al cual le voy a mostrar sus tratamientos
        val objIntent: Intent = intent
        var miIdPaciente = objIntent.getStringExtra("miIdPaciente")

        //Cosas de binding
        binding = ActivityTratamientosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TratamientosAdapter(this, getTratamientos(miIdPaciente!!))
        binding.lista.layoutManager = LinearLayoutManager(this)
        binding.lista.adapter = adapter
        adapter.notifyDataSetChanged()


        //Listener para el floatingActionButton de agregar
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, NuevoTratamientoActivity::class.java)
            intent.putExtra("miIdPaciente",miIdPaciente)
            startActivity(intent)
        }


    }

    override fun click(tratamiento: Tratamiento) {
        //Le pasamos los datos del contacto a la ContactoActivity
        val intent = Intent(this, InfoTratamientoActivity::class.java)
        intent.putExtra("tratamientoEnviado", tratamiento)
        startActivity(intent)
    }

    fun getTratamientos(idPaciente : String): MutableList<Tratamiento> {

        db = FirebaseFirestore.getInstance()
        db.collection("tratamientos").whereEqualTo("idPaciente",idPaciente)
            .get()
            .addOnSuccessListener { value ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        tratamientoss.add(dc.document.toObject(Tratamiento::class.java))
                    }
                }

                //Se llena el temp con los pacientes desordenados alfabéticamente
                tempArrayList.addAll(tratamientoss)

                //Ordenación de la binding.lista de pacientes original
                tratamientoss.sortBy {
                    it.nombreTratamiento
                }

                //Ordenación del temporal
                tempArrayList.sortBy {
                    it.nombreTratamiento
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
        return tempArrayList
    }
}