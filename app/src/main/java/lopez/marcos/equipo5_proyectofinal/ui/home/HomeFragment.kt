package lopez.marcos.equipo5_proyectofinal.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lopez.marcos.equipo5_proyectofinal.AgregarActivity
import lopez.marcos.equipo5_proyectofinal.EditarActivity
import lopez.marcos.equipo5_proyectofinal.Item
import lopez.marcos.equipo5_proyectofinal.R
import lopez.marcos.equipo5_proyectofinal.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MultiViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val fab: View = root.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            var intento: Intent = Intent(root.context,AgregarActivity::class.java)
            startActivity(intento)
        }
        recyclerView = root.findViewById(R.id.recyclerView)

        val itemList = mutableListOf<Item>(
            Item.Day("xx/xx/xxxx"),
            Item.Activity("Materia", "Asignación", "Lorem ipsum dolor sit amet..."),
            Item.Activity("Materia", "Examen", "Lorem ipsum dolor sit amet..."),
            Item.Day("xx/xx/xxxx"),
            Item.Activity("Materia", "Examen", "Lorem ipsum dolor sit amet...")
        )

        adapter = MultiViewAdapter(itemList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class MultiViewAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_TYPE_DAY = 1
            const val VIEW_TYPE_ACTIVITY = 2
        }

        override fun getItemViewType(position: Int): Int {
            return when (itemList[position]) {
                is Item.Day -> VIEW_TYPE_DAY
                is Item.Activity -> VIEW_TYPE_ACTIVITY
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
                    ActivityViewHolder(view, parent.context)
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is DayViewHolder -> holder.bind(itemList[position] as Item.Day)
                is ActivityViewHolder -> holder.bind(itemList[position] as Item.Activity)
            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)

            fun bind(day: Item.Day) {
                textViewDate.text = day.date
            }
        }

        inner class ActivityViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
            private val textViewSubject: TextView = itemView.findViewById(R.id.textViewSubject)
            private val textViewType: TextView = itemView.findViewById(R.id.textViewType)
            private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
            private val editButton: Button = itemView.findViewById(R.id.editButton)

            fun bind(activity: Item.Activity) {
                textViewSubject.text = activity.subject
                textViewType.text = activity.type
                textViewDescription.text = activity.description
                editButton.setOnClickListener {
                    val intent = Intent(context, EditarActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }

    }
}