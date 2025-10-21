package com.quirozsolucions.cortesapp.model

import androidx.compose.ui.graphics.Color

// Todo se maneja internamente en mm para evitar redondeos.
data class SheetSpec(
    val widthMm: Int,
    val heightMm: Int,
    val kerfMm: Int = 3
)

data class RectSpec(
    val widthMm: Int,
    val heightMm: Int,
    val quantity: Int = 1,
    val label: String = "",
    val canRotate: Boolean = true
)

data class PlacedRect(
    val xMm: Int,
    val yMm: Int,
    val widthMm: Int,
    val heightMm: Int,
    val label: String,
    val rotated: Boolean,
    val color: Color
)

data class PlanResult(
    val placed: List<PlacedRect>,
    val notPlaced: List<RectSpec>,
    val sheet: SheetSpec
) {
    val usedAreaMm2: Long = placed.sumOf { it.widthMm.toLong() * it.heightMm.toLong() }
    val sheetAreaMm2: Long = sheet.widthMm.toLong() * sheet.heightMm.toLong()
    val yieldPercent: Double = usedAreaMm2 * 100.0 / sheetAreaMm2
    val wasteAreaCm2: Double = (sheetAreaMm2 - usedAreaMm2) / 100.0 // mm² -> cm²
}
