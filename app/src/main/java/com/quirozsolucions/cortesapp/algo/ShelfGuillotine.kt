package com.quirozsolucions.cortesapp.algo

import com.quirozsolucions.cortesapp.model.Board
import com.quirozsolucions.cortesapp.model.LayoutResult
import com.quirozsolucions.cortesapp.model.Piece
import com.quirozsolucions.cortesapp.model.PlacedPiece

/**
 * Algoritmo tipo MaxRects (Best Area Fit) con soporte de kerf.
 *
 * Convenciones (todo en milímetros):
 *  - Board.widthMm  -> L1 (eje X)
 *  - Board.heightMm -> Beta (eje Y)
 */
object ShelfGuillotine {

    data class FreeRect(
        var x: Float,
        var y: Float,
        var w: Float,
        var h: Float
    )

    private fun rectsIntersect(a: FreeRect, b: FreeRect): Boolean {
        return !(b.x >= a.x + a.w ||
                b.x + b.w <= a.x ||
                b.y >= a.y + a.h ||
                b.y + b.h <= a.y)
    }

    fun layout(
        board: Board,
        inputPieces: List<Piece>,
        kerfMm: Int = 0,
        allowRotation: Boolean = false
    ): LayoutResult {

        val kerf = kerfMm.toFloat()
        val boardW = board.widthMm.toFloat()
        val boardH = board.heightMm.toFloat()

        // Expandir cantidades
        data class EPiece(
            val indexVisible: Int,
            val piece: Piece
        )

        val expanded = inputPieces.flatMap { p ->
            List(p.quantity) { idx ->
                EPiece(
                    indexVisible = 0,
                    piece = p.copy(
                        id = "${p.id}_${idx + 1}".hashCode(),
                        quantity = 1
                    )
                )
            }
        }.mapIndexed { idx, e ->
            e.copy(indexVisible = idx + 1)
        }

        // Ordenar por área descendente
        val pieces = expanded.sortedByDescending {
            it.piece.widthMm * it.piece.heightMm
        }

        // Lista de rectángulos libres
        val freeRects = mutableListOf(
            FreeRect(0f, 0f, boardW, boardH)
        )

        val placed = mutableListOf<PlacedPiece>()
        val unplaced = mutableListOf<Piece>()

        fun splitFreeRect(free: FreeRect, used: FreeRect, kerf: Float) {
            val x = free.x
            val y = free.y
            val w = free.w
            val h = free.h

            val ux = used.x
            val uy = used.y
            val uw = used.w
            val uh = used.h

            // Arriba
            if (uy > y) {
                val nh = uy - y - kerf
                if (nh > 0f) {
                    freeRects += FreeRect(
                        x = x,
                        y = y,
                        w = w,
                        h = nh
                    )
                }
            }

            // Abajo
            if (uy + uh < y + h) {
                val ny = uy + uh + kerf
                val nh = (y + h) - ny
                if (nh > 0f) {
                    freeRects += FreeRect(
                        x = x,
                        y = ny,
                        w = w,
                        h = nh
                    )
                }
            }

            // Izquierda
            if (ux > x) {
                val nw = ux - x - kerf
                if (nw > 0f) {
                    freeRects += FreeRect(
                        x = x,
                        y = y,
                        w = nw,
                        h = h
                    )
                }
            }

            // Derecha
            if (ux + uw < x + w) {
                val nx = ux + uw + kerf
                val nw = (x + w) - nx
                if (nw > 0f) {
                    freeRects += FreeRect(
                        x = nx,
                        y = y,
                        w = nw,
                        h = h
                    )
                }
            }
        }

        fun pruneFreeRects() {
            var i = 0
            while (i < freeRects.size) {
                var removed = false
                var j = i + 1
                while (j < freeRects.size) {
                    val a = freeRects[i]
                    val b = freeRects[j]

                    val containsAB =
                        a.x <= b.x &&
                                a.y <= b.y &&
                                a.x + a.w >= b.x + b.w &&
                                a.y + a.h >= b.y + b.h

                    val containsBA =
                        b.x <= a.x &&
                                b.y <= a.y &&
                                b.x + b.w >= a.x + a.w &&
                                b.y + b.h >= a.y + a.h

                    if (containsAB) {
                        freeRects.removeAt(j)
                        continue
                    }
                    if (containsBA) {
                        freeRects.removeAt(i)
                        removed = true
                        break
                    }
                    j++
                }
                if (!removed) i++
            }
        }

        // MAXRECTS BAF — Best Area Fit con criterio secundario
        for (ep in pieces) {
            val p = ep.piece
            val visible = ep.indexVisible

            val l1 = p.widthMm.toFloat()
            val beta = p.heightMm.toFloat()

            val orientations =
                if (allowRotation) listOf(l1 to beta, beta to l1)
                else listOf(l1 to beta)

            var bestRect: FreeRect? = null
            var bestPlacement: FreeRect? = null
            var bestWaste = Float.MAX_VALUE

            // Buscar el mejor hueco
            for (free in freeRects) {
                for ((w, h) in orientations) {
                    if (w + kerf <= free.w && h + kerf <= free.h) {
                        val waste = (free.w * free.h) - (w * h)

                        // Criterio:
                        // 1) Menos desperdicio
                        // 2) Si empatan, más arriba (menor y)
                        // 3) Si sigue empate, más a la izquierda (menor x)
                        val isBetter = when {
                            waste < bestWaste -> true
                            waste == bestWaste -> {
                                if (bestRect == null) {
                                    true
                                } else if (free.y < bestRect!!.y) {
                                    true
                                } else if (free.y == bestRect!!.y) {
                                    free.x < bestRect!!.x
                                } else {
                                    false
                                }
                            }
                            else -> false
                        }

                        if (isBetter) {
                            bestWaste = waste
                            bestRect = free
                            bestPlacement = FreeRect(
                                x = free.x,
                                y = free.y,
                                w = w,
                                h = h
                            )
                        }
                    }
                }
            }

            if (bestRect == null || bestPlacement == null) {
                // No se pudo ubicar esta pieza en este tablero
                unplaced += p
                continue
            }

            val usedRect = bestPlacement

            // Registrar la pieza colocada
            placed += PlacedPiece(
                id = p.id,
                index = visible,
                xMm = usedRect.x.toInt(),
                yMm = usedRect.y.toInt(),
                widthMm = usedRect.w.toInt(),
                heightMm = usedRect.h.toInt(),
                shelfIndex = 0
            )

            // Recortar el área ocupada de TODOS los rectángulos libres
            var i = 0
            while (i < freeRects.size) {
                val free = freeRects[i]
                if (rectsIntersect(free, usedRect)) {
                    freeRects.removeAt(i)
                    splitFreeRect(free, usedRect, kerf)
                    i--
                }
                i++
            }

            // Eliminar rectángulos redundantes
            pruneFreeRects()
        }

        // Cálculo de área (mm²)
        val usedArea = placed.sumOf { it.widthMm * it.heightMm }
        val boardArea = (boardW * boardH).toInt()
        val wasteArea = (boardArea - usedArea).coerceAtLeast(0)
        val util = usedArea.toFloat() / boardArea.toFloat()

        return LayoutResult(
            placed = placed,
            unplaced = unplaced,
            usedArea = usedArea,
            wasteArea = wasteArea,
            utilization = util
        )
    }
}
