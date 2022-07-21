package com.antonio.codental

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ItemPacienteBinding

class PacienteAdapter(
    private val listener: PacienteInterfaz,
    private val listaPacientes: MutableList<Paciente>
) :
    RecyclerView.Adapter<PacienteAdapter.PacientesViewHolder>() {
    private val copiedList = mutableListOf<Paciente>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PacienteAdapter.PacientesViewHolder {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_paciente
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_paciente, parent, false)
        return PacientesViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PacienteAdapter.PacientesViewHolder, position: Int) {
        listaPacientes.sortBy {
            it.paciente
        }
        val paciente = listaPacientes[position]
        holder.listenerItem(paciente)

        //Se asignan los componentes del item_pacientes con los de la lista
        holder.binding.imageView.setImageResource(R.drawable.contacto)
        holder.binding.tvNombrePaciente.text = paciente.paciente
        //holder.binding.tvTratamiento.text = paciente.tratamiento
    }

    //Obtengo el tamaño de la lista
    override fun getItemCount(): Int = listaPacientes.size

    fun add(paciente: Paciente) {
        if (!listaPacientes.contains(paciente) && !copiedList.contains(paciente)) {
            listaPacientes.add(paciente)
            copiedList.add(paciente)
            notifyItemInserted(listaPacientes.lastIndex)
        } else {
            update(paciente)
        }
    }

    private fun update(paciente: Paciente) {
        val i = listaPacientes.indexOf(paciente)
        val j = copiedList.indexOf(paciente)
        if (i != -1 && j != -1) {
            listaPacientes[i] = paciente
            copiedList[j] = paciente
            notifyItemChanged(i)
        }
    }

    fun filterListByQuery(query: String?) {
        if (query.isNullOrBlank()) {
            listaPacientes.clear()
            listaPacientes.addAll(copiedList)
        } else {
            listaPacientes.clear()
            copiedList.filter { paciente ->
                query.toString().trim().lowercase() in paciente.paciente!!.lowercase()
            }.also { filteredList ->
                listaPacientes.addAll(filteredList)
            }
        }
        notifyDataSetChanged()
    }


    inner class PacientesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPacienteBinding.bind(view)
        fun listenerItem(paciente: Paciente) {
            binding.root.setOnClickListener()
            {
                listener.click(paciente)
            }
        }
    }
}