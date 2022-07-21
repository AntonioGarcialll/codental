package com.antonio.codental

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ItemPreciosBinding

class PreciosAdapter(
    private val listener: PreciosInterfaz,
    private val listaPrecios: MutableList<Precios>
) :
    RecyclerView.Adapter<PreciosAdapter.PreciosViewHolder>() {
    private val copiedList = mutableListOf<Precios>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreciosViewHolder {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_gastoEgreso
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_precios, parent, false)
        return PreciosViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PreciosAdapter.PreciosViewHolder, position: Int) {
        listaPrecios.sortBy {
            it.servicio
        }
        val precio = listaPrecios[position]
        holder.listenerItem(precio)

        //Se asignan los componentes del item_gastoEgreso con los de la lista
        holder.binding.tvServicio.text = precio.servicio
        holder.binding.tvPrecio.text = precio.precio
    }

    override fun getItemCount(): Int = listaPrecios.size

    fun add(precio: Precios) {
        if (!listaPrecios.contains(precio) && !copiedList.contains(precio)) {
            listaPrecios.add(precio)
            copiedList.add(precio)
            notifyItemInserted(listaPrecios.lastIndex)
        } else {
            update(precio)
        }
    }

    private fun update(precio: Precios) {
        val i = listaPrecios.indexOf(precio)
        val j = copiedList.indexOf(precio)
        if (i != -1 && j != -1) {
            listaPrecios[i] = precio
            copiedList[j] = precio
            notifyItemChanged(i)
        }
    }

    fun filterListByQuery(query: String?) {
        if (query.isNullOrBlank()) {
            listaPrecios.clear()
            listaPrecios.addAll(copiedList)
        } else {
            listaPrecios.clear()
            copiedList.filter { precio ->
                query.toString().trim().lowercase() in precio.servicio!!.lowercase()
            }.also { filteredList ->
                listaPrecios.addAll(filteredList)
            }
        }
        notifyDataSetChanged()
    }

    inner class PreciosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPreciosBinding.bind(view)
        fun listenerItem(precio: Precios) {
            binding.root.setOnClickListener()
            {
                listener.click(precio)
            }
        }
    }
}
