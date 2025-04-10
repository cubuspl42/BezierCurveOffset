package app.algebra.linear.matrices.matrix4

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector4.Vector1x4
import app.algebra.linear.vectors.vector4.Vector4x1

// ...existing imports...

sealed class LazyMatrix4x4 : Matrix4x4() {
    override val row0: Vector1x4
        get() = computed.row0

    override val row1: Vector1x4
        get() = computed.row1

    override val row2: Vector1x4
        get() = computed.row2

    override val row3: Vector1x4
        get() = computed.row3

    override val column0: Vector4x1
        get() = computed.column0

    override val column1: Vector4x1
        get() = computed.column1

    override val column2: Vector4x1
        get() = computed.column2

    override val column3: Vector4x1
        get() = computed.column3

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double
    ): Boolean {
        return computed.equalsWithTolerance(other, absoluteTolerance)
    }

    override fun get(i: Int, j: Int): Double {
        return computed[i, j]
    }

    override val transposed: Matrix4x4
        get() = computed.transposed

    private val computed: EagerMatrix4x4 by lazy {
        compute()
    }

    abstract fun compute(): EagerMatrix4x4
}
