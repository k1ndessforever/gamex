package com.guardian.gamex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guardian.gamex.service.FpsMonitor
import com.guardian.gamex.ui.screens.*
import com.guardian.gamex.ui.theme.GameXTheme
import com.guardian.gamex.viewmodel.DashboardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : ComponentActivity() {

    private val fpsMonitor = FpsMonitor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fpsMonitor.attachToWindow(window)

        setContent {
            val darkTheme by remember { mutableStateOf(true) }

            GameXTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameXApp(fpsMonitor = fpsMonitor)
                }
            }
        }
    }

    override fun onDestroy() {
        fpsMonitor.detachFromWindow(window)
        super.onDestroy()
    }
}

@Composable
fun GameXApp(fpsMonitor: FpsMonitor) {
    val navController = rememberNavController()
    val dashboardViewModel: DashboardViewModel = viewModel()

    LaunchedEffect(fpsMonitor) {
        while (isActive) {
            val fpsData = fpsMonitor.fpsData.value
            dashboardViewModel.updateFpsData(fpsData)

            dashboardViewModel.updateCpuUsage((30..60).random().toFloat())
            dashboardViewModel.updateBatteryTemp((28..42).random().toFloat())

            delay(1000)
        }
    }

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onOptimizeClick = { navController.navigate("optimization") },
                onCrosshairClick = { navController.navigate("crosshair") },
                onBenchmarkClick = { navController.navigate("benchmark") },
                viewModel = dashboardViewModel
            )
        }

        composable("optimization") {
            OptimizationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("crosshair") {
            CrosshairScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("benchmark") {
            BenchmarkScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}