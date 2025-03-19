package app

data class Line(
    val point: Point,
    val direction: Vector,
) {
    fun intersection(
        other: Line,
    ): Point? {
        val d = direction.cross(other.direction)
        if (d == 0.0) return null

        val v = other.point - point
        val t = v.cross(other.direction) / d
        return point + direction.scale(t)
    }
}
