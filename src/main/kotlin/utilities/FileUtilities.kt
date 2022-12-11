package utilities

import domain.WeightedGraph
import utilities.GraphUtilities.toTypedGraph
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.Path

private const val STANDARD_INFINITY_INDICATOR = "M"

object FileUtilities {
    fun fetchGraphFromArguments(args: Array<String>) : WeightedGraph<Double>? =
        try {
            if (args.isNotEmpty()) {
                val filepath = Path(args[0])
                readFile(filepath).toTypedGraph(infinityValue = Double.MAX_VALUE)
            } else {
                println("The file with the graph is not specified. I will use standard graph.")
                fetchDefaultGraph()
            }
        } catch (_: FileNotFoundException) {
            System.err.println("The specified file was not found: ${Path(args[0])}")
            print("Use a standard graph? (y/n): ")
            if (readln() != "y") null
            else fetchDefaultGraph()
        } catch (ex: NumberFormatException) {
            System.err.println(ex.localizedMessage)
            null
        } catch (ex: IllegalArgumentException) {
            System.err.println(ex.localizedMessage)
            null
        }

    private fun readFile(path: Path) : Array<Array<Double>> {
        File(path.toString()).readLines().apply {
            println("File successfully fetched: $path")
            print("Which symbol will be used to indicate the infinite length of the path? ($STANDARD_INFINITY_INDICATOR): ")
            val userCallback = readln()
            val infinitePathIndicator = userCallback.ifBlank { STANDARD_INFINITY_INDICATOR }

            var result: MutableList<Array<Double>>? = null
            forEach {
                val arrayLine = it.split(Regex("\\s")).map { element ->
                    if (element == infinitePathIndicator)
                        Double.MAX_VALUE
                    else
                        element.toDouble().apply {
                            if (this <= 0.0)
                                throw IllegalArgumentException("The distance between the points should be positive ($this)")
                        }
                }.toTypedArray()
                if (result == null)
                    result = mutableListOf(arrayLine)
                else result!!.add(arrayLine)
            }
            return result!!.toTypedArray()
        }
    }

    private fun fetchDefaultGraph(): WeightedGraph<Double> =
        readFile(Path("examplematrix.txt"))
            .toTypedGraph(Double.MAX_VALUE)
}