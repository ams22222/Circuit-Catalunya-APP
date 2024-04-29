package com.example.m13_circuitdecatalunya.server

import com.example.m13_circuitdecatalunya.Comanda
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitEndPoints {
    @GET("api/comandes/{email}")
    suspend fun getComandaById(@Path("email") email: String): Response<ResponseBody>

    @GET("api/comandes/verificar-disponibilidad")
    suspend fun checkEspacioOcupadoEnFecha(
        @Query("fecha") fecha: String,
        @Query("espacio_id") espacio_id: Int
    ): Response<ResponseBody>

    @GET("api/espai/{espaiId}/capacidad")
    suspend fun obtenerCapacidadEspacio(@Path("espaiId") espaiId: Int): Response<ResponseBody>


    @GET("api/espai/{espaiId}/recursos")
    suspend fun obtenerRecursosVinculadosEspacio(@Path("espaiId") espaiId: Int): Response<ResponseBody>

    @POST("api/comandes/create")
    suspend fun createComanda(
        @Query("email") email: String,
        @Query("espai_id") espaiId: Int,
        @Query("data_solicitud") dataSolicitud: String,
        @Query("entrades") entrades: Int
    ): Response<ResponseBody>

    @POST("api/comandas-recursos")
    suspend fun createComandaRecurso(
        @Query("comanda_id") comandaId: Int,
        @Query("recurso_id") recursoId: Int
    ): Response<ResponseBody>
}
