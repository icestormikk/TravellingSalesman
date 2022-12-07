package interfaces

import domain.Vertex

interface Graph<T> {
    val adjVertices: MutableMap<Vertex, *>

    fun printGraph()
    fun getVertexByID(vertexID: Long): Vertex =
        adjVertices.keys.firstOrNull() { it.id == vertexID }
            ?: throw IllegalArgumentException("Vertex with id $vertexID does not exists")

    fun getVertexOrNull(vertexID: Long): Vertex? =
        adjVertices.keys.firstOrNull { it.id == vertexID }

}

inline fun <reified T> Array<Array<T>>.copy(): Array<Array<T>> =
    Array(size) { get(it).clone() }
