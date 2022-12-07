package utilities

object Algorithms {
    fun <T> Array<Array<T>>.rowsReduction(
        minus: (reduced: T, deductible: T) -> T
    ) where T: Number, T: Comparable<T> {
        val rowMinimums = indices.map { findMinimumInRow(it) }
        indices.forEach {
            rowReduction(it, rowMinimums[it]) { first: T, second: T ->
                minus(first, second)
            }
        }
    }

    fun <T> Array<Array<T>>.columnsReduction(
        minus: (reduced: T, deductible: T) -> T
    ) where T: Number, T: Comparable<T> {
        val columnMinimums = indices.map { findMinimumInColumn(it) }
        indices.forEach {
            columnReduction(it, columnMinimums[it]) { first: T, second: T ->
                minus(first, second)
            }
        }
    }

    fun <T> Array<Array<T>>.getMaxZeroDegreeCell(
        plus: (element1: T, element2: T) -> T
    ): Pair<Int, Int>
    where T: Number, T: Comparable<T> {
        val zeros = mutableListOf<Pair<Pair<Int, Int>, T>>()
        indices.forEach { i ->
            indices.forEach { j ->
                if (this[i][j] == 0) {
                    val degree = plus(
                        findMinimumInRow(rowIndex = i, excludedCell = Pair(i, j)),
                        findMinimumInColumn(columnIndex = j, excludedCell = Pair(i, j))
                    )
                    zeros.add(Pair(Pair(i, j), degree))
                }
            }
        }
        println(zeros)

        return zeros.maxBy { it.second }.first
    }

    fun Array<Array<Int>>.printAdjacencyMatrix() {
        forEach { array ->
            array.forEach {
                print(String.format("%-3s", if (it == Int.MAX_VALUE) 'M' else "$it"))
            }
            println()
        }
    }

    private fun <T> Array<Array<T>>.findMinimumInRow(
        rowIndex: Int,
        excludedCell: Pair<Int, Int>? = null
    ): T where T: Number, T: Comparable<T> {
        if (rowIndex !in indices)
            throw IllegalArgumentException("The row index is out of bounds: $rowIndex, $indices")

        return this[rowIndex].filterIndexed { index, _ ->
            if (excludedCell != null)
                index != excludedCell.second
            else true
        }.minBy { it }
    }

    private fun <T> Array<Array<T>>.findMinimumInColumn(
        columnIndex: Int,
        excludedCell: Pair<Int, Int>? = null
    ): T where T: Number, T: Comparable<T> {
        if (columnIndex !in indices)
            throw IllegalArgumentException("The column index is out of bounds: $columnIndex, $indices")

        return this.filterIndexed { index, _ ->
            if (excludedCell != null)
                index != excludedCell.first
            else true
        }.minOf { it[columnIndex] }
    }

    private fun <T> Array<Array<T>>.rowReduction(
        rowIndex: Int,
        subtractedValue: T,
        minus: (reduced: T, deductible: T) -> T
    ) where T: Number, T: Comparable<T> {
        for (i in this.indices)
            this[rowIndex][i] = minus(this[rowIndex][i], subtractedValue)
    }

    private fun <T> Array<Array<T>>.columnReduction(
        columnIndex: Int,
        subtractedValue: T,
        minus: (reduced: T, deductible: T) -> T
    ) where T: Number, T: Comparable<T> {
        for (i in this.indices)
            this[i][columnIndex] = minus(this[i][columnIndex], subtractedValue)
    }
}