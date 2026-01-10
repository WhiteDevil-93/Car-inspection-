package com.example.carinspection.data.model

import java.time.LocalDate

data class DailyInspection(
    val driverName: String,
    val startMileage: Int,
    val fuelLevelPercent: Int,
    val oilLevelOk: Boolean,
    val date: LocalDate = LocalDate.now()
)
