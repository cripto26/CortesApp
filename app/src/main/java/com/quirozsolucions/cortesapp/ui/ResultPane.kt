package com.quirozsolucions.cortesapp.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel
import kotlin.math.min

@Composable
fun ResultPane(vm: OptimizerViewModel, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Gráfico", style = MaterialTheme.typography.titleMedium)

        Card(Modifier.weight(1f).fillMaxWidth()) {
            val plan = vm.plan
            if (plan == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ejecuta la optimización para ver el layout.")
                }
            } else {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    val wMm = plan.sheet.widthMm.toFloat()
                    val hMm = plan.sheet.heightMm.toFloat()
                    val scale = min(size.width / wMm, size.height / hMm)

                    // Marco de la lámina
                    drawRect(
                        color = Color.LightGray,
                        topLeft = Offset.Zero,
                        size = Size(wMm * scale, hMm * scale),
                        style = Stroke(width = 2f)
                    )

                    // Piezas
                    plan.placed.forEach { r ->
                        val left = r.xMm * scale
                        val top = r.yMm * scale
                        val width = r.widthMm * scale
                        val height = r.heightMm * scale

                        drawRect(
                            color = r.color,
                            topLeft = Offset(left, top),
                            size = Size(width, height)
                        )

                        // Etiqueta centrada (texto)
                        drawIntoCanvas { canvas ->
                            val p = Paint().apply {
                                color = android.graphics.Color.BLACK
                                textAlign = Paint.Align.CENTER
                                textSize = min(width, height) / 6f
                                isAntiAlias = true
                            }
                            canvas.nativeCanvas.drawText(
                                r.label,
                                left + width / 2f,
                                top + height / 2f + p.textSize / 3f,
                                p
                            )
                        }
                    }
                }
            }
        }

        vm.plan?.let { plan ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Rendimiento: ${"%.1f".format(plan.yieldPercent)} %", style = MaterialTheme.typography.bodyLarge)
                Text("Área desperdiciada: ${"%.0f".format(plan.wasteAreaCm2)} cm²", style = MaterialTheme.typography.bodyLarge)
                if (plan.notPlaced.isNotEmpty()) {
                    Text("No ubicadas: ${plan.notPlaced.size}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
