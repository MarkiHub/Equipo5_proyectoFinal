package lopez.marcos.equipo5_proyectofinal

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import lopez.marcos.equipo5_proyectofinal.databinding.ActivityMainBinding
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NavActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    companion object {
        var nombreUsuario: String = "a"
        var correoUsuario: String = "a"
        var materias_ciclo =  ArrayList<String>()
        var tipos_asignaciones = listOf("Examen", "Tarea", "Proyecto", "Otro")
        fun actualizarMateriasCiclo(nuevasMaterias: List<String>) {
            materias_ciclo.clear()
            materias_ciclo.addAll(nuevasMaterias)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        val navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        navView.setupWithNavController(navController)

        obtenerNombreUsuario()
        configCiclos()
    }
    private fun obtenerNombreUsuario() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("usuarios").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nombre = document.getString("nombre")
                        if (nombre != null) {
                            nombreUsuario = nombre
                        }
                        val correo = currentUser.email
                        if (correo != null) {
                            correoUsuario = correo
                        }
                    }
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarMateriasPorCiclo(ciclo: String) {
        firestore.collection("ciclos").document(ciclo)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    actualizarMateriasCiclo(document["materias"] as? ArrayList<String> ?: emptyList())
                }
            }
    }
    private fun configCiclos() {
        firestore.collection("ciclos")
            .whereEqualTo("usuario", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                val cicloIds = documents.map { it.id }


                val selectedCicloId = getSelectedCiclo()
                if (selectedCicloId != null) {
                    val selectedIndex = cicloIds.indexOf(selectedCicloId)
                    if (selectedIndex >= 0) {
                        val cicloSeleccionado = cicloIds[selectedIndex]
                        cargarMateriasPorCiclo(cicloSeleccionado)
                    }
                }
            }
    }
    private fun getSelectedCiclo(): String? {
        val sharedPreferences = getSharedPreferences("ciclos_${auth.currentUser?.uid}", Activity.MODE_PRIVATE)
        return sharedPreferences.getString("selected_ciclo_id", null)
    }
}
