package com.example.carinspection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.ViewModelProvider
import com.example.carinspection.ui.InspectionViewModel
import com.example.carinspection.ui.MainScreen
import com.example.carinspection.ui.theme.CarInspectionTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[InspectionViewModel::class.java]

        setContent {
            CarInspectionTheme {
                val windowSize = calculateWindowSizeClass(this)
                MainScreen(windowSize, viewModel)
            }
        }
    }
}
