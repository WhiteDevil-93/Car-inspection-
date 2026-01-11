package com.example.carinspection.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.carinspection.auth.DriverInfoManager
import com.example.carinspection.data.InspectionRepository
import com.example.carinspection.data.excel.ExcelDailyInspectionWriter
import com.example.carinspection.data.model.DailyInspection
import com.example.carinspection.data.model.WeeklyInspectionStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class InspectionViewModel(application: Application) : AndroidViewModel(application) {

    private val driverInfoManager = DriverInfoManager(application)
    private val repository = InspectionRepository(getApplication(), ExcelDailyInspectionWriter())

    private val _weeklyStep = MutableStateFlow(WeeklyInspectionStep.FRONT_VIEW)
    val weeklyStep: StateFlow<WeeklyInspectionStep> = _weeklyStep

    private val _weeklyPhotos = MutableStateFlow<Map<WeeklyInspectionStep, List<File>>>(emptyMap())
    val weeklyPhotos: StateFlow<Map<WeeklyInspectionStep, List<File>>> = _weeklyPhotos

    fun submitDailyInspection(inspection: DailyInspection) {
        viewModelScope.launch {
            repository.appendDailyInspection(inspection)
        }
    }

    fun onWeeklyPhotoCaptured(step: WeeklyInspectionStep, file: File, driverName: String) {
        val existing = _weeklyPhotos.value[step].orEmpty()
        _weeklyPhotos.value = _weeklyPhotos.value + (step to (existing + file))
        viewModelScope.launch {
            repository.uploadWeeklyPhoto(file, step.fileLabel, driverName, LocalDate.now())
        }
    }

    fun moveToNextStep() {
        val steps = WeeklyInspectionStep.values().toList()
        val currentIndex = steps.indexOf(_weeklyStep.value)
        if (currentIndex < steps.lastIndex) {
            _weeklyStep.value = steps[currentIndex + 1]
        }
    }

    fun resetWeeklyFlow() {
        _weeklyStep.value = WeeklyInspectionStep.FRONT_VIEW
        _weeklyPhotos.value = emptyMap()
    }
}
