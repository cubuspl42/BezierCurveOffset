package app.geometry

import app.algebra.linear.Matrix3x3
import app.algebra.linear.Vector3
import app.get
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGMatrix

@Suppress("DataClassPrivateConstructor")
data class Transformation private constructor(
    val tm: Matrix3x3,
) {
    companion object {
        val identity = Transformation(
            tm = Matrix3x3.identity,
        )

        fun of(
            a: Double,
            b: Double,
            c: Double,
            d: Double,
            e: Double,
            f: Double,
        ): Transformation = Transformation(
            tm = Matrix3x3(
                row0 = Vector3(
                    x = a,
                    y = c,
                    z = e,
                ),
                row1 = Vector3(
                    x = b,
                    y = d,
                    z = f,
                ),
                row2 = Vector3(
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
    fun combineWith(
        base: Transformation,
    ): Transformation = Transformation(
        tm = tm * base.tm,
    )

    fun transform(
        point: Point,
    ): Point = Point.of(
        pv = tm.timesVertical(point.pv.toVec3()).vectorXy,
    )
}

val SVGGElement.transformation: Transformation
    get() = transform.baseVal[0].matrix.toTransformation()

fun SVGMatrix.toTransformation(): Transformation = Transformation.of(
    a = a.toDouble(),
    b = b.toDouble(),
    c = c.toDouble(),
    d = d.toDouble(),
    e = e.toDouble(),
    f = f.toDouble(),
)
