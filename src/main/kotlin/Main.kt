import domain.Vertex
import domain.WeightedEdge
import domain.WeightedGraph

fun main() {
    val vertex1 = Vertex("City A")
    val vertex2 = Vertex("City B")
    val vertex3 = Vertex("City C")
    val vertex4 = Vertex("City D")

    with (WeightedGraph<Int>()) {
        buildGraph(
            WeightedEdge(vertex1, vertex2, 10),
            WeightedEdge(vertex2, vertex3, 20),
            WeightedEdge(vertex3, vertex4, 30),
            WeightedEdge(vertex4, vertex1, 40),
            WeightedEdge(vertex1, vertex3, 5)
        )
        printGraph()
        println(totalDistance(0, 0, 3, 2) { source: Int, destination: Int ->
            source + destination
        })
    }
}