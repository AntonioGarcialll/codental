package com.antonio.codental

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.antonio.codental.databinding.ActivityGastosYegresosBinding
import com.antonio.codental.databinding.ActivityInfoPacienteBinding
import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.android.synthetic.main.activity_info_paciente.*

class InfoPacienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoPacienteBinding

    //Variables globales
    lateinit var miIdPaciente : String

    //Variable para la base de datos
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Cosas de binding
        binding = ActivityInfoPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        title = "Paciente"

        //Flecha para volver a atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Inicializamos la variable para la base de datos
        db = FirebaseFirestore.getInstance()

        //Recibimos el paciente seleccionado por medio del intent extra
        val pacienteRecibido = intent.getSerializableExtra("contactoEnviado") as Paciente

        //Le asignamos los valores del paciente seleccionado a los campos correspondientes de esta activity
        //Toast.makeText(this, pacienteRecibido.toString(), Toast.LENGTH_SHORT).show()
        binding.tvFecha.text = pacienteRecibido.fecha
        binding.tvPaciente.text = pacienteRecibido.paciente
        binding.tvDoctor.text = pacienteRecibido.doctor
        //binding.tvTratamiento.text = pacienteRecibido.tratamiento
        //binding.tvCosto.text = pacienteRecibido.costo
        //binding.tvFotos.text = pacienteRecibido.fotos
        miIdPaciente = pacienteRecibido.idPaciente!! //id del paciente al que se le dió clic
    }

    //Función para sobreescribir el menú por el que ya tiene los iconos de editar, eliminar y abonos
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.paciente_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Switch para hacer acciones al dar clic a los iconos de editar, eliminar o abonos.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.edit_item ->
            {
                //Se abre la activity para editar los datos, y se le pasa el paciente por el extra.
                val intent = Intent(this,NuevoPacienteActivity::class.java)
                intent.putExtra("tvFecha",binding.tvFecha.text.toString())
                intent.putExtra("tvPaciente",binding.tvPaciente.text.toString())
                intent.putExtra("tvDoctor",binding.tvDoctor.text.toString())
                //intent.putExtra("tvTratamiento",binding.tvTratamiento.text.toString())
                //intent.putExtra("tvCosto",binding.tvCosto.text.toString())
                //intent.putExtra("tvFotos",binding.tvFotos.text.toString())
                intent.putExtra("actualizar","actualizame")
                intent.putExtra("miIdPaciente",miIdPaciente)
                startActivity(intent)
                //Toast.makeText(this, "Le di clic a editar", Toast.LENGTH_SHORT).show()
            }

            R.id.delete_item ->
            {
                //AlerDialog para eliminar paciente
                AlertDialog.Builder(this).apply {
                    setTitle("Eliminar Paciente")
                    setMessage("¿Está seguro que desea eliminar este paciente?")
                    setPositiveButton("Aceptar"){ _: DialogInterface, _: Int ->
                        //Eliminamos el paciente con el id especificado
                        db.collection("pacientes").document(miIdPaciente)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this@InfoPacienteActivity, "Paciente elimiando correctamente", Toast.LENGTH_SHORT).show()
                                //Se cierra actividad y regresa a la activity main
                                val i = Intent(applicationContext, PacientesActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                i.putExtra("EXIT", true)
                                startActivity(i)
                                finish() }
                            .addOnFailureListener {
                                Toast.makeText(this@InfoPacienteActivity, "Ocurrió un error al eliminar al paciente", Toast.LENGTH_SHORT).show()
                                //Se cierra actividad y regresa a la activity main
                                val i = Intent(applicationContext, PacientesActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                i.putExtra("EXIT", true)
                                startActivity(i)
                                finish() }
                        }
                        setNegativeButton("Cancelar",null)
                    }.show()
            }

            R.id.tratamientos_item ->
            {
                //Toast.makeText(this, "Le di clic a abonos", Toast.LENGTH_SHORT).show()
                //Voy a tener que mandar el id del paciente para poder ver sus abonos.
                val intent = Intent(this,TratamientosActivity::class.java)
                intent.putExtra("miIdPaciente",miIdPaciente)
                startActivity(intent)
                finish()
            }


        }
        return super.onOptionsItemSelected(item)
    }
}