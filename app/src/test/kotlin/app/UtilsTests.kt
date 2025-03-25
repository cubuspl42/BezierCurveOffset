package app

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTests {
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
    fun testMapWithNeighbours_standardCase() {
        val numbers = listOf(1, 2, 3, 4)
        val result = numbers.mapWithNeighbours { prev, current, next ->
            (prev ?: 0) + current + (next ?: 0)
        }

        assertEquals(
            expected = listOf(3, 6, 9, 7),
            actual = result,
        )
    }

    @Test
    fun testMapWithNeighbours_singleElement() {
        val numbers = listOf(5)
        val result = numbers.mapWithNeighbours { prev, current, next ->
            (prev ?: 0) + current + (next ?: 0)
        }

        assertEquals(
            expected = listOf(5),
            actual = result,
        )
    }

    @Test
    fun testMapWithNeighbours_emptyList() {
        val numbers = emptyList<Int>()
        val result = numbers.mapWithNeighbours { prev, current, next ->
            (prev ?: 0) + current + (next ?: 0)
        }

        assertEquals(
            expected = emptyList(),
            actual = result,
        )
    }

    @Test
    fun testMapWithNeighbours_stringConcatenation() {
        val words = listOf("a", "b", "c")
        val result = words.mapWithNeighbours { prev, current, next ->
            "${prev ?: ""}${current}${next ?: ""}"
        }

        assertEquals(
            expected = listOf("ab", "abc", "bc"),
            actual = result,
        )
    }
}
