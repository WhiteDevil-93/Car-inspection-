package com.example.carinspection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carinspection.ui.MainScreen
import com.example.carinspection.ui.theme.CarInspectionTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarInspectionTheme {
                val windowSize = calculateWindowSizeClass(this)
                MainScreen(windowSize, viewModel())
            }
        }
    }
}
