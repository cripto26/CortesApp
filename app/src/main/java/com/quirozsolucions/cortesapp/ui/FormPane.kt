package com.quirozsolucions.cortesapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel



@Composable
fun FormPane(vm: OptimizerViewModel, modifier: Modifier = Modifier) {
    Column(modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Ingrese las dimensiones de una lámina", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = vm.sheetHeightCm, onValueChange = { vm.sheetHeightCm = it },
                label = { Text("Altura (cm)") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = vm.sheetWidthCm, onValueChange = { vm.sheetWidthCm = it },
                label = { Text("Ancho (cm)") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = vm.kerfMm, onValueChange = { vm.kerfMm = it },
            label = { Text("Kerf (mm)") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(160.dp)
        )

        Spacer(Modifier.height(8.dp))
        Text("Cortes", style = MaterialTheme.typography.titleMedium)

        vm.rows.forEachIndexed { idx, row ->
            Card {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = row.widthCm, onValueChange = { vm.setWidth(idx, it) },
                        label = { Text("Ancho (cm)") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = row.heightCm, onValueChange = { vm.setHeight(idx, it) },
                        label = { Text("Altura (cm)") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = row.qty, onValueChange = { vm.setQty(idx, it) },
                        label = { Text("Cantidad") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(110.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = row.rot,
                            onCheckedChange = { checked -> vm.setRot(idx, checked) }
                        )
                        Text("Rotar")
                    }
                    IconButton(onClick = { vm.removeRow(idx) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = vm::addRow) { Text("Agregar corte") }
            Button(onClick = vm::optimize) { Text("Optimizar") }
        }
    }
}
