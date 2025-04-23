package app

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleTests {
    @Test
    fun testSimple() {
        assertEquals(
            expected = 4.0,
            actual = 2.0 * 2.0,
        )
    }
}
