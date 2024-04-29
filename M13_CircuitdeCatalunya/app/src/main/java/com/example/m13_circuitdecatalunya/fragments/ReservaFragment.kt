    import android.app.Activity
    import android.app.DatePickerDialog
    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.*
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import com.example.m13_circuitdecatalunya.ComandaViewModel
    import com.example.m13_circuitdecatalunya.ComandaViewModelFactory
    import com.example.m13_circuitdecatalunya.R
    import com.example.m13_circuitdecatalunya.ReservaActivity
    import com.google.android.material.snackbar.Snackbar
    import java.text.SimpleDateFormat
    import java.util.*

    class ReservaFragment : Fragment() {

        private lateinit var fragmentView: View
        private var espacioId: Int = -1

        private lateinit var viewModel: ComandaViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            try {
                viewModel = ViewModelProvider(this, ComandaViewModelFactory(requireContext())).get(ComandaViewModel::class.java)
            } catch (e: Exception) {
                Log.e("ViewModelCreationError", e.message ?: "Unknown error")
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_reserva, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            fragmentView = view

            val spinnerEspacio: Spinner = fragmentView.findViewById(R.id.spinner_ubi)

            val espacios = resources.getStringArray(R.array.Ubicacio)

            spinnerEspacio.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, espacios)

            spinnerEspacio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    espacioId = position + 1
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            var formattedDate = ""


            val btnData: Button = fragmentView.findViewById(R.id.btnData)
            btnData.setOnClickListener {
                // Obtener la fecha actual
                val calendar: Calendar = Calendar.getInstance()
                val year: Int = calendar.get(Calendar.YEAR)
                val month: Int = calendar.get(Calendar.MONTH)
                val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

                // Crear el diálogo de selección de fecha
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        // Obtener la fecha actual
                        val currentDate = Calendar.getInstance()

                        // Crear la fecha seleccionada
                        val selectedDate = Calendar.getInstance()
                        selectedDate.set(year, month, dayOfMonth)

                        // Verificar si la fecha seleccionada es anterior o igual a la fecha actual
                        if (selectedDate <= currentDate) {
                            Toast.makeText(
                                requireContext(),
                                "Seleccione una fecha posterior a hoy",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@DatePickerDialog
                        }

                        // Actualizar el texto del botón con la fecha seleccionada
                        val dateString = "$dayOfMonth/${month + 1}/$year"
                        btnData.text = dateString

                        formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                    },
                    year, month, day
                )
                datePickerDialog.show()
            }
            viewModel.espacioOcupado.observe(viewLifecycleOwner, Observer { ocupado ->
                ocupado?.let {
                    if (it) {
                        Toast.makeText(
                            requireContext(),
                            "El espacio está ocupado en esa fecha",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Observer
                    }
                }
            })

            val btnAccept: Button = fragmentView.findViewById(R.id.btnAccept)
            btnAccept.setOnClickListener {
                val fechaSeleccionada = btnData.text.toString()

                if (fechaSeleccionada.isEmpty() || fechaSeleccionada == "Seleccionar fecha") {
                    Toast.makeText(
                        requireContext(),
                        "Seleccione una fecha",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Convertir la fecha al formato "Y-m-d"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = dateFormat.parse(fechaSeleccionada)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

                // Verificar si la fecha seleccionada es igual a la fecha actual
                val currentDate = Calendar.getInstance()
                val selectedDate = Calendar.getInstance()
                selectedDate.time = date

                if (selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                    selectedDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)) {
                    Toast.makeText(
                        requireContext(),
                        "Seleccione una fecha posterior a hoy",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                if (espacioId == -1) {
                    Toast.makeText(
                        requireContext(),
                        "Seleccione un espacio",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val espacioId = espacioId

                viewModel.checkEspacioOcupadoEnFecha(formattedDate, espacioId)
            }

            // Observar la ocupación del espacio
            viewModel.espacioOcupado.observe(viewLifecycleOwner, Observer { ocupado ->
                ocupado?.let {
                    if (it) {
                        Toast.makeText(
                            requireContext(),
                            "El espacio está ocupado en esa fecha",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Observer
                    }

                    // Continuar con la lógica adicional aquí si el espacio está disponible

                    // Obtener la capacidad del espacio
                    viewModel.obtenerCapacidadEspacio(espacioId)
                }
            })


            val resultsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // La reserva se ha completado
                    Snackbar.make(
                        fragmentView,
                        "¡Reserva completada!",
                        Snackbar.LENGTH_LONG
                    ).show()

                } else {
                    // La reserva se ha cancelado
                    Snackbar.make(
                        fragmentView,
                        "La reserva se ha cancelado",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            // Observar la capacidad del espacio
            viewModel.capacidadEspacio.observe(viewLifecycleOwner, Observer { capacidad ->
                capacidad?.let {
                    val edtNumEntrades = fragmentView.findViewById<EditText>(R.id.edtNumEntrades)

                    if (edtNumEntrades.text.toString().isNullOrEmpty()){
                        Toast.makeText(
                            requireContext(),
                            "Seleccione un numero de entradas",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Observer
                    }

                    val numEntrades = edtNumEntrades.text.toString().toInt()
                    if (numEntrades > capacidad) {
                        // La capacidad del espacio es menor al número de entradas
                        Toast.makeText(
                            requireContext(),
                            "La capacidad del espacio es insuficiente",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ReservaFragment", "Capacidad del espacio: $capacidad")
                        Log.d("ReservaFragment", "Número de entradas seleccionadas: $numEntrades")
                    } else {
                        val intent = Intent(requireContext(), ReservaActivity::class.java)
                        intent.putExtra("fechaSeleccionada", formattedDate)
                        intent.putExtra("numEntradas", numEntrades)
                        intent.putExtra("espacioId", espacioId)

                        resultsLauncher.launch(intent)
                    }
                }
            })
        }
    }
