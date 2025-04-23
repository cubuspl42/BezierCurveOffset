package app.geometry.transformations

sealed class SimpleTransformation : Transformation() {
    fun combineWith(
        other: SimpleTransformation,
    ): ComplexTransformation = ComplexTransformation(
        transformations = listOf(this, other),
    )

    abstract override val inverted: SimpleTransformation
}
