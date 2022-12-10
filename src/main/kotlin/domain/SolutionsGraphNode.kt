package domain

import enums.NodeType

data class SolutionsGraphNode<T>(
    val type: NodeType,
    val lowerBorder: T?,
    val path: List<Pair<String, String>> = listOf(),
    val currentAdjacencyMatrix: Array<Array<T>>,
    val rowsList: MutableList<String> = mutableListOf(),
    val columnsList: MutableList<String> = mutableListOf(),
    val rowsMinimums: List<T>? = null,
    val columnsMinimums: List<T>? = null
) {
    val id: Long = entityCounter++

    companion object {
        var entityCounter = 0L
            private set
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SolutionsGraphNode<*>) return false

        if (type != other.type) return false
        if (lowerBorder != other.lowerBorder) return false
        if (path != other.path) return false
        if (!currentAdjacencyMatrix.contentDeepEquals(other.currentAdjacencyMatrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (lowerBorder?.hashCode() ?: 0)
        result = 31 * result + path.hashCode()
        result = 31 * result + currentAdjacencyMatrix.contentDeepHashCode()
        return result
    }
}
