package com.example.carinspection.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.carinspection.auth.GoogleSignInManager
import com.google.android.gms.auth.api.signin.GoogleSignIn

private enum class AppSection(val title: String) {
    DAILY("Daily Inspection"),
    WEEKLY("Weekly Inspection")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(windowSizeClass: WindowSizeClass, viewModel: InspectionViewModel) {
    val (section, setSection) = remember { mutableStateOf(AppSection.DAILY) }
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val context = LocalContext.current
    val signInManager = remember { GoogleSignInManager(context) }
    val (signedIn, setSignedIn) = remember {
        mutableStateOf(GoogleSignIn.getLastSignedInAccount(context) != null)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val account = runCatching { GoogleSignIn.getSignedInAccountFromIntent(result.data).result }
                .getOrNull()
            setSignedIn(account != null)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Vehicle Inspection") }) }
    ) { padding ->
        if (!signedIn) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Sign in with Google to upload inspections to Drive.")
                Spacer(modifier = Modifier.padding(8.dp))
                Button(onClick = { launcher.launch(signInManager.signInClient().signInIntent) }) {
                    Text(text = "Sign in with Google")
                }
            }
        } else if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                NavigationRail {
                    AppSection.values().forEach { item ->
                        NavigationRailItem(
                            selected = section == item,
                            onClick = { setSection(item) },
                            label = { Text(item.title) },
                            icon = {}
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    when (section) {
                        AppSection.DAILY -> DailyInspectionScreen(viewModel)
                        AppSection.WEEKLY -> WeeklyInspectionScreen(viewModel)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AppSection.values().forEach { item ->
                            TextButton(
                                onClick = { setSection(item) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = item.title)
                            }
                        }
                    }
                }
                when (section) {
                    AppSection.DAILY -> DailyInspectionScreen(viewModel)
                    AppSection.WEEKLY -> WeeklyInspectionScreen(viewModel)
                }
            }
        }
    }
}
