package domain

data class Vertex(
    val label: String,
) {
    val id: Long = entityCounter++

    companion object {
        private var entityCounter = 0L
    }
}