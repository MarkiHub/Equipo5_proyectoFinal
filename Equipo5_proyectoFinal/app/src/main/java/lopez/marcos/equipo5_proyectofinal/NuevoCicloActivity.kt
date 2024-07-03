package lopez.marcos.equipo5_proyectofinal

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NuevoCicloActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private val itemList = mutableListOf<String>()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var addMateriaButton: Button
    private lateinit var addCicloButton: Button
    private lateinit var nombreEditText: EditText
    private lateinit var materiaEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevociclo)

        recyclerView = findViewById(R.id.recyclerView)
        addMateriaButton = findViewById(R.id.agregarmateria)
        addCicloButton = findViewById(R.id.agregar)
        nombreEditText = findViewById(R.id.nombre)
        materiaEditText = findViewById(R.id.materia)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        adapter = MyAdapter(itemList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addMateriaButton.setOnClickListener {
            addItemToList(materiaEditText)
        }

        addCicloButton.setOnClickListener {
            agregarCiclo(nombreEditText)
        }
    }

    private fun addItemToList(materiaEditText: EditText) {
        val newItem = materiaEditText.text.toString()
        if (newItem.isNotEmpty()) {
            adapter.addItem(newItem)
            materiaEditText.text.clear()
        }
    }

    private fun agregarCiclo(nombreEditText: EditText) {
        val nombreCiclo = nombreEditText.text.toString()
        if (nombreCiclo.isNotEmpty() && itemList.isNotEmpty()) {
            val ciclo = hashMapOf(
                "ciclo" to nombreCiclo,
                "usuario" to auth.currentUser?.uid,
                "materias" to itemList
            )

            firestore.collection("ciclos")
                .add(ciclo)
                .addOnSuccessListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }

        }
    }

    private class MyAdapter(private val itemList: MutableList<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)
            val deleteButton: Button = itemView.findViewById(R.id.eliminar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view2, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = itemList[position]
            holder.deleteButton.setOnClickListener {
                removeItem(position)
            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        fun addItem(item: String) {
            itemList.add(item)
            notifyItemInserted(itemList.size - 1)
        }

        private fun removeItem(position: Int) {
            if (position >= 0 && position < itemList.size) {
                itemList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemList.size)
            }
        }
    }
}
