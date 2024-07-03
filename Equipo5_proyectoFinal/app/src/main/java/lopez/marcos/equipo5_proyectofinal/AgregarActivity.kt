package lopez.marcos.equipo5_proyectofinal

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AgregarActivity : AppCompatActivity() {

    private lateinit var tipo_asignatura: Spinner
    private lateinit var asignaturas: Spinner
    private lateinit var registrarButton: Button
    private lateinit var etDate: EditText

    private lateinit var nombreText: EditText
    private lateinit var descripcionText: EditText
    private lateinit var prioridadGroup: RadioGroup

    private val calendar = Calendar.getInstance()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var materias_ciclo: ArrayList<String>
    private val tipo_asignacion: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        etDate = findViewById(R.id.etDate)
        tipo_asignatura = findViewById(R.id.tipo_asignatura)
        asignaturas = findViewById(R.id.asignaturas)
        registrarButton = findViewById(R.id.registrar)

        materias_ciclo = NavActivity.materias_ciclo

        fillTipo_asignacion()

        if (asignaturas != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materias_ciclo)
            asignaturas.adapter = adapter
        }

        if (tipo_asignatura != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipo_asignacion)
            tipo_asignatura.adapter = adapter
        }

        etDate.setOnClickListener {
            showDatePicker()
        }

        registrarButton.setOnClickListener {
            guardarTarea()
        }
    }

    private fun fillTipo_asignacion() {
        tipo_asignacion.add("Examen")
        tipo_asignacion.add("Tarea")
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                etDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun guardarTarea() {
        nombreText = findViewById(R.id.nombre)
        descripcionText = findViewById(R.id.descripcion)
        prioridadGroup = findViewById(R.id.prioridadGroup)

        val descripcion = descripcionText.text.toString()


        val asignatura = materias_ciclo[findViewById<Spinner>(R.id.asignaturas).selectedItemPosition]
        val tipoAsignatura = tipo_asignacion[findViewById<Spinner>(R.id.tipo_asignatura).selectedItemPosition]
        val fecha = etDate.text.toString()


        val currentUser = auth.currentUser

        val prioridad = when (prioridadGroup.checkedRadioButtonId) {
            R.id.radio_baja -> "Baja"
            R.id.radio_media -> "Media"
            R.id.radio_alta -> "Alta"
            else -> ""
        }

        if (currentUser != null) {
            val tarea = hashMapOf(
                "nombre" to nombreText.text.toString(),
                "asignatura" to asignatura,
                "tipo" to tipoAsignatura,
                "fecha" to fecha,
                "descripcion" to descripcion,
                "prioridad" to prioridad,
                "usuario" to currentUser.uid,
                "estado" to "pendiente"
            )

            firestore.collection("tareas")
                .add(tarea)
                .addOnSuccessListener { document ->
                    nombreText.setText("")
                    etDate.setText("")
                    findViewById<EditText>(R.id.descripcion).setText("")
                    prioridadGroup.clearCheck()

                    Toast.makeText(this, "Tarea agregada exitosamente.", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
        }
    }
}
