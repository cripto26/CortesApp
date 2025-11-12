package com.quirozsolucions.cortesapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.quirozsolucions.cortesapp.algo.ShelfGuillotine
import com.quirozsolucions.cortesapp.model.*

class OptimizerViewModel : ViewModel() {

    var board by mutableStateOf(Board(widthCm = 215, heightCm = 244))
        private set

    var kerfMm by mutableStateOf(0)
        private set

    var allowRotation by mutableStateOf(false)
        private set

    var pieces by mutableStateOf(
        listOf(
            Piece(1, 0, 0, 0),

        )
    )
        private set

    /** Resultado en múltiples láminas (páginas) */
    var pages by mutableStateOf<List<LayoutResult>>(emptyList())
        private set

    /** Piezas que definitivamente no caben en el tablero (demasiado grandes) */
    var unfit by mutableStateOf<List<Piece>>(emptyList())
        private set

    fun updateBoard(w: Int?, h: Int?) {
        board = board.copy(
            widthCm = w ?: board.widthCm,
            heightCm = h ?: board.heightCm
        )
    }

    fun updateKerf(mm: Int?) { kerfMm = mm ?: kerfMm }
    fun toggleRotation() { allowRotation = !allowRotation }

    fun updatePiece(index: Int, width: Int?, height: Int?, qty: Int?) {
        pieces = pieces.toMutableList().also { list ->
            val p = list[index]
            list[index] = p.copy(
                widthCm = width ?: p.widthCm,
                heightCm = height ?: p.heightCm,
                quantity = qty ?: p.quantity
            )
        }
    }

    fun addRow() {
        val nextId = (pieces.maxOfOrNull { it.id } ?: 0) + 1
        pieces = pieces + Piece(nextId, 10, 10, 1)
    }

    /** Ejecuta la optimización y reparte automáticamente en n tableros */
    fun optimizeAll() {
        // Expandir cantidades (cada unidad como pieza independiente)
        var remaining = pieces.flatMap { p -> List(p.quantity) { p.copy(quantity = 1) } }

        val generated = mutableListOf<LayoutResult>()
        val cannotFit = mutableListOf<Piece>()

        // Filtrar las que jamás caben en una sola lámina
        remaining.partition { it.widthCm <= board.widthCm && it.heightCm <= board.heightCm ||
                (allowRotation && it.heightCm <= board.widthCm && it.widthCm <= board.heightCm)
        }.also { (fit, out) ->
            remaining = fit
            cannotFit += out
        }

        // Iterar creando páginas hasta ubicar todas las que sí caben
        while (remaining.isNotEmpty()) {
            val res = ShelfGuillotine.layout(
                board = board,
                inputPieces = remaining,
                kerfMm = kerfMm,
                allowRotation = allowRotation
            )
            generated += res

            // Si no ubicó ninguna en esta pasada, evitamos bucle infinito
            if (res.placed.isEmpty()) {
                cannotFit += remaining
                break
            }

            // Las no ubicadas quedan para la próxima lámina
            remaining = res.unplaced
        }

        pages = generated
        unfit = cannotFit
    }
}
