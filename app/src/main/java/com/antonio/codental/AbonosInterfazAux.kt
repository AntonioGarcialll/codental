package com.antonio.codental

interface AbonosInterfazAux {
    fun obtenerAbonoSeleccionado() : Abonos?
    fun obtenerIdTratamiento() : String?
    fun obtenerCosto() : Double
    fun hayAbonos() : Boolean
    fun obtenerSaldo() : Double
    fun obtenerTratamiento() : String?
    fun obtenerUltimoActualizar() : Boolean?
}