package app.algebra.polynomials

import app.geometry.ImplicitLinearPolynomial

/**
 * @return A determinant of the 3x3 matrix of bi-linear polynomials
 */
private fun calculateDeterminant(
    a: ImplicitLinearPolynomial, b: ImplicitLinearPolynomial, c: ImplicitLinearPolynomial,
    d: ImplicitLinearPolynomial, e: ImplicitLinearPolynomial, f: ImplicitLinearPolynomial,
    g: ImplicitLinearPolynomial, h: ImplicitLinearPolynomial, i: ImplicitLinearPolynomial,
): ImplicitLinearPolynomial.CubedGeneralLineFunction {
    val aei = ImplicitLinearPolynomial.times(a, e, i)
    val afg = ImplicitLinearPolynomial.times(a, f, h)
    val bdi = ImplicitLinearPolynomial.times(b, d, i)
    val bfg = ImplicitLinearPolynomial.times(b, f, g)
    val cdh = ImplicitLinearPolynomial.times(c, d, h)
    val ceg = ImplicitLinearPolynomial.times(c, e, g)

    return aei - afg - bdi + bfg + cdh - ceg
}

/**
 * An intermediate cubic polynomial with polynomial coefficients.
 *
 * Sometimes labeled p(x, t) / q(x, t)
 */
private data class IntermediateMetaCubicPolynomial(
    val a3: Polynomial,
    val a2: Polynomial,
    val a1: Polynomial,
    val a0: Polynomial,
)

/**
 * @param pa - the first polynomial (on X)
 * @param pb - the second polynomial (on Y)
 */
private fun calculateResultant(
    pa: IntermediateMetaCubicPolynomial,
    pb: IntermediateMetaCubicPolynomial,
): ImplicitLinearPolynomial.CubedGeneralLineFunction {
    val a0 = pa.a0 as LinearPolynomial
    val a1 = pa.a1 as LinearPolynomial
    val a2 = pa.a2 as LinearPolynomial
    val a3 = pa.a3 as LinearPolynomial

    val b0 = pb.a0 as LinearPolynomial
    val b1 = pb.a1 as LinearPolynomial
    val b2 = pb.a2 as LinearPolynomial
    val b3 = pb.a3 as LinearPolynomial

    val a1b0 = ImplicitLinearPolynomial.times(a1, b0) - ImplicitLinearPolynomial.times(a0, b1)
    val a2b0 = ImplicitLinearPolynomial.times(a2, b0) - ImplicitLinearPolynomial.times(a0, b2)
    val a2b1 = ImplicitLinearPolynomial.times(a2, b1) - ImplicitLinearPolynomial.times(a1, b2)
    val a3b0 = ImplicitLinearPolynomial.times(a3, b0) - ImplicitLinearPolynomial.times(a0, b3)
    val a3b1 = ImplicitLinearPolynomial.times(a3, b1) - ImplicitLinearPolynomial.times(a1, b3)
    val a3b2 = ImplicitLinearPolynomial.times(a3, b2) - ImplicitLinearPolynomial.times(a2, b3)

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
): ImplicitLinearPolynomial.CubedGeneralLineFunction {
    val resultant = calculateResultant(
        pa = (parametricPolynomial.xFunction as CubicPolynomial).lift(),
        pb = (parametricPolynomial.yFunction as CubicPolynomial).lift(),
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
