package com.antonio.codental

interface AbonosInterfaz {
    fun click(abono : Abonos, ultimo : Boolean)
    fun eliminar(abono: Abonos)
    fun mostrarSaldo(sd : Double)
}