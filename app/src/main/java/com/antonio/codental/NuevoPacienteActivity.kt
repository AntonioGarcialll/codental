package com.antonio.codental

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class NuevoPacienteActivity : AppCompatActivity() {

    //Declaración de Variables
    var nombreDoctor = ""
    var arrayListDoctores = arrayListOf<Doctores>()
    lateinit var btnFecha: Button
    lateinit var tvFecha: TextView
    lateinit var etPaciente: EditText
    lateinit var etDoctor: EditText
    lateinit var etTratamiento: EditText
    lateinit var etCosto: EditText
    lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_paciente)


        //Obtengo los datos del paciente que me mandan a través del intent extra
        val objIntent: Intent = intent
        var fechaObtenido = objIntent.getStringExtra("tvFecha")
        var pacienteObtenido = objIntent.getStringExtra("tvPaciente")
        var actualizarObtenido = objIntent.getStringExtra("actualizar")
        var miIdPacienteObtenido = objIntent.getStringExtra("miIdPaciente")
        var nombreDoctorRecibido = objIntent.getStringExtra("tvDoctor")
        nombreDoctor = objIntent.getStringExtra("nombreDoctor").toString()

        //Inicializo variables y botones
        tvFecha = findViewById(R.id.tvFecha)
        etPaciente = findViewById(R.id.etPaciente)
        etDoctor = findViewById(R.id.etDoctor)
        btnFecha = findViewById(R.id.btnFecha)
        btnGuardar = findViewById(R.id.btnGuardar)

        //Seteo el nombre del doctor
        //FirebaseAuth.getInstance().currentUser?.let { etDoctor.setText("Dr. " + it.displayName) }
        etDoctor.setText(nombreDoctor)


        if (actualizarObtenido == "actualizame") {
            supportActionBar!!.title = "Editar Paciente"
            //Asigna los valores a las cajas de texto para que puedan ser modificadas
            tvFecha.text = fechaObtenido
            etPaciente.setText(pacienteObtenido)
            etDoctor.setText(nombreDoctorRecibido)
            //etTratamiento.setText(tratamientoObtenido)
            //etCosto.setText(costoObtenido)
        } else {

            supportActionBar!!.title = "Agregar Paciente"
        }


        //Variable para la base de datos de Firebase
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        //Método para el click del botón del calendario
        btnFecha.setOnClickListener {
            val dialogFecha =
                DatePickerFragment { year, month, day -> mostrarFecha(year, month, day) }
            dialogFecha.show(supportFragmentManager, "DatePicker")
        }


        //Método para el clic del botón de Guardar
        btnGuardar.setOnClickListener {
            if (tvFecha.text.isEmpty() || etPaciente.text.isEmpty() || etDoctor.text.isEmpty()) {
                Toast.makeText(this, "Error, no debe dejar campos vacíos.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //Se quiere actualizar los datos del paciente
                if (actualizarObtenido == "actualizame") {
                    db.collection("pacientes").document(miIdPacienteObtenido!!).update(
                        "fecha", tvFecha.text.toString(),
                        "paciente", etPaciente.text.toString(),
                        "doctor", etDoctor.text.toString()
                    )
                    Toast.makeText(this, "Datos de paciente actualizados", Toast.LENGTH_SHORT)
                        .show()
                    //Se cierra actividad y regresa a la activity de la lista de los pacientes
                    val i = Intent(applicationContext, PacientesActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    //Se guarda un nuevo paciente en la base de datos
                    val data = hashMapOf(
                        "fecha" to tvFecha.text.toString(),
                        "paciente" to etPaciente.text.toString(),
                        "doctor" to etDoctor.text.toString(),
                        "idPaciente" to "a",
                        "idDoctor" to FirebaseAuth.getInstance().currentUser?.let { it.uid }
                    )
                    db.collection("pacientes").add(data)
                        .addOnSuccessListener { documentReference ->

                            //Guardo el ID automatico en la variable miId
                            //val miId = db.collection("pacientes").document().id // el de muchos caracteres
                            var miId = documentReference.id

                            //Actualizo el id = "a" por el que me generó automatico
                            db.collection("pacientes").document(miId).update("idPaciente", miId)

                            Toast.makeText(
                                this,
                                "Paciente agregado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            //Toast.makeText(this, "Paciente agregado correctamente con el id: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                            //Se cierra actividad y regresa a la activity del listado de todos los pacientes
                            val i = Intent(
                                applicationContext,
                                PacientesActivity::class.java
                            )        // Specify any activity here e.g. home or splash or login etc
                            startActivity(i)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Ocurrió un error: $e", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }


    //Función para mostrar la fecha en el textView de fecha
    private fun mostrarFecha(year: Int, month: Int, day: Int) {
        tvFecha.text = "$day/$month/$year"
    }

    //Datos para el calendario
    class DatePickerFragment(val listener: (year: Int, month: Int, day: Int) -> Unit) :
        DialogFragment(), DatePickerDialog.OnDateSetListener {

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireActivity(), this, year, month, day)
        }

        //Los valores finales que tendrán las variables de la fecha
        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
            listener(year, month + 1, day)
        }
    }
}