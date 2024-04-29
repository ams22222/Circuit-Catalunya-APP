package com.example.m13_circuitdecatalunya

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.m13_circuitdecatalunya.server.RetrofitConnection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.IOException

data class ComandaResponse(val id: Int)

class ComandaViewModel(private val context: Context) : ViewModel() {
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _comandas = MutableLiveData<List<Comanda>>(emptyList())
    val comandas: LiveData<List<Comanda>> get() = _comandas

    private val _errorApiRest = MutableLiveData<String?>(null)
    val errorApiRest: LiveData<String?> get() = _errorApiRest

    private val _espacioOcupado = MutableLiveData<Boolean?>(null)
    val espacioOcupado: LiveData<Boolean?> get() = _espacioOcupado

    private val _capacidadEspacio = MutableLiveData<Int?>(null)
    val capacidadEspacio: LiveData<Int?> get() = _capacidadEspacio

    private val _recursos = MutableLiveData<List<Recurso>>()
    val recursos: LiveData<List<Recurso>> get() = _recursos

    private val _comandaId = MutableLiveData<Int?>(null)
    val comandaId: LiveData<Int?> get() = _comandaId

    init {
        loadInit()
    }

    private fun loadInit() {
        viewModelScope.launch {
            _loading.value = true
            _errorApiRest.value = null

            try {
                val prefs = context.getSharedPreferences("MY_PREFS_NAME", AppCompatActivity.MODE_PRIVATE)
                val savedMail = prefs.getString("email", null)!!

                val response = withContext(Dispatchers.IO) {
                    RetrofitConnection.service.getComandaById(savedMail)
                }

                if (response.isSuccessful) {
                    val responseBody = response.body()?.byteStream()
                    if (responseBody != null) {
                        val jsonResponse = responseBody.bufferedReader().use { it.readText() }
                        Log.d("RESPONSE", jsonResponse)
                        val comandas: List<Comanda> = Gson().fromJson(
                            jsonResponse,
                            object : TypeToken<List<Comanda>>() {}.type
                        )
                        _comandas.value = comandas
                    } else {
                        _errorApiRest.value = "Empty response body"
                    }
                } else {
                    _errorApiRest.value = response.errorBody()?.string()
                }
            } catch (e: IOException) {
                _errorApiRest.value = e.message
            }

            _loading.value = false
        }
    }

    fun checkEspacioOcupadoEnFecha(fecha: String, espacioId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitConnection.service.checkEspacioOcupadoEnFecha(fecha, espacioId)
                }
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("BackendRequest1", "Response Body: $responseBody")

                    val jsonResponse = JSONObject(responseBody)
                    val ocupado = jsonResponse.optBoolean("ocupado", false)

                    Log.d("BackendRequest2", "Fecha: $fecha, Espacio ID: $espacioId, Ocupado: $ocupado")
                    _espacioOcupado.value = ocupado
                } else {
                    _errorApiRest.value = response.errorBody()?.string()
                }
            } catch (e: Exception) {
                _errorApiRest.value = e.message
            }
        }
    }

    fun obtenerCapacidadEspacio(espaiId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitConnection.service.obtenerCapacidadEspacio(espaiId)
                }
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("BackendRequest3", "Response Body: $responseBody")

                    // Parsea la respuesta JSON y obtén la capacidad del espacio
                    val jsonResponse = JSONObject(responseBody)
                    val capacidad = jsonResponse.optInt("capacidad", -1)

                    _capacidadEspacio.value = capacidad // Actualiza el valor de capacidadEspacio
                } else {
                    _errorApiRest.value = response.errorBody()?.string()
                }
            } catch (e: Exception) {
                _errorApiRest.value = e.message
            }
        }
    }

    fun getRecursosVinculadosEspacio(espaiId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitConnection.service.obtenerRecursosVinculadosEspacio(espaiId)
                }
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("BackendRequest4", "Response Body: $responseBody")

                    // Parsea la respuesta JSON y obtén los recursos
                    val jsonResponse = JSONObject(responseBody)
                    Log.d("BackendResponse1", jsonResponse.toString())

                    val recursosArray = jsonResponse.optJSONArray("recursos")

                    val recursos = mutableListOf<Recurso>()
                    if (recursosArray != null) {
                        for (i in 0 until recursosArray.length()) {
                            val recursoId = recursosArray.getJSONObject(i).getInt("id")
                            val recursoNombre = recursosArray.getJSONObject(i).getString("nom")
                            val recurso = Recurso(recursoId, recursoNombre)
                            recursos.add(recurso)
                        }
                    }
                    _recursos.value = recursos // Actualiza la lista de recursos en LiveData

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("BackendRequest5", "Error Body: $errorBody")
                    _errorApiRest.value = errorBody
                }
            } catch (e: Exception) {
                Log.e("BackendRequest6", "Exception: ${e.message}")
                _errorApiRest.value = e.message
            }
        }
    }

    fun createComanda(email: String, espaiId: Int, dataSolicitud: String, entrades: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitConnection.service.createComanda(email, espaiId, dataSolicitud, entrades)
                }
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("BackendRequest7", "Response Body: $responseBody")

                    val jsonResponse = JSONObject(responseBody)
                    val comandaId = jsonResponse.optInt("comandaId", -1)
                    Log.d("IDDD", "La id: $comandaId")
                    if (comandaId != -1) {
                        _comandaId.value = comandaId
                    } else {
                        // La respuesta del servidor no incluye la ID de la comanda
                        _errorApiRest.value = "La respuesta del servidor no incluye la ID de la comanda"
                    }
                } else {
                    // Error al crear la comanda
                    val errorBody = response.errorBody()?.string()
                    Log.e("BackendRequest8", "Error Body: $errorBody")
                    _errorApiRest.value = errorBody
                }
            } catch (e: Exception) {
                // Error en la llamada a la API
                Log.e("BackendRequest9", "Exception: ${e.message}")
                _errorApiRest.value = e.message
            }
        }
    }

    fun createComandaRecurso(recursoId: Int) {
        viewModelScope.launch {
            val comandaId = comandaId.value
            if (comandaId != null) {
                try {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitConnection.service.createComandaRecurso(comandaId, recursoId)
                    }
                    if (response.isSuccessful) {
                        // Comanda-Recurso creado exitosamente
                    } else {
                        // Error al crear el Comanda-Recurso
                        val errorBody = response.errorBody()?.string()
                        Log.e("BackendRequest", "Error Body: $errorBody")
                        _errorApiRest.postValue(errorBody)
                    }
                } catch (e: Exception) {
                    // Error en la llamada a la API
                    Log.e("BackendRequest", "Exception: ${e.message}")
                    _errorApiRest.postValue(e.message)
                }
            } else {
                // No se ha obtenido la ID de la comanda
                _errorApiRest.postValue("No se ha obtenido la ID de la comanda")
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ComandaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ComandaViewModel(context) as T
    }
}
