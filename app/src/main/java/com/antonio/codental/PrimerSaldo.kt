package com.antonio.codental

import java.io.Serializable

data class PrimerSaldo(var primerSaldo:String?=null,
                       var idTratamiento:String?=null,
                       var idPrimerSaldo:String?=null) : Serializable {}