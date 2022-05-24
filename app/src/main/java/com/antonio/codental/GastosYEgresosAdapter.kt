package com.antonio.codental

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
//import kotlinx.android.synthetic.main.item_gastos_y_egresos.view.*
//import kotlinx.android.synthetic.main.item_paciente.view.*

/*class GastosYEgresosAdapter(private val mContext: Context, private val listaGastosYEgresos:List<GastosYEgresos>) : ArrayAdapter<GastosYEgresos>(mContext,0,listaGastosYEgresos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_gastos_y_egresos
        val layout = LayoutInflater.from(mContext).inflate(R.layout.item_gastos_y_egresos,parent,false)

        //Se guarda el gasto y egreso que se encuentre en la posición actual de la listaGastosYEgresos
        val gastoYEgreso =listaGastosYEgresos[position]

        //Se asignan los componentes del item_gastos_y_egresos con los de la lista
        layout.tvNombreGastoEgreso.text = gastoYEgreso.gastoEgreso
        layout.tvCostoGastoEgreso.text = gastoYEgreso.costo
        return layout
    }
}*/