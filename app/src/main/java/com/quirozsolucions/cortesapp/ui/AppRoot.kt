package com.quirozsolucions.cortesapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(vm: OptimizerViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Corte optimizado") }) }
    ) { padding ->
        Row(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FormPane(vm, Modifier.weight(1f).fillMaxHeight())
            Spacer(Modifier.width(16.dp))
            ResultPane(vm, Modifier.weight(1f).fillMaxHeight())
        }
    }
}
