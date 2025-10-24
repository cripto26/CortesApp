package com.quirozsolucions.cortesapp.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel
import kotlin.math.min
import java.util.Locale

@Composable
fun ResultPane(vm: OptimizerViewModel, modifier: Modifier = Modifier) {
    val res = vm.result

    // 1) Lee colores FUERA del Canvas (contexto @Composable)
    val grad = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        )
    )
    val boardOutline = MaterialTheme.colorScheme.outline
    val fillPalette = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.inversePrimary
    )

    Column(
        modifier
            .background(grad, shape = MaterialTheme.shapes.large)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Gráfico", style = MaterialTheme.typography.headlineMedium)

        Box(Modifier.fillMaxWidth().height(320.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val boardW = vm.board.widthCm.toFloat()
                val boardH = vm.board.heightCm.toFloat()
                val scale = min(size.width / boardW, size.height / boardH)

                // marco del tablero
                val bw = boardW * scale
                val bh = boardH * scale
                drawRoundRect(
                    color = boardOutline,
                    topLeft = Offset.Zero,
                    size = Size(bw, bh),
                    cornerRadius = CornerRadius(8f, 8f),
                    style = Stroke(width = 2f)
                )

                if (res != null) {
                    val textPaint = Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 28f
                        isAntiAlias = true
                    }

                    res.placed.forEachIndexed { i, p ->
                        val c = fillPalette[i % fillPalette.size]
                        val x = p.xCm * scale
                        val y = p.yCm * scale
                        val w = p.widthCm * scale
                        val h = p.heightCm * scale

                        // pieza y borde
                        drawRect(c, topLeft = Offset(x, y), size = Size(w, h))
                        drawRect(Color.Black, topLeft = Offset(x, y), size = Size(w, h), style = Stroke(width = 1.5f))

                        // etiqueta: "index ancho x alto cm"
                        drawIntoCanvas { canvas ->
                            val label = "${p.index}  ${p.widthCm}x${p.heightCm}cm"
                            canvas.nativeCanvas.drawText(label, x + 8f, y + 22f, textPaint)
                        }
                    }
                }
            }
        }

        if (res != null) {
            val util = String.format(Locale.getDefault(), "%.1f", res.utilization * 100)
            Text("Rendimiento  $util%", style = MaterialTheme.typography.titleMedium)
            Text("Área desperdiciada  ${res.wasteAreaCm2} cm²", style = MaterialTheme.typography.titleMedium)
            if (res.unplaced.isNotEmpty()) {
                Text("No ubicadas: ${res.unplaced.size}", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Text("Aún no has optimizado. Pulsa **Optimizar**.", color = MaterialTheme.colorScheme.outline)
        }
    }
}
