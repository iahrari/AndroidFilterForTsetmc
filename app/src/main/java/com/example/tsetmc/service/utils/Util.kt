package com.example.tsetmc.service.utils

import android.util.Log
import java.util.*

fun generateDynamicFolderName(): String{
    val calendar = GregorianCalendar()

    val year = calendar.get(JalaliCalendar.YEAR)
    val month = calendar.get(JalaliCalendar.MONTH)
    val day = calendar.get(JalaliCalendar.DAY_OF_MONTH)

    val convert =
        GregorianCalendar(year, month, day, 4, 8, 7)
    Log.i("currentTime", (Date().time == convert.time.time).toString())
    return "${convert.time.time}"
}