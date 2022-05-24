package com.antonio.codental

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.util.*

class NuevoGastoYEgresoActivity : AppCompatActivity() {

    //Declaración de Variables
    lateinit var btnFecha : Button
    lateinit var tvFecha : TextView
    lateinit var gastoEgreso : EditText
    lateinit var costo : EditText
    lateinit var estatus : EditText
    lateinit var btnGuardar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_gasto_yegreso)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#218eff")))
        supportActionBar!!.setTitle("Nuevo Gasto/Egreso")


        //Enlazamos las variables
        btnFecha = findViewById(R.id.btnFecha)
        tvFecha = findViewById(R.id.tvFecha)
        gastoEgreso = findViewById(R.id.etGastoEgreso)
        costo = findViewById(R.id.etCosto)
        estatus = findViewById(R.id.etEstatus)
        btnGuardar = findViewById(R.id.btnGuardar)

        //Método para el click del botón del calendario
        btnFecha.setOnClickListener {
            val dialogFecha = NuevoPacienteActivity.DatePickerFragment { year, month, day ->
                mostrarFecha(year, month, day) }
            dialogFecha.show(supportFragmentManager, "DatePicker")
        }

        //Listener para el clic al botón de guardar
        btnGuardar.setOnClickListener {
            if(tvFecha.text.isEmpty() || gastoEgreso.text.isEmpty() || costo.text.isEmpty() || estatus.text.isEmpty())
            {
                Toast.makeText(this, "No debe dejar campos vacíos", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "Gasto/Egreso guardado correctamente (prueba)", Toast.LENGTH_SHORT).show()

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

        }


    }

    //Función para mostrar la fecha en el textView de fecha
    private fun mostrarFecha(year: Int, month: Int, day: Int) {
        tvFecha.text = "$day/$month/$year"
    }

    //Datos para el calendario
    class DatePickerFragment(val listener: (year:Int, month:Int, day:Int) -> Unit): DialogFragment(), DatePickerDialog.OnDateSetListener{

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireActivity(),this,year,month,day)
        }

        //Los valores finales que tendrán las variables de la fecha
        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
            listener(year,month+1,day)
        }
    }
}