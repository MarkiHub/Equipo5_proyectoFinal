package lopez.marcos.equipo5_proyectofinal

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import lopez.marcos.equipo5_proyectofinal.ui.theme.Equipo5_proyectoFinalTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private lateinit var etDate: EditText
private val calendar = Calendar.getInstance()
val materias: ArrayList<String> = ArrayList()
val tipo_asignacion: ArrayList<String> = ArrayList()

class AgregarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar)

        etDate = findViewById(R.id.etDate)

        fillMaterias()
        fillTipo_asignacion()

        val tipo_asignatura: Spinner = findViewById(R.id.tipo_asignatura)
        val asignaturas: Spinner = findViewById(R.id.asignaturas)

        if (asignaturas != null){
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materias)
            asignaturas.adapter = adapter
        }

        if (tipo_asignatura != null){
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipo_asignacion)
            tipo_asignatura.adapter = adapter
        }

        etDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun fillMaterias(){
        materias.add("Materia 1")
        materias.add("Materia 2")
        materias.add("Materia 3")
        materias.add("Materia 4")
        materias.add("Materia 5")
    }

    private fun fillTipo_asignacion(){
        tipo_asignacion.add("Examen")
        tipo_asignacion.add("Tarea")

    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                etDate.setText("$formattedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }




}
