package app.utils.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PartitionTests {
    @Test
    fun testPartitionAt() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.partitionAt(2)
        assertEquals(Partitioned(listOf(1, 2), 3, listOf(4, 5)), result)

        val outOfBoundsResult = list.partitionAt(10)
        assertNull(outOfBoundsResult)

        val negativeIndexResult = list.partitionAt(-1)
        assertNull(negativeIndexResult)
    }

    @Test
    fun testPartitionAtCenter() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.partitionAtCenter()
        assertEquals(Partitioned(listOf(1, 2), 3, listOf(4, 5)), result)

        val emptyListResult = emptyList<Int>().partitionAtCenter()
        assertNull(emptyListResult)
    }

    @Test
    fun testEachPartitioned_empty() {
        val list = listOf<Int>()
        val result = list.eachPartitioned()

        assertEquals(
            expected = emptyList(),
            actual = result,
        )
    }

    @Test
    fun testEachPartitioned_singleElement() {
        val list = listOf(1)
        val result = list.eachPartitioned()

        assertEquals(
            expected = listOf(
                Partitioned(
                    previousElements = emptyList(),
                    innerElement = 1,
                    nextElements = emptyList(),
                ),
            ),
            actual = result,
        )
    }

    @Test
    fun testEachPartitioned_simple() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.eachPartitioned()

        assertEquals(
            expected = listOf(
                Partitioned(
                    previousElements = emptyList(),
                    innerElement = 1,
                    nextElements = listOf(2, 3, 4, 5),
                ),
                Partitioned(
                    previousElements = listOf(1),
                    innerElement = 2,
                    nextElements = listOf(3, 4, 5),
                ),
                Partitioned(
                    previousElements = listOf(1, 2),
                    innerElement = 3,
                    nextElements = listOf(4, 5),
                ),
                Partitioned(
                    previousElements = listOf(1, 2, 3),
                    innerElement = 4,
                    nextElements = listOf(5),
                ),
                Partitioned(
                    previousElements = listOf(1, 2, 3, 4),
                    innerElement = 5,
                    nextElements = emptyList(),
                ),
            ),
            actual = result,
        )
    }
}
