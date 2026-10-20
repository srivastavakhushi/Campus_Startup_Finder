package com.campus.startup.features.matching.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MatchingStatusSnackbar(
    viewModel: MatchingViewModel,
    host: SnackbarHostState
) {
    val status by viewModel.statusMessage.collectAsState()
    LaunchedEffect(status) {
        val message = status ?: return@LaunchedEffect
        host.showSnackbar(message)
        viewModel.consumeStatusMessage()
    }
}
