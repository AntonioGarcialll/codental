package com.antonio.codental

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.antonio.codental.databinding.ActivityInfoGastoYegresoBinding
import com.google.firebase.firestore.FirebaseFirestore

class InfoGastoYEgresoActivity : AppCompatActivity() {

    //Variables

    private lateinit var binding: ActivityInfoGastoYegresoBinding

    //Variables globales
    lateinit var miIdGastoEgreso: String

    //Variable para la base de datos
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cosas de binding
        binding = ActivityInfoGastoYegresoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializamos la variable para la base de datos
        db = FirebaseFirestore.getInstance()

        //Recibimos el gastoEgreso seleccionado por medio del intent extra
        val gastoEgresoRecibido =
            intent.getSerializableExtra("gastoEgresoEnviado") as GastosYEgresos

        //Le asignamos los valores del paciente seleccionado a los campos correspondientes de esta activity
        binding.tvFecha.text = gastoEgresoRecibido.fecha
        binding.tvGastoEgreso.text = gastoEgresoRecibido.gastoEgreso
        binding.tvCosto.text = gastoEgresoRecibido.costo
        binding.tvEstatus.text = gastoEgresoRecibido.estatus
        miIdGastoEgreso =
            gastoEgresoRecibido.idGastoEgreso!! //id del gastoEgreso al que se le dió clic
    }

    //Función para sobreescribir el menú por el que ya tiene los iconos de editar y eliminar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gasto_egreso_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Switch para hacer acciones al dar clic a los iconos de editar, eliminar o abonos.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_item -> {
                //Se abre la activity para editar los datos, y se le pasa el gastoEgreso por el extra.
                val intent = Intent(this, NuevoGastoYEgresoActivity::class.java)
                intent.putExtra("tvFecha", binding.tvFecha.text.toString())
                intent.putExtra("tvGastoEgreso", binding.tvGastoEgreso.text.toString())
                intent.putExtra("tvCosto", binding.tvCosto.text.toString())
                intent.putExtra("tvEstatus", binding.tvEstatus.text.toString())
                intent.putExtra("actualizar", "actualizame")
                intent.putExtra("miIdGastoEgreso", miIdGastoEgreso)
                startActivity(intent)
            }

            R.id.delete_item -> {
                //AlerDialog para eliminar paciente
                AlertDialog.Builder(this).apply {
                    setTitle("Eliminar Gasto/Egreso")
                    setMessage("¿Está seguro que desea eliminar este Gasto/Egreso?")
                    setPositiveButton("Aceptar") { _: DialogInterface, _: Int ->
                        //Eliminamos el gastoEgreso con el id especificado
                        db.collection("gastosEgresos").document(miIdGastoEgreso)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@InfoGastoYEgresoActivity,
                                    "Gasto/Egreso Elimiando Correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //Se cierra actividad y regresa a la activity main
                                val i =
                                    Intent(applicationContext, GastosYEgresosActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                i.putExtra("EXIT", true)
                                startActivity(i)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@InfoGastoYEgresoActivity,
                                    "Ocurrió Un Error",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //Se cierra actividad y regresa a la activity main
                                val i =
                                    Intent(applicationContext, GastosYEgresosActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                i.putExtra("EXIT", true)
                                startActivity(i)
                                finish()
                            }
                    }
                    setNegativeButton("Cancelar", null)
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}