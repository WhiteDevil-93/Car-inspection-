package com.example.carinspection.data.model

enum class WeeklyInspectionStep(val title: String, val fileLabel: String) {
    FRONT_VIEW("Front View", "FrontBumper"),
    PASSENGER_SIDE("Passenger Side", "PassengerSide"),
    REAR_VIEW("Rear View", "RearBumper"),
    DRIVER_SIDE("Driver Side", "DriverSide"),
    WINDSCREEN("Windscreen", "Windscreen"),
    TIRES("Tires", "Tires"),
    ODOMETER("Odometer", "Odometer")
}
