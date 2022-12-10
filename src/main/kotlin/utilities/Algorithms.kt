package utilities

import domain.SolutionsGraphNode
import domain.WeightedGraph
import domain.toAdjacencyMatrix
import enums.NodeType
import enums.NodeType.PATH_EXCLUDED
import enums.NodeType.PATH_INCLUDED
import enums.NodeType.ROOT

object Algorithms {
    inline fun <reified T> WeightedGraph<T>.travellingSalesman(
        labelsList: MutableList<String>,
        zeroValue: T,
        infinityValue: T,
        noinline onMinus: (x: T, y: T) -> T,
        noinline onPlus: (x: T, y: T) -> T
    ) where T: Number, T: Comparable<T> {
        val solutions = mutableListOf<SolutionsGraphNode<T>>()
        var currentSolution = SolutionsGraphNode(
            type = ROOT,
            lowerBorder = null,
            currentAdjacencyMatrix = this.toAdjacencyMatrix(infinityValue),
            rowsList = labelsList,
            columnsList = labelsList
        )

        while (!currentSolution.currentAdjacencyMatrix.pathIsFind(infinityValue)) {
            currentSolution.currentAdjacencyMatrix.apply {
                val rowsMinimums = if (currentSolution.type != PATH_INCLUDED)
                    rowsReduction(onMinus)
                else currentSolution.rowsMinimums!!
                val columnsMinimums = if (currentSolution.type != PATH_INCLUDED) {
                    columnsReduction(onMinus)
                } else currentSolution.columnsMinimums!!
                val localLowerBorder = if (currentSolution.type != PATH_INCLUDED)
                    getLocalLowerBorder(
                        type = currentSolution.type,
                        zeroValue = zeroValue,
                        previousValue =
                            if (currentSolution.type != ROOT) currentSolution.lowerBorder else null,
                        minimumsByRowsAndColumns =
                            if (currentSolution.type == ROOT) Pair(rowsMinimums, columnsMinimums) else null,
                        onPlus = onPlus,
                        onMinus = onMinus
                    )
                else currentSolution.lowerBorder

                val maxZeroDegreeCell = getMaxZeroDegreeCell(zeroValue, onPlus)
                val reducedMatrix = reduction(
                    rowLabels = currentSolution.rowsList,
                    columnLabels = currentSolution.columnsList,
                    excludingCell = maxZeroDegreeCell.first,
                    infinityValue = infinityValue
                )

                val removedRowLabel = currentSolution.rowsList.elementAt(maxZeroDegreeCell.first.first)
                val removedColumnLabel = currentSolution.columnsList.elementAt(maxZeroDegreeCell.first.second)

                val updatedRowsList = currentSolution.rowsList.minus(removedRowLabel).toMutableList()
                val updatedColumnsList = currentSolution.columnsList.minus(removedColumnLabel).toMutableList()

                with(solutions) {
                    removeIf { it.id == currentSolution.id }
                    if (localLowerBorder != null) {
                        addAll(
                            listOf(
                                SolutionsGraphNode(
                                    type = PATH_INCLUDED,
                                    lowerBorder = reducedMatrix.getLocalLowerBorder(
                                        type = PATH_INCLUDED,
                                        zeroValue = zeroValue,
                                        previousValue = localLowerBorder,
                                        onPlus = onPlus,
                                        onMinus = onMinus
                                    ),
                                    path = currentSolution.path.plus(Pair(removedRowLabel, removedColumnLabel)),
                                    currentAdjacencyMatrix = reducedMatrix,
                                    rowsList = updatedRowsList,
                                    columnsList = updatedColumnsList,
                                    rowsMinimums = rowsMinimums,
                                    columnsMinimums = columnsMinimums
                                ),
                                SolutionsGraphNode(
                                    type = PATH_EXCLUDED,
                                    lowerBorder = onPlus(localLowerBorder, maxZeroDegreeCell.second),
                                    path = currentSolution.path,
                                    currentAdjacencyMatrix = currentSolution.currentAdjacencyMatrix,
                                    rowsList = currentSolution.rowsList,
                                    columnsList = currentSolution.columnsList,
                                    rowsMinimums = rowsMinimums,
                                    columnsMinimums = columnsMinimums
                                )
                            )
                        )
                    }
                }
            }
            currentSolution = solutions
                .filter { (it.currentAdjacencyMatrix.size > 1) || (it.currentAdjacencyMatrix[0][0] != infinityValue) }
                .minBy { it.lowerBorder ?: infinityValue }
        }

        with(currentSolution) {
            val pathFinalPart =
                if (currentAdjacencyMatrix.size > 1)
                    currentAdjacencyMatrix.getMaxZeroDegreeCell(zeroValue, onPlus).first
                else Pair(0,0)
            val finalPath = path.plus(Pair(rowsList[pathFinalPart.first], columnsList[pathFinalPart.second]))

            println("""
                The most optimal path: ${finalPath.toOneSortedPath().joinToString(" -> ")}
                Total length: ${currentSolution.lowerBorder}""".trimIndent()
            )
        }
    }

    fun <T> Array<Array<T>>.rowsReduction(
        minus: (reduced: T, deductible: T) -> T
    ) : List<T> where T: Number, T: Comparable<T> {
        val rowMinimums = indices.map { findMinimumInRow(it) }
        indices.forEach {
            rowReduction(it, rowMinimums[it]) { first: T, second: T ->
                minus(first, second)
            }
        }
        return rowMinimums
    }

