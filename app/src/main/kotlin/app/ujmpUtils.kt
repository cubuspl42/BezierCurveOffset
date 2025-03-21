package app

import org.ujmp.core.Matrix
import org.ujmp.core.matrix.factory.DefaultDenseMatrixFactory

fun Matrix.invSafe(): Matrix = when {
    det() == 0.0 -> invSPD()
    else -> inv()
}

fun <T> DefaultDenseMatrixFactory.fillFrom(
    collection: Collection<T>,
    rowWidth: Int,
    buildRow: (T) -> DoubleArray,
): Matrix = this.fill(0.0, collection.size.toLong(), rowWidth.toLong()).apply {
    collection.forEachIndexed { i, v ->
        val row = buildRow(v)

        if (row.size != rowWidth) {
            throw IllegalArgumentException("Row width must be $rowWidth, but was ${row.size}")
        }

        row.forEachIndexed { j, value ->
            setAsDouble(value, i.toLong(), j.toLong())
        }
    }
}

fun <T> DefaultDenseMatrixFactory.fillColumnFrom(
    collection: Collection<T>,
    buildValue: (T) -> Double,
): Matrix = fillFrom(
    collection = collection,
    rowWidth = 1,
    buildRow = { v -> doubleArrayOf(buildValue(v)) },
)

fun Matrix.single(): Double {
    if (rowCount != 1L || columnCount != 1L) {
        throw IllegalArgumentException("Matrix must be 1x1, but was ${rowCount}x${columnCount}")
    }

    return getAsDouble(0, 0)
}
