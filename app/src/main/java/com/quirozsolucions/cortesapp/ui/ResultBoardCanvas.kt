package com.quirozsolucions.cortesapp.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.layout.fillMaxSize
import com.quirozsolucions.cortesapp.OptimizerViewModel
import com.quirozsolucions.cortesapp.model.LayoutResult
import kotlin.math.min

@Composable
fun ResultBoardCanvas(
    vm: OptimizerViewModel,
    pageResult: LayoutResult,
    modifier: Modifier = Modifier
) {
    val palette = listOf(
        Color(0xFFE0E7FF),
        Color(0xFFFFE4E6),
        Color(0xFFE9D5FF),
        Color(0xFFDCFCE7),
        Color(0xFFFFF7ED)
    )

    // 👇 aseguramos tamaño
    Canvas(modifier = modifier.fillMaxSize()) {
        val boardW = vm.board.widthCm.toFloat()
        val boardH = vm.board.heightCm.toFloat()
        val scale = min(size.width / boardW, size.height / boardH)

        val bw = boardW * scale
        val bh = boardH * scale

        drawRoundRect(
            color = Color(0xFF777777),
            topLeft = Offset.Zero,
            size = Size(bw, bh),
            cornerRadius = CornerRadius(10f, 10f),
            style = Stroke(width = 3f)
        )

        val baseText = Paint().apply {
            color = android.graphics.Color.BLACK
            isAntiAlias = true
        }

        pageResult.placed.forEachIndexed { i, p ->
            val x = p.xCm * scale
            val y = p.yCm * scale
            val w = p.widthCm * scale
            val h = p.heightCm * scale

            drawRect(palette[i % palette.size], topLeft = Offset(x, y), size = Size(w, h))
            drawRect(Color.Black, topLeft = Offset(x, y), size = Size(w, h), style = Stroke(width = 1.6f))

            val ts = (min(w, h) * 0.18f).coerceIn(18f, 42f)
            baseText.textSize = ts

            drawIntoCanvas { c ->
                val label = "${p.index}  ${p.widthCm}x${p.heightCm}cm"
                c.nativeCanvas.drawText(label, x + 8f, y + ts + 6f, baseText)
            }
        }
    }
}


