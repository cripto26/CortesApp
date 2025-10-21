package com.quirozsolucions.cortesapp.algo

import androidx.compose.ui.graphics.Color
import com.quirozsolucions.cortesapp.model.*

/**
 * Heurístico “Shelf First-Fit Decreasing”.
 * - Ordena piezas por su lado mayor (desc).
 * - Coloca de izq->der en el estante actual. Si no cabe, intenta rotar.
 * - Si no cabe en ningún estante, crea uno nuevo debajo (guillotina horizontal).
 * - Respeta kerf entre piezas y entre estantes.
 */
object ShelfGuillotine {

    private val palette = listOf(
        Color(0xFF8BC34A), Color(0xFFFFC107), Color(0xFF29B6F6),
        Color(0xFFF06292), Color(0xFF81D4FA), Color(0xFFFFAB91),
        Color(0xFFCE93D8), Color(0xFFA5D6A7), Color(0xFFFFF59D)
    )

    fun optimize(sheet: SheetSpec, specs: List<RectSpec>): PlanResult {
        // Expandir cantidades
        val expanded = specs.flatMap { s ->
            List(s.quantity) { idx ->
                s.copy(label = if (s.label.isNotBlank()) s.label else "${s.widthMm}x${s.heightMm} #${idx + 1}")
            }
        }.sortedByDescending { maxOf(it.widthMm, it.heightMm) }

        data class Shelf(var y: Int, var height: Int, var cursorX: Int)
        val kerf = sheet.kerfMm
        val shelves = mutableListOf<Shelf>()
        val placed = mutableListOf<PlacedRect>()
        val notPlaced = mutableListOf<RectSpec>()

        fun tryPlaceOnShelf(item: RectSpec, shelf: Shelf, color: Color): PlacedRect? {
            // Intento sin rotación
            if (item.widthMm <= sheet.widthMm - shelf.cursorX &&
                item.heightMm <= shelf.height) {
                val r = PlacedRect(
                    xMm = shelf.cursorX,
                    yMm = shelf.y,
                    widthMm = item.widthMm,
                    heightMm = item.heightMm,
                    label = item.label,
                    rotated = false,
                    color = color
                )
                shelf.cursorX += item.widthMm + kerf
                return r
            }
            // Intento rotado
            if (item.canRotate &&
                item.heightMm <= sheet.widthMm - shelf.cursorX &&
                item.widthMm <= shelf.height) {
                val r = PlacedRect(
                    xMm = shelf.cursorX,
                    yMm = shelf.y,
                    widthMm = item.heightMm,
                    heightMm = item.widthMm,
                    label = item.label,
                    rotated = true,
                    color = color
                )
                shelf.cursorX += item.heightMm + kerf
                return r
            }
            return null
        }

        var colorIdx = 0
        for (item in expanded) {
            var placedHere: PlacedRect? = null
            // Probar estantes existentes
            for (s in shelves) {
                placedHere = tryPlaceOnShelf(item, s, palette[colorIdx % palette.size])
                if (placedHere != null) break
            }
            // Crear estante si no cupo
            if (placedHere == null) {
                val shelfHeight = maxOf(item.heightMm, if (item.canRotate) item.widthMm else 0)
                val newY = if (shelves.isEmpty()) 0 else shelves.last().let { it.y + it.height + kerf }
                if (newY + shelfHeight <= sheet.heightMm) {
                    val s = Shelf(y = newY, height = shelfHeight, cursorX = 0)
                    val r = tryPlaceOnShelf(item, s, palette[colorIdx % palette.size])
                    if (r != null) {
                        shelves += s
                        placedHere = r
                    }
                }
            }
            if (placedHere != null) {
                placed += placedHere
                colorIdx++
            } else {
                notPlaced += item
            }
        }
        return PlanResult(placed = placed, notPlaced = notPlaced, sheet = sheet)
    }
}
