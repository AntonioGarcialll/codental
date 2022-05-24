package com.antonio.codental

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antonio.codental.databinding.ItemPacienteBinding

class PacienteAdapter(
    private val listener: PacienteInterfaz,
    private val listaPacientes: List<Paciente>
) :
    RecyclerView.Adapter<PacienteAdapter.PacientesViewHolder>() {

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
        val paciente = listaPacientes[position]
        holder.listenerItem(paciente)

        //Se asignan los componentes del item_pacientes con los de la lista
        holder.binding.imageView.setImageResource(R.drawable.contacto)
        holder.binding.tvNombrePaciente.text = paciente.paciente
        //holder.binding.tvTratamiento.text = paciente.tratamiento
    }

    //Obtengo el tamaño de la lista
    override fun getItemCount(): Int = listaPacientes.size


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