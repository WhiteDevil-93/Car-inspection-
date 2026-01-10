package com.example.carinspection.data.model

import java.io.File



data class WeeklyPhoto(
    val step: WeeklyInspectionStep,
    val file: File,
    val driverName: String
)
