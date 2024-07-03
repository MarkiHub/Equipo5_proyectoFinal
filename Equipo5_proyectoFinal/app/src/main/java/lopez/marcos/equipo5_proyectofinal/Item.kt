package lopez.marcos.equipo5_proyectofinal

sealed class Item {
    data class Dia(val fecha: String) : Item()
    data class Tarea(val nombre: String, val asignatura: String, val tipo: String, val descripcion: String, val prioridad: String, val fecha: String, val id: String, val estado: String) : Item()
}
