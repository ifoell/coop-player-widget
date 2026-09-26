package com.example.coopwidget.domain.usecase

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object AgeCalculator {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD

    /**
     * Calculates current age (LV) given a birthDate string in YYYY-MM-DD format.
     * Optionally accepts targetDate for testing or custom date calculations.
     */
    fun calculateLevel(birthDateStr: String, targetDate: LocalDate = LocalDate.now()): Int {
        val birthDate = parseDate(birthDateStr) ?: return 0
        if (targetDate.isBefore(birthDate)) return 0

        return Period.between(birthDate, targetDate).years.coerceAtLeast(0)
    }

    /**
     * Parses ISO YYYY-MM-DD date string safely.
     */
    fun parseDate(birthDateStr: String): LocalDate? {
        return try {
            LocalDate.parse(birthDateStr.trim(), formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /**
     * Formats a LocalDate into YYYY-MM-DD ISO string.
     */
    fun formatDate(date: LocalDate): String {
        return date.format(formatter)
    }
}
