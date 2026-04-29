package dam.pmdm.tarea6_gutierrezruiz_francisco.datos

data class Mision(
    val puntoControl: String,
    val actividad: String,
    val enunciado: String,
    val codigoMisionCompletada: String,
    var completada: Boolean = false
)
