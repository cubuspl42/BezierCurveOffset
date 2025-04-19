package app.utils.iterable_utils_tests

import app.utils.BlueHalfDominoBlock
import app.utils.FullDominoBlock
import app.utils.iterable.WithPrevious
import app.utils.iterable.withPrevious
import app.utils.iterable.withPreviousCyclic
import kotlin.test.Test
import kotlin.test.assertEquals

class WithPreviousTests {
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
}
