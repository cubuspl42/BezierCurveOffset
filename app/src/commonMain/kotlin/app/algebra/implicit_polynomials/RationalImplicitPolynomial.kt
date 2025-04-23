package app.algebra.implicit_polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.Ratio
import app.algebra.equalsWithTolerance
import app.geometry.RawVector

data class RationalImplicitPolynomial(
    val nominatorFunction: ImplicitPolynomial,
    val denominatorFunction: ImplicitPolynomial,
) : NumericObject {
    fun apply(v: RawVector): Ratio = Ratio(
        nominator = nominatorFunction.apply(v),
        denominator = denominatorFunction.apply(v),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is RationalImplicitPolynomial -> false
        !nominatorFunction.equalsWithTolerance(other.nominatorFunction, tolerance = tolerance) -> false
        !denominatorFunction.equalsWithTolerance(other.denominatorFunction, tolerance = tolerance) -> false
        else -> true
    }
}
