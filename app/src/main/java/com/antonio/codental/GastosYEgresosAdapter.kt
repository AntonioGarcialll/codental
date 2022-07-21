package com.antonio.codental

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ItemGastosYEgresosBinding

class GastosYEgresosAdapter(
    private val listener: GastoEgresoInterfaz,
    private val listaGastosEgresos: MutableList<GastosYEgresos>
) :
    RecyclerView.Adapter<GastosYEgresosAdapter.GastosEgresosViewHolder>() {
    private val copiedList = mutableListOf<GastosYEgresos>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastosEgresosViewHolder {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_gastoEgreso
        val layout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gastos_y_egresos, parent, false)
        return GastosEgresosViewHolder(layout)
    }

    override fun onBindViewHolder(
        holder: GastosYEgresosAdapter.GastosEgresosViewHolder,
        position: Int
    ) {
        listaGastosEgresos.sortBy {
            it.gastoEgreso
        }
        val gastoEgreso = listaGastosEgresos[position]
        holder.listenerItem(gastoEgreso)

        //Se asignan los componentes del item_gastoEgreso con los de la lista
        holder.binding.tvNombreGastoEgreso.text = gastoEgreso.gastoEgreso
        holder.binding.tvCostoGastoEgreso.text = gastoEgreso.costo
    }

    override fun getItemCount(): Int = listaGastosEgresos.size

    fun add(gastoEgreso: GastosYEgresos) {
        if (!listaGastosEgresos.contains(gastoEgreso) && !copiedList.contains(gastoEgreso)) {
            listaGastosEgresos.add(gastoEgreso)
            copiedList.add(gastoEgreso)
            notifyItemInserted(listaGastosEgresos.lastIndex)
        } else {
            update(gastoEgreso)
        }
    }

    private fun update(gastoEgreso: GastosYEgresos) {
        val i = listaGastosEgresos.indexOf(gastoEgreso)
        val j = copiedList.indexOf(gastoEgreso)
        if (i != -1 && j != -1) {
            listaGastosEgresos[i] = gastoEgreso
            copiedList[j] = gastoEgreso
            notifyItemChanged(i)
        }
    }

    fun filterListByQuery(query: String?) {
        if (query.isNullOrBlank()) {
            listaGastosEgresos.clear()
            listaGastosEgresos.addAll(copiedList)
        } else {
            listaGastosEgresos.clear()
            copiedList.filter { gastoEgreso ->
                query.toString().trim().lowercase() in gastoEgreso.gastoEgreso!!.lowercase()
            }.also { filteredList ->
                listaGastosEgresos.addAll(filteredList)
            }
        }
        notifyDataSetChanged()
    }

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


