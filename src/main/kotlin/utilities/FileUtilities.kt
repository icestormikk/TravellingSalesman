package utilities

import domain.Vertex
import domain.WeightedEdge
import domain.WeightedGraph
import utilities.GraphUtilities.toTypedGraph
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.Path

private const val STANDARD_INFINITY_INDICATOR = "M"

object FileUtilities {
    fun fetchGraphFromArguments(args: Array<String>) : WeightedGraph<Double>? =
        if (args.isNotEmpty()) {
            val filepath = Path(args[0])
            try {
                readFile(filepath).toTypedGraph(infinityValue = Double.MAX_VALUE)
            } catch (_: FileNotFoundException) {
                System.err.println("The specified file was not found: $filepath")
                print("Use a standard graph? (y/n): ")
                if (readln() != "y")
                    fetchDefaultGraph()
                else null
            } catch (ex: NumberFormatException) {
                println(ex.localizedMessage)
                null
            }
        } else {
            println("The file with the graph is not specified. I will use standard graph.")
            fetchDefaultGraph()
        }

    private fun readFile(path: Path) : Array<Array<Double>> {
        File(path.toUri()).readLines().apply {
            println("File successfully fetched: $path")
            print("Which symbol will be used to indicate the infinite length of the path? ($STANDARD_INFINITY_INDICATOR): ")
            val userCallback = readln()
            val infinitePathIndicator = userCallback.ifBlank { STANDARD_INFINITY_INDICATOR }

            var result: MutableList<Array<Double>>? = null
            forEach {
                val arrayLine = it.split(Regex("\\s")).map { element ->
                    if (element == infinitePathIndicator) Double.MAX_VALUE
                    else element.toDouble()
                }.toTypedArray()
                if (result == null)
                    result = mutableListOf(arrayLine)
                else result!!.add(arrayLine)
            }
            return result!!.toTypedArray()
        }
    }

    private fun fetchDefaultGraph(): WeightedGraph<Double> {
        val vertex1 = Vertex("City A")
        val vertex2 = Vertex("City B")
        val vertex3 = Vertex("City C")
        val vertex4 = Vertex("City D")
        val vertex5 = Vertex("City E")
        return WeightedGraph(
            WeightedEdge(vertex1, vertex2, 20.0),
            WeightedEdge(vertex2, vertex3, 40.0),
            WeightedEdge(vertex3, vertex4, 60.0),
            WeightedEdge(vertex4, vertex5, 70.0),
            WeightedEdge(vertex5, vertex1, 10.0),
            WeightedEdge(vertex1, vertex4, 1.0),
            WeightedEdge(vertex1, vertex3, 2.0),
            WeightedEdge(vertex2, vertex4, 10.0),
            WeightedEdge(vertex2, vertex5, 40.0),
        )
    }
}