package com.antonio.codental

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.antonio.codental.databinding.DialogFragmentAbonosBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddDialogFragment : DialogFragment(), DialogInterface.OnShowListener {

    private var _binding: DialogFragmentAbonosBinding? = null
    private val binding get() = _binding!!

    private var positiveButton: Button? = null
    private var negativeButton: Button? = null

    private var abonos: Abonos? = null

    private var mIdTratamiento: String? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { act ->
            _binding = DialogFragmentAbonosBinding.inflate(layoutInflater)


            val builder = MaterialAlertDialogBuilder(act)
                .setTitle("Agregar Abono")
                .setPositiveButton("Agregar", null)
                .setNegativeButton("Cancelar", null)
                .setView(binding.root)

            val dialog = builder.create()
            dialog.setOnShowListener(this)

            return dialog

        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onShow(dialogInterface: DialogInterface?) {
        initAbonos()

        val dialog = dialog as? AlertDialog
        dialog?.let {
            positiveButton = it.getButton(Dialog.BUTTON_POSITIVE)
            negativeButton = it.getButton(Dialog.BUTTON_NEGATIVE)
            (activity as? AbonosInterfazAux)?.obtenerUltimoActualizar()?.let {
                positiveButton?.isEnabled = it
            }
            abonos?.let {
                positiveButton?.text = "Actualizar"
            }
            binding.tvCosto.text = (activity as? AbonosInterfazAux)?.obtenerCosto().toString()
            binding.tvTratamiento.text =
                (activity as? AbonosInterfazAux)?.obtenerTratamiento() ?: "No existe"

            //Método para el click del botón del segundo calendario 2
            binding.btnFecha2.setOnClickListener {
                val dialogFecha = NuevoAbonoActivity.DatePickerFragment2 { year, month, day ->
                    mostrarFecha2(year, month, day)
                }
                dialogFecha.show(parentFragmentManager, "DatePicker")
            }



            positiveButton?.setOnClickListener {

                enableUI(false)

                if (abonos == null) {
                    val abonos = Abonos(
                        fecha = SimpleDateFormat(
                            "dd/MM/yyyy - h:mm:ss a",
                            Locale.getDefault()
                        ).format(Date()),
                        pieza = binding.etPieza.text.toString().trim(),
                        tratamiento = binding.tvTratamiento.text.toString().trim(),
                        costoTotal = binding.tvCosto.text.toString().toDouble(),
                        abono = binding.etAbono.text.toString().toDouble(),
                        saldo = binding.tvSaldo.text.toString().toDouble(),
                        saldoAnterior = binding.tvSaldoAnterior.text.toString().toDouble(),
                        firma = binding.etFirma.text.toString().trim(),
                        proximaCita = binding.tvFecha2.text.toString().trim(),
                        horaProximaCita = binding.etHoraProxCita.text.toString().trim(),
                        timestamp = Date().time
                    )
                    save(abonos)
                } else {
                    abonos?.apply {
                        fecha =
                            SimpleDateFormat("dd/MM/yyyy - h:mm:ss a", Locale.getDefault()).format(
                                Date()
                            )
                        pieza = binding.etPieza.text.toString().trim()
                        tratamiento = binding.tvTratamiento.text.toString().trim()
                        costoTotal = binding.tvCosto.text.toString().toDouble()
                        abono = binding.etAbono.text.toString().toDouble()
                        saldo = binding.tvSaldo.text.toString().toDouble()
                        saldoAnterior = binding.tvSaldoAnterior.text.toString().toDouble()
                        firma = binding.etFirma.text.toString().trim()
                        proximaCita = binding.tvFecha2.text.toString().trim()
                        horaProximaCita = binding.etHoraProxCita.text.toString().trim()
                        timestamp = Date().time

                        update(this)
                    }
                }

            }

            negativeButton?.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun initAbonos() {
        mIdTratamiento = (activity as? AbonosInterfazAux)?.obtenerIdTratamiento()
        abonos = (activity as? AbonosInterfazAux)?.obtenerAbonoSeleccionado()
        if (abonos == null) { //Presioné el botón agregar
            foco(true)
            if ((activity as AbonosInterfazAux).hayAbonos()) { //ya hay abonos
                binding.tvSaldo.text = (activity as? AbonosInterfazAux)?.obtenerSaldo().toString()

            } else { //No hay ningun abono en la lista
                binding.tvSaldo.text = (activity as? AbonosInterfazAux)?.obtenerCosto().toString()
            }
        } else {
            foco(false)
            dialog?.setTitle("Actualizar Abono")
            binding.tvFecha.text = abonos?.fecha
            binding.etPieza.setText(abonos?.pieza)
            binding.tvTratamiento.text = abonos?.tratamiento
            //binding.tvCosto.text = (activity as? AbonosInterfazAux)?.obtenerCosto().toString()
            binding.etAbono.setText(abonos?.abono.toString())
            binding.tvSaldo.text = abonos?.saldo.toString()
            binding.tvSaldoAnterior.text = abonos?.saldoAnterior.toString()
            binding.etFirma.setText(abonos?.firma)
            binding.tvFecha2.text = abonos?.proximaCita
            binding.etHoraProxCita.setText(abonos?.horaProximaCita)
        }
    }

    private fun foco(opcion: Boolean) {
        if (opcion) {
            binding.etAbono.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
                if (b) {
                    binding.tvSaldo.text =
                        (activity as? AbonosInterfazAux)?.obtenerSaldo().toString()
                } else {
                    if (binding.etAbono.text.isNullOrBlank()) {
                        binding.tvSaldo.text =
                            (activity as? AbonosInterfazAux)?.obtenerSaldo().toString()
                    } else {
                        if (binding.etAbono.text.toString()
                                .toDouble() > binding.tvSaldo.text.toString().toDouble()
                        ) {
                            binding.etAbono.setText("0.0")// se limpia el campo del abono
                            Toast.makeText(
                                activity,
                                "Error, el abono no debe ser mayor al saldo actual",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        //Se le pone automáticamente al tvSaldoAnterior el saldo actual, ya que el saldo actual cambiará, y así se respaldará.
                        binding.tvSaldoAnterior.text = binding.tvSaldo.text

                        val abono = binding.etAbono.text.toString().toDouble()
                        val saldo = binding.tvSaldo.text.toString().toDouble()
                        val resultado = saldo - abono
                        binding.tvSaldo.text = resultado.toString()
                    }
                }
            }
        } else {
            binding.etAbono.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
                if (b) {
                    binding.tvSaldo.text =
                        (activity as? AbonosInterfazAux)?.obtenerSaldo().toString()
                } else {
                    if (binding.etAbono.text.isNullOrBlank()) {
                        binding.tvSaldo.text =
                            (activity as? AbonosInterfazAux)?.obtenerSaldo().toString()
                    } else {
                        if (binding.etAbono.text.toString()
                                .toDouble() > binding.tvSaldoAnterior.text.toString().toDouble()
                        ) {

                            if (binding.tvSaldo.text == "0.0") {
                                val abono = binding.etAbono.text.toString().toDouble()
                                val saldoAnterior =
                                    binding.tvSaldoAnterior.text.toString().toDouble()
                                val resultado = saldoAnterior - abono
                                binding.tvSaldo.text = resultado.toString()
                            } else {
                                binding.etAbono.setText("0.0")// se limpia el campo del abono
                                Toast.makeText(
                                    activity,
                                    "Error, el abono no debe ser mayor al saldo actual",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        //Se le pone automáticamente al tvSaldoAnterior el saldo actual, ya que el saldo actual cambiará, y así se respaldará.
                        //binding.tvSaldoAnterior.text = binding.tvSaldo.text

                        val abono = binding.etAbono.text.toString().toDouble()
                        val saldoAnterior = binding.tvSaldoAnterior.text.toString().toDouble()
                        val resultado = saldoAnterior - abono
                        binding.tvSaldo.text = resultado.toString()
                    }
                }
            }
        }
    }


    private fun save(abonos: Abonos) {
        mIdTratamiento?.let { id ->
            val db = Firebase.firestore
            db.collection("tratamientos").document(id).collection("abonos")
                .add(abonos)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Abono Agregado.", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Error al insertar.", Toast.LENGTH_SHORT).show()
                    enableUI(true)
                }
        }

    }

    private fun update(abonos: Abonos) {
        mIdTratamiento?.let {
            val db = Firebase.firestore

            abonos.idAbono?.let { id ->
                db.collection("tratamientos").document(it).collection("abonos")
                    .document(id)
                    .set(abonos)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Abonoso actualizado.", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Error al actualizar.", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    private fun enableUI(enable: Boolean) {
        positiveButton?.isEnabled = enable
        negativeButton?.isEnabled = enable

        with(binding) {
            //btnFecha.isEnabled = enable
            etPieza.isEnabled = enable
            tvTratamiento.isEnabled = enable
            tvCosto.isEnabled = enable
            etAbono.isEnabled = enable
            tvSaldo.isEnabled = enable
            tvSaldoAnterior.isEnabled = enable
            etFirma.isEnabled = enable
            btnFecha2.isEnabled = enable
            etHoraProxCita.isEnabled = enable

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*//Función para mostrar la fecha en el textView de fecha
    private fun mostrarFecha1(year: Int, month: Int, day: Int) {
        binding.tvFecha.text = "$day/$month/$year"
    }*/

    //Función para mostrar la fecha en el textView de fecha
    private fun mostrarFecha2(year: Int, month: Int, day: Int) {
        binding.tvFecha2.text = "$day/$month/$year"
    }

    /*//Datos para el calendario 1
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
    }*/

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
}