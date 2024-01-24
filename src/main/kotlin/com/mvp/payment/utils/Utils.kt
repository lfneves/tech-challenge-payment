package com.mvp.payment.utils

import java.time.LocalDateTime

object Utils {

    fun convertLocalDateTimeToList(localDateTime: LocalDateTime): List<Int> {
        val year = localDateTime.year
        val month = localDateTime.monthValue
        val day = localDateTime.dayOfMonth
        val hour = localDateTime.hour
        val minute = localDateTime.minute
        val second = localDateTime.second
        val nanosecond = localDateTime.nano

        return listOf(year, month, day, hour, minute, second, nanosecond)
    }
}