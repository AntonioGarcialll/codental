package com.antonio.codental

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ActivityPreciosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

//import kotlinx.android.synthetic.main.activity_pacientes.*

class PreciosActivity : AppCompatActivity(), PreciosInterfaz {

    private lateinit var binding: ActivityPreciosBinding

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore

    //Copia del Array para guardar los precios
    private lateinit var tempArrayList: ArrayList<Precios>

    //Adapter global
    private lateinit var adapter: PreciosAdapter

    lateinit var precios: MutableList<Precios>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityPreciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializa array para lupa
        tempArrayList = arrayListOf<Precios>()

        //Inicializa la binding.lista de los pacientes
        precios = mutableListOf()

        //Cosas para dividir los items en pantalla con linea
        val manager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, manager.orientation)
        adapter = PreciosAdapter(this, getPrecios())
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
                        dy < 0 -> binding.fabAgregar.show()
                        dy > 0 -> binding.fabAgregar.hide()
                    }
                }
            })
        }

        //Listener para el floatingActionButton de agregar
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, NuevoPrecioActivity::class.java)
            startActivity(intent)
        }
    }

    @JvmName("getPrecios1")
    fun getPrecios(): MutableList<Precios> {

        db = FirebaseFirestore.getInstance()
        db.collection("servicios")
            .whereEqualTo("idDoctor", FirebaseAuth.getInstance().currentUser?.let { it.uid })
            .get()
            .addOnSuccessListener { value ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        precios.add(dc.document.toObject(Precios::class.java))
                    }
                }

                //Se llena el temp con los pacientes desordenados alfabéticamente
                tempArrayList.addAll(precios)

                //Ordenación de la binding.lista de pacientes original
                precios.sortBy {
                    it.servicio
                }

                //Ordenación del temporal
                tempArrayList.sortBy {
                    it.servicio
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
        return tempArrayList
    }

    //Menú para la lupa y que realice búsqueda
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lupa_menu, menu)

        val item = menu?.findItem(R.id.lupa_item)
        val searchView = item?.actionView as SearchView

        //Para abrir teclado en mayúscula la primer letra
        searchView.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempArrayList.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    precios.forEach {
                        if (it.servicio!!.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempArrayList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    tempArrayList.clear()
                    tempArrayList.addAll(precios)
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun click(precio: Precios) {
        //Le pasamos los datos del contacto a la ContactoActivity
        val intent = Intent(this, InfoPrecioActivity::class.java)
        intent.putExtra("precioEnviado", precio)
        startActivity(intent)
    }
}