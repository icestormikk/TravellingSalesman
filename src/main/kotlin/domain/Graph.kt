package domain

import interfaces.Graph

open class NotWeightedGraph(
    edges: List<NotWeightedEdge>
) : Graph<NotWeightedEdge> {
    open val id: Long = entityCounter++
    override val adjVertices = mutableMapOf<Vertex, MutableList<Vertex>>()

    constructor(vararg edges: NotWeightedEdge) : this(edges.toList())

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
    edges: List<WeightedEdge<T>>
) : Graph<WeightedEdge<T>> {
    val id: Long = entityCounter++
    override val adjVertices = mutableMapOf<Vertex, MutableList<Pair<Vertex, T>>>()

    constructor(vararg edges: WeightedEdge<T>) : this(edges.toList())

    companion object {
        private var entityCounter = 0L
    }

    init {
        edges.toSet().forEach {
            if (edges.firstOrNull { edge -> edge.fromVertex.id == it.toVertex.id && edge.toVertex.id == it.fromVertex.id } == null)
                throw IllegalArgumentException(
                    "The road from point ${it.toVertex.label} to point ${it.fromVertex.label} was not found, " +
                            "but the road from point ${it.fromVertex.label} to point ${it.toVertex.label} exists"
                )

            with (adjVertices) {
                putIfAbsent(it.fromVertex, mutableListOf())
                this[it.fromVertex]!!.add(Pair(it.toVertex, it.weight))
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

inline fun <reified T : Number> WeightedGraph<T>.toAdjacencyMatrix(
    infinityValue: T
) : Array<Array<T>> {
    val matrix = Array(adjVertices.size) {
        val initialValue = adjVertices[adjVertices.keys.first()]!![0].second
        Array(adjVertices.size) { initialValue }
    }

    for (i in matrix.indices) {
        for (j in matrix.indices) {
            with (adjVertices) {
                val distance =
                    get(adjVertices.keys.elementAt(i))!!
                    .firstOrNull { it.first.id == adjVertices.keys.elementAt(j).id }
                    ?.second ?: infinityValue
                matrix[i][j] = distance
            }
        }
    }

    return matrix
}
