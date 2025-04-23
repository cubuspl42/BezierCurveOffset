package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.euclidean.bezier_binomials.RealFunction
import app.algebra.equalsWithTolerance
import app.algebra.equalsZeroWithTolerance
import app.geometry.Constants
import app.utils.iterable.uncons
import app.utils.iterable.untrail
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

private fun areCloseNever(
    x0: Double,
    x1: Double
): Boolean = false

data class Polynomial<out D : Polynomial.Degree>(
    val coefficients: List<Double>,
) : RealFunction<Double>, NumericObject {
    sealed interface Degree {
        companion object {
            fun of(n: Int): Degree = when (n) {
                0 -> Degree.Constant
                1 -> Degree.Linear
                2 -> Degree.Quadratic
                3 -> Degree.Cubic
                else -> Degree.High(
                    n = n,
                )
            }
        }

        data class High(
            override val n: Int,
        ) : CubicOrAbove

        sealed interface CubicOrAbove : QuadraticOrAbove

        data object Cubic : CubicOrAbove {
            override val n = 3
        }

        sealed interface QuadraticOrAbove : LinearOrAbove

        data object Quadratic : QuadraticOrAbove {
            override val n = 2
        }

        sealed interface LinearOrAbove : Degree

        data object Linear : LinearOrAbove {
            override val n = 1
        }

        data object Constant : Degree {
            override val n = 0
        }

        val n: Int
    }

    companion object {
        val zero: Polynomial<Degree.Constant>
            get() = Polynomial(
                coefficients = listOf(0.0),
            )

        fun of(
            vararg coefficients: Double,
        ): Polynomial<*> = of(
            coefficients = coefficients.toList(),
        )

        fun constant(
            a0: Double,
        ): ConstantPolynomial {
            @Suppress("UNCHECKED_CAST") return of(
                a0,
            ) as ConstantPolynomial
        }

        fun linear(
            a0: Double,
            a1: Double,
        ): LinearPolynomial {
            @Suppress("UNCHECKED_CAST") return of(
                a0, a1,
            ) as LinearPolynomial
        }

        fun quadratic(
            a0: Double,
            a1: Double,
            a2: Double,
        ): QuadraticPolynomial {
            @Suppress("UNCHECKED_CAST") return of(
                a0, a1, a2,
            ) as QuadraticPolynomial
        }

        fun cubic(
            a0: Double,
            a1: Double,
            a2: Double,
            a3: Double,
        ): CubicPolynomial {
            @Suppress("UNCHECKED_CAST") return of(
                a0, a1, a2, a3,
            ) as CubicPolynomial
        }

        fun of(
            coefficients: List<Double>,
        ): Polynomial<*> {
            val (lowerDegreeCoefficients, highestDegreeCoefficient) = coefficients.untrail() ?: return Polynomial.zero

            return when {
                highestDegreeCoefficient == 0.0 -> of(coefficients = lowerDegreeCoefficients)
                else -> Polynomial<Degree>(
                    coefficients = coefficients,
                )
            }
        }
    }

    init {
        require(coefficients.isNotEmpty())
        require(coefficients.size == 1 || coefficients.last() != 0.0)
    }

    /**
     * Divides the polynomial by a linear polynomial of the form (x - x0).
     *
     * @return Pair of quotient and remainder.
     */
    fun divide(
        x0: Double,
    ): Pair<Polynomial<*>, Double> {
        if (degree == Degree.Constant) {
            return Pair(zero, a0)
        }

        val (highestDegreeCoefficient, lowerDegreeCoefficients) = coefficients.reversed().uncons()!!

        val intermediateCoefficients = lowerDegreeCoefficients.scan(
            initial = highestDegreeCoefficient,
        ) { higherDegreeCoefficient, coefficient ->
            higherDegreeCoefficient * x0 + coefficient
        }

        val (quotientCoefficients, remainder) = intermediateCoefficients.untrail()!!

        val quotient = Polynomial.of(
            coefficients = quotientCoefficients.reversed(),
        )

        return Pair(quotient, remainder)
    }

    /**
     * Deflates the polynomial by a linear polynomial of the form (x - x0).
     *
     * @param x0 - a root of this polynomial
     */
    fun deflate(
        x0: Double,
    ): Polynomial<*> {
        // The remainder is sometimes a non-zero number (is this fine?)
        val (quotient, _) = divide(x0 = x0)

        return quotient
    }

    operator fun plus(
        other: Polynomial<*>,
    ): Polynomial<*> = Polynomial.of(
        coefficients = List(max(degree.n, other.degree.n) + 1) { i ->
            (getCoefficient(i) ?: 0.0) + (other.getCoefficient(i) ?: 0.0)
        },
    )

    operator fun plus(
        other: Double,
    ): Polynomial<*> = this + Polynomial.constant(other)


    operator fun minus(
        other: Polynomial<*>,
    ): Polynomial<*> = other + (-other)

    operator fun times(
        other: Polynomial<*>,
    ): Polynomial<*> {
        // Degree of p0[n] * p1[m] = m + n
        val productDegree = this.degree.n + other.degree.n

        return of(
            coefficients = (0..productDegree).map { k ->
                (0..k).filter { i ->
                    i in this.coefficients.indices && (k - i) in other.coefficients.indices
                }.sumOf { i ->
                    this.coefficients[i] * other.coefficients[k - i]
                }
            },
        )
    }

    operator fun unaryMinus(): Polynomial<*> = Polynomial.of(
        coefficients = coefficients.map { -it },
    )

    operator fun times(factor: Double): Polynomial<*> = Polynomial.of(
        coefficients = coefficients.map { it * factor },
    )

    fun <R> match(
        constant: (ConstantPolynomial) -> R,
        linear: (LinearPolynomial) -> R,
        quadratic: (QuadraticPolynomial) -> R,
        cubic: (CubicPolynomial) -> R,
        high: (HighPolynomial) -> R,
    ): R {
        @Suppress("UNCHECKED_CAST") return when (degree) {
            Degree.Constant -> constant(this as ConstantPolynomial)
            Degree.Linear -> linear(this as LinearPolynomial)
            Degree.Quadratic -> quadratic(this as QuadraticPolynomial)
            Degree.Cubic -> cubic(this as CubicPolynomial)
            is Degree.High -> high(this as HighPolynomial)
        }
    }

    fun findRoots(
        maxDepth: Int = 20,
        guessedRoot: Double = 0.5,
        tolerance: Tolerance = Tolerance.Absolute(absoluteTolerance = Constants.epsilon),
        areClose: (x0: Double, x1: Double) -> Boolean = ::areCloseNever,
    ): List<Double> = match(
        constant = { emptyList() },
        linear = { it.findRootsLinear() },
        quadratic = { it.findRootsQuadratic() },
        cubic = { it.findRootsCubic() },
        high = {
            it.findRootsNumeric(
                maxDepth = maxDepth,
                guessedRoot = guessedRoot,
                tolerance = tolerance,
                areClose = areClose,
            )
        },
    )

    val degree: Degree
        get() = Degree.of(coefficients.size - 1)

    fun getCoefficient(i: Int): Double? = coefficients.getOrNull(i)

    val derivative: Polynomial<*>
        get() = Polynomial.of(
            coefficients = coefficients.mapIndexed { i, ai ->
                i * ai
            }.drop(1),
        )

    override fun apply(
        x: Double,
    ): Double = coefficients.withIndex().sumOf { (i, ai) ->
        ai * x.pow(i)
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Polynomial<*> -> false
        else -> coefficients.equalsWithTolerance(other.coefficients, tolerance = tolerance)
    }
}