    fun <T> Array<Array<T>>.columnsReduction(
        minus: (reduced: T, deductible: T) -> T
    ) : List<T> where T: Number, T: Comparable<T> {
        val columnMinimums = indices.map { findMinimumInColumn(it) }
        indices.forEach {
            columnReduction(it, columnMinimums[it]) { first: T, second: T ->
                minus(first, second)
            }
        }
        return columnMinimums
    }

    fun <T> Array<Array<T>>.getLocalLowerBorder(
        type: NodeType,
        zeroValue: T,
        previousValue: T? = null,
        minimumsByRowsAndColumns: Pair<List<T>, List<T>>? = null,
        onPlus: (x: T, y: T) -> T,
        onMinus: ((x: T, y: T) -> T)? = null
    ): T where T: Number, T: Comparable<T> {
        return when (type) {
            ROOT -> {
                with (minimumsByRowsAndColumns!!) {
                    onPlus(
                        first.fold(zeroValue, onPlus),
                        second.fold(zeroValue, onPlus)
                    )
                }
            }
            PATH_INCLUDED -> {
                val rowMinimumsSum = rowsReduction(onMinus!!).fold(zeroValue, onPlus)
                val columnMinimumSum = columnsReduction(onMinus).fold(zeroValue, onPlus)
                val totalSum = onPlus(rowMinimumsSum, columnMinimumSum)

                onPlus(previousValue!!, totalSum)
            }
            PATH_EXCLUDED -> {
                onPlus(
                    previousValue!!,
                    getMaxZeroDegreeCell(zeroValue, onPlus).second
                )
            }
        }
    }

    fun <T> Array<Array<T>>.getMaxZeroDegreeCell(
        zeroValue: T,
        plus: (x: T, y: T) -> T
    ): Pair<Pair<Int, Int>, T> where T: Number, T: Comparable<T> {
        val zeros = mutableListOf<Pair<Pair<Int, Int>, T>>()
        indices.forEach { i ->
            indices.forEach { j ->
                if (this[i][j] == zeroValue) {
                    val degree = plus(
                        findMinimumInRow(rowIndex = i, excludedCell = Pair(i, j)),
                        findMinimumInColumn(columnIndex = j, excludedCell = Pair(i, j))
                    )
                    zeros.add(Pair(Pair(i, j), degree))
                }
            }
        }
        return zeros.maxBy { it.second }
    }

    fun <T> Array<Array<T>>.printAdjacencyMatrix(infinityValue: T) {
        forEach { array ->
            array.forEach {
                print(String.format("%-3s", if (it == infinityValue) 'M' else "$it"))
            }
            println()
        }
    }

    inline fun <reified T> Array<Array<T>>.reduction(
        rowLabels: List<String>,
        columnLabels: List<String>,
        excludingCell: Pair<Int, Int>,
        infinityValue: T
    ): Array<Array<T>> where T: Number, T: Comparable<T> {
        val result = Array(size - 1) { arrayOf<T>() }
        var rowInResultCounter = 0
        closeOppositePath(
            rowLabels = rowLabels,
            columnLabels = columnLabels,
            currentCellCords = excludingCell,
            infinityValue = infinityValue
        )

        forEachIndexed { index: Int, values: Array<T> ->
            if (index != excludingCell.first) {
                result[rowInResultCounter] =
                    values.filterIndexed { elementIndex, _ ->
                        elementIndex != excludingCell.second
                    }.toTypedArray()
                rowInResultCounter++
            }
        }

        return result
    }

    fun <T> Array<Array<T>>.pathIsFind(
        infinityValue: T
    ) : Boolean where T: Number, T: Comparable<T> {
        return size*size - sumOf { it.count { elem -> elem == infinityValue } } == 1
    }

    fun <T> Array<Array<T>>.closeOppositePath(
        rowLabels: List<String>,
        columnLabels: List<String>,
        currentCellCords: Pair<Int, Int>,
        infinityValue: T,
    ) {
        val currentLabelsPair = Pair(rowLabels[currentCellCords.first], columnLabels[currentCellCords.second])

        val newRowIndex = rowLabels.indexOfFirst { it == currentLabelsPair.second }.apply {
            if (this < 0) return
        }
        val newColumnIndex = columnLabels.indexOfFirst { it == currentLabelsPair.first }.apply {
            if (this < 0) return
        }
        this[newRowIndex][newColumnIndex] = infinityValue
    }

    fun List<Pair<String, String>>.toOneSortedPath(): List<String> {
        val result = mutableSetOf<String>()
        var sortingPair = minBy { it.first }

        while (!result.contains(sortingPair.second)) {
            result.addAll(sortingPair.toList())
            sortingPair = first { it.first == sortingPair.second }
        }

        return result.toMutableList().plus(sortingPair.second)
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
        minus: (x: T, y: T) -> T
    ) where T: Number, T: Comparable<T> {
        for (i in this.indices)
            this[rowIndex][i] = minus(this[rowIndex][i], subtractedValue)
    }

    private fun <T> Array<Array<T>>.columnReduction(
        columnIndex: Int,
        subtractedValue: T,
        minus: (x: T, y: T) -> T
    ) where T: Number, T: Comparable<T> {
        for (i in this.indices)
            this[i][columnIndex] = minus(this[i][columnIndex], subtractedValue)
    }
}