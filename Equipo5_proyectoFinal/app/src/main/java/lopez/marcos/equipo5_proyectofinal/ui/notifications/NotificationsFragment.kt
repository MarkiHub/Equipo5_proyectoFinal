package lopez.marcos.equipo5_proyectofinal.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import lopez.marcos.equipo5_proyectofinal.NuevoCicloActivity
import lopez.marcos.equipo5_proyectofinal.R
import lopez.marcos.equipo5_proyectofinal.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val button_nuevociclo: Button = root.findViewById(R.id.nuevociclo)

        button_nuevociclo.setOnClickListener {
            var intento: Intent = Intent(root.context,NuevoCicloActivity::class.java)
            startActivity(intento)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}