package com.antonio.codental

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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

    private var fechaActual : String ?= null


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
            binding.tvCosto.setText((activity as? AbonosInterfazAux)?.obtenerCosto().toString())
            binding.tvTratamiento.setText((activity as? AbonosInterfazAux)?.obtenerTratamiento() ?: "No existe")


            //Método para el click del botón del segundo calendario 2
            binding.btnFecha2.setOnClickListener {
                val dialogFecha = NuevoAbonoActivity.DatePickerFragment2 { year, month, day ->
                    mostrarFecha2(year, month, day)
                }
                dialogFecha.show(parentFragmentManager, "DatePicker")
            }

            //Método para el clic del reloj
            binding.btnReloj.setOnClickListener {
                val currentTime = Calendar.getInstance()
                val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
                val startMinute = currentTime.get(Calendar.MINUTE)

                TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener{view, hourOfDay, minute ->
                    binding.etHoraProxCita.setText("$hourOfDay:$minute")
                }, startHour, startMinute, false).show()
            }



            positiveButton?.setOnClickListener {
                if(binding.tvFecha.text.toString().isEmpty() || binding.etPieza.text.toString().isEmpty() || binding.tvTratamiento.text.toString().isEmpty() ||
                    binding.tvCosto.text.toString().isEmpty() || binding.etAbono.text.toString().isEmpty() || binding.tvSaldo.text.toString().isEmpty() ||
                    binding.tvSaldoAnterior.text.toString().isEmpty() || binding.etFirma.text.toString().isEmpty() || binding.etHojaClinica.text.toString().isEmpty() ||
                    binding.tvFecha2.text.toString().isEmpty() || binding.etHoraProxCita.text.toString().isEmpty())
                {
                    Toast.makeText(activity, "Error, no debe dejar campos vacíos.", Toast.LENGTH_SHORT).show()
                }
                else
                {
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
                            hojaClinica = binding.etHojaClinica.text.toString().trim(),
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
                            hojaClinica = binding.etHojaClinica.text.toString().trim()
                            proximaCita = binding.tvFecha2.text.toString().trim()
                            horaProximaCita = binding.etHoraProxCita.text.toString().trim()
                            timestamp = Date().time

                            update(this)
                        }
                    }
                }
            }

            negativeButton?.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun initAbonos() {
        //Cuando se abra el dialog para agregar un nuevo abono se va a poner la fecha actual
        //automáticamente
        fechaActual = SimpleDateFormat(
            "dd/MM/yyyy - h:mm:ss a",
            Locale.getDefault()
        ).format(Date())
        binding.tvFecha.setText(fechaActual)

        //Pongo en ceros el saldo anterior, cambiará hasta que se ingrese un abono
        binding.tvSaldoAnterior.setText("0.0")

        mIdTratamiento = (activity as? AbonosInterfazAux)?.obtenerIdTratamiento()
        abonos = (activity as? AbonosInterfazAux)?.obtenerAbonoSeleccionado()
        if (abonos == null) { //Presioné el botón agregar
            foco(true)
            if ((activity as AbonosInterfazAux).hayAbonos()) { //ya hay abonos
                binding.tvSaldo.setText((activity as? AbonosInterfazAux)?.obtenerSaldo().toString())

            } else { //No hay ningun abono en la lista
                binding.tvSaldo.setText((activity as? AbonosInterfazAux)?.obtenerCosto().toString())
            }
        } else {
            foco(false)
            dialog?.setTitle("Actualizar Abono")
            binding.tvFecha.setText(abonos?.fecha)
            binding.etPieza.setText(abonos?.pieza)
            binding.tvTratamiento.setText(abonos?.tratamiento)
            //binding.tvCosto.text = (activity as? AbonosInterfazAux)?.obtenerCosto().toString()
            binding.etAbono.setText(abonos?.abono.toString())
            binding.tvSaldo.setText(abonos?.saldo.toString())
            binding.tvSaldoAnterior.setText(abonos?.saldoAnterior.toString())
            binding.etFirma.setText(abonos?.firma)
            binding.etHojaClinica.setText(abonos?.hojaClinica.toString())
            binding.tvFecha2.setText(abonos?.proximaCita)
            binding.etHoraProxCita.setText(abonos?.horaProximaCita)
        }
    }

    private fun foco(opcion: Boolean) {
        if (opcion) {
            binding.etAbono.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
                if (b) {
                    binding.tvSaldo.setText((activity as? AbonosInterfazAux)?.obtenerSaldo().toString())

                } else {
                    if (binding.etAbono.text.isNullOrBlank()) {
                        binding.tvSaldo.setText((activity as? AbonosInterfazAux)?.obtenerSaldo().toString())

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
                        binding.tvSaldo.setText(resultado.toString())
                    }
                }
            }
        } else {
            binding.etAbono.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
                if (b) {
                    binding.tvSaldo.setText((activity as? AbonosInterfazAux)?.obtenerSaldo().toString())

                } else {
                    if (binding.etAbono.text.isNullOrBlank()) {
                        binding.tvSaldo.setText((activity as? AbonosInterfazAux)?.obtenerSaldo().toString())

                    } else {
                        if (binding.etAbono.text.toString()
                                .toDouble() > binding.tvSaldoAnterior.text.toString().toDouble()
                        ) {

                            if (binding.tvSaldo.text.toString() == "0.0") {
                                val abono = binding.etAbono.text.toString().toDouble()
                                val saldoAnterior =
                                    binding.tvSaldoAnterior.text.toString().toDouble()
                                val resultado = saldoAnterior - abono
                                binding.tvSaldo.setText(resultado.toString())
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
                        binding.tvSaldo.setText(resultado.toString())
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
                        Toast.makeText(activity, "Abono Actualizado.", Toast.LENGTH_SHORT).show()
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
            etHojaClinica.isEnabled = enable
            btnFecha2.isEnabled = enable
            etHoraProxCita.isEnabled = enable

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Función para mostrar la fecha en el textView de fecha
    private fun mostrarFecha2(year: Int, month: Int, day: Int) {
        binding.tvFecha2.setText("$day/$month/$year")
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
}