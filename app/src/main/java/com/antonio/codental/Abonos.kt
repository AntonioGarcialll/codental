package com.antonio.codental

import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.sql.Timestamp

data class Abonos(
    var fecha: String? = null,
    var pieza: String? = null,
    var tratamiento: String? = null,
    var costoTotal: Double = 0.0,
    var abono: Double = 0.0,
    var saldo: Double = 0.0,
    var saldoAnterior: Double = 0.0,
    var firma: String? = null,
    var hojaClinica: String ?= null,
    var proximaCita: String? = null,
    var horaProximaCita: String?=null,
    @get: Exclude var idAbono: String? = null,
    var timestamp: Long = 0
) : Serializable {

    fun abonosDeElementos() : Double = abono
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Abonos

        if (idAbono != other.idAbono) return false

        return true
    }

    override fun hashCode(): Int {
        return idAbono?.hashCode() ?: 0
    }
}