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
import com.antonio.codental.databinding.ActivityTratamientosBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class TratamientosActivity : AppCompatActivity(), TratamientosInterfaz {

    private lateinit var binding: ActivityTratamientosBinding

    var idPaciente: String? = null

    //Adapter global
    private lateinit var adapterClase: TratamientosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Obtengo el id del paciente al cual le voy a mostrar sus tratamientos

        //Cosas de binding
        binding = ActivityTratamientosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idPaciente = intent?.getStringExtra("miIdPaciente")

        //Cosas para dividir los items en pantalla con linea
        val manager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, manager.orientation)
        adapterClase = TratamientosAdapter(this, mutableListOf())
        binding.lista.apply {
            setHasFixedSize(true)
            layoutManager = manager
            adapter = this@TratamientosActivity.adapterClase
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
            idPaciente?.let { id ->
                val intent = Intent(this, NuevoTratamientoActivity::class.java)
                intent.putExtra("miIdPaciente", id)
                startActivity(intent)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        idPaciente?.let { id ->
            getTratamientos(id)
        }
    }

    override fun click(tratamiento: Tratamiento) {
        //Le pasamos los datos del contacto a la ContactoActivity
        val intent = Intent(this, InfoTratamientoActivity::class.java)
        intent.putExtra("tratamientoEnviado", tratamiento)
        startActivity(intent)
        //finish()
    }

    fun getTratamientos(idPaciente: String) {
        val db = Firebase.firestore
        db.collection("tratamientos")
            .whereEqualTo("idPaciente", idPaciente)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tratamiento = document.toObject<Tratamiento>()
                    tratamiento.idTratamiento = document.id
                    adapterClase.add(tratamiento)
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

        //Para abrir teclado en mayúscula la primer letra
        //searchView.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterClase.filterListByQuery(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}