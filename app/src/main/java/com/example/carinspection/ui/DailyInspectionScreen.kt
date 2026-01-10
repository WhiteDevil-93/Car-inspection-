package com.example.carinspection.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.carinspection.data.model.DailyInspection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyInspectionScreen(viewModel: InspectionViewModel) {
    val (driverName, setDriverName) = remember { mutableStateOf("") }
    val (startMileage, setStartMileage) = remember { mutableStateOf("") }
    val (fuelLevel, setFuelLevel) = remember { mutableStateOf(50f) }
    val (oilLevelOk, setOilLevelOk) = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Daily Inspection")
        OutlinedTextField(
            value = driverName,
            onValueChange = setDriverName,
            label = { Text("Driver Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = startMileage,
            onValueChange = setStartMileage,
            label = { Text("Start Mileage") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = "Fuel Level: ${fuelLevel.toInt()}%")
        Slider(
            value = fuelLevel,
            onValueChange = setFuelLevel,
            valueRange = 0f..100f
        )
        Column {
            Text(text = "Oil Level Check")
            Checkbox(checked = oilLevelOk, onCheckedChange = setOilLevelOk)
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            onClick = {
                val mileage = startMileage.toIntOrNull() ?: 0
                viewModel.submitDailyInspection(
                    DailyInspection(
                        driverName = driverName.ifBlank { "Unknown" },
                        startMileage = mileage,
                        fuelLevelPercent = fuelLevel.toInt(),
                        oilLevelOk = oilLevelOk
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Daily Inspection")
        }
    }
}
