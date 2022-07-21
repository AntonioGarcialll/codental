package com.antonio.codental

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ActivityGastosYegresosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

//import kotlinx.android.synthetic.main.activity_pacientes.*

class GastosYEgresosActivity : AppCompatActivity(), GastoEgresoInterfaz {

    //Declaración de variables
    private lateinit var binding: ActivityGastosYegresosBinding

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore

    //Copia del Array para guardar los gastosYEgresos
    private lateinit var tempArrayList: ArrayList<GastosYEgresos>

    //Adapter global
    private lateinit var adapterClase: GastosYEgresosAdapter

    lateinit var gastosEgresos: MutableList<GastosYEgresos>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityGastosYegresosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializa array para lupa
        tempArrayList = arrayListOf<GastosYEgresos>()

        //Inicializa la binding.lista de los pacientes
        gastosEgresos = mutableListOf()

        //Cosas para dividir los items en pantalla con linea
        val manager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, manager.orientation)
        adapterClase = GastosYEgresosAdapter(this, mutableListOf())
        binding.lista.apply {
            setHasFixedSize(true)
            layoutManager = manager
            adapter = this@GastosYEgresosActivity.adapterClase
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

        //Listener para el floatingActionButton de agregar
        binding.fabAgregarGE.setOnClickListener {
            val intent = Intent(this, NuevoGastoYEgresoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getGastosEgresos()
    }

    @JvmName("getGastosEgresos1")
    fun getGastosEgresos() {

        db = FirebaseFirestore.getInstance()
        db.collection("gastosEgresos")
            .whereEqualTo("idDoctor", FirebaseAuth.getInstance().currentUser?.let { it.uid })
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val gastoEgreso = document.toObject<GastosYEgresos>()
                    gastoEgreso.idGastoEgreso = document.id
                    adapterClase.add(gastoEgreso)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
    }

    //Menú para la lupa y que realice búsqueda
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lupa_menu, menu)

        val item = menu?.findItem(R.id.lupa_item)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterClase.filterListByQuery(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun click(gastoEgreso: GastosYEgresos) {
        //Le pasamos los datos del contacto a la ContactoActivity
        val intent = Intent(this, InfoGastoYEgresoActivity::class.java)
        intent.putExtra("gastoEgresoEnviado", gastoEgreso)
        startActivity(intent)
    }
}