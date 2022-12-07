package domain

import interfaces.Graph

open class NotWeightedGraph(
    vararg edges: NotWeightedEdge
) : Graph<NotWeightedEdge> {
    open val id: Long = entityCounter++
    override val adjVertices = mutableMapOf<Vertex, MutableList<Vertex>>()

    companion object {
        private var entityCounter = 0L
    }

    init {
        edges.forEach {
            with (adjVertices) {
                putIfAbsent(it.fromVertex, mutableListOf())
                this[it.fromVertex]!!.add(it.toVertex)
            }
        }
    }

    override fun printGraph() {
        adjVertices.keys.forEach {
            adjVertices[it]?.forEach {
                    outgoingVertices -> print("${it.id} -> ${outgoingVertices.id} ")
            }
            println()
        }
    }
}

class WeightedGraph<T : Number>(
    vararg edges: WeightedEdge<T>
) : Graph<WeightedEdge<T>> {
    companion object {
        private var entityCounter = 0L
    }
    val id: Long = entityCounter++
    override val adjVertices = mutableMapOf<Vertex, MutableList<Pair<Vertex, T>>>()

    init {
        edges.forEach {
            with (adjVertices) {
                putIfAbsent(it.fromVertex, mutableListOf())
                putIfAbsent(it.toVertex, mutableListOf())
                this[it.fromVertex]!!.add(Pair(it.toVertex, it.weight))
                this[it.toVertex]!!.add(Pair(it.fromVertex, it.weight))
            }
        }
    }

    override fun printGraph() {
        adjVertices.keys.forEach {
            adjVertices[it]?.forEach {
                    outgoingVertices -> print("${it.id} -${outgoingVertices.second}-> ${outgoingVertices.first.id} ")
            }
            println()
        }
    }

    fun totalDistance(
        initialValue: T,
        vararg ids: Long,
        operator: (source: T, destination: T) -> T
    ): T {
        var currentVertex = getVertexByID(ids[0])
        var result = initialValue
        (1 until ids.size).forEach { i ->
            val edgeInfo = adjVertices[currentVertex]!!
                .firstOrNull { it.first == getVertexByID(ids[i]) }
                ?: error("Vertexes with ids ${ids[i - 1]} and ${ids[i]} do not connected")

            result = operator(result, edgeInfo.second)
            currentVertex = edgeInfo.first
        }

        return result
    }
}

inline fun <reified T : Number> WeightedGraph<T>.toAdjacencyMatrix() : Array<Array<T>> {
    val matrix = Array(adjVertices.size) {
        Array(adjVertices.size) { 0 as T }
    }

    for (i in matrix.indices) {
        for (j in i until matrix[0].size) {
            with (adjVertices) {
                val distance = (get(adjVertices.keys.elementAt(i))!!
                    .firstOrNull { it.first.id == adjVertices.keys.elementAt(j).id }
                    ?.second ?: Int.MAX_VALUE) as T
                matrix[i][j] = distance
                matrix[j][i] = distance
            }
        }
    }

    return matrix
}
