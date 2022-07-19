package com.antonio.codental

//import kotlinx.android.synthetic.main.activity_pacientes.*
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ActivityPacientesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class PacientesActivity : AppCompatActivity(), PacienteInterfaz {

    //Declaración de variables
    private lateinit var binding: ActivityPacientesBinding

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore

    //Copia del Array para guardar los pacientes
    private lateinit var tempArrayList: ArrayList<Paciente>

    //Adapter global
    private lateinit var adapterClase: PacienteAdapter

    lateinit var pacientes: MutableList<Paciente>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityPacientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializa array para lupa
        tempArrayList = arrayListOf<Paciente>()

        //Inicializa la binding.lista de los pacientes
        pacientes = mutableListOf()

        //Cosas para dividir los items en pantalla con linea
        val manager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, manager.orientation)
        adapterClase = PacienteAdapter(this, mutableListOf())
        binding.lista.apply {
            setHasFixedSize(true)
            layoutManager = manager
            adapter = this@PacientesActivity.adapterClase
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
            val intent = Intent(this, NuevoPacienteActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        getPacientes()
    }

    @JvmName("getPacientes1")
    fun getPacientes() {

        db = FirebaseFirestore.getInstance()
        db.collection("pacientes")
            .whereEqualTo("idDoctor", FirebaseAuth.getInstance().currentUser?.let { it.uid })
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val paciente = document.toObject<Paciente>()
                    paciente.idPaciente = document.id
                    adapterClase.add(paciente)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
        //return tempArrayList
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

    //Listener para hacer acción al darle clic a algún paciente
    override fun click(paciente: Paciente) {
        //Le pasamos los datos del contacto a la ContactoActivity
        val intent = Intent(this, InfoPacienteActivity::class.java)
        intent.putExtra("contactoEnviado", paciente)
        startActivity(intent)
    }

}


