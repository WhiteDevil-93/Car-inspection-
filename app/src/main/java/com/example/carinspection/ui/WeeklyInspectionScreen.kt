package com.example.carinspection.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.carinspection.data.model.WeeklyInspectionStep
import java.io.File
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyInspectionScreen(viewModel: InspectionViewModel) {
    val context = LocalContext.current
    val currentStep by viewModel.weeklyStep.collectAsState()
    val weeklyPhotos by viewModel.weeklyPhotos.collectAsState()
    val (driverName, setDriverName) = remember { mutableStateOf("") }
    val (error, setError) = remember { mutableStateOf<String?>(null) }

    val capturedCount = weeklyPhotos[currentStep]?.size ?: 0
    val requiredCount = if (currentStep == WeeklyInspectionStep.TIRES) 4 else 1
    val canProceed = capturedCount >= requiredCount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Weekly Inspection")
        OutlinedTextField(
            value = driverName,
            onValueChange = setDriverName,
            label = { Text("Driver Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = "Step: ${currentStep.title}")
        Text(text = "Captured: $capturedCount / $requiredCount")

        val outputFile = remember(currentStep, capturedCount) {
            File(context.cacheDir, "${LocalDate.now()}_${currentStep.fileLabel}_${capturedCount + 1}.jpg")
        }

        CameraCapture(
            outputFile = outputFile,
            onImageCaptured = { file ->
                viewModel.onWeeklyPhotoCaptured(
                    step = currentStep,
                    file = file,
                    driverName = driverName.ifBlank { "Unknown" }
                )
                setError(null)
            },
            onError = { exception ->
                setError(exception.message)
            }
        )

        error?.let { Text(text = "Camera error: $it") }

        Button(
            onClick = {
                if (canProceed) {
                    viewModel.moveToNextStep()
                }
            },
            enabled = canProceed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (currentStep == WeeklyInspectionStep.ODOMETER) "Finish" else "Next Step")
        }

        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            onClick = viewModel::resetWeeklyFlow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart Weekly Inspection")
        }
    }
}
