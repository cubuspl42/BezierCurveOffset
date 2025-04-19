package app.utils

import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import org.apache.commons.math3.analysis.solvers.LaguerreSolver
import org.apache.commons.math3.complex.Complex

fun <T> List<T>.productOf(
    selector: (T) -> Complex,
): Complex = this.fold(initial = Complex.ONE) { acc, element ->
    if (acc.isNaN || acc.isInfinite) throw AssertionError()

    val se = selector(element)
    val p = acc * se

    if (p.isNaN || p.isInfinite) throw AssertionError()

    p
}
operator fun Complex.plus(
    complex: Complex,
): Complex = this.add(complex)

operator fun Complex.plus(
    real: Double,
): Complex = this.add(real)

operator fun Complex.minus(
    complex: Complex,
): Complex = this.subtract(complex)

operator fun Complex.times(
    real: Double,
): Complex = this.multiply(real)

operator fun Complex.times(
    complex: Complex,
): Complex = this.multiply(complex)

operator fun Double.times(
    complex: Complex,
): Complex = complex.multiply(this)

fun Complex.equalsWithTolerance(
    other: Complex,
    tolerance: Tolerance,
): Boolean = when {
    !real.equalsWithTolerance(other.real, tolerance = tolerance) -> false
    !imaginary.equalsWithTolerance(other.imaginary, tolerance = tolerance) -> false
    else -> true
}

fun Complex.equalsZeroWithTolerance(
    tolerance: Tolerance,
): Boolean = this.equalsWithTolerance(
    other = Complex.ZERO,
    tolerance = tolerance,
)

operator fun Complex.div(other: Complex): Complex = this.divide(other)

fun Complex.asReal(
    tolerance: Tolerance,
): Double? = takeIf {
    imaginary.equalsWithTolerance(
        other = 0.0,
        tolerance = tolerance,
    )
}?.real

fun LaguerreSolver.solveAllReal(
    coefficients: Collection<Double>,
    initial: Double,
): Collection<Double> {
    val complexRoots = solveAllComplex(
        coefficients.toDoubleArray(),
        initial,
    )

    return complexRoots.mapNotNull { complex ->
        complex.takeIf {
            it.imaginary.equalsZeroApproximately(epsilon = 10e-8)
        }?.real
    }
}
