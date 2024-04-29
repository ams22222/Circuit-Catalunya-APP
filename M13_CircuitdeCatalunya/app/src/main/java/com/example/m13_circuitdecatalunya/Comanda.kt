package com.example.m13_circuitdecatalunya

data class Comanda(
     val id: Int,
     val email: String,
     val dataSolicitud: String,
     val entrades: Int,
     val espai_id: Int,
     val estat: String,
     val estat_comanda: String
): java.io.Serializable