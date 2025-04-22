package app.algebra.euclidean.bezier_binomials

sealed class BezierBinomial : ParametricCurveFunction() {
    data class CriticalPointSet(
        val criticalPointsX: Set<Double>,
        val criticalPointsY: Set<Double>,
    ) {
        companion object {
            private const val eps = 0.00001
            private val range = 0.0..1.0
        }

        val criticalPointsXY: Set<Double> by lazy {
            criticalPointsX + criticalPointsY
        }

        fun filterInteresting(): CriticalPointSet = CriticalPointSet(
            criticalPointsX = criticalPointsX.filter { tX -> isInteresting(t = tX) }.toSet(),
            criticalPointsY = criticalPointsY.filter { tY -> isInteresting(t = tY) }.toSet(),
        )

        private fun isInteresting(t: Double): Boolean = t > (0.0 + eps) && t < (1.0 - eps)
    }
}
