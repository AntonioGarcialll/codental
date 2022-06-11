package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ActivityGastosYegresosBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

//import kotlinx.android.synthetic.main.activity_pacientes.*

class GastosYEgresosActivity : AppCompatActivity(), GastoEgresoInterfaz {

    //Declaración de variables

    private lateinit var binding: ActivityGastosYegresosBinding

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore

    //Copia del Array para guardar los gastosYEgresos
    private lateinit var tempArrayList: ArrayList<GastosYEgresos>

    //Adapter global
    private lateinit var adapter: GastosYEgresosAdapter

    lateinit var gastosEgresos: MutableList<GastosYEgresos>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Flecha para volver a atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Cosas de binding
        binding = ActivityGastosYegresosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Gastos y Egresos"

        //Inicializa array para lupa
        tempArrayList = arrayListOf<GastosYEgresos>()

        //Inicializa la binding.lista de los pacientes
        gastosEgresos = mutableListOf()

        //Cosas para dividir los items en pantalla con linea
        val manager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, manager.orientation)
        adapter = GastosYEgresosAdapter(this, getGastosEgresos())
        binding.lista.layoutManager = LinearLayoutManager(this)
        binding.lista.adapter = adapter
        adapter.notifyDataSetChanged()
        binding.lista.apply {
            setHasFixedSize(true)
            layoutManager = manager
            adapter = this.adapter
            addItemDecoration(divider)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    when {
                        dy < 0 -> binding.fabAgregarGE.show()
                        dy > 0 -> binding.fabAgregarGE.hide()
                    }
                }
            })
        }

        /*adapter = GastosYEgresosAdapter(this,getGastosEgresos())
        binding.lista.layoutManager = LinearLayoutManager(this)
        binding.lista.adapter = adapter
        adapter.notifyDataSetChanged()*/

        //Listener para el floatingActionButton de agregar
        binding.fabAgregarGE.setOnClickListener {
            val intent = Intent(this, NuevoGastoYEgresoActivity::class.java)
            startActivity(intent)
        }
    }

    @JvmName("getGastosEgresos1")
    fun getGastosEgresos(): MutableList<GastosYEgresos> {

        db = FirebaseFirestore.getInstance()
        db.collection("gastosEgresos")
            .get()
            .addOnSuccessListener { value ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        gastosEgresos.add(dc.document.toObject(GastosYEgresos::class.java))
                    }
                }

                //Se llena el temp con los pacientes desordenados alfabéticamente
                tempArrayList.addAll(gastosEgresos)

                //Ordenación de la binding.lista de pacientes original
                /*gastosEgresos.sortBy {
                    it.fecha
                }*/

                //Ordenación del temporal
                /*tempArrayList.sortBy {
                    it.fecha
                }*/

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
        return tempArrayList

    }

    override fun click(gastoEgreso: GastosYEgresos) {
        //Le pasamos los datos del contacto a la ContactoActivity
        val intent = Intent(this, InfoGastoYEgresoActivity::class.java)
        intent.putExtra("gastoEgresoEnviado", gastoEgreso)
        startActivity(intent)
    }
}