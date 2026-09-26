package com.example.coopwidget

import com.example.coopwidget.domain.usecase.AgeCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class AgeCalculatorTest {

    @Test
    fun testStandardBirthdayPassed() {
        val birthDate = "1991-05-10"
        val currentDate = LocalDate.of(2026, 9, 23)
        val level = AgeCalculator.calculateLevel(birthDate, currentDate)
        assertEquals(35, level)
    }

    @Test
    fun testStandardBirthdayNotYetPassed() {
        val birthDate = "1991-10-15"
        val currentDate = LocalDate.of(2026, 9, 23)
        val level = AgeCalculator.calculateLevel(birthDate, currentDate)
        assertEquals(34, level)
    }

    @Test
    fun testExactBirthdayToday() {
        val birthDate = "1991-09-23"
        val currentDate = LocalDate.of(2026, 9, 23)
        val level = AgeCalculator.calculateLevel(birthDate, currentDate)
        assertEquals(35, level)
    }

    @Test
    fun testBabyUnderOneYear() {
        val birthDate = "2026-01-15"
        val currentDate = LocalDate.of(2026, 9, 23)
        val level = AgeCalculator.calculateLevel(birthDate, currentDate)
        assertEquals(0, level)
    }

    @Test
    fun testFutureBirthDateReturnsZero() {
        val birthDate = "2030-01-01"
        val currentDate = LocalDate.of(2026, 9, 23)
        val level = AgeCalculator.calculateLevel(birthDate, currentDate)
        assertEquals(0, level)
    }

    @Test
    fun testLeapYearBirthdayOnLeapYear() {
        val birthDate = "2000-02-29"
        val currentDate = LocalDate.of(2024, 2, 29)
        val level = AgeCalculator.calculateLevel(birthDate, currentDate)
        assertEquals(24, level)
    }

    @Test
    fun testLeapYearBirthdayOnNonLeapYearBeforeFeb28() {
        val birthDate = "2000-02-29"
        val currentDate = LocalDate.of(2025, 2, 27)
        val level = AgeCalculator.calculateLevel(birthDate, currentDate)
        assertEquals(24, level)
    }

    @Test
    fun testInvalidDateStringReturnsZero() {
        val level = AgeCalculator.calculateLevel("invalid-date", LocalDate.of(2026, 9, 23))
        assertEquals(0, level)
    }
}
