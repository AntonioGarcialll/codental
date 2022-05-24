package com.antonio.codental

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ItemPacienteBinding
import com.antonio.codental.databinding.ItemTratamientosBinding

class TratamientosAdapter(private val listener:TratamientosInterfaz, private val listaTratamientos: List<Tratamiento>) :
    RecyclerView.Adapter<TratamientosAdapter.TratamientosViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TratamientosAdapter.TratamientosViewHolder {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_paciente
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_tratamientos, parent, false)
        return TratamientosViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TratamientosAdapter.TratamientosViewHolder, position: Int) {
        val tratamiento = listaTratamientos[position]
        holder.listenerItem(tratamiento)

        //Se asignan los componentes del item_pacientes con los de la lista
        holder.binding.tvFechaTratamiento.text = tratamiento.fecha
        holder.binding.tvTratamiento.text = tratamiento.nombreTratamiento
        holder.binding.tvPrecioTratamiento.text = tratamiento.costo
    }

    //Obtengo el tamaño de la lista
    override fun getItemCount(): Int = listaTratamientos.size

    inner class TratamientosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTratamientosBinding.bind(view)
        fun listenerItem(tratamiento : Tratamiento){
            binding.root.setOnClickListener()
            {
                listener.click(tratamiento)
            }
        }
    }
}