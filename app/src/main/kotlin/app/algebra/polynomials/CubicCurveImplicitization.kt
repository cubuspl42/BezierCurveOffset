package app.algebra.polynomials

import app.geometry.ImplicitCubicPolynomial
import app.geometry.ImplicitLinearPolynomial
import app.geometry.ImplicitQuadraticPolynomial
import app.geometry.times

/**
 * @return A determinant of the 3x3 matrix of bi-linear polynomials
 */
fun calculateDeterminant(
    a: Double, b: Double, c: ImplicitLinearPolynomial,
    d: Double, e: ImplicitLinearPolynomial, f: ImplicitLinearPolynomial,
    g: ImplicitLinearPolynomial, h: ImplicitLinearPolynomial, i: ImplicitLinearPolynomial,
): ImplicitCubicPolynomial {
    val aei = a * e * i
    val afh = a * f * h
    val bdi = b * d * i
    val bfg = b * f * g
    val cdh = c * d * h
    val ceg = c * e * g
    return aei - afh - bdi + bfg + cdh - ceg
}

/**
 * An intermediate cubic polynomial with polynomial coefficients.
 *
 * Sometimes labeled p(x, t) / q(x, t)
 */
internal data class IntermediateMetaCubicPolynomial(
    val a3: Polynomial,
    val a2: Polynomial,
    val a1: Polynomial,
    val a0: Polynomial,
)

/**
 * @param px - the first polynomial (on X)
 * @param py - the second polynomial (on Y)
 */
internal fun calculateResultant(
    px: CubicPolynomial,
    py: CubicPolynomial,
): ImplicitCubicPolynomial {
    // -x + pa.a0
    val a0: LinearPolynomial = LinearPolynomial.of2(
        a0 = px.a0,
        a1 = -1.0,
    ) as LinearPolynomial

    val a1: Double = px.a1
    val a2: Double = px.a2
    val a3: Double = px.a3

    // -y + pa.a0
    val b0: LinearPolynomial = LinearPolynomial.of2(
        a0 = py.a0,
        a1 = -1.0,
    ) as LinearPolynomial

    val b1: Double = py.a1
    val b2: Double = py.a2
    val b3: Double = py.a3

//    val a1b0 = ImplicitBilinearPolynomial.times(a1, b0) - ImplicitBilinearPolynomial.times(a0, b1)
//    val a2b0 = ImplicitBilinearPolynomial.times(a2, b0) - ImplicitBilinearPolynomial.times(a0, b2)
//    val a2b1 = ImplicitBilinearPolynomial.times(a2, b1) - ImplicitBilinearPolynomial.times(a1, b2)
//    val a3b0 = ImplicitBilinearPolynomial.times(a3, b0) - ImplicitBilinearPolynomial.times(a0, b3)
//    val a3b1 = ImplicitBilinearPolynomial.times(a3, b1) - ImplicitBilinearPolynomial.times(a1, b3)
//    val a3b2 = ImplicitBilinearPolynomial.times(a3, b2) - ImplicitBilinearPolynomial.times(a2, b3)

    val a1b0: ImplicitLinearPolynomial = ImplicitLinearPolynomial.minus(py = a1 * b0, px = a0 * b1)
    val a2b0: ImplicitLinearPolynomial = ImplicitLinearPolynomial.minus(py = a2 * b0, px = a0 * b2)
    val a2b1: Double = a2 * b1 - a1 * b2
    val a3b0: ImplicitLinearPolynomial = ImplicitLinearPolynomial.minus(py = a3 * b0, px = a0 * b3)
    val a3b1: Double = a3 * b1 - a1 * b3
    val a3b2: Double = a3 * b2 - a2 * b3

    val determinant = calculateDeterminant(
        a3b2, a3b1, a3b0,
        a3b1, a3b0 + a2b1, a2b0,
        a3b0, a2b0, a1b0,
    )

    return determinant
}

private fun CubicPolynomial.lift(): IntermediateMetaCubicPolynomial = IntermediateMetaCubicPolynomial(
    a0 = LinearPolynomial.of(b = a3, a = 0.0),
    a1 = LinearPolynomial.of(b = a2, a = 0.0),
    a2 = LinearPolynomial.of(b = a1, a = 0.0),
    a3 = LinearPolynomial.of(b = d, a = -1.0),
)

private fun implicitize(
    parametricPolynomial: ParametricPolynomial,
): ImplicitCubicPolynomial {
    val resultant = calculateResultant(
        px = parametricPolynomial.xFunction as CubicPolynomial,
        py = parametricPolynomial.yFunction as CubicPolynomial,
    )

    return resultant
}

fun solveIntersection(
    b0: ParametricPolynomial,
    b1: ParametricPolynomial,
): Set<Double> {
    val implicitB1 = implicitize(b1)
    val intersectionPolynomial = implicitB1.put(b0)
    val roots = intersectionPolynomial.findRoots()

    return roots
}
