package com.antonio.codental

import java.io.Serializable

data class GastosYEgresos(var fecha:String?=null,
                          var gastoEgreso:String?=null,
                          var costo:String?=null,
                          var estatus:String?=null,
                          var idGastoEgreso:String?=null) : Serializable {
}