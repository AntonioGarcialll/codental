package com.antonio.codental

import java.io.Serializable

data class Tratamiento(
    var fecha: String? = null,
    val nombreTratamiento: String? = null,
    val costo: String? = null,
    val fotos: String? = null,
    val fotos2: String? = null,
    val idPaciente: String? = null,
    var idTratamiento: String? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tratamiento

        if (idTratamiento != other.idTratamiento) return false

        return true
    }

    override fun hashCode(): Int {
        return idTratamiento?.hashCode() ?: 0
    }
}