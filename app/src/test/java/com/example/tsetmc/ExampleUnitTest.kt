package com.example.tsetmc

import com.example.tsetmc.service.utils.JalaliCalendar
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val calendar = JalaliCalendar()

        val year = calendar.get(JalaliCalendar.YEAR)
        val month = calendar.get(JalaliCalendar.MONTH)
        val day = calendar.get(JalaliCalendar.DAY_OF_MONTH)

        val convert =
        JalaliCalendar(year, month, day, 1, 0, 0)

        assertNotEquals(Date().time, convert.time.time)
    }
}
