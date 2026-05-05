package com.labin.piggybank.utilities

import com.labin.piggybank.ui.model.CalendarSelectionResult
import java.time.LocalDate
import java.time.ZoneId

fun CalendarSelectionResult.toTimestampRange(): Pair<Long, Long> {
    val zone = ZoneId.systemDefault()
    return when (this) {
        is CalendarSelectionResult.SingleDate -> {
            val ts = date.atStartOfDay(zone).toInstant().toEpochMilli()
            ts to ts
        }
        is CalendarSelectionResult.DateRange -> {
            val start = start.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = end.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
            start to end
        }
        is CalendarSelectionResult.Year -> {
            val y = year.value
            val start = LocalDate.of(y, 1, 1).atStartOfDay(zone).toInstant().toEpochMilli()
            val end = LocalDate.of(y, 12, 31).atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
            start to end
        }
        is CalendarSelectionResult.DayOfWeek -> 0L to Long.MAX_VALUE // Вся история
    }
}