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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel
import com.quirozsolucions.cortesapp.model.LengthUnit

@Composable
fun FormPane(
    vm: OptimizerViewModel,
    onAddRow: () -> Unit,
    onOptimize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    // Estados del tablero/kerf como String para permitir borrar/estados intermedios
    var boardWText by rememberSaveable {
        mutableStateOf(vm.lengthValueInCurrentUnit(vm.board.widthMm).toString())
    }
    var boardHText by rememberSaveable {
        mutableStateOf(vm.lengthValueInCurrentUnit(vm.board.heightMm).toString())
    }
    var kerfText by rememberSaveable { mutableStateOf(vm.kerfMm.toString()) }
    var allowRot by rememberSaveable { mutableStateOf(vm.allowRotation) }

    // Mantener sincronía si el VM cambia desde fuera (p.ej., cambio de unidad)
    LaunchedEffect(vm.board.widthMm, vm.board.heightMm, vm.kerfMm, vm.allowRotation, vm.unit) {
        boardWText = vm.lengthValueInCurrentUnit(vm.board.widthMm).toString()
        boardHText = vm.lengthValueInCurrentUnit(vm.board.heightMm).toString()
        kerfText = vm.kerfMm.toString()
        allowRot = vm.allowRotation
    }

    Column(
        modifier = modifier
            .verticalScroll(scroll)
            .padding(8.dp)
    ) {
        Text("Ingrese las dimensiones de una lámina")
        Spacer(Modifier.height(6.dp))

        // Selector de unidad (mm / cm / m)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unidad:")
            LengthUnit.values().forEach { u ->
                OutlinedButton(
                    onClick = { vm.changeUnit(u) },
                    enabled = vm.unit != u
                ) {
                    Text(u.label)
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = boardWText,
                onValueChange = { new ->
                    if (new.all { it.isDigit() } || new.isEmpty()) {
                        boardWText = new
                        new.toIntOrNull()?.let { w -> vm.updateBoard(w = w, h = null) }
                    }
                },
                label = { Text("Ancho (${vm.unit.label})") },
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
                label = { Text("Altura (${vm.unit.label})") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
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

        vm.pieces.forEachIndexed { index, piece ->
            Spacer(Modifier.height(8.dp))

            var l1Text by rememberSaveable(piece.id, "l1") {
                mutableStateOf(vm.lengthValueInCurrentUnit(piece.widthMm).toString())
            }
            var betaText by rememberSaveable(piece.id, "beta") {
                mutableStateOf(vm.lengthValueInCurrentUnit(piece.heightMm).toString())
            }
            var qText by rememberSaveable(piece.id, "q") {
                mutableStateOf(piece.quantity.toString())
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = l1Text,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() } || new.isEmpty()) {
                            l1Text = new
                            new.toIntOrNull()?.let { l1 ->
                                vm.updatePiece(index, width = l1, height = null, qty = null)
                            }
                        }
                    },
                    label = { Text("L1 (${vm.unit.label})") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = betaText,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() } || new.isEmpty()) {
                            betaText = new
                            new.toIntOrNull()?.let { beta ->
                                vm.updatePiece(index, width = null, height = beta, qty = null)
                            }
                        }
                    },
                    label = { Text("Beta (${vm.unit.label})") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = qText,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() } || new.isEmpty()) {
                            qText = new
                            new.toIntOrNull()?.let { q ->
                                vm.updatePiece(index, width = null, height = null, qty = q)
                            }
                        }
                    },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                // Botón para eliminar la fila (no se muestra si solo hay una fila)
                if (vm.pieces.size > 1) {
                    IconButton(onClick = { vm.removeRow(index) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar fila"
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botón de añadir fila centrado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = onAddRow,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Añadir fila")
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
