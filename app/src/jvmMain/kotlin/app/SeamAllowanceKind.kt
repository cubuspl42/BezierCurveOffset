package app

sealed interface SeamAllowanceKind {
    data object None : SeamAllowanceKind {
        override val widthMm = 0.0
    }

    data object Standard : SeamAllowanceKind {
        override val widthMm = 6.0
    }

    data object Tunnel : SeamAllowanceKind {
        override val widthMm = 9.0
    }

    data object Edging : SeamAllowanceKind {
        override val widthMm = 12.0
    }

    val widthMm: Double
}