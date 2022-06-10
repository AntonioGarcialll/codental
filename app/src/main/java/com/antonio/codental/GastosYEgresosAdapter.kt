package com.antonio.codental

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ItemGastosYEgresosBinding
import com.antonio.codental.databinding.ItemPacienteBinding

class GastosYEgresosAdapter(
    private val listener: GastoEgresoInterfaz,
    private val listaGastosEgresos: List<GastosYEgresos>
) :
    RecyclerView.Adapter<GastosYEgresosAdapter.GastosEgresosViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastosEgresosViewHolder {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_gastoEgreso
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gastos_y_egresos, parent, false)
        return GastosEgresosViewHolder(layout)
    }

    override fun onBindViewHolder(holder: GastosYEgresosAdapter.GastosEgresosViewHolder, position: Int) {
        val gastoEgreso = listaGastosEgresos[position]
        holder.listenerItem(gastoEgreso)

        //Se asignan los componentes del item_gastoEgreso con los de la lista
        holder.binding.tvNombreGastoEgreso.text = gastoEgreso.gastoEgreso
        holder.binding.tvCostoGastoEgreso.text = gastoEgreso.costo
    }

    override fun getItemCount(): Int = listaGastosEgresos.size

    inner class GastosEgresosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemGastosYEgresosBinding.bind(view)
        fun listenerItem(gastoEgreso: GastosYEgresos) {
            binding.root.setOnClickListener()
            {
                listener.click(gastoEgreso)
            }
        }
    }
}


