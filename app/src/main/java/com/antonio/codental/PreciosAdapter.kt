package com.antonio.codental

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ItemPreciosBinding

class PreciosAdapter(
    private val listener: PreciosInterfaz,
    private val listaPrecios: List<Precios>
) :
    RecyclerView.Adapter<PreciosAdapter.PreciosViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreciosViewHolder {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_gastoEgreso
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_precios, parent, false)
        return PreciosViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PreciosAdapter.PreciosViewHolder, position: Int) {
        val precio = listaPrecios[position]
        holder.listenerItem(precio)

        //Se asignan los componentes del item_gastoEgreso con los de la lista
        holder.binding.tvServicio.text = precio.servicio
        holder.binding.tvPrecio.text = precio.precio
    }

    override fun getItemCount(): Int = listaPrecios.size

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
