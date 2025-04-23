package app.utils.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeconstructionTests {
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
