package app.algebra.implicit_polynomials

import app.algebra.NumericObject
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.polynomials.Polynomial
import app.geometry.RawVector

sealed class ImplicitPolynomial : NumericObject {
    abstract fun apply(
        v: RawVector,
    ): Double

    abstract fun substitute(
        parametricPolynomial: ParametricPolynomial,
    ): Polynomial<*>
}
