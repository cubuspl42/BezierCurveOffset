package app.algebra.linear

sealed class VectorOrientation {
    data object Horizontal : VectorOrientation()

    data object Vertical : VectorOrientation()
}
