package com.guardian.gamex.core.optimizer

import android.app.Application
import android.content.Intent
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class OptimizationState(
    val isRunning: Boolean = false,
    val currentStepIndex: Int = 0,
    val completedSteps: Int = 0,
    val steps: List<OptimizationStep> = emptyList()
)

sealed class OptimizationStep {
    abstract val completed: Boolean

    data class DND(override val completed: Boolean = false) : OptimizationStep()
    data class BatteryOptimization(override val completed: Boolean = false) : OptimizationStep()
    data class BackgroundRestriction(override val completed: Boolean = false) : OptimizationStep()
    data class DisplaySettings(override val completed: Boolean = false) : OptimizationStep()
    data class GameMode(override val completed: Boolean = false) : OptimizationStep()
}

class OptimizationEngine(private val context: Application) {

    private val _state = MutableStateFlow(OptimizationState())
    val state: StateFlow<OptimizationState> = _state

    fun startOptimization() {
        val steps = listOf(
            OptimizationStep.DND(),
            OptimizationStep.BatteryOptimization(),
            OptimizationStep.BackgroundRestriction(),
            OptimizationStep.DisplaySettings(),
            OptimizationStep.GameMode()
        )

        _state.value = OptimizationState(
            isRunning = true,
            currentStepIndex = 0,
            completedSteps = 0,
            steps = steps
        )
    }

    fun markStepCompleted(stepIndex: Int) {
        val currentSteps = _state.value.steps.toMutableList()
        if (stepIndex in currentSteps.indices) {
            currentSteps[stepIndex] = when (val step = currentSteps[stepIndex]) {
                is OptimizationStep.DND -> step.copy(completed = true)
                is OptimizationStep.BatteryOptimization -> step.copy(completed = true)
                is OptimizationStep.BackgroundRestriction -> step.copy(completed = true)
                is OptimizationStep.DisplaySettings -> step.copy(completed = true)
                is OptimizationStep.GameMode -> step.copy(completed = true)
            }

            val completedCount = currentSteps.count {
                when (it) {
                    is OptimizationStep.DND -> it.completed
                    is OptimizationStep.BatteryOptimization -> it.completed
                    is OptimizationStep.BackgroundRestriction -> it.completed
                    is OptimizationStep.DisplaySettings -> it.completed
                    is OptimizationStep.GameMode -> it.completed
                }
            }

            val nextIndex = if (stepIndex < currentSteps.size - 1) stepIndex + 1 else stepIndex

            _state.value = _state.value.copy(
                steps = currentSteps,
                completedSteps = completedCount,
                currentStepIndex = nextIndex
            )
        }
    }

    fun getIntentForStep(step: OptimizationStep): Intent? {
        return when (step) {
            is OptimizationStep.DND -> Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            is OptimizationStep.BatteryOptimization -> Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            is OptimizationStep.BackgroundRestriction -> Intent(Settings.ACTION_SETTINGS)
            is OptimizationStep.DisplaySettings -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
            is OptimizationStep.GameMode -> Intent(Settings.ACTION_SETTINGS)
        }
    }

    fun getStepDescription(step: OptimizationStep): String {
        return when (step) {
            is OptimizationStep.DND -> "Enable Do Not Disturb mode to prevent notification interruptions"
            is OptimizationStep.BatteryOptimization -> "Disable battery optimization for GameX to maintain performance"
            is OptimizationStep.BackgroundRestriction -> "Allow background activity for optimal performance"
            is OptimizationStep.DisplaySettings -> "Set display refresh rate to maximum"
            is OptimizationStep.GameMode -> "Enable system Game Mode if available"
        }
    }

    fun getExpectedGain(step: OptimizationStep): String {
        return when (step) {
            is OptimizationStep.DND -> "Eliminates frame drops from notifications"
            is OptimizationStep.BatteryOptimization -> "+5-10% sustained performance"
            is OptimizationStep.BackgroundRestriction -> "+3-5 FPS average"
            is OptimizationStep.DisplaySettings -> "Smoother visuals, reduced input lag"
            is OptimizationStep.GameMode -> "+10-15% performance boost"
        }
    }

    fun reset() {
        _state.value = OptimizationState()
    }
}