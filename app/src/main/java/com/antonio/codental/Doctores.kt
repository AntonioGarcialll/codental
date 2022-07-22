package com.antonio.codental

import java.io.Serializable

class Doctores(
    var nombre: String? = null,
    val correo: String? = null,
    val codigoSeguridad: String? = null,
    var miIdDoctor: String? = null
) : Serializable