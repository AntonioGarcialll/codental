package com.antonio.codental

import java.io.Serializable

data class Paciente(
    var fecha: String? = null,
    val paciente: String? = null,
    val doctor: String? = null,
    var idPaciente: String? = null,
    val idDoctor: String? = null
) : Serializable