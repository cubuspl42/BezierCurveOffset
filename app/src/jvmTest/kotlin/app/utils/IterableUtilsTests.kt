package app.utils

import app.utils.iterable.Split
import app.utils.iterable.WithNeighbours
import app.utils.iterable.WithNext
import app.utils.iterable.WithPrevious
import app.utils.iterable.indexOfMaxBy
import app.utils.iterable.interleave
import app.utils.iterable.mapCarrying
import app.utils.iterable.shiftWhile
import app.utils.iterable.splitAfter
import app.utils.iterable.splitBy
import app.utils.iterable.withNeighbours
import app.utils.iterable.withNext
import app.utils.iterable.withNextCyclic
import app.utils.iterable.withPrevious
import app.utils.iterable.withPreviousCyclic
import org.junit.Assert.assertThrows
import kotlin.math.absoluteValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IterableUtilsTests {
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

    @Test
    fun testSplitBy_empty() {
        val result = emptyList<Int>().splitBy { it % 2 == 0 }

        assertEquals(
            expected = emptyList(),
            actual = result,
        )
    }

    @Test
    fun testSplitBy_noMatching() {
        val list = listOf(1, 5, 9, 7, 3, 3, 9, 1)
        val result = list.splitBy { it % 2 == 0 }

        assertEquals(
            expected = listOf(list),
            actual = result,
        )
    }

    @Test
    fun testSplitBy_allMatching() {
        val list = listOf(2, 4, 6, 8, 10)
        val result = list.splitBy { it % 2 == 0 }

        assertEquals(
            expected = listOf(
                listOf(2),
                listOf(4),
                listOf(6),
                listOf(8),
                listOf(10),
            ),
            actual = result,
        )
    }

    @Test
    fun testSplitBy_simple() {
        val result = listOf(1, 5, 9, 2, 7, 3, 3, 4, 9, 2, 8, 6, 1).splitBy { it % 2 == 0 }

        assertEquals(
            expected = listOf(
                listOf(1, 5, 9),
                listOf(2, 7, 3, 3),
                listOf(4, 9),
                listOf(2),
                listOf(8),
                listOf(6, 1),
            ),
            actual = result,
        )
    }

    @Test
    fun testShiftWhile_empty() {
        assertFailsWith<IllegalArgumentException> {
            emptyList<Char>().shiftWhile { it.isLowerCase() }
        }
    }

    @Test
    fun testShiftWhile_noMatching() {
        val list = listOf('C', 'D', 'E', 'F')
        val result = list.shiftWhile { it.isLowerCase() }

        assertEquals(
            expected = list,
            actual = result,
        )
    }

    @Test
    fun testShiftWhile_allMatching() {
        val list = listOf('a', 'b', 'c', 'd')

        assertFailsWith<IllegalArgumentException> {
            list.shiftWhile { it.isLowerCase() }
        }
    }

    @Test
    fun testShiftWhile_someMatching() {
        val list = listOf('a', 'b', 'C', 'x', 'D', 'E', 'F')
        val result = list.shiftWhile { it.isLowerCase() }

        assertEquals(
            expected = listOf('C', 'x', 'D', 'E', 'F', 'a', 'b'),
            actual = result,
        )
    }

    @Test
    fun testMapCarrying_empty() {
        val list = emptyList<String>()

        val (result, finalCarry) = list.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair(value.uppercase(), value.last().toString())
        }

        assertEquals(
            expected = emptyList(),
            actual = result,
        )

        assertEquals(
            expected = "x",
            actual = finalCarry,
        )
    }

    @Test
    fun testMapCarrying_single() {
        val list = listOf("ab")

        val (result, finalCarry) = list.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair("$carry${value.uppercase()}", value.last().toString())
        }

        assertEquals(
            expected = listOf("xAB"),
            actual = result,
        )

        assertEquals(
            expected = "b",
            actual = finalCarry,
        )
    }

    @Test
    fun testMapCarrying_simple_sameType() {
        val list = listOf("ab", "cd", "ef")
        val (result, finalCarry) = list.mapCarrying(
            initialCarry = "x",
        ) { carry, value ->
            Pair("$carry${value.uppercase()}", value.last().toString())
        }

        assertEquals(
            expected = listOf("xAB", "bCD", "dEF"),
            actual = result,
        )

        assertEquals(
            expected = "f",
            actual = finalCarry,
        )
    }

    @Test
    fun testMapCarrying_simple_differentType() {
        val list = listOf(0, 1, 2, 3)
        val (result, finalCarry) = list.mapCarrying(
            initialCarry = 'x',
        ) { carry: Char, value: Int ->
            Pair("$carry:${value * -2}", '0' + value)
        }

        assertEquals(
            expected = listOf("x:0", "0:-2", "1:-4", "2:-6"),
            actual = result,
        )

        assertEquals(
            expected = '3',
            actual = finalCarry,
        )
    }

    @Test
    fun testSplitAfter_empty() {
        val list = emptyList<Int>()
        val result = list.splitAfter(0)

        assertEquals(
            expected = Split(
                leadingElements = emptyList(),
                trailingElements = emptyList(),
            ),
            actual = result,
        )
    }

    @Test
    fun testSplitAfter_simple() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.splitAfter(2)

        assertEquals(
            expected = Split(
                leadingElements = listOf(1, 2),
                trailingElements = listOf(3, 4, 5),
            ),
            actual = result,
        )
    }

    @Test
    fun testSplitAfter_negative() {
        val list = listOf(1, 2, 3, 4, 5)

        assertFailsWith<IllegalArgumentException> {
            list.splitAfter(-2)
        }
    }

    @Test
    fun testSplitAfter_biggerThanList() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.splitAfter(7)

        assertEquals(
            expected = Split(
                leadingElements = list,
                trailingElements = emptyList(),
            ),
            actual = result,
        )
    }
}
