package com.quirozsolucions.cortesapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel

@Composable
fun FormPane(
    vm: OptimizerViewModel,
    onAddRow: () -> Unit,
    onOptimize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    // Estados del tablero/kerf como String para permitir borrar/estados intermedios
    var boardWText by rememberSaveable { mutableStateOf(vm.board.widthCm.toString()) }
    var boardHText by rememberSaveable { mutableStateOf(vm.board.heightCm.toString()) }
    var kerfText   by rememberSaveable { mutableStateOf(vm.kerfMm.toString()) }
    var allowRot   by rememberSaveable { mutableStateOf(vm.allowRotation) }

    // Mantener sincronía si el VM cambia desde fuera (p.ej., reset)
    LaunchedEffect(vm.board.widthCm, vm.board.heightCm, vm.kerfMm, vm.allowRotation) {
        boardWText = vm.board.widthCm.toString()
        boardHText = vm.board.heightCm.toString()
        kerfText   = vm.kerfMm.toString()
        allowRot   = vm.allowRotation
    }

    Column(
        modifier = modifier
            .verticalScroll(scroll)
            .padding(8.dp)
    ) {
        Text("Ingrese las dimensiones de una lámina")
        Spacer(Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = boardWText,
                onValueChange = { new ->
                    if (new.all { it.isDigit() } || new.isEmpty()) {
                        boardWText = new
                        new.toIntOrNull()?.let { w -> vm.updateBoard(w = w, h = null) }
                    }
                },
                label = { Text("Ancho (cm)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = boardHText,
                onValueChange = { new ->
                    if (new.all { it.isDigit() } || new.isEmpty()) {
                        boardHText = new
                        new.toIntOrNull()?.let { h -> vm.updateBoard(w = null, h = h) }
                    }
                },
                label = { Text("Altura (cm)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = kerfText,
                onValueChange = { new ->
                    if (new.all { it.isDigit() } || new.isEmpty()) {
                        kerfText = new
                        new.toIntOrNull()?.let(vm::updateKerf)
                    }
                },
                label = { Text("Kerf (mm)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Start) {
                Checkbox(
                    checked = allowRot,
                    onCheckedChange = {
                        allowRot = it
                        vm.toggleRotation()
                    }
                )
                Text("Permitir rotación 90°")
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Cortes")

        // Filas dinámicas: estados de texto por pieza, recordados por ID
        vm.pieces.forEachIndexed { index, piece ->
            Spacer(Modifier.height(8.dp))

            // Cada campo usa rememberSaveable con clave por pieza e identificador del campo
            var wText by rememberSaveable(piece.id, "w") { mutableStateOf(piece.widthCm.toString()) }
            var hText by rememberSaveable(piece.id, "h") { mutableStateOf(piece.heightCm.toString()) }
            var qText by rememberSaveable(piece.id, "q") { mutableStateOf(piece.quantity.toString()) }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = wText,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() } || new.isEmpty()) {
                            wText = new
                            new.toIntOrNull()?.let { w -> vm.updatePiece(index, width = w, height = null, qty = null) }
                        }
                    },
                    label = { Text("Ancho") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = hText,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() } || new.isEmpty()) {
                            hText = new
                            new.toIntOrNull()?.let { h -> vm.updatePiece(index, width = null, height = h, qty = null) }
                        }
                    },
                    label = { Text("Altura") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = qText,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() } || new.isEmpty()) {
                            qText = new
                            new.toIntOrNull()?.let { q -> vm.updatePiece(index, width = null, height = null, qty = q) }
                        }
                    },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onAddRow, modifier = Modifier.weight(1f)) {
                Text("Añadir fila")
            }
            Button(
                onClick = {
                    vm.optimizeAll()
                    onOptimize()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Optimizar")
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
