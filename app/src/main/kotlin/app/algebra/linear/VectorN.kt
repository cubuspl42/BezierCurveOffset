package app.algebra.linear

data class VectorN(
    val elements: List<Double>,
) {
    fun dot(
        other: VectorN,
    ): Double {
        require(elements.size == other.elements.size)
        return elements.zip(other.elements).sumOf { (a, b) -> a * b }
    }
}
