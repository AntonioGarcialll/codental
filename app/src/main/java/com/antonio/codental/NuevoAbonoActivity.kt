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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class NuevoAbonoActivity : AppCompatActivity() {

    //Declaración de variables
    lateinit var btnFecha: Button
    lateinit var tvFecha: TextView
    lateinit var etPieza: EditText
    lateinit var etTratamiento: EditText
    lateinit var etCosto: EditText
    lateinit var etAbono: EditText
    lateinit var etSaldo: EditText
    lateinit var etFirma: EditText
    lateinit var btnFecha2: Button
    lateinit var tvFecha2: TextView
    lateinit var etHoraProxCita: EditText
    lateinit var btnGuardar: Button
    var db = Firebase.firestore
    var saldo: String? = null
    var saldoFinal: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_abono)

        //Enlazamos las variables
        btnFecha = findViewById(R.id.btnFecha)
        tvFecha = findViewById(R.id.tvFecha)
        etPieza = findViewById(R.id.etPieza)
        etTratamiento = findViewById(R.id.etTratamiento)
        etCosto = findViewById(R.id.etCosto)
        etAbono = findViewById(R.id.etAbono)
        etSaldo = findViewById(R.id.etSaldo)
        etFirma = findViewById(R.id.etFirma)
        btnFecha2 = findViewById(R.id.btnFecha2)
        tvFecha2 = findViewById(R.id.tvFecha2)
        etHoraProxCita = findViewById(R.id.etHoraProxCita)
        btnGuardar = findViewById(R.id.btnGuardar)

        //Obtengo el id del tratamiento
        val objIntent: Intent = intent
        var idTratamiento = objIntent.getStringExtra("idTratamiento")
        var tratamiento = objIntent.getStringExtra("tratamiento")
        var costo = objIntent.getStringExtra("costo")
        var estado = objIntent.getStringExtra("estado")

        //Asigno los valores al xml del tratamiento y del costo
        etTratamiento.setText(tratamiento.toString())
        etCosto.setText(costo.toString())


        //Método para el click del botón del primer calendario 1
        btnFecha.setOnClickListener {
            val dialogFecha = NuevoAbonoActivity.DatePickerFragment1 { year, month, day ->
                mostrarFecha1(year, month, day)
            }
            dialogFecha.show(supportFragmentManager, "DatePicker")
        }

        //Método para el click del botón del segundo calendario 2
        btnFecha2.setOnClickListener {
            val dialogFecha = NuevoAbonoActivity.DatePickerFragment2 { year, month, day ->
                mostrarFecha2(year, month, day)
            }
            dialogFecha.show(supportFragmentManager, "DatePicker")
        }

        //Listener para el boton de guardar
        btnGuardar.setOnClickListener {
            if (tvFecha.text.isEmpty() || etPieza.text.isEmpty() || etTratamiento.text.isEmpty() || etCosto.text.isEmpty() ||
                etAbono.text.isEmpty() || etFirma.text.isEmpty() || tvFecha2.text.isEmpty() ||
                etHoraProxCita.text.isEmpty()
            ) {
                Toast.makeText(this, "No debe dejar campos vacíos", Toast.LENGTH_SHORT).show()
            } else {
                //Se guarda un nuevo abono

                if (estado == "vacio") {

                    //Se evalúa si es el primer abono, si es así, se hace la resta del costo del tratamiento menos el abono.
                    saldo = ((etCosto.text.toString().toInt()) - (etAbono.text.toString()
                        .toInt())).toString().trim()
                    saldoFinal = saldo

                    //primerSaldo(saldoFinal.toString(),idTratamiento.toString())
                } else {
                    //Se evalúa si ya no es el primer abono, si es así, se hace la resta del saldo actual menos el abono.
                    saldo = ((etSaldo.text.toString().toInt()) - (etAbono.text.toString()
                        .toInt())).toString().trim()
                }
                val abono = hashMapOf(
                    "fecha" to tvFecha.text.toString().trim(),
                    "pieza" to etPieza.text.toString().trim(),
                    "tratamiento" to etTratamiento.text.toString().trim(),
                    "costo" to etCosto.text.toString().trim(),
                    "abono" to etAbono.text.toString().trim(),
                    "saldo" to saldo,
                    "firma" to etFirma.text.toString().trim(),
                    "proximaCita" to tvFecha2.text.toString() + " - " + etHoraProxCita.text.toString()
                        .trim(),
                    "idTratamiento" to idTratamiento.toString().trim(),
                    "idAbono" to "a"
                )
                db.collection("abonos").add(abono)
                    .addOnSuccessListener { documentReference ->

                        //Guardo el ID automatico en la variable miId
                        //val miId = db.collection("pacientes").document().id // el de muchos caracteres
                        var miId = documentReference.id

                        //Actualizo el id = "a" por el que me generó automatico
                        db.collection("abonos").document(miId).update("idAbono", miId)

                        Toast.makeText(
                            this,
                            "Abono agregado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        val i = Intent(
                            applicationContext,
                            AbonosActivity::class.java
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

    //Función para mostrar la fecha en el textView de fecha
    private fun mostrarFecha1(year: Int, month: Int, day: Int) {
        tvFecha.text = "$day/$month/$year"
    }

    //Función para mostrar la fecha en el textView de fecha
    private fun mostrarFecha2(year: Int, month: Int, day: Int) {
        tvFecha2.text = "$day/$month/$year"
    }

    //Datos para el calendario 1
    class DatePickerFragment1(val listener: (year: Int, month: Int, day: Int) -> Unit) :
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

    //Datos para el calendario 2
    class DatePickerFragment2(val listener: (year: Int, month: Int, day: Int) -> Unit) :
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

    /*fun primerSaldo(primerSaldo : String, idTratamiento : String)
    {
        val id = Firebase.firestore.collection("primerSaldo").document().id
        val saldoPrimero = hashMapOf(
            "primerSaldo" to primerSaldo.toString().trim(),
            "idTratamiento" to idTratamiento.toString().trim(),
            "idPrimerSaldo" to id
        )
        db.collection("primerSaldo").document(id).set(saldoPrimero)
            .addOnSuccessListener { documentReference ->

                //Guardo el ID automatico en la variable miId
                //val miId = db.collection("pacientes").document().id // el de muchos caracteres


                Toast.makeText(
                    this,
                    "Primer Saldo Agregado Correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ocurrió un error: $e", Toast.LENGTH_SHORT).show()
            }
    }*/
}