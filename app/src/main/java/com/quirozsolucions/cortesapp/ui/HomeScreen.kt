package com.quirozsolucions.cortesapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel

@Composable
fun HomeScreen(
    vm: OptimizerViewModel,
    onOptimize: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // 👇 El formulario ocupa el espacio naturalmente y es scroll interno
        FormPane(
            vm = vm,
            onAddRow = { vm.addRow() },
            onOptimize = onOptimize
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón grande para optimizar y navegar
        OutlinedButton(
            onClick = onOptimize,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Optimizar y ver gráficos")
        }
    }
}
