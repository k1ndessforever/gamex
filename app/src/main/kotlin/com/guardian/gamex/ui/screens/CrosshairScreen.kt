package com.guardian.gamex.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guardian.gamex.service.OverlayService
import com.guardian.gamex.viewmodel.CrosshairViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosshairScreen(
    onNavigateBack: () -> Unit,
    viewModel: CrosshairViewModel = viewModel()
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    var showTosDialog by remember { mutableStateOf(!settings.tosAccepted) }

    if (showTosDialog) {
        TosDialog(
            onAccept = {
                viewModel.acceptTos()
                showTosDialog = false
            },
            onDismiss = onNavigateBack
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crosshair Overlay") },
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
                    containerColor = if (settings.enabled) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Crosshair Overlay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (settings.enabled) "Active" else "Inactive",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = settings.enabled,
                        onCheckedChange = { enabled ->
                            if (enabled && !Settings.canDrawOverlays(context)) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            } else {
                                viewModel.setEnabled(enabled)
                                val intent = Intent(context, OverlayService::class.java).apply {
                                    action = if (enabled) {
                                        OverlayService.ACTION_SHOW
                                    } else {
                                        OverlayService.ACTION_HIDE
                                    }
                                    putExtra(OverlayService.EXTRA_STYLE, settings.style)
                                    putExtra(OverlayService.EXTRA_SIZE, settings.size)
                                    putExtra(OverlayService.EXTRA_COLOR, settings.color)
                                    putExtra(OverlayService.EXTRA_OPACITY, settings.opacity)
                                }
                                context.startService(intent)
                            }
                        }
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                    Text(
                        text = "Using overlays in competitive multiplayer games may violate their terms of service.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Style",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("dot", "plus", "hollow", "sniper").forEach { style ->
                            FilterChip(
                                selected = settings.style == style,
                                onClick = { viewModel.setStyle(style) },
                                label = { Text(style.replaceFirstChar { it.uppercase() }) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Size: ${settings.size.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = settings.size,
                        onValueChange = { viewModel.setSize(it) },
                        valueRange = 5f..30f,
                        steps = 24
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Opacity: ${(settings.opacity * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = settings.opacity,
                        onValueChange = { viewModel.setOpacity(it) },
                        valueRange = 0.2f..1f
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val colors = listOf(
                            0xFFFF0844.toInt() to "Red",
                            0xFF00F0FF.toInt() to "Cyan",
                            0xFF00FF00.toInt() to "Green",
                            0xFFFFFF00.toInt() to "Yellow",
                            0xFFFFFFFF.toInt() to "White"
                        )
                        colors.forEach { (color, name) ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(color))
                                    .border(
                                        width = if (settings.color == color) 3.dp else 1.dp,
                                        color = if (settings.color == color) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.outline
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setColor(color) }
                            )
                        }
                    }
                }
            }

            if (settings.enabled) {
                Button(
                    onClick = {
                        val intent = Intent(context, OverlayService::class.java).apply {
                            action = OverlayService.ACTION_UPDATE
                            putExtra(OverlayService.EXTRA_STYLE, settings.style)
                            putExtra(OverlayService.EXTRA_SIZE, settings.size)
                            putExtra(OverlayService.EXTRA_COLOR, settings.color)
                            putExtra(OverlayService.EXTRA_OPACITY, settings.opacity)
                        }
                        context.startService(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Apply Changes")
                }
            }
        }
    }
}

@Composable
private fun TosDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, null) },
        title = { Text("Terms of Use") },
        text = {
            Text(
                "Crosshair overlays are provided for single-player and casual use only. " +
                        "Using overlays in competitive multiplayer games may violate their terms of service " +
                        "and could result in penalties or bans. By accepting, you agree to use this feature responsibly."
            )
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("I Accept")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}