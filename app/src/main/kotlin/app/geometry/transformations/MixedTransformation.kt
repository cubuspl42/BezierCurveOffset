package app.geometry.transformations

import app.algebra.linear.Matrix3x3
import app.algebra.linear.Vector1x3
import app.geometry.Point
import app.get
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGMatrix

@Suppress("DataClassPrivateConstructor")
data class MixedTransformation private constructor(
    val tm: Matrix3x3,
): Transformation() {
    companion object {
        val identity = MixedTransformation(
            tm = Matrix3x3.identity,
        )

        fun of(
            a: Double,
            b: Double,
            c: Double,
            d: Double,
            e: Double,
            f: Double,
        ): MixedTransformation = MixedTransformation(
            tm = Matrix3x3(
                row0 = Vector1x3.of(
                    x = a,
                    y = c,
                    z = e,
                ),
                row1 = Vector1x3.of(
                    x = b,
                    y = d,
                    z = f,
                ),
                row2 = Vector1x3.of(
                    x = 0.0,
                    y = 0.0,
                    z = 1.0,
                ),
            ),
        )
    }

    /**
     * Combines this transformation with another one
     *
     * @param base - The transformation that comes before this transformation
     */
    fun applyOver(
        base: MixedTransformation,
    ): MixedTransformation = MixedTransformation(
        tm = tm * base.tm,
    )

    override fun transform(
        point: Point,
    ): Point = Point.of(
        pv = tm.times(point.pv.toVec3()).vectorXy,
    )
}

val SVGGElement.transformation: MixedTransformation
    get() = transform.baseVal[0].matrix.toTransformation()

fun SVGMatrix.toTransformation(): MixedTransformation = MixedTransformation.of(
    a = a.toDouble(),
    b = b.toDouble(),
    c = c.toDouble(),
    d = d.toDouble(),
    e = e.toDouble(),
    f = f.toDouble(),
)
