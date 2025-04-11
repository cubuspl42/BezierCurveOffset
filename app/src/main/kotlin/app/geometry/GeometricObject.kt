package app.geometry

interface GeometricObject {
    fun equalsWithTolerance(
        other: GeometricObject,
        tolerance: GeometricTolerance,
    ): Boolean
}
