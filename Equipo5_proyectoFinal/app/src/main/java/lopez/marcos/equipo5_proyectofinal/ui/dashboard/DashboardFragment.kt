package lopez.marcos.equipo5_proyectofinal.ui.dashboard

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.echo.holographlibrary.Bar
import com.echo.holographlibrary.BarGraph
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import lopez.marcos.equipo5_proyectofinal.R
import lopez.marcos.equipo5_proyectofinal.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import android.graphics.Color
val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

class DashboardFragment : Fragment() {
    private lateinit var fechaInicio: EditText
    private lateinit var fechaFin: EditText
    private lateinit var tipo_asignatura: Spinner
    private lateinit var asignaturas: Spinner
    private val tipo_asignacion: ArrayList<String> = ArrayList()
    private lateinit var materias_ciclo: ArrayList<String>
    private lateinit var database: DatabaseReference
    private lateinit var barGraph: BarGraph

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseDatabase.getInstance().reference.child("tareas")
        barGraph=root.findViewById(R.id.barGraph)

        fechaInicio = root.findViewById(R.id.fechaInicio)
        fechaFin = root.findViewById(R.id.fechFin)
        tipo_asignatura = root.findViewById(R.id.tipo_asignatura)
        asignaturas = root.findViewById(R.id.asignaturas)
        val calendar = Calendar.getInstance()
        fillTipo_asignacion()


        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        if (asignaturas != null) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, materias_ciclo)
            asignaturas.adapter = adapter
        }

        if (tipo_asignatura != null) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipo_asignacion)
            tipo_asignatura.adapter = adapter
        }

        fechaInicio.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    fechaInicio.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

        fechaFin.setOnClickListener{
            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    fechaFin.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

        fetchDataFromFirebase(fechaInicio,fechaFin)

        return root
    }

    private fun fetchDataFromFirebase(fechaIncio: EditText, fechaFin: EditText) {
        val startDateObj = dateFormat.parse(fechaIncio.toString())
        val endDateObj = dateFormat.parse(fechaFin.toString())
        val barComplete=Bar()
        barComplete.color = Color.parseColor("#188d10") // Color para la barra
        barComplete.name = "Tareas Completadas"
        val barIncomplete = Bar()
        barIncomplete.color = Color.parseColor("#d62906") // Color para la barra
        barIncomplete.name = "Tareas Pendiente"

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bars = mutableListOf<Bar>()
                for (dataSnapshot in snapshot.children) {
                    val dateStr =dataSnapshot.child("fecha").getValue(String::class.java)
                    val estado = dataSnapshot.child("estado").getValue(String::class.java)
                    if ( estado != null && estado=="completada" && dateStr!= null) {
                        val dateObj= dateFormat.parse(dateStr)
                        if (dateObj != null && startDateObj != null && endDateObj != null){
                            if(dateObj in startDateObj..endDateObj){

                                barComplete.value++

                            }
                        }
                    }
                    if ( estado != null && estado=="pendiente" && dateStr!= null) {
                        val dateObj= dateFormat.parse(dateStr)
                        if (dateObj != null && startDateObj != null && endDateObj != null){
                            if(dateObj in startDateObj..endDateObj){
                                barIncomplete.value++
                            }
                        }
                    }
                }
                bars.add(barComplete)
                bars.add(barIncomplete)
                barGraph.bars = bars as ArrayList<Bar>?
                barGraph.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores si es necesario
            }
        })
    }


    private fun fillTipo_asignacion() {
        tipo_asignacion.add("Examen")
        tipo_asignacion.add("Tarea")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}