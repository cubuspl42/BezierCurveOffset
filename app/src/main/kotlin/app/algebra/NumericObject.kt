package app.algebra

import app.algebra.NumericObject.Tolerance
import app.utils.equalsApproximately
import app.utils.equalsWithTolerance
import org.apache.commons.math3.complex.Complex
import kotlin.math.abs

interface NumericObject {
    sealed class Tolerance {
        data object Zero : Tolerance() {
            override fun equalsApproximately(
                value: Double,
                reference: Double
            ): Boolean = value == reference
        }

        data class Absolute(
            val absoluteTolerance: Double,
        ) : Tolerance() {
            override fun equalsApproximately(
                value: Double, reference: Double
            ): Boolean = abs(value - reference) <= absoluteTolerance
        }

        data class Relative(
            val relativeTolerance: Double,
        ) : Tolerance() {
            init {
                require(relativeTolerance > 0.0 && relativeTolerance < 0.25)
            }

            override fun equalsApproximately(
                value: Double,
                reference: Double,
            ): Boolean {
                val absDiff = abs(value - reference)
                val threshold = relativeTolerance * abs(reference)
                return absDiff <= threshold
            }
        }

        fun equalsZeroApproximately(
            value: Double,
        ): Boolean = equalsApproximately(
            value = value,
            reference = 0.0,
        )

        abstract fun equalsApproximately(
            value: Double,
            reference: Double,
        ): Boolean
    }

    fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean
}

fun NumericObject.equalsWithAbsoluteTolerance(
    other: NumericObject,
    absoluteTolerance: Double,
): Boolean = equalsWithTolerance(
    other = other,
    tolerance = Tolerance.Absolute(
        absoluteTolerance = absoluteTolerance,
    ),
)

fun NumericObject.equalsWithRelativeTolerance(
    other: NumericObject,
    relativeTolerance: Double,
): Boolean = equalsWithTolerance(
    other = other,
    tolerance = Tolerance.Relative(
        relativeTolerance = relativeTolerance,
    ),
)

fun NumericObject.equalsWithNoTolerance(
    other: NumericObject,
): Boolean = equalsWithAbsoluteTolerance(
    other = other,
    absoluteTolerance = 0.0,
)

fun Double.equalsWithTolerance(
    other: Double,
    absoluteTolerance: Double,
): Boolean = absoluteTolerance.equalsApproximately(this, other)

fun Double.equalsWithTolerance(
    other: Double,
    tolerance: Tolerance,
): Boolean = tolerance.equalsApproximately(this, other)

fun Double.equalsZeroWithTolerance(
    tolerance: Tolerance,
): Boolean = equalsWithTolerance(
    other = 0.0,
    tolerance = tolerance,
)

@JvmName("equalsWithToleranceListDouble")
fun List<Double>.equalsWithTolerance(
    other: List<Double>,
    tolerance: Tolerance,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}

@JvmName("equalsWithToleranceListComplex")
fun List<Complex>.equalsWithTolerance(
    other: List<Complex>,
    tolerance: Tolerance,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}

@JvmName("equalsWithToleranceListNumericObject")
fun List<NumericObject>.equalsWithTolerance(
    other: List<NumericObject>,
    tolerance: Tolerance,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}
