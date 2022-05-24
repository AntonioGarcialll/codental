package com.antonio.codental

import java.io.Serializable

data class Tratamiento(var fecha:String?=null,
                        val nombreTratamiento:String?=null,
                        val costo:String?=null,
                        val fotos:String?=null,
                        val idPaciente:String?=null,
                        val idTratamiento:String?=null) : Serializable {}