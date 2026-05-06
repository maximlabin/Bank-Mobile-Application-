package com.labin.piggybank.utilities

import com.labin.piggybank.ui.model.CalendarSelectionResult
import java.time.LocalDate
import java.time.ZoneId

fun getMonthNameNominative(month: java.time.Month): String = when (month) {
    java.time.Month.JANUARY -> "Январь"
    java.time.Month.FEBRUARY -> "Февраль"
    java.time.Month.MARCH -> "Март"
    java.time.Month.APRIL -> "Апрель"
    java.time.Month.MAY -> "Май"
    java.time.Month.JUNE -> "Июнь"
    java.time.Month.JULY -> "Июль"
    java.time.Month.AUGUST -> "Август"
    java.time.Month.SEPTEMBER -> "Сентябрь"
    java.time.Month.OCTOBER -> "Октябрь"
    java.time.Month.NOVEMBER -> "Ноябрь"
    java.time.Month.DECEMBER -> "Декабрь"
}

fun CalendarSelectionResult.toTimestampRange(): Pair<Long, Long> {
    val zone = ZoneId.systemDefault()
    return when (this) {
        is CalendarSelectionResult.SingleDate -> {
            val zone = ZoneId.systemDefault()
            val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = date.plusDays(1)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli() - 1
            start to end
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
        is CalendarSelectionResult.Month -> {
            val currentYear = LocalDate.now().year
            val start = LocalDate.of(currentYear, month, 1)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()
            val end = LocalDate.of(currentYear, month, 1)
                .plusMonths(1)
                .minusDays(1)
                .atTime(23, 59, 59)
                .atZone(zone)
                .toInstant()
                .toEpochMilli()
            start to end
        }
    }
}