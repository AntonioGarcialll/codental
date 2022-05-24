package com.antonio.codental

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
//import kotlinx.android.synthetic.main.item_gastos_y_egresos.view.*
//import kotlinx.android.synthetic.main.item_precios.view.*

/*class PreciosAdapter(private val mContext: Context, private val listaPrecios:List<Precios>) : ArrayAdapter<Precios>(mContext,0,listaPrecios) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //Le pasamos el layout que tiene los elementos a mostrar, o sea, el de item_precios
        val layout = LayoutInflater.from(mContext).inflate(R.layout.item_precios,parent,false)

        //Se guarda el servicio con su precio que se encuentre en la posición actual de la listaPrecios
        val precio =listaPrecios[position]

        //Se asignan los componentes del item_gastos_y_egresos con los de la lista
        layout.tvServicio.text = precio.servcio
        layout.tvPrecio.text = precio.precio
        return layout
    }
}*/