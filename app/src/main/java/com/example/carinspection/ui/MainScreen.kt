package com.example.carinspection.ui

import android.app.Activity
import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.carinspection.auth.DriverInfoManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

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

    val driverInfoManager = remember { DriverInfoManager(context) }

    val (signedIn, setSignedIn) = remember { mutableStateOf(driverInfoManager.isDriverInfoPresent()) }
    val (driverName, setDriverName) = remember { mutableStateOf(driverInfoManager.getDriverName()) }
    val (driverSurname, setDriverSurname) = remember { mutableStateOf(driverInfoManager.getDriverSurname()) }

    // Google Sign-In Logic
    val (isGoogleSignedIn, setIsGoogleSignedIn) = remember {
        mutableStateOf(GoogleSignIn.getLastSignedInAccount(context) != null)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                setIsGoogleSignedIn(true)
            }
        }
    }

    fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(context, gso)
        googleSignInLauncher.launch(client.signInIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicle Inspection") },
                actions = {
                    if (signedIn) {
                        IconButton(onClick = {
                            driverInfoManager.saveDriverInfo("", "")
                            setSignedIn(false)
                        }) {
                            Icon(Icons.Default.ExitToApp, "Change Driver")
                        }
                    }
                    IconButton(onClick = { startGoogleSignIn() }) {
                        Icon(
                            imageVector = if (isGoogleSignedIn) Icons.Default.CloudUpload else Icons.Default.CloudOff,
                            contentDescription = if (isGoogleSignedIn) "Drive Connected" else "Connect Drive"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (!signedIn) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Please enter your name to start inspection.",
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                OutlinedTextField(
                    value = driverName,
                    onValueChange = setDriverName,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedTextField(
                    value = driverSurname,
                    onValueChange = setDriverSurname,
                    label = { Text("Surname") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Button(
                    onClick = {
                        if (driverName.isNotBlank() && driverSurname.isNotBlank()) {
                            driverInfoManager.saveDriverInfo(driverName, driverSurname)
                            setSignedIn(true)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    Text(text = "Start Inspection")
                }

                Spacer(modifier = Modifier.padding(16.dp))

                if (!isGoogleSignedIn) {
                    TextButton(
                        onClick = { startGoogleSignIn() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Connect Google Drive (Admin)")
                    }
                } else {
                    Text(
                        text = "Drive Connected",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
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
