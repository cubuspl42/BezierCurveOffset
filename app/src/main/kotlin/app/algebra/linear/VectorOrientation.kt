package app.algebra.linear

sealed class VectorOrientation {
    sealed class Relevant : VectorOrientation()

    data object Horizontal : Relevant()

    data object Vertical : Relevant()

    data object Irrelevant : VectorOrientation()
}
