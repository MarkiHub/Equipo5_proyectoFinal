package lopez.marcos.equipo5_proyectofinal.ui.home

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import lopez.marcos.equipo5_proyectofinal.AgregarActivity
import lopez.marcos.equipo5_proyectofinal.EditarActivity
import lopez.marcos.equipo5_proyectofinal.Item
import lopez.marcos.equipo5_proyectofinal.NotificationReceiver
import lopez.marcos.equipo5_proyectofinal.R
import lopez.marcos.equipo5_proyectofinal.databinding.FragmentHomeBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MultiViewAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var agregarAL: ActivityResultLauncher<Intent>
    private lateinit var editarAL: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val fab: View = root.findViewById(R.id.fab)

        agregarAL = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cargarTareas("pendiente")
            }
        }

        editarAL = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cargarTareas("pendiente")
            }
        }

        fab.setOnClickListener {
            val intent = Intent(requireContext(), AgregarActivity::class.java)
            agregarAL.launch(intent)
        }

        recyclerView = root.findViewById(R.id.recyclerView)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(root.context)
        adapter = MultiViewAdapter(mutableListOf(), editarAL)
        recyclerView.adapter = adapter

        val btnPendientes: Button = root.findViewById(R.id.pendientes)
        val btnCompletadas: Button = root.findViewById(R.id.completadas)

        btnPendientes.setOnClickListener {
            cargarTareas("pendiente")
        }

        btnCompletadas.setOnClickListener {
            cargarTareas("completada")
        }

        cargarTareas("pendiente")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cargarTareas(estado: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("tareas")
                .whereEqualTo("usuario", currentUser.uid)
                .whereEqualTo("estado", estado)
                .get()
                .addOnSuccessListener { documents ->
                    val tareasPorDia = mutableMapOf<String, MutableList<Item.Tarea>>()
                    val tareas = mutableListOf<Item.Tarea>()

                    for (document in documents) {
                        val id = document.id
                        val fecha = document.getString("fecha") ?: "No disponible"
                        val asignatura = document.getString("asignatura") ?: "No disponible"
                        val tipo = document.getString("tipo") ?: "No disponible"
                        val descripcion = document.getString("descripcion") ?: "No disponible"
                        val prioridad = document.getString("prioridad") ?: "No disponible"
                        val nombre = document.getString("nombre") ?: "No disponible"
                        val estado = document.getString("estado") ?: "No disponible"

                        val tarea = Item.Tarea(nombre, asignatura, tipo, descripcion, prioridad, fecha, id, estado)
                        tareas.add(tarea)

                        if (tareasPorDia.containsKey(fecha)) {
                            tareasPorDia[fecha]?.add(tarea)
                        } else {
                            tareasPorDia[fecha] = mutableListOf(tarea)
                        }
                    }

                    val sortedFechas = tareasPorDia.keys.sorted()

                    val itemList = mutableListOf<Item>()
                    for (fecha in sortedFechas) {
                        tareasPorDia[fecha]?.let { actividades ->
                            itemList.add(Item.Dia(fecha))
                            itemList.addAll(actividades)
                        }
                    }

                    adapter.updateItems(itemList)

                    programarNotificaciones(tareas)
                }
        }
    }
    private fun programarNotificaciones(tareas: List<Item.Tarea>) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        for (tarea in tareas) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("notificationId", tarea.id.hashCode())
                putExtra("message", "La tarea '${tarea.nombre}' vence hoy.")
            }
            val pendingIntent = PendingIntent.getBroadcast(context, tarea.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

            try {
                val fechaEntrega = dateFormat.parse(tarea.fecha)?.time ?: continue

                val triggerTime = fechaEntrega + TimeUnit.HOURS.toMillis(8)

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                Log.d("HomeFragment", "Notificación programada para la tarea '${tarea.nombre}' a las $triggerTime")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    private class MultiViewAdapter(
        private var itemList: MutableList<Item>,
        private val editarActivityLauncher: ActivityResultLauncher<Intent>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_TYPE_DAY = 1
            const val VIEW_TYPE_ACTIVITY = 2
        }

        override fun getItemViewType(position: Int): Int {
            return when (itemList[position]) {
                is Item.Dia -> VIEW_TYPE_DAY
                is Item.Tarea -> VIEW_TYPE_ACTIVITY
                else -> throw IllegalArgumentException("Tipo desconocido")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_TYPE_DAY -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
                    DayViewHolder(view)
                }
                VIEW_TYPE_ACTIVITY -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
                    ActivityViewHolder(view, parent.context, editarActivityLauncher)
                }
                else -> throw IllegalArgumentException("Tipo desconocido")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is DayViewHolder -> holder.bind(itemList[position] as Item.Dia)
                is ActivityViewHolder -> holder.bind(itemList[position] as Item.Tarea)
            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        fun updateItems(newItems: List<Item>) {
            itemList.clear()
            itemList.addAll(newItems)
            notifyDataSetChanged()
        }

        inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)

            fun bind(day: Item.Dia) {
                textViewDate.text = day.fecha
            }
        }

        inner class ActivityViewHolder(
            itemView: View,
            private val context: Context,
            private val editarActivityLauncher: ActivityResultLauncher<Intent>
        ) : RecyclerView.ViewHolder(itemView) {
            private val textViewSubject: TextView = itemView.findViewById(R.id.textViewSubject)
            private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
            private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
            private val editButton: Button = itemView.findViewById(R.id.editButton)

            fun bind(activity: Item.Tarea) {

                textViewSubject.text = activity.asignatura
                textViewName.text = activity.nombre
                textViewDescription.text = activity.descripcion
                if(activity.estado.equals("completada", true)){
                    editButton.isEnabled = false
                }else {
                    editButton.isEnabled = true
                }

                editButton.setOnClickListener {

                    val intent = Intent(context, EditarActivity::class.java).apply {
                        putExtra("actividadId", activity.id)
                        putExtra("nombre", activity.nombre)
                        putExtra("asignatura", activity.asignatura)
                        putExtra("fecha", activity.fecha)
                        putExtra("descripcion", activity.descripcion)
                        putExtra("asignatura", activity.asignatura)
                        putExtra("tipo", activity.tipo)
                        putExtra("prioridad", activity.prioridad)
                    }
                    editarActivityLauncher.launch(intent)
                }
            }
        }

    }
}
