package domain

import interfaces.Edge

data class NotWeightedEdge(
    override val fromVertex: Vertex,
    override val toVertex: Vertex,
) : Edge {
    val id: Long = entityCounter++

    companion object {
        private var entityCounter: Long = 0L
    }
}

data class WeightedEdge<T>(
    override val fromVertex: Vertex,
    override val toVertex: Vertex,
    val weight: T
) : Edge
