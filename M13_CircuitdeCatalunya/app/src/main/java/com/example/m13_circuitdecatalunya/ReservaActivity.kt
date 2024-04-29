package com.example.m13_circuitdecatalunya

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class ReservaActivity : AppCompatActivity() {
    private lateinit var checkboxContainer: LinearLayout
    private lateinit var comandaViewModel: ComandaViewModel
    private lateinit var email: String
    private var espacioId: Int = 0
    private lateinit var fechaSolicitud: String
    private var numEntradas: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)

        checkboxContainer = findViewById(R.id.checkboxContainer)

        comandaViewModel = ViewModelProvider(this, ComandaViewModelFactory(applicationContext)).get(ComandaViewModel::class.java)

        // Obtener datos del intent
        email = obtenerEmailGuardado()
        espacioId = intent.getIntExtra("espacioId", 0)
        fechaSolicitud = intent.getStringExtra("fechaSeleccionada") ?: ""
        numEntradas = intent.getIntExtra("numEntradas", 0)

        comandaViewModel.recursos.observe(this, Observer { recursos ->
            checkboxContainer.removeAllViews()

            val recursosOrdenados = recursos.sortedBy { it.nom }

            for (recurso in recursosOrdenados) {
                agregarCheckboxDinamico(recurso.nom, recurso.id)
            }
        })

        comandaViewModel.getRecursosVinculadosEspacio(espacioId)

        val btnAceptar: Button = findViewById(R.id.btnAcceptar)
        btnAceptar.setOnClickListener {
            comandaViewModel.createComanda(email, espacioId, fechaSolicitud, numEntradas)

            comandaViewModel.comandaId.observe(this, Observer { comandaId ->
                if (comandaId != null) {
                    val recursosSeleccionados = obtenerRecursosSeleccionados()
                    if (recursosSeleccionados.isNotEmpty()) {
                        for (recursoId in recursosSeleccionados) {
                            comandaViewModel.createComandaRecurso(recursoId)
                        }
                        val returnIntent = Intent()
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    } else {
                        val returnIntent = Intent()
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    }
                }
            })
        }

        val btnCancelar: Button = findViewById(R.id.btnCancelar)
        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun obtenerEmailGuardado(): String {
        val prefs = getSharedPreferences("MY_PREFS_NAME", AppCompatActivity.MODE_PRIVATE)
        return prefs.getString("email", "") ?: ""
    }

    private fun agregarCheckboxDinamico(nombre: String, id: Int) {
        val nuevoCheckbox = CheckBox(this)
        nuevoCheckbox.text = nombre
        checkboxContainer.addView(nuevoCheckbox)
        nuevoCheckbox.tag = id
    }

    private fun obtenerRecursosSeleccionados(): List<Int> {
        val recursosSeleccionados = mutableListOf<Int>()

        for (i in 0 until checkboxContainer.childCount) {
            val checkbox = checkboxContainer.getChildAt(i) as CheckBox
            if (checkbox.isChecked) {
                val recursoId = checkbox.tag as Int
                recursosSeleccionados.add(recursoId)
            }
        }

        return recursosSeleccionados
    }
}
