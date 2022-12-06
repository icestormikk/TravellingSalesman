package interfaces

import domain.Vertex

interface Edge {
    val fromVertex: Vertex
    val toVertex: Vertex
}