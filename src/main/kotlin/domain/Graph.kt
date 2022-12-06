package domain

import interfaces.Graph

open class NotWeightedGraph : Graph<NotWeightedEdge> {
    open val id: Long = entityCounter++
    override val adjVertices = mutableMapOf<Vertex, MutableList<Vertex>>()

    companion object {
        private var entityCounter = 0L
    }

    override fun buildGraph(vararg edges: NotWeightedEdge) {
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

data class WeightedGraph<T>(
    val id: Long = entityCounter++
) : Graph<WeightedEdge<T>> {
    companion object {
        private var entityCounter = 0L
    }
    override val adjVertices = mutableMapOf<Vertex, MutableList<Pair<Vertex, T>>>()

    override fun buildGraph(vararg edges: WeightedEdge<T>) {
        edges.forEach {
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