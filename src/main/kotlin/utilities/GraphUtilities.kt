package utilities

import domain.Vertex
import domain.WeightedEdge
import domain.WeightedGraph

private const val ENGLISH_ALPHABET_LETTERS_COUNT = 26
private const val ASCII_ENGLISH_ALPHABET_START_CODE = 65

object GraphUtilities {
    fun <T: Number> Array<Array<T>>.toTypedGraph(
        infinityValue: T
    ) : WeightedGraph<T> {
        val verticesList = mutableListOf<Vertex>().also { list ->
            indices.forEach { list.add(Vertex(pickLetterByCount(it))) }
        }
        val edgesList = mutableListOf<WeightedEdge<T>>().also { list ->
            indices.forEach { i ->
                indices.forEach { j ->
                    if (this[i][j] != infinityValue)
                        list.add(WeightedEdge(verticesList[i], verticesList[j], this[i][j]))
                }
            }
        }

        return WeightedGraph(edgesList)
    }

    private fun pickLetterByCount(count: Int) : String =
        StringBuilder().apply {
            repeat(count / ENGLISH_ALPHABET_LETTERS_COUNT) { append('Z') }
            val letterIndex = count % ENGLISH_ALPHABET_LETTERS_COUNT
            append(
                (ASCII_ENGLISH_ALPHABET_START_CODE + letterIndex).toChar()
            )
        }.toString()
}