operator fun Double.times(
    polynomial: Polynomial<*>,
): Polynomial<*> = polynomial * this

typealias ConstantPolynomial = Polynomial<Polynomial.Degree.Constant>
typealias LinearPolynomial = Polynomial<Polynomial.Degree.Linear>
typealias QuadraticPolynomial = Polynomial<Polynomial.Degree.Quadratic>
typealias CubicPolynomial = Polynomial<Polynomial.Degree.Cubic>
typealias HighPolynomial = Polynomial<Polynomial.Degree.High>

val Polynomial<Polynomial.Degree.CubicOrAbove>.a3: Double
    get() = getCoefficient(3)!!

val Polynomial<Polynomial.Degree.QuadraticOrAbove>.a2: Double
    get() = getCoefficient(2)!!

val Polynomial<Polynomial.Degree.LinearOrAbove>.a1: Double
    get() = getCoefficient(1)!!

val Polynomial<*>.a0: Double
    get() = getCoefficient(0)!!

fun LinearPolynomial.findRootsLinear(): List<Double> = listOf(findRootLinear())

fun LinearPolynomial.findRootLinear(): Double = -a0 / a1

fun QuadraticPolynomial.findRootsQuadratic(): List<Double> {
    val a = a2
    val b = a1
    val c = a0

    val discriminant: Double = b * b - 4 * a * c

    fun buildRoot(
        sign: Double,
    ): Double = (-b + sign * sqrt(discriminant)) / (2 * a)

    return when {
        discriminant >= 0 -> listOf(
            buildRoot(sign = -1.0),
            buildRoot(sign = 1.0),
        )

        else -> emptyList()
    }
}

