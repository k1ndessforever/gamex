package com.guardian.gamex.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guardian.gamex.viewmodel.OptimizationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizationScreen(
    onNavigateBack: () -> Unit,
    viewModel: OptimizationViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.markStepCompleted(state.currentStepIndex)
    }

    LaunchedEffect(Unit) {
        if (!state.isRunning) {
            viewModel.startOptimization()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("One-Touch Optimization") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.completedSteps}/${state.steps.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { if (state.steps.isEmpty()) 0f else state.completedSteps.toFloat() / state.steps.size },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Info, null)
                    Text(
                        text = "Each step will open the relevant system settings. Apply the suggested changes and return here.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            state.steps.forEachIndexed { index, step ->
                OptimizationStepCard(
                    step = step,
                    stepIndex = index,
                    isActive = index == state.currentStepIndex,
                    viewModel = viewModel,
                    onLaunchSettings = { intent ->
                        activityLauncher.launch(intent)
                    }
                )
            }

            if (state.completedSteps == state.steps.size && state.steps.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Optimization Complete!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your device is now optimized for gaming",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = onNavigateBack) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptimizationStepCard(
    step: com.guardian.gamex.core.optimizer.OptimizationStep,
    stepIndex: Int,
    isActive: Boolean,
    viewModel: OptimizationViewModel,
    onLaunchSettings: (android.content.Intent) -> Unit
) {
    val isCompleted = when (step) {
        is com.guardian.gamex.core.optimizer.OptimizationStep.DND -> step.completed
        is com.guardian.gamex.core.optimizer.OptimizationStep.BatteryOptimization -> step.completed
        is com.guardian.gamex.core.optimizer.OptimizationStep.BackgroundRestriction -> step.completed
        is com.guardian.gamex.core.optimizer.OptimizationStep.DisplaySettings -> step.completed
        is com.guardian.gamex.core.optimizer.OptimizationStep.GameMode -> step.completed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> MaterialTheme.colorScheme.primaryContainer
                isActive -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step ${stepIndex + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Circle,
                    contentDescription = null,
                    tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = viewModel.getStepDescription(step),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Expected Gain: ${viewModel.getExpectedGain(step)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            if (isActive && !isCompleted) {
                Button(
                    onClick = {
                        viewModel.getIntentForStep(step)?.let { intent ->
                            onLaunchSettings(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.OpenInNew, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Open Settings")
                }
            }
        }
    }
}