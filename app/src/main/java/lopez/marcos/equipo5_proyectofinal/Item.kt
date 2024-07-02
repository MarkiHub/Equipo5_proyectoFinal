package lopez.marcos.equipo5_proyectofinal

sealed class Item {
    data class Day(val date: String) : Item()
    data class Activity(val subject: String, val type: String, val description: String) : Item()
}
