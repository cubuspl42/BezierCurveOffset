package app.algebra.polynomials

/**
 * A multivariate polynomial in two variables (x, y) of degree 1, in the form:
 * a0 * x + a1 * y + b
 */
data class BiLinearPolynomial(
    val a0: Double,
    val a1: Double,
    val b: Double,
) {
    fun put(
        x: Polynomial,
        y: Polynomial,
    ): Polynomial = a0 * x + a1 * y + b

    fun put(
        p: ParametricPolynomial,
    ): Polynomial = put(
        x = p.xFunction,
        y = p.yFunction,
    )
}
