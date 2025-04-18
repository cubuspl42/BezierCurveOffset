package app.geometry

interface GeometricObject {
    fun equalsWithGeometricTolerance(
        other: GeometricObject,
        tolerance: GeometricTolerance,
    ): Boolean
}
