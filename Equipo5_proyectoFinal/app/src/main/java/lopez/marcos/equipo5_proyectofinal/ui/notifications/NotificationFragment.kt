package lopez.marcos.equipo5_proyectofinal.ui.notifications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import lopez.marcos.equipo5_proyectofinal.MainActivity
import lopez.marcos.equipo5_proyectofinal.NavActivity
import lopez.marcos.equipo5_proyectofinal.NuevoCicloActivity
import lopez.marcos.equipo5_proyectofinal.R
import lopez.marcos.equipo5_proyectofinal.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var nombreText: EditText
    private lateinit var correoText: EditText
    private lateinit var cicloSpinner: Spinner
    private lateinit var cerrarSesionButton: Button
    private lateinit var nuevoCicloButton: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var nuevoCicloAL: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        nombreText = root.findViewById(R.id.nombre)
        correoText = root.findViewById(R.id.correo)
        cicloSpinner = root.findViewById(R.id.asignaturas)
        cerrarSesionButton = root.findViewById(R.id.cerrarsesion)
        nuevoCicloButton = root.findViewById(R.id.nuevociclo)

        nombreText.setText(NavActivity.nombreUsuario)
        correoText.setText(NavActivity.correoUsuario)
        nombreText.isEnabled = false
        correoText.isEnabled = false

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        nuevoCicloAL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
            if (r.resultCode == Activity.RESULT_OK) {
                configSpinnerCiclos()
            }
        }

        nuevoCicloButton.setOnClickListener {
            val intent = Intent(root.context, NuevoCicloActivity::class.java)
            nuevoCicloAL.launch(intent)
        }

        cerrarSesionButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            clearSelectedCiclo()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            NavActivity.materias_ciclo.clear()
        }

        configSpinnerCiclos()

        return root
    }

    private fun saveSelectedCiclo(cicloId: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("ciclos_${auth.currentUser?.uid}", Activity.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("selected_ciclo_id", cicloId)
            apply()
        }
    }

    private fun clearSelectedCiclo() {
        val sharedPreferences = requireActivity().getSharedPreferences("ciclos_${auth.currentUser?.uid}", Activity.MODE_PRIVATE)
        sharedPreferences.edit().remove("selected_ciclo_id").apply()
    }

    private fun getSelectedCiclo(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("ciclos_${auth.currentUser?.uid}", Activity.MODE_PRIVATE)
        return sharedPreferences.getString("selected_ciclo_id", null)
    }

    private fun configSpinnerCiclos() {
        firestore.collection("ciclos")
            .whereEqualTo("usuario", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                val ciclos = documents.map { it.getString("ciclo") ?: "" }
                val cicloIds = documents.map { it.id }

                val cicloAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ciclos)
                cicloAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                cicloSpinner.adapter = cicloAdapter

                val selectedCicloId = getSelectedCiclo()
                if (selectedCicloId != null) {
                    val selectedIndex = cicloIds.indexOf(selectedCicloId)
                    if (selectedIndex >= 0) {
                        cicloSpinner.setSelection(selectedIndex)
                    }
                }

                cicloSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val cicloSeleccionado = cicloIds[position]
                        saveSelectedCiclo(cicloSeleccionado)
                        cargarMateriasPorCiclo(cicloSeleccionado)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            }
    }

    private fun cargarMateriasPorCiclo(ciclo: String) {
        firestore.collection("ciclos").document(ciclo)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    NavActivity.actualizarMateriasCiclo(document["materias"] as? ArrayList<String> ?: emptyList())
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
