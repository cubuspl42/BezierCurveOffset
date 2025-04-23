package app.geometry.curves

import app.geometry.Curve

abstract class QuasiSegmentCurve : Curve() {
    companion object {
        val segmentTRange = 0.0..1.0
    }

    final override fun containsTValue(t: Double): Boolean = t in segmentTRange
}
