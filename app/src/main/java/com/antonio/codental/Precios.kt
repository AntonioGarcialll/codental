package com.antonio.codental

import java.io.Serializable

data class Precios(var servcio:String?=null,
              var precio:String?=null,
              var idServicio:String?=null, ) : Serializable {
}