package app.arithmetic.bezier_formulas

import app.arithmetic.polynomial_formulas.PolynomialFormula
import app.Vector

/**
 * @param V - the type of the weights and the result
 */
sealed class BezierFormula<V>  {
    abstract fun findDerivative(): BezierFormula<V>

    abstract fun evaluate(t: Double): V
}

fun BezierFormula<Double>.toPolynomialFormula(): PolynomialFormula = when (this) {
    is LinearBezierFormula<Double> -> this.toPolynomialFormulaLinear()
    is QuadraticBezierFormula<Double> -> this.toPolynomialFormulaQuadratic()
    is CubicBezierFormula<Double> -> this.toPolynomialFormulaCubic()
}

val BezierFormula<Vector>.componentX: BezierFormula<Double>
    get() = when (this) {
        is LinearBezierFormula<Vector> ->  this.componentXLinear
        is QuadraticBezierFormula<Vector> -> this.componentXQuadratic
        is CubicBezierFormula<Vector> ->  this.componentXCubic
    }

val BezierFormula<Vector>.componentY: BezierFormula<Double>
    get() = when (this) {
        is LinearBezierFormula<Vector> ->  this.componentYLinear
        is QuadraticBezierFormula<Vector> -> this.componentYQuadratic
        is CubicBezierFormula<Vector> ->  this.componentYCubic
    }

fun BezierFormula<Double>.findRoots(): Set<Double> = toPolynomialFormula().findRoots()

fun BezierFormula<Double>.findLocalExtremities(): Set<Double> = findDerivative().findRoots()
