package lopez.marcos.equipo5_proyectofinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lopez.marcos.equipo5_proyectofinal.ui.theme.Equipo5_proyectoFinalTheme

class NuevoCicloActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private val itemList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevociclo)
        recyclerView = findViewById(R.id.recyclerView)
        val fab: Button = findViewById(R.id.agregarmateria)

        adapter = MyAdapter(itemList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            addItemToList()
        }

    }
    private fun addItemToList() {
        val materia: EditText = findViewById(R.id.materia)

        val newItem = "${materia.text.toString()}"
        adapter.addItem(newItem)
    }


    private class MyAdapter(private val itemList: MutableList<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = itemList[position]
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        fun addItem(item: String) {
            itemList.add(item)
            notifyItemInserted(itemList.size - 1)
        }
    }
}