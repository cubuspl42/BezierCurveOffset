package app

import kotlin.math.absoluteValue
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTests {
    sealed interface DominoBlockRed {
        val redNumber: Int
    }

    sealed interface DominoBlockBlue {
        val blueNumber: Int
    }

    data class RedHalfDominoBlock(
        override val redNumber: Int,
    ) : DominoBlockRed

    data class FullDominoBlock(
        override val redNumber: Int,
        override val blueNumber: Int,
    ) : DominoBlockRed, DominoBlockBlue

    data class BlueHalfDominoBlock(
        override val blueNumber: Int,
    ) : DominoBlockBlue

    @Test
    fun testMapWithNextEdge_emptyList() {
        val numbers = emptyList<Int>()
        val result = numbers.mapWithNext(rightEdge = 0) { a, b -> a + b }

        assertEquals(
            expected = emptyList(),
            actual = result,
            message = "Expected an empty list as the result",
        )
    }

    @Test
    fun testMapWithNextEdge_nonEmptyList() {
        val result = listOf(1, 2, 3).mapWithNext(rightEdge = 0) { a, b -> a + b }

        assertEquals(
            expected = listOf(3, 5, 3),
            actual = result,
            message = "Expected [3, 5, 3] as the result",
        )
    }

    @Test
    fun testMapWithNextEdge_singleElement() {
        val result = listOf(5).mapWithNext(rightEdge = 10) { a, b -> a + b }

        assertEquals(
            expected = listOf(element = 15),
            actual = result,
            message = "Expected [15] as the result",
        )
    }

    @Test
    fun testMapWithNextEdge_stringConcatenation() {
        val result = listOf("Hello", "World").mapWithNext<Any, String, String>(
            rightEdge = '!',
        ) { a: String, b: Any -> "$a $b" }

        assertEquals(
            expected = listOf("Hello World", "World !"),
            actual = result,
            message = "Expected [\"Hello World\", \"World !\"] as the result",
        )
    }

    @Test
    fun testWithPrevious_standardCase() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
            FullDominoBlock(
                redNumber = 6,
                blueNumber = 2,
            ),
            FullDominoBlock(
                redNumber = 2,
                blueNumber = 5,
            ),
        ).withPrevious(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
        )

        assertEquals(
            expected = listOf(
                WithPrevious(
                    prevElement = BlueHalfDominoBlock(
                        blueNumber = 1,
                    ),
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                ),
                WithPrevious(
                    prevElement = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    element = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                ),
                WithPrevious(
                    prevElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    element = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithPrevious_singleElement() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
        ).withPrevious(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
        )

        assertEquals(
            expected = listOf(
                WithPrevious(
                    prevElement = BlueHalfDominoBlock(
                        blueNumber = 1,
                    ),
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithPrevious_emptyList() {
        val actual = emptyList<FullDominoBlock>().withPrevious(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }

    @Test
    fun testWithPreviousCyclic_standardCase() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
            FullDominoBlock(
                redNumber = 6,
                blueNumber = 2,
            ),
            FullDominoBlock(
                redNumber = 2,
                blueNumber = 5,
            ),
        ).withPreviousCyclic()

        assertEquals(
            expected = listOf(
                WithPrevious(
                    prevElement = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                ),
                WithPrevious(
                    prevElement = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    element = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                ),
                WithPrevious(
                    prevElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    element = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNext_standardCase() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
            FullDominoBlock(
                redNumber = 6,
                blueNumber = 2,
            ),
            FullDominoBlock(
                redNumber = 2,
                blueNumber = 5,
            ),
        ).withNext(
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = listOf(
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                ),
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                ),
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                    nextElement = RedHalfDominoBlock(
                        redNumber = 5,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNext_singleElement() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
        ).withNext(
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = listOf(
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = RedHalfDominoBlock(
                        redNumber = 5,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNext_emptyList() {
        val actual = emptyList<FullDominoBlock>().withNext(
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }

    @Test
    fun testWithNextCyclic_standardCase() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
            FullDominoBlock(
                redNumber = 6,
                blueNumber = 2,
            ),
            FullDominoBlock(
                redNumber = 2,
                blueNumber = 5,
            ),
        ).withNextCyclic()

        assertEquals(
            expected = listOf(
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                ),
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                ),
                WithNext(
                    element = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNeighbours_standardCase() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
            FullDominoBlock(
                redNumber = 6,
                blueNumber = 2,
            ),
            FullDominoBlock(
                redNumber = 2,
                blueNumber = 5,
            ),
        ).withNeighbours(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = listOf(
                WithNeighbours(
                    prevElement = BlueHalfDominoBlock(
                        blueNumber = 1,
                    ),
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                ),
                WithNeighbours(
                    prevElement = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    element = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    nextElement = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                ),
                WithNeighbours(
                    prevElement = FullDominoBlock(
                        redNumber = 6,
                        blueNumber = 2,
                    ),
                    element = FullDominoBlock(
                        redNumber = 2,
                        blueNumber = 5,
                    ),
                    nextElement = RedHalfDominoBlock(
                        redNumber = 5,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNeighbours_singleElement() {
        val actual = listOf(
            FullDominoBlock(
                redNumber = 1,
                blueNumber = 6,
            ),
        ).withNeighbours(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = listOf(
                WithNeighbours(
                    prevElement = BlueHalfDominoBlock(
                        blueNumber = 1,
                    ),
                    element = FullDominoBlock(
                        redNumber = 1,
                        blueNumber = 6,
                    ),
                    nextElement = RedHalfDominoBlock(
                        redNumber = 5,
                    ),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWithNeighbours_emptyList() {
        val actual = emptyList<FullDominoBlock>().withNeighbours(
            outerLeft = BlueHalfDominoBlock(
                blueNumber = 1,
            ),
            outerRight = RedHalfDominoBlock(
                redNumber = 5,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }

    @Test
    fun testInterleave_emptyList() {
        val numbers = emptyList<Int>()
        val result = numbers.interleave(transform = { "($it)" }, separate = { a, b -> "[$a$b]" })
        assertEquals(
            expected = emptyList(),
            actual = result,
        )
    }

    @Test
    fun testInterleave_singleElement() {
        val numbers = listOf(1)
        val result = numbers.interleave(transform = { "($it)" }, separate = { a, b -> "[$a$b]" })
        assertEquals(
            expected = listOf("(1)"),
            actual = result,
        )
    }

    @Test
    fun testInterleave_twoElements() {
        val numbers = listOf(1, 2)
        val result = numbers.interleave(
            transform = { "($it)" },
            separate = { a, b -> "[$a$b]" },
        )
        assertEquals(
            expected = listOf("(1)", "[12]", "(2)"),
            actual = result,
        )
    }

    @Test
    fun testInterleave_multipleElements() {
        val numbers = listOf(1, 2, 3, 4)
        val result = numbers.interleave(
            transform = { "($it)" },
            separate = { a, b -> "[$a$b]" },
        )
        assertEquals(
            expected = listOf("(1)", "[12]", "(2)", "[23]", "(3)", "[34]", "(4)"),
            actual = result,
        )
    }

    @Test
    fun testIndexOfMaxBy() {
        assertEquals(
            expected = 3,
            actual = listOf(22.0, 6.0, 10.0, -14.0).indexOfMaxBy(fromIndex = 1) {
                it.absoluteValue
            },
        )
    }
}
