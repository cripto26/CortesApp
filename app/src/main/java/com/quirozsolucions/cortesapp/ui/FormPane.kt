package com.quirozsolucions.cortesapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel



@Composable
fun FormPane(vm: OptimizerViewModel, modifier: Modifier = Modifier) {
    val grad = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.50f)
        )
    )
    Column(
        modifier
            .background(grad, shape = MaterialTheme.shapes.large)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HistoryTag()
        Text("Corte optimizado", style = MaterialTheme.typography.headlineMedium)
        Text("Ingrese las dimensiones de una lámina", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(100.dp)) {
            var w by remember { mutableStateOf(vm.board.widthCm.toString()) }
            var h by remember { mutableStateOf(vm.board.heightCm.toString()) }
            NumberField("Ancho (cm)", w, { w = it; vm.updateBoard(w.toIntOrNull(), null) }, Modifier.weight(1f))
            NumberField("Altura (cm)", h, { h = it; vm.updateBoard(null, h.toIntOrNull()) }, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            var kerf by remember { mutableStateOf(vm.kerfMm.toString()) }
            NumberField("Kerf (mm)", kerf, { kerf = it; vm.updateKerf(kerf.toIntOrNull()) }, Modifier.weight(1f))
            FilterChip(
                selected = vm.allowRotation,
                onClick = { vm.toggleRotation() },
                label = { Text("Permitir rotación 90°") }
            )
        }

        Text("Cortes", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.heightIn(max = 280.dp)
        ) {
            itemsIndexed(vm.pieces) { idx, piece ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${idx + 1}", modifier = Modifier.width(24.dp), style = MaterialTheme.typography.titleMedium)
                    var w by remember(piece.id) { mutableStateOf(piece.widthCm.toString()) }
                    var h by remember(piece.id) { mutableStateOf(piece.heightCm.toString()) }
                    var q by remember(piece.id) { mutableStateOf(piece.quantity.toString()) }

                    NumberField("Ancho", w, { w = it; vm.updatePiece(idx, w.toIntOrNull(), null, null) }, Modifier.weight(1f))
                    NumberField("Altura", h, { h = it; vm.updatePiece(idx, null, h.toIntOrNull(), null) }, Modifier.weight(1f))
                    NumberField("Cantidad", q, { q = it; vm.updatePiece(idx, null, null, q.toIntOrNull()) }, Modifier.weight(1f))
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { vm.addRow() }) { Text("Añadir fila") }
            PrimaryButton(
                text = "Optimizar",
                onClick = { vm.optimize() }
            )
        }
    }
}

// Si tienes tu propio tema, cámbialo por él (p. ej., CortesAppTheme { ... }).
@Preview(name = "FormPane – Light", showBackground = true, widthDp = 390)
@Preview(name = "FormPane – Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, widthDp = 390)
@Composable
private fun FormPanePreview() {
    val vm = remember {
        OptimizerViewModel().apply {
            // (Opcional) valores de muestra para que se vea “bonito”:
            updateBoard(215, 244)
            updateKerf(3)
            // deja las piezas por defecto o agrega algunas con addRow/updatePiece(...)
        }
    }
    MaterialTheme {
        Surface(Modifier.fillMaxWidth().padding(16.dp)) {
            FormPane(vm = vm, modifier = Modifier.fillMaxWidth())
        }
    }
}