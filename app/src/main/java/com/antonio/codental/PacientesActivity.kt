package com.antonio.codental

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonio.codental.databinding.ActivityAbonosBinding
import com.antonio.codental.databinding.ActivityPacientesBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
//import kotlinx.android.synthetic.main.activity_pacientes.*
import java.util.*
import kotlin.collections.ArrayList

class PacientesActivity : AppCompatActivity(), PacienteInterfaz {


    //Declaración de variables
    private lateinit var binding: ActivityPacientesBinding

    //Variable para la base de datos
    private lateinit var db : FirebaseFirestore


    //Array para guardar los pacientes
    private lateinit var listaArrayList : ArrayList<Paciente>

    //Copia del Array para guardar los pacientes
    private lateinit var tempArrayList : ArrayList<Paciente>

    //Adapter global
    private lateinit var adapter: PacienteAdapter

    lateinit var pacientes:MutableList<Paciente>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityPacientesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Mis Pacientes"

        //Flecha para volver a atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Inicializa array para lupa
        tempArrayList = arrayListOf<Paciente>()

        //Inicializa la binding.lista de los pacientes
        pacientes = mutableListOf()

        adapter = PacienteAdapter(this,getPacientes())
        binding.lista.layoutManager = LinearLayoutManager(this)
        binding.lista.adapter = adapter
        adapter.notifyDataSetChanged()




        //Listener para el floatingActionButton de agregar
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this,NuevoPacienteActivity::class.java)
            startActivity(intent)
        }

    }

    @JvmName("getPacientes1")
    fun getPacientes() : MutableList<Paciente>{

        db = FirebaseFirestore.getInstance()
        db.collection("pacientes")
            .get()
            .addOnSuccessListener { value ->
                for (dc : DocumentChange in value?.documentChanges!!) {
                    if(dc.type == DocumentChange.Type.ADDED)
                    {
                        pacientes.add(dc.document.toObject(Paciente::class.java))
                    }
                }

                //Se llena el temp con los pacientes desordenados alfabéticamente
                tempArrayList.addAll(pacientes)

                //Ordenación de la binding.lista de pacientes original
                pacientes.sortBy {
                    it.paciente
                }

                //Ordenación del temporal
                tempArrayList.sortBy {
                    it.paciente
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Algó ocurrió", Toast.LENGTH_SHORT).show()
            }
        return tempArrayList
    }


    /*fun obtenerPacientes() : MutableList<Paciente>{

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "José García",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "1",
                fotos = "foto 1",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Pedro Martínez",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "2",
                fotos = "foto 2",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Yolanda Colin",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "3",
                fotos = "foto 3",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Antonio García",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "4",
                fotos = "foto 4",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Soledad Torres",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "5",
                fotos = "foto 5",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Edgar Velázquez",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "6",
                fotos = "foto 6",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Eduardo López",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "7",
                fotos = "foto 7",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Jennifer Macías",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "8",
                fotos = "foto 8",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Leonardo Rodríguez",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "9",
                fotos = "foto 9",
            )
        )

        pacientes.add(
            Paciente(
                fecha = "01/03/222",
                paciente = "Christopher Solorio",
                doctor = "Dr. Jairo",
                tratamiento = "Ortodoncia",
                costo = "10000",
                idPaciente = "10",
                fotos = "foto 10",
            )
        )

        tempArrayList.addAll(pacientes) // se llena el temp con los pacientes desordenados alfabéticamente

        //Ordenación de la binding.lista de pacientes original
        pacientes.sortBy {
            it.paciente
        }

        //Ordenación del temporal
        tempArrayList.sortBy {
            it.paciente
        }

        return tempArrayList
    }*/

    //Menú para la lupa y que realice búsqueda
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.lupa_menu,menu)

        val item = menu?.findItem(R.id.lupa_item)
        val searchView = item?.actionView as SearchView

        //Para abrir teclado en mayúscula la primer letra
        searchView.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty())
                {
                    pacientes.forEach{
                        if(it.paciente!!.toLowerCase(Locale.getDefault()).contains(searchText))
                        {
                            tempArrayList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                else
                {
                    tempArrayList.clear()
                    tempArrayList.addAll(pacientes)
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    //Listener para hacer acción al darle clic a algún paciente
    override fun click(paciente: Paciente) {
        //Le pasamos los datos del contacto a la ContactoActivity
        val intent = Intent(this,InfoPacienteActivity::class.java)
        intent.putExtra("contactoEnviado",paciente)
        startActivity(intent)
    }
}


