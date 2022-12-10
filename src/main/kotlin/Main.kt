import utilities.Algorithms.travellingSalesman
import utilities.FileUtilities.fetchGraphFromArguments
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val graph = fetchGraphFromArguments(args) ?: return

    val infinityDoubleValue = Double.MAX_VALUE
    val minus: (x: Double, y: Double) -> Double = { x, y ->
        if (x != Double.MAX_VALUE && y != Double.MAX_VALUE)
            x - y
        else x
    }
    val plus: (x: Double, y: Double) -> Double = { x, y ->
        if (x != Double.MAX_VALUE && y != Double.MAX_VALUE)
            x + y
        else x
    }

    val duration = measureTime {
        graph.travellingSalesman(
            labelsList = graph.getAllVertices().map { it.label }.toMutableList(),
            zeroValue = 0.0,
            infinityValue = infinityDoubleValue,
            onMinus = minus,
            onPlus = plus
        )
    }
    println("\nExecution time: $duration")
}