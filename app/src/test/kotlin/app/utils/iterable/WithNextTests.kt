package app.utils.iterable

import app.utils.FullDominoBlock
import app.utils.RedHalfDominoBlock
import kotlin.test.Test
import kotlin.test.assertEquals

class WithNextTests {
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
}
