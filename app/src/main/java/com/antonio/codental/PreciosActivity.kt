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
import com.antonio.codental.databinding.ActivityPreciosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

//import kotlinx.android.synthetic.main.activity_pacientes.*

class PreciosActivity : AppCompatActivity(), PreciosInterfaz {

    private lateinit var binding: ActivityPreciosBinding

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore

    //Adapter global
    private lateinit var adapterClase: PreciosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityPreciosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Cosas para dividir los items en pantalla con linea
        val manager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, manager.orientation)
        adapterClase = PreciosAdapter(this, mutableListOf())
        binding.lista.apply {
            setHasFixedSize(true)
            layoutManager = manager
            adapter = this@PreciosActivity.adapterClase
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

    override fun onResume() {
        super.onResume()
        getPrecios()
    }

    @JvmName("getPrecios1")
    fun getPrecios() {

        db = FirebaseFirestore.getInstance()
        db.collection("servicios")
            .whereEqualTo("idDoctor", FirebaseAuth.getInstance().currentUser?.let { it.uid })
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val precio = document.toObject<Precios>()
                    precio.idServicio = document.id
                    adapterClase.add(precio)
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

    override fun click(precio: Precios) {
        //Le pasamos los datos del contacto a la ContactoActivity
        val intent = Intent(this, InfoPrecioActivity::class.java)
        intent.putExtra("precioEnviado", precio)
        startActivity(intent)
    }
}