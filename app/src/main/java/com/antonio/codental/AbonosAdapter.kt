package com.antonio.codental

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ItemAbonosBinding

class AbonosAdapter(
    private val listener: AbonosInterfaz,
    private val listaAbonos: MutableList<Abonos>
) :
    RecyclerView.Adapter<AbonosAdapter.AbonosViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbonosAdapter.AbonosViewHolder {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_paciente
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_abonos, parent, false)
        return AbonosViewHolder(layout)
    }

    override fun onBindViewHolder(holder: AbonosAdapter.AbonosViewHolder, position: Int) {
        val abono = listaAbonos[position]
        holder.listenerItem(abono)

        //Se asignan los componentes del item_pacientes con los de la lista
        holder.binding.tvFecha.text = "Fecha: " + abono.fecha
        holder.binding.tvAbono.text = "Abono: " + abono.abono
        holder.binding.tvSaldo.text = "Saldo: " + abono.saldo
    }

    //Obtengo el tamaño de la lista
    override fun getItemCount(): Int = listaAbonos.size

    fun add(abonos: Abonos) {

        if (!listaAbonos.contains(abonos)) {
            listaAbonos.add(0,abonos)
            //listaAbonos.sortByDescending { it.timestamp }
            notifyItemInserted(0)
            calcTotal(abonos)
        } else {
            update(abonos)
        }
    }

    fun update(abonos: Abonos) {
        val index = listaAbonos.indexOf(abonos)
        if (index != -1) {
            listaAbonos[index] = abonos
            //listaAbonos.sortByDescending { it.timestamp }
            notifyItemChanged(index)
            calcTotal(abonos)
        }
    }

    fun delete(abonos: Abonos) {
        val index = listaAbonos.indexOf(abonos)
        if (index != -1) {
            listaAbonos.removeAt(index)
            //listaAbonos.sortByDescending { it.timestamp }
            notifyItemRemoved(index)
            calcTotal(abonos)
        }
    }

    //Eliminar el parametro de "miAbono"
    private fun calcTotal(miAbono: Abonos) {

        var result = 0.0
        if (listaAbonos.size == 1) {
            result = miAbono.costoTotal - miAbono.abono //poner la lista.first en lugar de mi abono
            listener.mostrarSaldo(result)
        } else {
            var resActualizado = 0.0
            for (abonos in listaAbonos) {
                resActualizado += abonos.abonosDeElementos() // 700

            }
            var saldoFinal = miAbono.costoTotal - resActualizado
            listener.mostrarSaldo(saldoFinal)
            resActualizado = 0.0
            saldoFinal = 0.0
        }
    }


    inner class AbonosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemAbonosBinding.bind(view)
        fun listenerItem(abono: Abonos) {
            binding.root.setOnClickListener()
            {
                if (listaAbonos.first() == abono) {
                    listener.click(abono, true)
                } else {
                    listener.click(abono, false)
                }
            }
            binding.root.setOnLongClickListener {
                listener.eliminar(abono)
                true
            }
        }
    }


}