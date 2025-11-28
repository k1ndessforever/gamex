package com.guardian.gamex.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.guardian.gamex.core.optimizer.OptimizationEngine
import com.guardian.gamex.core.optimizer.OptimizationState
import com.guardian.gamex.core.optimizer.OptimizationStep
import kotlinx.coroutines.flow.StateFlow

class OptimizationViewModel(application: Application) : AndroidViewModel(application) {

    private val optimizationEngine = OptimizationEngine(application)

    val state: StateFlow<OptimizationState> = optimizationEngine.state

    fun startOptimization() {
        optimizationEngine.startOptimization()
    }

    fun getIntentForStep(step: OptimizationStep): Intent? {
        return optimizationEngine.getIntentForStep(step)
    }

    fun markStepCompleted(stepIndex: Int) {
        optimizationEngine.markStepCompleted(stepIndex)
    }

    fun getStepDescription(step: OptimizationStep): String {
        return optimizationEngine.getStepDescription(step)
    }

    fun getExpectedGain(step: OptimizationStep): String {
        return optimizationEngine.getExpectedGain(step)
    }

    fun reset() {
        optimizationEngine.reset()
    }
}