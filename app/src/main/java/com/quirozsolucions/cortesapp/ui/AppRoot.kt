package com.quirozsolucions.cortesapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel

@Composable
fun AppRoot(vm: OptimizerViewModel, isWide: Boolean) {
    if (isWide) {
        Row(Modifier.fillMaxSize().padding(8.dp)) {
            FormPane(vm, Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            ResultPane(vm, Modifier.weight(1f))
        }
    } else {
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            FormPane(vm)
            Spacer(Modifier.height(8.dp))
            ResultPane(vm)
        }
    }
}
