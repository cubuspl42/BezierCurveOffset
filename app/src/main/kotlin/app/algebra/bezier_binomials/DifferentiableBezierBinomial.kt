package app.algebra.bezier_binomials

sealed class DifferentiableBezierBinomial<out V> : BezierBinomial<V>() {
    abstract fun findDerivative(): BezierBinomial<V>
}