fun CubicPolynomial.findRootsCubic(): List<Double> {
    val a = a3
    val b = a2
    val c = a1
    val d = a0

    val f = (3.0 * a * c - b * b) / (3.0 * a * a)
    val g = (2.0 * b * b * b - 9.0 * a * b * c + 27.0 * a * a * d) / (27.0 * a * a * a)
    val h = g * g / 4.0 + f * f * f / 27.0

    return when {
        h > 0 -> {
            // One real root

            val r = -g / 2.0
            val s = sqrt(h)
            val u = cbrt(r + s)
            val v = cbrt(r - s)

            val x0 = u + v - (b / (3.0 * a))

            listOf(x0)
        }

        h == 0.0 -> {
            // All roots real, at least two equal

            val u = cbrt(-g / 2.0)

            val x0 = 2.0 * u - (b / (3.0 * a))
            val x1 = -u - (b / (3.0 * a))

            listOf(x0, x1)
        }

        else -> {
            // Three distinct real roots

            val i = sqrt(g * g / 4.0 - h)
            val j = cbrt(i)
            val k = acos(-g / (2.0 * i))
            val m = cos(k / 3.0)
            val n = sqrt(3.0) * sin(k / 3.0)
            val p = -b / (3.0 * a)

            val x0 = 2.0 * j * m + p
            val x1 = j * (-m + n) + p
            val x2 = j * (-m - n) + p

            listOf(x0, x1, x2)
        }
    }
}

fun Polynomial<*>.findRootsNumeric(
    maxDepth: Int,
    guessedRoot: Double,
    tolerance: Tolerance,
    areClose: (x0: Double, x1: Double) -> Boolean,
): List<Double> {
    val primaryRoot = findPrimaryRootNumeric(
        maxDepth = maxDepth,
        tolerance = tolerance,
        guessedRoot = guessedRoot,
        areClose = areClose,
    ) ?: return emptyList()

    val deflatedPolynomial = this.deflate(
        x0 = primaryRoot,
    )

    val lowerDegreeRoots = deflatedPolynomial.findRoots(
        maxDepth = maxDepth,
        guessedRoot = guessedRoot,
        tolerance = tolerance,
        areClose = areClose,
    )

    return listOf(primaryRoot) + lowerDegreeRoots
}

/**
 * @param tolerance - when p(x0) equals zero within the tolerance, x0 is
 * considered a root
 * @param areClose - when p(x0)) and p(x1) have different signs and x0 and x1
 * "are close" then avg(x0, x1) is considered a root
 */
fun Polynomial<*>.findPrimaryRootNumeric(
    maxDepth: Int,
    guessedRoot: Double,
    tolerance: Tolerance,
    areClose: (x0: Double, x1: Double) -> Boolean,
): Double? {
    val n = degree.n.toDouble()

    val firstDerivative = derivative
    val secondDerivative = firstDerivative.derivative

    tailrec fun improveRoot(
        approximatedRoot: Double,
        depth: Int,
    ): Double? {
        if (depth > maxDepth) {
            return approximatedRoot
        }

        val p0 = apply(approximatedRoot)

        if (p0.equalsZeroWithTolerance(tolerance = tolerance)) {
            return approximatedRoot
        }

        val p1 = firstDerivative.apply(approximatedRoot)
        val p2 = secondDerivative.apply(approximatedRoot)

        val g = p1 / p0
        val g2 = g * g
        val h = g2 - p2 / p0

        val i = (n - 1) * (n * h - g2)

        if (i < 0.0) {
            // Entering the complex domain is not supported
            return null
        }

        val d = sqrt(i)

        val gd = listOf(
            g + d,
            g - d,
        ).maxBy(::abs)

        val a = n / gd

        val improvedRoot = approximatedRoot - a

        val improvedP0 = apply(improvedRoot)

        val areSignsDifferent = p0.sign != improvedP0.sign

        if (areSignsDifferent && areClose(approximatedRoot, improvedRoot)) {
            return (approximatedRoot + improvedRoot) / 2.0
        }

        return improveRoot(
            approximatedRoot = improvedRoot,
            depth = depth + 1,
        )
    }

    return improveRoot(
        approximatedRoot = guessedRoot,
        depth = 0,
    )
}
