package interfaces

import domain.Vertex

interface Graph<T> {
    val adjVertices: MutableMap<Vertex, *>

    fun buildGraph(vararg edges: T)
    fun printGraph()
    fun getVertexByID(vertexID: Long) : Vertex =
        adjVertices.keys.firstOrNull() { it.id == vertexID }
            ?: throw IllegalArgumentException("Vertex with id $vertexID does not exists")
    fun getVertexOrNull(vertexID: Long) : Vertex? =
        adjVertices.keys.firstOrNull { it.id == vertexID }
}