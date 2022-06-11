package com.antonio.codental

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class NuevoGastoYEgresoActivity : AppCompatActivity() {

    //Declaración de Variables
    lateinit var btnFecha: Button
    lateinit var tvFecha: TextView
    lateinit var gastoEgreso: EditText
    lateinit var costo: EditText
    lateinit var estatus: EditText
    lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_gasto_yegreso)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))

        //Flecha para volver a atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Obtengo los datos del gastoEgreso que me mandan
        val objIntent: Intent = intent
        var tvFechaObtenido = objIntent.getStringExtra("tvFecha")
        var tvGastoEgresoObtenido = objIntent.getStringExtra("tvGastoEgreso")
        var tvCostoObtenido = objIntent.getStringExtra("tvCosto")
        var tvEstatusObtenido = objIntent.getStringExtra("tvEstatus")
        var actualizarObtenido = objIntent.getStringExtra("actualizar")
        var miIdGastoEgresoObtenido = objIntent.getStringExtra("miIdGastoEgreso")


        //Enlazamos las variables
        btnFecha = findViewById(R.id.btnFecha)
        tvFecha = findViewById(R.id.tvFecha)
        gastoEgreso = findViewById(R.id.etGastoEgreso)
        costo = findViewById(R.id.etCosto)
        estatus = findViewById(R.id.etEstatus)
        btnGuardar = findViewById(R.id.btnGuardar)

        if (actualizarObtenido == "actualizame") {
            supportActionBar!!.title = "Editar Gasto/Egreso"
            //Asigna los valores a las cajas de texto para que puedan ser modificadas
            tvFecha.text = tvFechaObtenido
            gastoEgreso.setText(tvGastoEgresoObtenido)
            costo.setText(tvCostoObtenido)
            estatus.setText(tvEstatusObtenido)
        } else {
            supportActionBar!!.title = "Agregar Gasto/Egreso"
        }


        //Variable para la base de datos de Firebase
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        //Método para el click del botón del calendario
        btnFecha.setOnClickListener {
            val dialogFecha = NuevoPacienteActivity.DatePickerFragment { year, month, day ->
                mostrarFecha(year, month, day)
            }
            dialogFecha.show(supportFragmentManager, "DatePicker")
        }

        //Listener para el clic al botón de guardar
        btnGuardar.setOnClickListener {
            if (tvFecha.text.isEmpty() || gastoEgreso.text.isEmpty() || costo.text.isEmpty() || estatus.text.isEmpty()) {
                Toast.makeText(this, "Error, no debe dejar campos vacíos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //Se quiere actualizar los datos del paciente
                if (actualizarObtenido == "actualizame") {
                    db.collection("gastosEgresos").document(miIdGastoEgresoObtenido!!).update(
                        "fecha", tvFecha.text.toString(),
                        "gastoEgreso", gastoEgreso.text.toString(),
                        "costo", costo.text.toString(),
                        "estatus", estatus.text.toString()
                    )
                    Toast.makeText(this, "Datos de Gasto/Egreso Actualizados", Toast.LENGTH_SHORT)
                        .show()
                    //Se cierra actividad y regresa a la activity de la lista de los pacientes
                    val i = Intent(applicationContext, GastosYEgresosActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("EXIT", true)
                    startActivity(i)
                    finish()
                } else {
                    //No se le dió clic al botón del lápiz para editar, o sea que se requiere agregar un nuevo gastoEgreso.
                    //Se guarda el nuevo gasto/egreso en la base de datos
                    val data = hashMapOf(
                        "fecha" to tvFecha.text.toString(),
                        "gastoEgreso" to gastoEgreso.text.toString(),
                        "costo" to costo.text.toString(),
                        "estatus" to estatus.text.toString(),
                        "idGastoEgreso" to "a"
                    )
                    db.collection("gastosEgresos").add(data)
                        .addOnSuccessListener { documentReference ->

                            var miId = documentReference.id

                            db.collection("gastosEgresos").document(miId)
                                .update("idGastoEgreso", miId)
                            Toast.makeText(
                                this,
                                "Gasto/Egreso Agregado Correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = Intent(
                                applicationContext,
                                GastosYEgresosActivity::class.java
                            )        // Specify any activity here e.g. home or splash or login etc
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            i.putExtra("EXIT", true)
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