package lopez.marcos.equipo5_proyectofinal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditarActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var fechaEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var asignaturaSpinner: Spinner
    private lateinit var tipoSpinner: Spinner
    private lateinit var prioridadRadioGroup: RadioGroup
    private lateinit var bajaRadioButton: RadioButton
    private lateinit var mediaRadioButton: RadioButton
    private lateinit var altaRadioButton: RadioButton
    private lateinit var actualizarButton: Button
    private lateinit var cancelarButton: Button
    private lateinit var completadaButton: Button
    private lateinit var eliminarButton: TextView
    private var tareaId: String? = null

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar)

        nombreEditText = findViewById(R.id.nombre)
        fechaEditText = findViewById(R.id.etDate)
        descripcionEditText = findViewById(R.id.descripcion)
        asignaturaSpinner = findViewById(R.id.asignaturas)
        tipoSpinner = findViewById(R.id.tipo_asignatura)
        prioridadRadioGroup = findViewById(R.id.prioridadGroup)
        bajaRadioButton = findViewById(R.id.radio_baja)
        mediaRadioButton = findViewById(R.id.radio_media)
        altaRadioButton = findViewById(R.id.radio_alta)
        actualizarButton = findViewById(R.id.actualizarB)
        cancelarButton = findViewById(R.id.cancelar_act)
        completadaButton = findViewById(R.id.completada)
        eliminarButton = findViewById(R.id.eliminar)

        val asignaturaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, NavActivity.materias_ciclo)
        asignaturaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        asignaturaSpinner.adapter = asignaturaAdapter

        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, NavActivity.tipos_asignaciones)
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoSpinner.adapter = tipoAdapter

        intent.extras?.let {
            tareaId = it.getString("actividadId")
            nombreEditText.setText(it.getString("nombre"))
            fechaEditText.setText(it.getString("fecha"))
            descripcionEditText.setText(it.getString("descripcion"))
            setSpinnerSelection(asignaturaSpinner, it.getString("asignatura"))
            setSpinnerSelection(tipoSpinner, it.getString("tipo"))

            when (it.getString("prioridad")) {
                "Baja" -> bajaRadioButton.isChecked = true
                "Media" -> mediaRadioButton.isChecked = true
                "Alta" -> altaRadioButton.isChecked = true
            }
        }

        actualizarButton.setOnClickListener {
            actualizarTarea() }


        cancelarButton.setOnClickListener { finish() }

        completadaButton.setOnClickListener { marcarCompletada() }

        eliminarButton.setOnClickListener{ eliminarTarea() }
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String?) {
        val adapter = spinner.adapter as ArrayAdapter<String>
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == value) {
                spinner.setSelection(i)
                break
            }
        }
    }

    private fun actualizarTarea() {
        val nombre = nombreEditText.text.toString()
        val fecha = fechaEditText.text.toString()
        val descripcion = descripcionEditText.text.toString()
        val asignatura = asignaturaSpinner.selectedItem.toString()
        val tipo = tipoSpinner.selectedItem.toString()
        val prioridad = findViewById<RadioButton>(prioridadRadioGroup.checkedRadioButtonId).text.toString()

        tareaId?.let {
            println("salio bien")
            val tareaRef = firestore.collection("tareas").document(it)
            tareaRef.update(
                mapOf(
                    "nombre" to nombre,
                    "fecha" to fecha,
                    "descripcion" to descripcion,
                    "asignatura" to asignatura,
                    "tipo" to tipo,
                    "prioridad" to prioridad
                )
            ).addOnSuccessListener {

                Toast.makeText(this, "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
    private fun eliminarTarea() {
        val tareaId = intent.getStringExtra("actividadId")

        tareaId?.let {
            val tareaRef = firestore.collection("tareas").document(it)
            tareaRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
        }
    }
    private fun marcarCompletada() {
        tareaId?.let {
            firestore.collection("tareas").document(it)
                .update("estado", "completada")
                .addOnSuccessListener {
                    Toast.makeText(this, "Tarea marcada como completada", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
        }
    }
}
