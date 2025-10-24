package com.quirozsolucions.cortesapp.algo

import com.quirozsolucions.cortesapp.model.*

/**
 * Heurística Shelf/Guillotine:
 * - Ordena por altura (desc) y llena estantes (cortes horizontales).
 * - Soporta kerf (ancho de corte) en mm y rotación opcional 90°.
 */
object ShelfGuillotine {

    fun layout(
        board: Board,
        inputPieces: List<Piece>,
        kerfMm: Int = 0,
        allowRotation: Boolean = false
    ): LayoutResult {

        val kerfCm = kerfMm.toFloat() / 10f // 10 mm = 1 cm (float para cálculos intermedios)

        // Explota cantidades y asigna índice visible
        val expanded = inputPieces.flatMap { p ->
            List(p.quantity) { idx -> p.copy(id = "${p.id}_${idx+1}".hashCode()) to (idx + 1) }
        }.mapIndexed { globalIdx, pair -> Triple(globalIdx + 1, pair.first, pair.second) }

        // Ordena por altura y ancho
        val pieces = expanded.sortedWith(
            compareByDescending<Triple<Int, Piece, Int>> { it.second.heightCm }
                .thenByDescending { it.second.widthCm }
        )

        data class Shelf(var y: Float, var height: Float, var xCursor: Float)
        val shelves = mutableListOf(Shelf(0f, 0f, 0f))

        val placed = mutableListOf<PlacedPiece>()
        val unplaced = mutableListOf<Piece>()

        val boardW = board.widthCm.toFloat()
        val boardH = board.heightCm.toFloat()

        var current = shelves.last()

        fun fitsOnShelf(w: Float, h: Float) =
            h <= current.height && current.xCursor + w <= boardW

        pieces.forEach { (visibleIdx, p, localIdx) ->
            // Elegir orientación si se permite rotación
            val options = if (allowRotation)
                listOf(p.widthCm to p.heightCm, p.heightCm to p.widthCm)
            else listOf(p.widthCm to p.heightCm)

            var placedNow = false

            for ((w0, h0) in options) {
                val w = w0.toFloat()
                val h = h0.toFloat()

                if (current.height == 0f) current.height = h

                if (fitsOnShelf(w, h)) {
                    placed += PlacedPiece(
                        id = p.id,
                        index = visibleIdx,
                        xCm = current.xCursor.toInt(),
                        yCm = current.y.toInt(),
                        widthCm = w.toInt(),
                        heightCm = h.toInt(),
                        shelfIndex = shelves.lastIndex
                    )
                    current.xCursor += w + kerfCm
                    placedNow = true
                    break
                }
            }

            if (!placedNow) {
                // Intentar nuevo estante con orientación óptima (la más baja)
                val (w0, h0) = options.minBy { it.second }
                val w = w0.toFloat()
                val h = h0.toFloat()

                val newY = current.y + current.height + kerfCm
                if (newY + h <= boardH && w <= boardW) {
                    current = Shelf(newY, h, 0f)
                    shelves += current
                    placed += PlacedPiece(
                        id = p.id,
                        index = visibleIdx,
                        xCm = 0,
                        yCm = current.y.toInt(),
                        widthCm = w.toInt(),
                        heightCm = h.toInt(),
                        shelfIndex = shelves.lastIndex
                    )
                    current.xCursor = w + kerfCm
                } else {
                    unplaced += p
                }
            }
        }

        val usedArea = placed.sumOf { it.widthCm * it.heightCm }
        val boardArea = (boardW * boardH).toInt()
        val waste = (boardArea - usedArea).coerceAtLeast(0)
        val util = if (boardArea > 0) usedArea.toFloat() / boardArea else 0f

        return LayoutResult(placed, unplaced, usedArea, waste, util)
    }
}
